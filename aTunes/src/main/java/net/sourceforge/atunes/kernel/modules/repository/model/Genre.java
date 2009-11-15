/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.repository.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.modules.repository.Repository;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a genre, with a name, and a set of artist of this
 * genre.
 */
public class Genre implements Serializable, TreeObject {

    private static final long serialVersionUID = -6552057266561177152L;

    /** Name of the genre. */
    private String name;

    /** Artists of this genre */
    private List<String> artists;

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     */
    public Genre(String name) {
        this.name = name;
        artists = new ArrayList<String>();
    }

    /**
     * Adds an artist to this genre.
     * 
     * @param a
     *            the a
     */
    public void addArtist(String a) {
        artists.add(a);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Genre)) {
            return false;
        }
        return ((Genre) o).name.equals(name);
    }

    /**
     * Returns all songs of this genre (all songs of all artists) from the given repository
     * 
     * @return the audio objects
     */
    @Override
    public List<AudioObject> getAudioObjects(Repository repository) {
        List<AudioObject> songs = new ArrayList<AudioObject>();
        for (String artist : artists) {
            songs.addAll(repository.getStructure().getArtistStructure().get(artist).getAudioObjects(repository));
        }
        return songs;
    }

    /**
     * Returns the name of this genre.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Removes an artist from this genre.
     * 
     * @param a
     *            the a
     */
    public void removeArtist(Artist a) {
        artists.remove(a.getName());
    }

    /**
     * String representation.
     * 
     * @return the string
     */
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getToolTip(Repository repository) {
        int artistNumber = artists.size();
        int songs = getAudioObjects(repository).size();
        return StringUtils.getString(getName(), " (", I18nUtils.getString("ARTISTS"), ": ", artistNumber, ", ", I18nUtils.getString("SONGS"), ": ", songs, ")");
    }

    @Override
    public boolean isExtendedToolTipSupported() {
        return true;
    }

    @Override
    public void setExtendedToolTip(ExtendedToolTip toolTip, Repository repository) {
        toolTip.setLine1(name);
        int artistNumber = artists.size();
        toolTip.setLine2(StringUtils.getString(artistNumber, " ", (artistNumber > 1 ? I18nUtils.getString("ARTISTS") : I18nUtils.getString("ARTIST"))));
        int songs = getAudioObjects(repository).size();
        toolTip.setLine3(StringUtils.getString(songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG"))));
    }

    @Override
    public ImageIcon getExtendedToolTipImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Return unknown genre text
     * 
     * @return
     */
    public static String getUnknownGenre() {
        return I18nUtils.getString("UNKNOWN_GENRE");
    }

    /**
     * Return <code>true</code> if this genre is unknown
     * 
     * @return
     */
    public boolean isUnknownGenre() {
        return getName().equalsIgnoreCase(getUnknownGenre());
    }

    /**
     * Return <code>true</code> if this genre is unknown
     * 
     * @return
     */
    public static boolean isUnknownGenre(String genre) {
        return getUnknownGenre().equalsIgnoreCase(genre);
    }

	/**
	 * @return the artists
	 */
	public List<String> getArtists() {
		return artists;
	}

}
