/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.model;

import java.io.Serializable;

/**
 * Represents information about an albums' track, retrieved from a web service
 * @author alex
 *
 */
public interface ITrackInfo extends Serializable {

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle();

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public String getUrl();

    /**
     * Sets the title.
     * 
     * @param title
     *            the title to set
     */
    public void setTitle(String title);

    /**
     * Sets the url.
     * 
     * @param url
     *            the url to set
     */
    public void setUrl(String url);    

}