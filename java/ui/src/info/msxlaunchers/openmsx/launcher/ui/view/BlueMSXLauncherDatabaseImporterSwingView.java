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
import info.msxlaunchers.openmsx.launcher.ui.presenter.BlueMSXLauncherDatabasesImporterPresenter;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.ImportBlueMSXLauncherDatabasesWindow;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.component.MessageBoxUtil;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.language.LanguageDisplayFactory;

import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;

/**
 * Swing-based implementation of <code>BlueMSXLauncherDatabaseImporterView</code>
 * 
 * @since v1.3
 * @author Sam Elsharif
 *
 */
class BlueMSXLauncherDatabaseImporterSwingView implements BlueMSXLauncherDatabaseImporterView
{
	private final BlueMSXLauncherDatabasesImporterPresenter presenter;

	ImportBlueMSXLauncherDatabasesWindow importBlueMSXLauncherDatabasesWindow = null;

	@Inject
	BlueMSXLauncherDatabaseImporterSwingView( BlueMSXLauncherDatabasesImporterPresenter presenter )
	{
		this.presenter = presenter;
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.BlueMSXLauncherDatabaseImporterView#displayScreen(info.msxlaunchers.openmsx.launcher.data.settings.constants.Language, boolean, java.util.Set)
	 */
	@Override
	public void displayScreen( Language language, boolean rightToLeft, Set<String> machines )
	{
		importBlueMSXLauncherDatabasesWindow = new ImportBlueMSXLauncherDatabasesWindow(presenter, language, rightToLeft, machines);

		importBlueMSXLauncherDatabasesWindow.displayScreen();
	}

	/* (non-Javadoc)
	 * @see info.msxlaunchers.openmsx.launcher.ui.view.BlueMSXLauncherDatabaseImporterView#displayAndGetActionDecider(java.lang.String, info.msxlaunchers.openmsx.launcher.data.settings.constants.Language, boolean)
	 */
	@Override
	public int displayAndGetActionDecider( String databaseName, Language language, boolean rightToLeft )
	{
		//TODO once I merge Swing classes with the views the following will be unnecessary
		Map<String,String> messages = LanguageDisplayFactory.getDisplayMessages(ImportBlueMSXLauncherDatabasesWindow.class, language);

		return MessageBoxUtil.showYesNoAllMessageBox(importBlueMSXLauncherDatabasesWindow,
				"<html>\"" + databaseName + "\" " + messages.get( "IMPORT_DATABASE_CONFLICT_MESSAGE" ) + "</html>", messages, rightToLeft);
	}
}
