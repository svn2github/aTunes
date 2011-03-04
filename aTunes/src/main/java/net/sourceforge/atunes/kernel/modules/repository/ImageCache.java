/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.ImageIcon;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.LyricsCache;
import net.sourceforge.atunes.misc.AbstractCache;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.model.LocalAudioObject;

public class ImageCache extends AbstractCache {

    private static final String COVERS = "covers";

    private Logger logger;

    public ImageCache() {
        super(LyricsCache.class.getResource("/settings/ehcache-covers.xml"));
    }

    /**
     * Clears the cache.
     * 
     * @return If an Exception occurred during clearing
     */
    public synchronized boolean clearCache() {
        try {
            getCache().removeAll();
            getCache().flush();
        } catch (IllegalStateException e) {
            getLogger().info(LogCategories.FILE_DELETE, "Could not delete all files from cover cache");
            return true;
        } catch (CacheException e) {
            getLogger().info(LogCategories.FILE_DELETE, "Could not delete all files from cover cache");
            return true;
        }
        return false;
    }

    public synchronized boolean clear(LocalAudioObject audioFile) {
        try {
            for (ImageSize imageSize : ImageSize.values()) {
                getCache().remove(id(audioFile, imageSize));
            }
            getCache().flush();
        } catch (IllegalStateException e) {
            getLogger().info(LogCategories.FILE_DELETE, "Could not delete all files from cover cache");
            return true;
        } catch (CacheException e) {
            getLogger().info(LogCategories.FILE_DELETE, "Could not delete all files from cover cache");
            return true;
        }
        return false;
    }

    public synchronized ImageIcon retrieveImage(AudioFile audioFile, ImageSize imageSize) {
        Element element = getCache().get(id(audioFile, imageSize));
        if (element != null) {
            return (ImageIcon) element.getValue();
        } else {
            return null;
        }
    }

    private static int id(LocalAudioObject audioFile, ImageSize imageSize) {
        return (audioFile.getUrl() + imageSize.toString()).hashCode();
    }

    public synchronized void storeImage(AudioFile audioFile, ImageSize imageSize, ImageIcon cover) {
        if (audioFile == null || imageSize == null || cover == null) {
            return;
        }
        Element element = new Element(id(audioFile, imageSize), cover);
        getCache().put(element);
        getLogger().debug(LogCategories.CACHE, "Stored image for ", audioFile);
    }

    private Cache getCache() {
        return getCache(COVERS);
    }

    public void shutdown() {
        getCache().dispose();
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
