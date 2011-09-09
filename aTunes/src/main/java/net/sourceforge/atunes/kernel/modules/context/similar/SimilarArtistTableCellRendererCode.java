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

package net.sourceforge.atunes.kernel.modules.context.similar;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

class SimilarArtistTableCellRendererCode extends ContextTableRowPanel<ArtistInfo> {

	@Override
    public JComponent getComponent(JComponent superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return getPanelForTableRenderer(((ArtistInfo) value).getImage(), 
        							    StringUtils.getString("<html><br>", ((ArtistInfo) value).getName(), "<br>", ((ArtistInfo) value).getMatch(), "%<br>", ((ArtistInfo) value).isAvailable() ? I18nUtils.getString("AVAILABLE_IN_REPOSITORY") : "", "</html>"), 
        							    superComponent.getBackground(),
        							    superComponent.getForeground(),
        							    Constants.CONTEXT_IMAGE_WIDTH, 
        							    Constants.CONTEXT_IMAGE_HEIGHT,
        							    hasFocus);
    }

	@Override
	public List<ContextTableAction<ArtistInfo>> getActions() {
		List<ContextTableAction<ArtistInfo>> actions = new ArrayList<ContextTableAction<ArtistInfo>>();
		actions.add(new ContextTableAction<ArtistInfo>(I18nUtils.getString("READ_MORE"), (ContextTable) table) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -4964635263019533125L;

			@Override
			protected void execute(ArtistInfo object) {
                DesktopUtils.openURL(object.getUrl());
			}

			@Override
			protected ArtistInfo getSelectedObject(int row) {
				return ((SimilarArtistsTableModel) table.getModel()).getArtist(row);
			}
			
			@Override
			protected boolean isEnabledForObject(ArtistInfo object) {
				return true;
			}
			
		});
		actions.add(new ContextTableAction<ArtistInfo>(I18nUtils.getString("ADD_ALBUM_ARTIST_TO_PLAYLIST"), (ContextTable) table) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -3920095074089169426L;

			@Override
			protected void execute(ArtistInfo object) {
				GuiHandler.getInstance().showAddArtistDragDialog(RepositoryHandler.getInstance().getArtist(object.getName()));
			}
			
			@Override
			protected ArtistInfo getSelectedObject(int row) {
				return ((SimilarArtistsTableModel) table.getModel()).getArtist(row);
			}
			
			@Override
			protected boolean isEnabledForObject(ArtistInfo object) {
				return RepositoryHandler.getInstance().getArtist(object.getName()) != null;
			}				
		});
		
		return actions;
	}
}    