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

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.LocalAudioObjectFilter;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Calls export process
 * 
 * @author fleax
 * 
 */
public class ExportPlayListSelectionAction extends AbstractExportAction {

	private static final long serialVersionUID = -6661702915765846089L;

	private IPlayListHandler playListHandler;

	/**
	 * Constructor
	 */
	public ExportPlayListSelectionAction() {
		super(StringUtils.getString(I18nUtils.getString("EXPORT_PLAYLIST_SELECTION"), "..."));
		putValue(SHORT_DESCRIPTION, StringUtils.getString(I18nUtils.getString("EXPORT_PLAYLIST_SELECTION"), "..."));
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	@Override
	public List<ILocalAudioObject> getAudioObjectsToExport() {
		LocalAudioObjectFilter filter = new LocalAudioObjectFilter();
		// Get only LocalAudioObject objects of current play list
		return filter.getLocalAudioObjects(playListHandler.getSelectedAudioObjects());
	}

	@Override
	public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
		return !playListHandler.getCurrentPlayList(true).isEmpty();
	}
}