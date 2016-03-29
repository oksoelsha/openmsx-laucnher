/*
 * Copyright 2015 Sam Elsharif
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.msxlaunchers.openmsx.launcher.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import com.csvreader.CsvReader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import info.msxlaunchers.openmsx.common.Utils;
import info.msxlaunchers.openmsx.launcher.data.game.Game;
import info.msxlaunchers.openmsx.launcher.data.game.constants.Genre;
import info.msxlaunchers.openmsx.launcher.persistence.favorite.FavoritePersister;
import info.msxlaunchers.openmsx.launcher.persistence.filter.FilterPersister;
import info.msxlaunchers.openmsx.launcher.persistence.game.DerbyLogSuppressor;
import info.msxlaunchers.openmsx.launcher.persistence.game.GamePersistenceException;
import info.msxlaunchers.openmsx.launcher.persistence.game.GamePersister;
import info.msxlaunchers.openmsx.launcher.persistence.search.GameFinder;
import info.msxlaunchers.openmsx.launcher.persistence.settings.SettingsPersister;

/**
 * Implementation of the <code>LauncherPersistence</code> interface that persists data and retrieves them from an embedded
 * database. This class is designated with Guice's Singleton annotation to keep only one instance of it in the lifetime
 * of the Guice injector (which is the lifetime of the entire application)
 * 
 * @since v1.4
 * @author Sam Elsharif
 *
 */
@Singleton
final class EmbeddedDatabaseLauncherPersistence implements LauncherPersistence
{
	private final String CREATE_DATABASE_TABLE_STATEMENT = "CREATE TABLE database (ID BIGINT not null generated always as identity," +
			" name VARCHAR(64) not null unique, primary key (ID))";
	private final String CREATE_DATABASE_BACKUP_TABLE_STATEMENT = "CREATE TABLE database_backup (ID BIGINT not null generated always as identity," +
			" time TIMESTAMP not null, IDDB BIGINT not null, primary key (ID))";
	private final String ADD_FOREIGN_KEY_TO_DATABASE_BACKUP_TABLE = "ALTER TABLE database_backup ADD CONSTRAINT DATABASE_FK Foreign Key (IDDB) REFERENCES database (ID) ON DELETE CASCADE";
	private final String GAME_TABLE_DEF = " (ID BIGINT not null generated always as identity," +
			"name VARCHAR(128) not null, info VARCHAR(512), machine VARCHAR(64) not null," +
			"romA VARCHAR(512), extension_rom VARCHAR(20), romB VARCHAR(512)," +
			"diskA VARCHAR(512), diskB VARCHAR(512), tape VARCHAR(512), harddisk VARCHAR(512), laserdisc VARCHAR(512), tcl_script VARCHAR(512)," + 
			"msx BOOLEAN default false, msx2 BOOLEAN default false, msx2plus BOOLEAN default false, turbo_r BOOLEAN default false," +
			"psg BOOLEAN default false, scc BOOLEAN default false, scc_i BOOLEAN default false, pcm BOOLEAN default false," +
			"msx_music BOOLEAN default false, msx_audio BOOLEAN default false, moonsound BOOLEAN default false, midi BOOLEAN default false," +
			"genre1 INTEGER, genre2 INTEGER, msx_genid INTEGER, screenshot_suffix VARCHAR(10), sha1 VARCHAR(40), size BIGINT," +
			"IDDB BIGINT not null, primary key (ID))";
	private final String CREATE_GAME_TABLE_STATEMENT = "CREATE TABLE game" + GAME_TABLE_DEF;
	private final String ADD_FOREIGN_KEY_TO_GAME_TABLE = "ALTER TABLE game ADD CONSTRAINT DATABASE_GAME_FK Foreign Key (IDDB) REFERENCES database (ID) ON DELETE CASCADE";
	private final String ADD_UNIQUE_CONSTRAINT_TO_GAME_TABLE = "ALTER TABLE game ADD CONSTRAINT UNIQUE_GAMENAME UNIQUE(name,IDDB)";
	private final String CREATE_GAME_BACKUP_TABLE_STATEMENT = "CREATE TABLE game_backup" + GAME_TABLE_DEF;
	private final String ADD_FOREIGN_KEY_TO_GAME_BACKUP_TABLE = "ALTER TABLE game_backup ADD CONSTRAINT DATABASE_BAK_FK Foreign Key (IDDB) REFERENCES database_backup (ID) ON DELETE CASCADE";
	private final String CREATE_FAVORITE_TABLE_STATEMENT = "CREATE TABLE favorite (ID BIGINT not null generated always as identity, IDGAME BIGINT not null unique, primary key (ID))";
	private final String ADD_FOREIGN_KEY_TO_FAVORITE_TABLE = "ALTER TABLE favorite ADD CONSTRAINT GAME_FK Foreign Key (IDGAME) REFERENCES game (ID) ON DELETE CASCADE";

	private final GamePersister gamePersister;
	private final FavoritePersister favoritePersister;
	private final FilterPersister filterPersister;
	private final SettingsPersister settingsPersister;
	private final GameFinder gameFinder;
	private final String userDataDirectory;
	private final File databasesDirectory;
	private final String databaseFullPath;

	private static final String BACKUPS_DIRECTORY = "backups";
	private final String DATABASE_EXTENSION = ".dbo";
	private final String DATABASE_BACKUP_EXTENSION = ".bak";

	@Inject
	EmbeddedDatabaseLauncherPersistence( GamePersister gamePersister,
			FavoritePersister favoritePersister,
			FilterPersister filterPersister,
			SettingsPersister settingsPersister,
			GameFinder gameFinder,
			@Named("UserDataDirectory") String userDataDirectory,
			@Named("DatabasesDirectoryName") String databasesDirectoryName,
			@Named("EmbeddedDatabaseFullPath") String databaseFullPath )
	{
		this.gamePersister = gamePersister;
		this.favoritePersister = favoritePersister;
		this.filterPersister = filterPersister;
		this.settingsPersister = settingsPersister;
		this.gameFinder = gameFinder;
		this.userDataDirectory = userDataDirectory;
		this.databasesDirectory = new File( userDataDirectory, databasesDirectoryName );
		this.databaseFullPath = databaseFullPath;
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.persistence.LauncherPersistence#initialize()
	 */
	@Override
	public void initialize() throws LauncherPersistenceException
	{
		//disable Derby logging
		System.setProperty( "derby.stream.error.method", DerbyLogSuppressor.class.getName() + ".getDevNull" );

		if( !databasesDirectory.exists() )
		{
			databasesDirectory.mkdir();
		}

		String dbURL = "jdbc:derby:" + databaseFullPath + ";create=true";

		try( Connection connection = DriverManager.getConnection( dbURL ) )
		{
			try
			{
				if( connection.getWarnings() == null )
				{
					//then this database did not exist before => create all tables and populate with old CSV files if found
					createTables( connection );

					importOldCSVFilesIfNecessary( connection );
				}
			}
			catch( SQLException se )
			{
				connection.rollback();
				throw new LauncherPersistenceException();
			}
		}
    	catch( SQLException se )
    	{
    		//TODO What to do?
    	}

		deleteOldBackupsDirectoryIfExists();
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.persistence.LauncherPersistence#shutdown()
	 */
	@Override
	public void shutdown() throws LauncherPersistenceException
	{
		String dbURL = "jdbc:derby:;shutdown=true";

		try( Connection connection = DriverManager.getConnection( dbURL ) )
		{
		}
    	catch( SQLException se )
    	{
    		//Ignore
    	}
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.persistence.LauncherPersistence#getGamePersister()
	 */
	@Override
	public GamePersister getGamePersister()
	{
		return gamePersister;
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.persistence.LauncherPersistence#getFavoritePersister()
	 */
	@Override
	public FavoritePersister getFavoritePersister()
	{
		return favoritePersister;
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.persistence.LauncherPersistence#getFiltersPersister()
	 */
	@Override
	public FilterPersister getFiltersPersister()
	{
		return filterPersister;
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.persistence.LauncherPersistence#getSettingsPersister()
	 */
	@Override
	public SettingsPersister getSettingsPersister()
	{
		return settingsPersister;
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.persistence.LauncherPersistence#getGameFinder()
	 */
	@Override
	public GameFinder getGameFinder()
	{
		return gameFinder;
	}

	private void createTables( Connection connection ) throws SQLException
	{
		createDatabaseTable( connection );
		createGameTable( connection );
		createDatabaseBackupTable( connection );
		createGameBackupTable( connection );
		createFavoriteTable( connection );
	}

	private void createDatabaseTable( Connection connection ) throws SQLException
	{
		try( Statement statement = connection.createStatement() )
		{
			statement.execute( CREATE_DATABASE_TABLE_STATEMENT );
		}
	}

	private void createGameTable( Connection connection ) throws SQLException
	{
		try( Statement statement = connection.createStatement() )
		{
			statement.execute( CREATE_GAME_TABLE_STATEMENT );
			statement.execute( ADD_FOREIGN_KEY_TO_GAME_TABLE );
			statement.execute( ADD_UNIQUE_CONSTRAINT_TO_GAME_TABLE );
		}
	}

	private void createDatabaseBackupTable( Connection connection ) throws SQLException
	{
		try( Statement statement = connection.createStatement() )
		{
			statement.execute( CREATE_DATABASE_BACKUP_TABLE_STATEMENT );
			statement.execute( ADD_FOREIGN_KEY_TO_DATABASE_BACKUP_TABLE );
		}
	}

	private void createGameBackupTable( Connection connection ) throws SQLException
	{
		try( Statement statement = connection.createStatement() )
		{
			statement.execute( CREATE_GAME_BACKUP_TABLE_STATEMENT );
			statement.execute( ADD_FOREIGN_KEY_TO_GAME_BACKUP_TABLE );
		}
	}

	private void createFavoriteTable( Connection connection ) throws SQLException
	{
		try( Statement statement = connection.createStatement() )
		{
			statement.execute( CREATE_FAVORITE_TABLE_STATEMENT );
			statement.execute( ADD_FOREIGN_KEY_TO_FAVORITE_TABLE );
		}
	}

	private void importOldCSVFilesIfNecessary( Connection connection ) throws LauncherPersistenceException
	{
		File[] files = databasesDirectory.listFiles();

		if( files != null )
		{
			for( File file: files )
			{
				if( file.isFile() && file.getName().endsWith( DATABASE_EXTENSION ) ) 
				{
					try
					{
						importCSVFile( connection, file );
					}
					catch( IOException ioe )
					{
						throw new LauncherPersistenceException();
					}
	
					//backup the file in case they're needed later
					file.renameTo( new File( file.getParent(), file.getName() + DATABASE_BACKUP_EXTENSION ) );
				}
			}
		}
	}

	private void importCSVFile( Connection connection, File database ) throws IOException, LauncherPersistenceException
	{
		String databaseName = database.getName().substring( 0, database.getName().lastIndexOf( DATABASE_EXTENSION ) );

		try
		{
			getGamePersister().createDatabase( databaseName );

			getGamePersister().saveGames( getGames( database ), databaseName );
		}
		catch( GamePersistenceException gpe )
		{
			throw new LauncherPersistenceException();
		}
	}

	private Set<Game> getGames( File database ) throws IOException
	{
		Set<Game> games = new HashSet<Game>();
		CsvReader gameRecords = null;
		try
		{
			gameRecords = new CsvReader( database.getAbsolutePath() );

			while ( gameRecords.readRecord() )
			{
				try
				{
					Game game = Game.name( gameRecords.get( 0 ) )
						.info( gameRecords.get( 1 ) )
						.machine( gameRecords.get( 2 ) )
						.romA( gameRecords.get( 3 ) )
						.extensionRom( gameRecords.get( 4 ) )
						.romB( gameRecords.get( 5 ) )
						.diskA( gameRecords.get( 6 ) )
						.diskB( gameRecords.get( 7 ) )
						.tape( gameRecords.get( 8 ) )
						.harddisk( gameRecords.get( 9 ) )
						.laserdisc( gameRecords.get( 10 ) )
						.tclScript( gameRecords.get( 11 ) )
						.isMSX( getBoolean( gameRecords.get( 12 ) ) )
						.isMSX2( getBoolean( gameRecords.get( 13 ) ) )
						.isMSX2Plus( getBoolean( gameRecords.get( 14 ) ) )
						.isTurboR( getBoolean( gameRecords.get( 15 ) ) )
						.isPSG( getBoolean( gameRecords.get( 16 ) ) )
						.isSCC( getBoolean( gameRecords.get( 17 ) ) )
						.isSCCI( getBoolean( gameRecords.get( 18 ) ) )
						.isPCM( getBoolean( gameRecords.get( 19 ) ) )
						.isMSXMUSIC( getBoolean( gameRecords.get( 20 ) ) )
						.isMSXAUDIO( getBoolean( gameRecords.get( 21 ) ) )
						.isMoonsound( getBoolean( gameRecords.get( 22 ) ) )
						.isMIDI( getBoolean( gameRecords.get( 23 ) ) )
						.genre1( Genre.fromValue( Utils.getNumber( gameRecords.get( 24 ) ) ) )
						.genre2( Genre.fromValue( Utils.getNumber( gameRecords.get( 25 ) ) ) )
						.msxGenID( Utils.getNumber( gameRecords.get( 26 ) ) )
						.screenshotSuffix( gameRecords.get( 27 ) )
						.sha1Code( gameRecords.get( 28 ) )
						.size( Utils.getNumber( gameRecords.get( 29 ) ) )
						.build();

					games.add( game );
				}
				catch( IllegalArgumentException iaex )
				{
					//just skip this game
				}
			}
		}
		finally
		{
			if( gameRecords != null )
			{
				gameRecords.close();
			}
		}

		return games;
	}

	private static boolean getBoolean( String string )
	{
		boolean value;

		if( string.equals( "1" ) )
		{
			value = true;
		}
		else
		{
			value = false;
		}

		return value;
	}

	/*
	 * The following code was taken (and modified slightly) from:
	 * http://fahdshariff.blogspot.ru/2011/08/java-7-deleting-directory-by-walking.html
	 */
	private void deleteOldBackupsDirectoryIfExists()
	{
		String  backupsDirectory = new File( userDataDirectory, BACKUPS_DIRECTORY ).toString();

		Path dir = Paths.get( backupsDirectory );
		try
		{
			Files.walkFileTree( dir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile( Path file, BasicFileAttributes attrs) throws IOException
				{
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory( Path dir, IOException ioe ) throws IOException
				{
					if( ioe == null )
					{
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}
					else
					{
						throw ioe;
					}
				}
			});
		}
		catch( IOException ioe )
		{
			//TODO what do we do here?
		}
	}
}