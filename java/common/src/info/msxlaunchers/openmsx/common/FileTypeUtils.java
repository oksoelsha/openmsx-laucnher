/*
 * Copyright 2013 Sam Elsharif
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
package info.msxlaunchers.openmsx.common;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class that contains static methods that handle <code>Game</code> file fields 
 * 
 * @since v1.0
 * @author Sam Elsharif
 */
public final class FileTypeUtils
{
	private static final Set<String> romExtensions = getUnmodifiableSet( "ri", "rom", "col", "mx1", "mx2" );
	private static final Set<String> diskExtensions = getUnmodifiableSet( "di1", "di2", "dmk", "dsk", "xsa", "fd1", "fd2" );
	private static final Set<String> tapeExtensions = getUnmodifiableSet( "cas", "wav", "tsx" );
	private static final Set<String> harddiskExtensions = getUnmodifiableSet( "dsk", "hdd" );
	private static final Set<String> laserdiscExtensions = getUnmodifiableSet( "ogv" );
	private static final Set<String> zipExtensions = getUnmodifiableSet( "zip", "gz" );
	private static final Set<String> xmlExtension = getUnmodifiableSet( "xml" );
	private static final Set<String> patchExtensions = getUnmodifiableSet( "ips", "ups" );
	private static final Set<String> lhaExtensions = getUnmodifiableSet( "lha", "lzh" );

	public static final long MAX_DISK_FILE_SIZE = 737280;

	/**
	 * Returns if the given file is a ROM based on its extension
	 * 
	 * @param file File
	 * @return true if file is a ROM, false otherwise
	 */
	public static boolean isROM( File file )
	{
		return isType( file, romExtensions );
	}

	/**
	 * Returns if the given file is a disk based on its extension
	 * 
	 * @param file File
	 * @return true if file is a disk, false otherwise
	 */
	public static boolean isDisk( File file )
	{
		return isType( file, diskExtensions );
	}
	
	/**
	 * Returns if the given file is a tape based on its extension
	 * 
	 * @param file File
	 * @return true if file is a tape, false otherwise
	 */
	public static boolean isTape( File file )
	{
		return isType( file, tapeExtensions );
	}
	
	/**
	 * Returns if the given file is a harddisk based on its extension
	 * 
	 * @param file File
	 * @return true if file is a harddisk, false otherwise
	 */
	public static boolean isHarddisk( File file )
	{
		return isType( file, harddiskExtensions );
	}

	/**
	 * Returns if the given file is a laserdisc based on its extension
	 * 
	 * @param file File
	 * @return true if file is a laserdisc, false otherwise
	 */
	public static boolean isLaserdisc( File file )
	{
		return isType( file, laserdiscExtensions );
	}
	
	/**
	 * Returns if the given file is a ZIP file based on its extension
	 * 
	 * @param file File
	 * @return true if file is a ZIP file, false otherwise
	 */
	public static boolean isZIP( File file )
	{
		return isType( file, zipExtensions );
	}

	/**
	 * Returns if the given file is an XML file based on its extension
	 * 
	 * @param file File
	 * @return true if file is an XML file, false otherwise
	 */
	public static boolean isXML( File file )
	{
		return isType( file, xmlExtension );
	}

	/**
	 * Returns if the given file is a patch file based on its extension
	 * 
	 * @param file File
	 * @return true if file is a patch file, false otherwise
	 */
	public static boolean isPatch( File file )
	{
		return isType( file, patchExtensions );
	}

	/**
	 * Returns if the given file is a compressed LHA file based on its extension
	 * 
	 * @param file File
	 * @return true if file is an LHA compressed file, false otherwise
	 */
	public static boolean isLHA( File file )
	{
		return isType( file, lhaExtensions );
	}

	/**
	 * Returns which of the given game fields is the main file for that game. The check is to find the first 
	 * non-empty game field in the following order:
	 * ROM A then ROM B then disk A then disk B then tape then hard disk then laserdisc then script.
	 * 
	 * @param romA ROM A file name
	 * @param romB ROM B file name
	 * @param diskA Disk A file name
	 * @param diskB Disk B file name
	 * @param tape Tape file name
	 * @param harddisk Hard disk file name
	 * @param laserdisc Laserdisc file name
	 * @param script Script file name
	 * @return First non-empty field in the above mentioned order
	 */
	public static String getMainFile( String romA, String romB, String diskA, String diskB,
			String tape, String harddisk, String laserdisc, String script )
	{
		String mainFile = null;

		if( !Utils.isEmpty( romA ) )
		{
			mainFile = romA;
		}
		else if( !Utils.isEmpty( romB ) )
		{
			mainFile = romB;
		}
		else if( !Utils.isEmpty( diskA ) )
		{
			mainFile = diskA;
		}
		else if( !Utils.isEmpty( diskB ) )
		{
			mainFile = diskB;
		}
		else if( !Utils.isEmpty( tape ) )
		{
			mainFile = tape;
		}
		else if( !Utils.isEmpty( harddisk ) )
		{
			mainFile = harddisk;
		}
		else if( !Utils.isEmpty( laserdisc ) )
		{
			mainFile = laserdisc;
		}
		else if( !Utils.isEmpty( script ) )
		{
			mainFile = script;
		}

		return mainFile;
	}

	/**
	 * Returns set containing ROM file extensions supported by openMSX
	 * 
	 * @return Non-modifiable set containing ROM file extensions
	 */
	public static Set<String> getROMExtensions()
	{
		return romExtensions;
	}

	/**
	 * Returns set containing disk file extensions supported by openMSX
	 * 
	 * @return Non-modifiable set containing disk file extensions
	 */
	public static Set<String> getDiskExtensions()
	{
		return diskExtensions;
	}

	/**
	 * Returns set containing tape file extensions supported by openMSX
	 * 
	 * @return Non-modifiable set containing tape file extensions
	 */
	public static Set<String> getTapeExtensions()
	{
		return tapeExtensions;
	}

	/**
	 * Returns set containing harddisk file extensions supported by openMSX
	 * 
	 * @return Non-modifiable set containing harddisk file extensions
	 */
	public static Set<String> getHarddiskExtensions()
	{
		return harddiskExtensions;
	}

	/**
	 * Returns set containing laserdisc file extensions supported by openMSX
	 * 
	 * @return Non-modifiable set containing laserdisc file extensions
	 */
	public static Set<String> getLaserdiscExtensions()
	{
		return laserdiscExtensions;
	}

	/**
	 * Returns set containing ZIP file extensions supported by openMSX
	 * 
	 * @return Non-modifiable set containing ZIP file extensions
	 */
	public static Set<String> getZIPExtensions()
	{
		return zipExtensions;
	}

	/**
	 * Returns set containing patch file extensions
	 * 
	 * @return Non-modifiable set containing patch file extensions
	 */
	public static Set<String> getPatchExtensions()
	{
		return patchExtensions;
	}

	/**
	 * Returns set containing LHA file extensions
	 * 
	 * @return Non-modifiable set containing LHA file extensions
	 */
	public static Set<String> getLHAExtensions()
	{
		return lhaExtensions;
	}

	private static Set<String> getUnmodifiableSet( String...extensions )
	{
		return Arrays.asList(extensions).stream()
				.collect( Collectors.collectingAndThen( Collectors.toSet(), Collections::unmodifiableSet ) );
	}

	private static boolean isType( File file, Set<String> validExtensions )
	{
		boolean isType = false;
		
		if( file == null || file.isDirectory() )
		{
			isType = false;
		}
		else
		{
			String filename = file.getName();
			String extension = filename.substring( filename.lastIndexOf( '.' ) + 1 ).toLowerCase();

			isType = validExtensions.contains( extension );
		}

		return isType;
	}
}
