/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;

import net.sourceforge.atunes.model.LocalAudioObjectFormat;

import org.apache.commons.io.FilenameUtils;

public class LocalAudioObjectValidator {

    /**
     * Checks if is valid audio file.
     * 
     * @param file
     *            the file
     * 
     * @return true, if is valid audio file
     */
    public static boolean isValidAudioFile(File file) {
        return !file.isDirectory() && isOneOfTheseFormats(file.getName(), LocalAudioObjectFormat.values());
    }
    
    /**
     * Checks if a file is a valid audio file given its name
     * 
     * @param fileName
     * @param formats
     * @return if the file is a valid audio file
     */
    public static boolean isOneOfTheseFormats(String fileName, LocalAudioObjectFormat... formats) {
    	if (fileName == null) {
    		return false;
    	}
        String extension = FilenameUtils.getExtension(fileName);
        for (LocalAudioObjectFormat format : formats) {
            if (extension.equalsIgnoreCase(format.getExtension())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is valid audio file.
     * 
     * @param file
     *            the file
     * 
     * @return true, if is valid audio file
     */
    public static boolean isValidAudioFile(String file) {
        File f = new File(file);
        return f.exists() && isValidAudioFile(f);
    }



}
