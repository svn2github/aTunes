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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;

import net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;

class ContextController extends AbstractSimpleController<ContextPanel> {

	ContextController(ContextPanel componentControlled) {
		super(componentControlled);
		addBindings();
	}

	@Override
	protected void addBindings() {
		getComponentControlled().getContextSelector().addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				getComponentControlled().showContextPanel((AbstractContextPanel)e.getItem());
                ContextHandler.getInstance().contextPanelChanged();
            }
        });
		
		getComponentControlled().getContextSelector().setRenderer(LookAndFeelSelector.getDefaultLookAndFeel().getListCellRenderer(new AbstractListCellRendererCode() {
			
			@Override
			public Component getComponent(Component superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				((JLabel)superComponent).setIcon(((AbstractContextPanel)value).getIcon());
				((JLabel)superComponent).setText(((AbstractContextPanel)value).getTitle());
				return superComponent;
			}
		}));
	}

	@Override
	protected void addStateBindings() {
	}

	@Override
	protected void notifyReload() {
	}

	/**
	 * Selects context tab
	 * @param selectedContextTab
	 */
	void setContextTab(int selectedContextTab) {
		getComponentControlled().setSelectedIndex(selectedContextTab);
	}
	
	/**
	 * Returns context tab
	 * @return
	 */
	int getContextTab() {
		return getComponentControlled().getSelectedIndex();
	}

	void updateContextTabs() {
		getComponentControlled().updateContextTabs();
	}

	void removeContextPanel(AbstractContextPanel instance) {
		getComponentControlled().removeContextPanel(instance);
	}

	/**
	 * Adds context panels
	 * @param contextPanels
	 */
	void addContextPanels(List<AbstractContextPanel> contextPanels) {
		for (AbstractContextPanel panel : contextPanels) {
			getComponentControlled().addContextPanel(panel);
		}		
	}

}
