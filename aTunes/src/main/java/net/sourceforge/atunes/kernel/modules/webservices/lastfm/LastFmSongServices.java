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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.StringUtils;

public class LastFmSongServices {
	
	private LastFmAlbumServices lastFmAlbumServices;
	
	private IUnknownObjectChecker unknownObjectChecker;
	
	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}
	
	/**
	 * @param lastFmAlbumServices
	 */
	public void setLastFmAlbumServices(LastFmAlbumServices lastFmAlbumServices) {
		this.lastFmAlbumServices = lastFmAlbumServices;
	}

    /**
     * Return title of an LocalAudioObject known its artist and album
     * 
     * @param f
     * @return
     */
    String getTitleForFile(ILocalAudioObject f) {
        // If has valid artist name, album name, and track number...
        if (!unknownObjectChecker.isUnknownArtist(f.getArtist())
                && !unknownObjectChecker.isUnknownAlbum(f.getAlbum()) && f.getTrackNumber() > 0) {
            // Find album
            IAlbumInfo albumRetrieved = lastFmAlbumServices.getAlbum(f.getArtist(), f.getAlbum());
            if (albumRetrieved != null && albumRetrieved.getTracks().size() >= f.getTrackNumber()) {
            	// Get track
            	return albumRetrieved.getTracks().get(f.getTrackNumber() - 1).getTitle();
            }
        }
        return null;
    }

    /**
     * Return track number of an LocalAudioObject known its artist and album
     * 
     * @param f
     * @return
     */
    int getTrackNumberForFile(ILocalAudioObject f) {
        // If has valid artist name, album name and title
        if (!unknownObjectChecker.isUnknownArtist(f.getArtist())
                && !unknownObjectChecker.isUnknownAlbum(f.getAlbum()) && !StringUtils.isEmpty(f.getTitle())) {
            // Find album
            IAlbumInfo albumRetrieved = lastFmAlbumServices.getAlbum(f.getArtist(), f.getAlbum());
            if (albumRetrieved != null) {
                // Try to match titles to get track
                int trackIndex = 1;
                for (ITrackInfo track : albumRetrieved.getTracks()) {
                    if (track.getTitle().equalsIgnoreCase(f.getTitle())) {
                        return trackIndex;
                    }
                    trackIndex++;
                }
            }
        }
        return 0;
    }

}
