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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.utils.I18nUtils;

public class MarkPodcastListenedAction extends CustomAbstractAction {

    private static final long serialVersionUID = 2594418895817769179L;

    private INavigationHandler navigationHandler;
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    public MarkPodcastListenedAction() {
        super(I18nUtils.getString("MARK_PODCAST_AS_LISTENED"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("MARK_PODCAST_AS_LISTENED"));
    }

    @Override
    protected void executeAction() {
        TreePath path = navigationHandler.getView(PodcastNavigationView.class).getTree().getSelectionPath();
        IPodcastFeed podcastFeedToMarkAsListened = (IPodcastFeed) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        podcastFeedToMarkAsListened.markEntriesAsListened();
        navigationHandler.refreshView(PodcastNavigationView.class);
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (rootSelected) {
            return false;
        }

        boolean nonListenedEntries = false;
        for (DefaultMutableTreeNode node : selection) {
            List<IPodcastFeedEntry> podcastFeedEntries = ((IPodcastFeed) node.getUserObject()).getPodcastFeedEntries();
            for (IPodcastFeedEntry entry : podcastFeedEntries) {
                if (!entry.isListened()) {
                    nonListenedEntries = true;
                }
            }
        }

        return nonListenedEntries;
    }
}
