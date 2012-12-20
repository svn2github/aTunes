/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.utils.I18nUtils;

final class FullScreenBackgroundFileFilter extends FileFilter {
	
	@Override
	public boolean accept(File pathname) {
	    if (pathname.isDirectory()) {
	        return true;
	    }
	    String fileName = pathname.getName().toUpperCase();
	    return fileName.endsWith("JPG") || fileName.endsWith("JPEG") || fileName.endsWith("PNG");
	}

	@Override
	public String getDescription() {
	    return I18nUtils.getString("IMAGES");
	}
}