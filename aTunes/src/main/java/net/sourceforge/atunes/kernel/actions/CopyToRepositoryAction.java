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

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.device.TransferToRepositoryProcess;
import net.sourceforge.atunes.kernel.modules.process.ProcessListener;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class CopyToRepositoryAction extends ActionOverSelectedObjects<AudioFile> {

    private static final long serialVersionUID = 2416674807979541242L;

    CopyToRepositoryAction() {
        super(I18nUtils.getString("COPY_TO_REPOSITORY"), ImageLoader.getImage(ImageLoader.EXPORT), AudioFile.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("COPY_TO_REPOSITORY"));
    }

    @Override
    protected void performAction(List<AudioFile> objects) {
        final TransferToRepositoryProcess importer = new TransferToRepositoryProcess(objects);
        importer.addProcessListener(new ProcessListener() {
            @Override
            public void processCanceled() { /* Nothing to do */
            }

            @Override
            public void processFinished(final boolean ok) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!ok) {
                            VisualHandler.getInstance().showErrorDialog(I18nUtils.getString("ERRORS_IN_COPYING_PROCESS"));
                        }
                        // Force a refresh of repository to add new songs
                        RepositoryHandler.getInstance().refreshRepository();
                    }
                });
            }
        });
        importer.execute();
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return !selection.isEmpty();
    }
}
