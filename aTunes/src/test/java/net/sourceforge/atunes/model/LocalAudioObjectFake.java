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

package net.sourceforge.atunes.model;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.modules.tags.AbstractTag;

public class LocalAudioObjectFake implements LocalAudioObject {

	@Override
	public String getAlbum() {
		return null;
	}

	@Override
	public String getAlbumArtist() {
		return null;
	}

	@Override
	public String getAlbumArtistOrArtist() {
		return null;
	}

	@Override
	public String getArtist() {
		return null;
	}

	@Override
	public long getBitrate() {
		return 0;
	}

	@Override
	public String getComposer() {
		return null;
	}

	@Override
	public int getDuration() {
		return 0;
	}

	@Override
	public int getFrequency() {
		return 0;
	}

	@Override
	public String getGenre() {
		return null;
	}

	@Override
	public String getLyrics() {
		return null;
	}

	@Override
	public int getStars() {
		return 0;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getTitleOrFileName() {
		return null;
	}

	@Override
	public int getTrackNumber() {
		return 0;
	}

	@Override
	public String getUrl() {
		return null;
	}

	@Override
	public String getYear() {
		return null;
	}

	@Override
	public Date getDate() {
		return null;
	}

	@Override
	public String getComment() {
		return null;
	}

	@Override
	public void setStars(int stars) {
	}

	@Override
	public boolean isSeekable() {
		return false;
	}

	@Override
	public int getDiscNumber() {
		return 0;
	}

	@Override
	public ImageIcon getGenericImage(GenericImageSize imageSize) {
		return null;
	}

	@Override
	public ImageIcon getImage(ImageSize imageSize) {
		return null;
	}

	@Override
	public int compareTo(LocalAudioObject arg0) {
		return 0;
	}

	@Override
	public File getFile() {
		return null;
	}

	@Override
	public boolean isUpToDate() {
		return false;
	}

	@Override
	public void setExternalPictures(List<File> externalPictures) {
	}

	@Override
	public void addExternalPicture(File picture) {
		
	}

	@Override
	public AbstractTag getTag() {
		return null;
	}

	@Override
	public void setTag(AbstractTag tag) {
	}

	@Override
	public String getNameWithoutExtension() {
		return null;
	}

	@Override
	public void refreshTag() {
	}

	@Override
	public void setFile(File file) {
	}

	@Override
	public boolean supportsInternalPicture() {
		return false;
	}

	@Override
	public boolean hasInternalPicture() {
		return false;
	}
	
}