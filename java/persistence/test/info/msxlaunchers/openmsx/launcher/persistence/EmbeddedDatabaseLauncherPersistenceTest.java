package info.msxlaunchers.openmsx.launcher.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import info.msxlaunchers.openmsx.launcher.persistence.favorite.FavoritePersister;
import info.msxlaunchers.openmsx.launcher.persistence.filter.FilterPersister;
import info.msxlaunchers.openmsx.launcher.persistence.game.GamePersister;
import info.msxlaunchers.openmsx.launcher.persistence.search.GameFinder;
import info.msxlaunchers.openmsx.launcher.persistence.settings.SettingsPersister;

import static org.junit.Assert.assertTrue;

@RunWith( MockitoJUnitRunner.class )
public class EmbeddedDatabaseLauncherPersistenceTest
{
	@Mock GamePersister gamePersister;
	@Mock FavoritePersister favoritePersister;
	@Mock FilterPersister filterPersister;
	@Mock SettingsPersister settingsPersister;
	@Mock GameFinder gameFinder;

	private String userDataDirectory;
	private String databasesDirectoryName;
	private String databaseFullPath;

	private EmbeddedDatabaseLauncherPersistence launcherPersistence;

	@Before
	public void setup()
	{
		userDataDirectory = "userDataDirectory";
		databasesDirectoryName = "databasesDirectoryName";
		databaseFullPath = "databaseFullPath";

		launcherPersistence = new EmbeddedDatabaseLauncherPersistence( gamePersister, favoritePersister, filterPersister,
				settingsPersister, gameFinder, userDataDirectory, databasesDirectoryName, databaseFullPath );
	}

	@Test
	public void test_whenGetGamePersister_thenReturnGamePersister()
	{
		assertTrue( gamePersister == launcherPersistence.getGamePersister() );
	}

	@Test
	public void test_whenGetFavoritePersister_thenReturnFavoritePersister()
	{
		assertTrue( favoritePersister == launcherPersistence.getFavoritePersister() );
	}

	@Test
	public void test_whenGetFiltersPersister_thenReturnFiltersPersister()
	{
		assertTrue( filterPersister == launcherPersistence.getFiltersPersister() );
	}

	@Test
	public void test_whenGetSettingsPersister_thenReturnSettingsPersister()
	{
		assertTrue( settingsPersister == launcherPersistence.getSettingsPersister() );
	}

	@Test
	public void test_whenGetGameFinder_thenReturnGameFinder()
	{
		assertTrue( gameFinder == launcherPersistence.getGameFinder() );
	}
}