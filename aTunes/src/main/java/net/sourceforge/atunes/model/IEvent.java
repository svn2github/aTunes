/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.ImageIcon;

import org.joda.time.DateTime;

/**
 * Event of an artist
 * 
 * @author alex
 * 
 */
public interface IEvent {

	/**
	 * Title of event
	 * 
	 * @param title
	 */
	void setTitle(String title);

	/**
	 * @return title of event
	 */
	String getTitle();

	/**
	 * @param artist
	 */
	void setArtist(String artist);

	/**
	 * @return artist whose search returned this event
	 */
	String getArtist();

	/**
	 * @param startDate
	 */
	void setStartDate(DateTime startDate);

	/**
	 * @return start date of event
	 */
	DateTime getStartDate();

	/**
	 * @param url
	 *            of event
	 */
	void setUrl(String url);

	/**
	 * @return url of event
	 */
	String getUrl();

	/**
	 * @param city
	 *            of event
	 */
	void setCity(String city);

	/**
	 * @return city of event
	 */
	String getCity();

	/**
	 * @param country
	 *            of event
	 */
	void setCountry(String country);

	/**
	 * @return country of event
	 */
	String getCountry();

	/**
	 * @param imageUrl
	 */
	void setImageUrl(String imageUrl);

	/**
	 * @return
	 */
	String getImageUrl();

	/**
	 * @param image
	 */
	void setImage(ImageIcon image);

	/**
	 * @return image
	 */
	ImageIcon getImage();
}
