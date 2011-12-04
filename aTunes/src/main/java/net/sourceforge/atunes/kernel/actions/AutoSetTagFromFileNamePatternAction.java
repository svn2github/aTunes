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

import net.sourceforge.atunes.gui.views.dialogs.PatternInputDialog;
import net.sourceforge.atunes.kernel.modules.pattern.AbstractPattern;
import net.sourceforge.atunes.kernel.modules.tags.EditTagFromFileNamePatternProcess;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Calls process to set tag from a pattern of file name entered by user
 * 
 * @author fleax
 * 
 */
public class AutoSetTagFromFileNamePatternAction extends AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = -8458591967408812850L;

    private IFrame frame;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private IPlayListHandler playListHandler;
    
    private IRepositoryHandler repositoryHandler;
    
    private IPlayerHandler playerHandler;
    
    private ILocalAudioObjectValidator localAudioObjectValidator;
    
    /**
     * @param localAudioObjectValidator
     */
    public void setLocalAudioObjectValidator(ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}
    
    /**
     * @param frame
     */
    public void setFrame(IFrame frame) {
		this.frame = frame;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
    
    /**
     * @param playerHandler
     */
    public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
    
    public AutoSetTagFromFileNamePatternAction() {
        super(StringUtils.getString(I18nUtils.getString("AUTO_SET_TAG_FROM_FILE_NAME_PATTERN"), "..."));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("AUTO_SET_TAG_FROM_FILE_NAME_PATTERN"));
    }

    @Override
    protected void executeAction(List<ILocalAudioObject> objects) {
        // Show pattern input dialog
        PatternInputDialog inputDialog = new PatternInputDialog(frame.getFrame(), false, getState(), lookAndFeelManager.getCurrentLookAndFeel());
        inputDialog.show(AbstractPattern.getRecognitionPatterns(), objects.get(0).getNameWithoutExtension());
        String pattern = inputDialog.getResult();

        // If user entered a pattern apply to files
        if (pattern != null) {
            new EditTagFromFileNamePatternProcess(objects, pattern, getState(), playListHandler, repositoryHandler, playerHandler, localAudioObjectValidator).execute();
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return !selection.isEmpty();
    }

}
