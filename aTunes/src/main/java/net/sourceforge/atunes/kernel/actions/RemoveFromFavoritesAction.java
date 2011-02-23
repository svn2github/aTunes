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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.kernel.modules.navigator.FavoritesNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class RemoveFromFavoritesAction extends AbstractAction {

    private static final long serialVersionUID = -4288879781314486222L;

    public RemoveFromFavoritesAction() {
        super(I18nUtils.getString("REMOVE_FROM_FAVORITES"), Images.getImage(Images.DELETE_TAG));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("REMOVE_FROM_FAVORITES"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (NavigationHandler.getInstance().getPopupMenuCaller() == NavigationHandler.getInstance().getView(FavoritesNavigationView.class).getTree()) {
            TreePath[] paths = NavigationHandler.getInstance().getView(FavoritesNavigationView.class).getTree().getSelectionPaths();
            if (paths != null) {
                List<TreeObject> objects = new ArrayList<TreeObject>();
                for (TreePath element : paths) {
                    objects.add((TreeObject) ((DefaultMutableTreeNode) element.getLastPathComponent()).getUserObject());
                }
                FavoritesHandler.getInstance().removeFromFavorites(objects);
            }
        } else {
            int[] rows = NavigationHandler.getInstance().getNavigationTable().getSelectedRows();
            if (rows.length > 0) {
                List<AudioObject> audioObjects = ((NavigationTableModel) NavigationHandler.getInstance().getNavigationTable()
                        .getModel()).getAudioObjectsAt(rows);
                FavoritesHandler.getInstance().removeSongsFromFavorites(audioObjects);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        for (DefaultMutableTreeNode node : selection) {
            // Only allow to remove album if does not belong to a favorite artist
            if (node.getUserObject() instanceof Album) {
                Object[] objs = node.getUserObjectPath();
                for (Object element : objs) {
                    if (element instanceof Artist) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        // Enabled if all selected items are favorite songs (not belong to favorite artist nor album)
        return FavoritesHandler.getInstance().getFavoriteSongsInfo().values().containsAll(AudioFile.getAudioFiles(selection));
    }
}
