/*
 * Copyright 2017 Sam Elsharif
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
package info.msxlaunchers.openmsx.launcher.feed;

import java.io.IOException;
import java.util.List;

import info.msxlaunchers.openmsx.launcher.data.feed.FeedMessage;
/**
 * Feed reader interface
 * 
 * @since v1.10
 * @author Sam Elsharif
 *
 */
public interface FeedReader
{
	/**
	 * Reads feed from the given URL
	 * 
	 * @param feedUrl URL of the feed
	 * @param siteName Site name
	 * @param siteUrl URL of the site
	 * @return Unmodifiable list of messages from the given URL
	 * @throws IOException
	 */
	List<FeedMessage> read( String feedUrl, String siteName, String siteUrl ) throws IOException;
}
