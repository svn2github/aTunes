/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.views.controls.NavigationTable;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ArrangeNavigationTableColumns;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationView;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.GuiUtils;

public final class NavigationPanel extends JPanel {

    private static final long serialVersionUID = -2900418193013495812L;

    /** The navigation table. */
    private NavigationTable navigationTable;

    /** The navigation table button panel. */
    private JPanel navigationTableButtonPanel;

    /** The navigation table container. */
    private JPanel navigationTableContainer;

    /** The tabbed pane. */
    private JTabbedPane tabbedPane;

    /** The split pane. */
    private JSplitPane splitPane;

    /** Popup menu for header of navigation table */
    private JPopupMenu navigationTableHeaderMenu;

    /**
     * The tree filter
     */
    private NavigationFilterPanel treeFilterPanel;

    /**
     * The table filter
     */
    private NavigationFilterPanel tableFilterPanel;

    /**
     * Instantiates a new navigation panel.
     */
    public NavigationPanel() {
        super(new BorderLayout(), true);
        addContent();
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        JPanel treePanel = new JPanel(new BorderLayout());

        // Create tabbed pane. Set tab position from configuration
        tabbedPane = new JTabbedPane(ApplicationState.getInstance().isShowNavigatorTabsAtLeft() ? SwingConstants.LEFT : SwingConstants.TOP);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());

        // Add tabs
        addTabs();

        // Set navigator tabs text based on configuration 
        setNavigatorTabsText(ApplicationState.getInstance().isShowNavigatorTabsText());

        treeFilterPanel = new NavigationFilterPanel();

        treePanel.add(tabbedPane, BorderLayout.CENTER);
        treePanel.add(treeFilterPanel, BorderLayout.SOUTH);

        treeFilterPanel.setVisible(false);

        navigationTable = new NavigationTable();
        navigationTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Disable autoresize, as we will control it
        navigationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        navigationTableHeaderMenu = new JPopupMenu();
        navigationTableHeaderMenu.add(Actions.getAction(ArrangeNavigationTableColumns.class));

        JScrollPane scrollPane2 = new JScrollPane(navigationTable);
        //scrollPane2.setBorder(BorderFactory.createEmptyBorder());

        navigationTableContainer = new JPanel(new BorderLayout());
        //navigationTableContainer.setMinimumSize(d);
        navigationTableButtonPanel = new JPanel(new GridBagLayout());

        tableFilterPanel = new NavigationFilterPanel();
        tableFilterPanel.setVisible(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        navigationTableButtonPanel.add(tableFilterPanel, c);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        navigationTableContainer.add(scrollPane2, BorderLayout.CENTER);
        navigationTableContainer.add(navigationTableButtonPanel, BorderLayout.SOUTH);
        splitPane.add(treePanel);
        splitPane.add(navigationTableContainer);

        splitPane.setResizeWeight(0.8);

        add(splitPane);

        // Apply component orientation to all popup menus
        for (NavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            GuiUtils.applyComponentOrientation(view.getTreePopupMenu());
        }
    }

    /**
     * Gets the navigation table.
     * 
     * @return the navigation table
     */
    public JTable getNavigationTable() {
        return navigationTable;
    }

    /**
     * Gets the navigation table container.
     * 
     * @return the navigation table container
     */
    public JPanel getNavigationTableContainer() {
        return navigationTableContainer;
    }

    /**
     * Gets the split pane.
     * 
     * @return the split pane
     */
    public JSplitPane getSplitPane() {
        return splitPane;
    }

    /**
     * Gets the tabbed pane.
     * 
     * @return the tabbed pane
     */
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     * Updates tabbed pane to show all tabs
     */
    private void addTabs() {
        tabbedPane.removeAll();

        for (NavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            tabbedPane.addTab(view.getTitle(), view.getIcon(), view.getTreeScrollPane(), view.getTooltip());
        }
    }

    /**
     * Shows or hides tabbed pane text
     * 
     * @param set
     */
    public void setNavigatorTabsText(boolean set) {
        int i = 0;
        for (NavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            tabbedPane.setTitleAt(i++, set ? view.getTitle() : null);
        }
    }

    /**
     * Updates tabs keeping selected tab view
     */
    public void updateTabs() {
        int selectedTab = tabbedPane.getSelectedIndex();
        addTabs();
        setNavigatorTabsText(ApplicationState.getInstance().isShowNavigatorTabsText());
        if (tabbedPane.getTabCount() > selectedTab) {
            tabbedPane.setSelectedIndex(selectedTab);
        } else {
            tabbedPane.setSelectedIndex(0);
        }
    }

    /**
     * @return the treeFilterPanel
     */
    public NavigationFilterPanel getTreeFilterPanel() {
        return treeFilterPanel;
    }

    /**
     * @return the tableFilterPanel
     */
    public NavigationFilterPanel getTableFilterPanel() {
        return tableFilterPanel;
    }

    /**
     * @return the navigationTableHeaderMenu
     */
    public JPopupMenu getNavigationTableHeaderMenu() {
        return navigationTableHeaderMenu;
    }
}
