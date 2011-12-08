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

import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.IInputDialogFactory;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class RenameRadioLabelAction extends CustomAbstractAction {

    private static final long serialVersionUID = -606790181321223318L;

    private INavigationHandler navigationHandler;
    
    private IRadioHandler radioHandler;
    
    private IInputDialogFactory inputDialogFactory;
    
    private INavigationView radioNavigationView;
    
    /**
     * @param radioNavigationView
     */
    public void setRadioNavigationView(INavigationView radioNavigationView) {
		this.radioNavigationView = radioNavigationView;
	}
    
    /**
     * @param inputDialogFactory
     */
    public void setInputDialogFactory(IInputDialogFactory inputDialogFactory) {
		this.inputDialogFactory = inputDialogFactory;
	}
    
    /**
     * @param radioHandler
     */
    public void setRadioHandler(IRadioHandler radioHandler) {
		this.radioHandler = radioHandler;
	}
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    public RenameRadioLabelAction() {
        super(I18nUtils.getString("RENAME_LABEL"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RENAME_LABEL"));
    }

    @Override
    protected void executeAction() {
        TreePath path = radioNavigationView.getTree().getSelectionPath();
        Object o = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        
        IInputDialog dialog = inputDialogFactory.getDialog();
        dialog.setTitle(I18nUtils.getString("RENAME_LABEL"));

        if (o instanceof String) {
            String label = (String) o;
            dialog.showDialog(label);
            String result = dialog.getResult();
            if (result != null) {
                List<IRadio> radios = radioHandler.getRadios(label);
                radioHandler.setLabel(radios, result);
                navigationHandler.refreshView(radioNavigationView);
            }
        } else if (o instanceof IRadio) {
            IRadio radio = (IRadio) o;
            dialog.showDialog(radio.getLabel());
            String result = dialog.getResult();
            if (result != null) {
                radio.setLabel(result);
                navigationHandler.refreshView(radioNavigationView);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && selection.size() == 1;
    }

}
