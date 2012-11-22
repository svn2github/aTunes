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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.gui.AbstractCommonColumnModel;
import net.sourceforge.atunes.model.IColumnSelectorDialog;
import net.sourceforge.atunes.model.IDialogFactory;

final class SelectColumnsActionListener implements ActionListener {
    private final AbstractCommonColumnModel model;

    private final IDialogFactory dialogFactory;
    
    SelectColumnsActionListener(AbstractCommonColumnModel model, final IDialogFactory dialogFactory) {
        this.model = model;
        this.dialogFactory = dialogFactory;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Show column selector
    	IColumnSelectorDialog selector = dialogFactory.newDialog(IColumnSelectorDialog.class);
        selector.setColumnSetToSelect(model.getColumnSet());
        selector.showDialog();

        // Apply changes
        model.arrangeColumns(true);
    }
}