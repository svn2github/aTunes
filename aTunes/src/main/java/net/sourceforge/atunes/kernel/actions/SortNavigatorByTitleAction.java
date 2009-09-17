/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler.SortType;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class SortNavigatorByTitleAction extends Action {

    private static final long serialVersionUID = -839238449400105763L;

    SortNavigatorByTitleAction() {
        super(I18nUtils.getString("SORT_BY_TITLE"), ImageLoader.getImage(ImageLoader.TITLE));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SORT_BY_TITLE"));
        putValue(SELECTED_KEY, ApplicationState.getInstance().getSortType() == SortType.BY_TITLE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ApplicationState.getInstance().getSortType() != SortType.BY_TITLE) {
            ApplicationState.getInstance().setSortType(SortType.BY_TITLE);
            List<AudioObject> songs = ((NavigationTableModel) ControllerProxy.getInstance().getNavigationController().getNavigationPanel().getNavigationTable().getModel())
                    .getSongs();
            ((NavigationTableModel) ControllerProxy.getInstance().getNavigationController().getNavigationPanel().getNavigationTable().getModel()).setSongs(ControllerProxy
                    .getInstance().getNavigationController().sort(songs, SortType.BY_TITLE));
        }
    }

}
