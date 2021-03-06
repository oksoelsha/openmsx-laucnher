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

import com.google.inject.Inject;

import info.msxlaunchers.openmsx.launcher.data.settings.constants.Language;
import info.msxlaunchers.openmsx.launcher.ui.presenter.UpdateCheckerPresenter;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.UpdateCheckerWindow;

/**
 * Swing-based implementation of <code>UpdateCheckerView</code>
 * 
 * @since v1.4
 * @author Sam Elsharif
 *
 */
class UpdateCheckerSwingView implements UpdateCheckerView
{
	private final UpdateCheckerPresenter presenter;

	@Inject
	public UpdateCheckerSwingView( UpdateCheckerPresenter presenter )
	{
		this.presenter = presenter;
	}

	@Override
	public void displayScreen( Language language, boolean rightToLeft )
	{
		UpdateCheckerWindow updateCheckerWindow = new UpdateCheckerWindow(presenter, language, rightToLeft);

		updateCheckerWindow.display();
	}
}
