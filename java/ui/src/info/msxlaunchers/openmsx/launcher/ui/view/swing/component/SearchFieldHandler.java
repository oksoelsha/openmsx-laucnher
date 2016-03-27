/*
 * Copyright 2016 Sam Elsharif
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
package info.msxlaunchers.openmsx.launcher.ui.view.swing.component;

import java.util.Set;

/**
 * Interface containing a method to process text string entered by user by searching the database for matches
 * 
 * @since v1.6
 * @author Sam Elsharif
 *
 */
public interface SearchFieldHandler
{
	/**
	 * Get search matches for the given string
	 * 
	 * @param searchString String entered by user to use in the search
	 */
	Set<String> getSearchMatches(String searchString);

	/**
	 * Handle search selection (e.g. go the selected game on the UI)
	 * 
	 * @param searchSelection Search selection made by user
	 */
	void handleSearchSelection(String searchSelection);
}
