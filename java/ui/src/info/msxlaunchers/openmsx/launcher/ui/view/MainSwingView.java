/*
 * Copyright 2014 Sam Elsharif
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
package info.msxlaunchers.openmsx.launcher.ui.view;

import info.msxlaunchers.openmsx.launcher.data.settings.constants.Language;
import info.msxlaunchers.openmsx.launcher.ui.presenter.GameLabel;
import info.msxlaunchers.openmsx.launcher.ui.presenter.MainPresenter;
import info.msxlaunchers.openmsx.launcher.ui.view.platform.PlatformViewProperties;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.CreateEmptyDatabaseWindow;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.HelpWindow;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.MainWindow;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.MoveGamesWindow;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.component.MessageBoxUtil;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.language.LanguageDisplayFactory;

import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import com.google.inject.Inject;

/**
 * Swing-based implementation of <code>MainView</code>
 * 
 * @since v1.2
 * @author Sam Elsharif
 *
 */
class MainSwingView implements MainView
{
	private final MainPresenter mainPresenter;
	private final MainWindow mainWindow;

	@Inject
	MainSwingView( MainPresenter mainPresenter, PlatformViewProperties platformViewProperties )
	{
		this.mainPresenter = mainPresenter;
		this.mainWindow = new MainWindow( mainPresenter, platformViewProperties );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#displayMain(info.msxlaunchers.openmsx.launcher.data.settings.constants.Language, java.util.Set, java.util.Set, java.lang.String, java.util.Set, boolean)
	 */
	@Override
	public void displayMain( Language language,
			Set<GameLabel> games,
			Set<String> databases,
			String defaultDatabase,
			boolean rightToLeft )
	{
		SwingUtilities.invokeLater( new MainWindowStarter( language, games, databases, defaultDatabase, rightToLeft ) );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#refreshLanguage(info.msxlaunchers.openmsx.launcher.data.settings.constants.Language)
	 */
	@Override
	public void refreshLanguage( Language language )
	{
		mainWindow.refreshLanguage( language );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#flipOrientationLeftToRight()
	 */
	@Override
	public void flipOrientationLeftToRight()
	{
		mainWindow.flipOrientationLeftToRight();
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#flipOrientationRightToLeft()
	 */
	@Override
	public void flipOrientationRightToLeft()
	{
		mainWindow.flipOrientationRightToLeft();
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#fillGameList(java.lang.String, java.util.Set, java.lang.String)
	 */
	@Override
	public void fillGameList( String currentDatabase, Set<GameLabel> games, String selectedGame )
	{
		mainWindow.fillGameList( currentDatabase, games, selectedGame );
		mainWindow.updateGameCount( games.size() );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#updateGameCount(int)
	 */
	@Override
	public void updateGameCount( int total )
	{
		mainWindow.updateGameCount( total );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#displayAbout(info.msxlaunchers.openmsx.launcher.data.settings.constants.Language, boolean, java.lang.String, java.lang.String)
	 */
	@Override
	public void displayAbout( Language language, boolean rightToLeft, String extraDataVersion, String screenshotsVersion )
	{
		HelpWindow helpWindow = new HelpWindow( language, rightToLeft, extraDataVersion, screenshotsVersion, mainWindow );

		helpWindow.display();
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#showGameScreenshots(java.lang.String, java.lang.String)
	 */
	@Override
	public void showGameScreenshots( String screenshot1, String screenshot2 )
	{
		mainWindow.showGameScreenshot( screenshot1, screenshot2 );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#enableButtons(boolean, boolean, boolean, boolean)
	 */
	@Override
	public void enableButtons( boolean launchFlag, boolean removeFlag, boolean editFlag, boolean infoFlag )
	{
		mainWindow.enableButtons( launchFlag, removeFlag, editFlag, infoFlag );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#displayAndGetMoveGames(info.msxlaunchers.openmsx.launcher.data.settings.constants.Language, java.util.Set, java.lang.String, java.util.Set, boolean)
	 */
	@Override
	public Set<String> displayAndGetMoveGames( Language language, Set<String> gameNames, String oldDatabase,
			Set<String> targetDatabases, boolean rightToLeft )
	{
		MoveGamesWindow moveGamesWindow = new MoveGamesWindow( mainPresenter, language, rightToLeft, mainWindow,
				gameNames, oldDatabase, targetDatabases );

		return moveGamesWindow.displayAndGetMovedGames();
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#displayAndGetActionDecider(java.lang.String, info.msxlaunchers.openmsx.launcher.data.settings.constants.Language, boolean)
	 */
	@Override
	public int displayAndGetActionDecider( String gameName, Language language, boolean rightToLeft )
	{
		//TODO 'Move Game' should have a separate presenter and view
		//TODO once I merge Swing classes with the views the following will be unnecessary
		Map<String,String> messages = LanguageDisplayFactory.getDisplayMessages(MainWindow.class, language);

		return MessageBoxUtil.showYesNoAllMessageBox(mainWindow, "<html>\"" + gameName + "\" " + messages.get( "MOVE_GAME_CONFLICT_MESSAGE" ) + "</html>", messages, rightToLeft);
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#addDatabase(java.lang.String)
	 */
	@Override
	public void addDatabase( String database )
	{
		mainWindow.addDatabase( database );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#removeDatabase(java.lang.String)
	 */
	@Override
	public void removeDatabase( String database )
	{
		mainWindow.removeDatabase( database );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#renameDatabase(java.lang.String, java.lang.String)
	 */
	@Override
	public void renameDatabase( String oldDatabase, String newDatabase )
	{
		mainWindow.renameDatabase( oldDatabase, newDatabase );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#displayCreateEmptyDatabase(info.msxlaunchers.openmsx.launcher.data.settings.constants.Language, boolean)
	 */
	@Override
	public void displayCreateEmptyDatabase( Language language, boolean rightToLeft )
	{
		CreateEmptyDatabaseWindow createEmptyDatabaseWindow = new CreateEmptyDatabaseWindow( mainPresenter, language, rightToLeft, mainWindow );

		createEmptyDatabaseWindow.displayAndGetNewDatabaseName();
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#showGameCompanyYearSizeData(java.lang.String, java.lang.String, long)
	 */
	@Override
	public void showGameCompanyYearSizeData( String company, String year, long size )
	{
		mainWindow.showGameCompanyYearSizeData( company, year, size );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#resetGameCompanyYearSizeData()
	 */
	@Override
	public void resetGameCompanyYearSizeData()
	{
		mainWindow.resetGameCompanyYearSizeData();
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#showFavoritesMenu(java.util.Set)
	 */
	@Override
	public void showFavoritesMenu( Set<String> favoritesAsStrings )
	{
		mainWindow.showFavoritesMenu( favoritesAsStrings );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#highlightGame(java.lang.String)
	 */
	@Override
	public void highlightGame( String gameName )
	{
		mainWindow.higlightGame( gameName );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#showFiltersMenu(java.util.Set, boolean, boolean)
	 */
	@Override
	public void showFiltersMenu( Set<String> filterNames, boolean isFilterSelected, boolean isEditCurrentUntitledFilter )
	{
		mainWindow.showFiltersMenu( filterNames, isFilterSelected, isEditCurrentUntitledFilter );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#updateFilterNameLabel(java.lang.String)
	 */
	@Override
	public void updateFilterNameLabel( String filtersName )
	{
		mainWindow.updateFilterNameLabel( filtersName );
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.MainView#setFilterNameLabelUntitled()
	 */
	@Override
	public void setFilterNameLabelUntitled()
	{
		mainWindow.setFilterNameLabelUntitled();
	}

	private class MainWindowStarter implements Runnable
	{
		private final Language language;
		private final Set<GameLabel> games;
		private final Set<String> databases;
		private final String defaultDatabase;
		private final boolean rightToLeft;

		MainWindowStarter( Language language,
				Set<GameLabel> games,
				Set<String> databases,
				String defaultDatabase,
				boolean rightToLeft )
		{
			this.language = language;
			this.games = games;
			this.databases = databases;
			this.defaultDatabase = defaultDatabase;
			this.rightToLeft = rightToLeft;
		}

		@Override
		public void run()
		{
			mainWindow.display( language, games, databases, defaultDatabase, rightToLeft );
		}		
	}
}