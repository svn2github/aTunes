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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.List;

import net.sourceforge.atunes.gui.views.panels.PlayListSelectorPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListSelectorPanel;

final class PlayListTabController extends AbstractSimpleController<PlayListSelectorPanel> {

	private IPlayListHandler playListHandler;
	
	private IPlayListSelectorPanel playListSelectorPanel;
	
	/**
	 * @param playListSelectorPanel
	 */
	public void setPlayListSelectorPanel(
			IPlayListSelectorPanel playListSelectorPanel) {
		this.playListSelectorPanel = playListSelectorPanel;
	}
	
	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
	
    /**
     * Initializes controller
     */
    public void initialize() {
    	setComponentControlled((PlayListSelectorPanel)playListSelectorPanel);
        addBindings();
        addStateBindings();
    }

    @Override
	public void addBindings() {
    	PlayListTabListener l = new PlayListTabListener(getComponentControlled(), playListHandler);
    	getComponentControlled().getOptions().addActionListener(l);
        getComponentControlled().getPlayListCombo().addItemListener(l);
    	getComponentControlled().getPlayListCombo().setModel(PlayListComboModel.getNewComboModel());
    }

    /**
     * Delete play list.
     * 
     * @param index
     *            the index
     */
    void deletePlayList(int index) {
    	int selectedPlaylist = getSelectedPlayListIndex();
   		((PlayListComboModel)getComponentControlled().getPlayListCombo().getModel()).removeItemAt(index);
   		if (index == selectedPlaylist) {
   			forceSwitchTo(0);
   		}
    }

    /**
     * Force switch to.
     * 
     * @param index
     *            the index
     */
    void forceSwitchTo(int index) {
        getComponentControlled().getPlayListCombo().setSelectedIndex(index);
    }

    /**
     * New play list.
     * 
     * @param name
     *            the name
     */
    void newPlayList(String name) {
    	((PlayListComboModel)getComponentControlled().getPlayListCombo().getModel()).addItem(name);
    	
    }

    /**
     * Rename play list.
     * 
     * @param index
     *            the index
     * @param newName
     *            the new name
     */
    void renamePlayList(int index, String newName) {
    	((PlayListComboModel)getComponentControlled().getPlayListCombo().getModel()).rename(index, newName);
    	// Forces update of combo box by selecting again current play list
    	getComponentControlled().getPlayListCombo().setSelectedIndex(index);
    }

    /**
     * Return names of play lists.
     * 
     * @return the names of play lists
     */
    List<String> getNamesOfPlayLists() {
    	return ((PlayListComboModel)getComponentControlled().getPlayListCombo().getModel()).getItems();
    }

    /**
     * Returns selected play list index
     * 
     * @return
     */
    int getSelectedPlayListIndex() {
        return getComponentControlled().getPlayListCombo().getSelectedIndex();
    }

    /**
     * Returns name of play list at given position
     * 
     * @param index
     * @return
     */
    String getPlayListName(int index) {
        return ((PlayListComboModel)getComponentControlled().getPlayListCombo().getModel()).getElementAt(index);
    }
}
