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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action creates a new play list with all songs of the artist that is
 * currently selected in play list
 * 
 * If more than one artist is selected, creates one play list for each one
 * 
 * @author fleax
 * 
 */
public class CreatePlayListWithSelectedArtistsAction extends AbstractActionOverSelectedObjects<IAudioObject> {

	private static final long serialVersionUID = 242525309967706255L;

	private IRepositoryHandler repositoryHandler;

	private IPlayListHandler playListHandler;

	private IAudioObjectComparator audioObjectComparator;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param audioObjectComparator
	 */
	public void setAudioObjectComparator(final IAudioObjectComparator audioObjectComparator) {
		this.audioObjectComparator = audioObjectComparator;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Default constructor
	 */
	public CreatePlayListWithSelectedArtistsAction() {
		super(I18nUtils.getString("SET_ARTIST_AS_PLAYLIST"));
		putValue(SHORT_DESCRIPTION, I18nUtils.getString("ARTIST_BUTTON_TOOLTIP"));
		setEnabled(false);
	}

	@Override
	protected void executeAction(final List<IAudioObject> objects) {
		// Get selected artists from play list
		List<IArtist> selectedArtists = new ArrayList<IArtist>();
		for (IAudioObject ao : objects) {
			String artist = ao.getArtist(unknownObjectChecker);
			IArtist a = repositoryHandler.getArtist(artist);
			if (a != null && !selectedArtists.contains(a)) {
				selectedArtists.add(a);
			}
		}

		// For every artist create a play list
		createPlayLists(selectedArtists);
	}

	/**
	 * @param selectedArtists
	 */
	private void createPlayLists(final List<IArtist> selectedArtists) {
		for (IArtist artist : selectedArtists) {
			List<ILocalAudioObject> audioObjects = repositoryHandler.getAudioFilesForArtists(Collections.singletonMap(artist.getName(), artist));
			audioObjectComparator.sort(audioObjects);

			// Create a new play list with artist as name and audio objects selected
			playListHandler.newPlayList(artist.getName(), audioObjects);
		}
	}
}
