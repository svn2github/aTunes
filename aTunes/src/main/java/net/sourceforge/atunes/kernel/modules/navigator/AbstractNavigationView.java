/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellDecorator;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellRendererCode;
import net.sourceforge.atunes.gui.model.AudioObjectsSource;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.filter.FilterHandler;
import net.sourceforge.atunes.kernel.modules.repository.AudioObjectComparator;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;

public abstract class AbstractNavigationView implements AudioObjectsSource {

    private final class ArtistNamesComparator implements Comparator<String> {
		private final Pattern PATTERN = Pattern.compile("(.*)\\s+(.*?)");

		@Override
		public int compare(String s1, String s2) {
		    String[] ss1 = s1.split("[,\\&]");
		    String[] ss2 = s2.split("[,\\&]");
		    String d1 = getStringForSorting(s1, ss1);
		    String d2 = getStringForSorting(s2, ss2);
		    return getCollator().compare(d1.toLowerCase(), d2.toLowerCase());
		}

		private String getStringForSorting(String s, String[] ss) {
		    StringBuilder sb = new StringBuilder();
		    for (String k : ss) {
		        Matcher matcher = PATTERN.matcher(k.trim());
		        String m = s;
		        String n = "";
		        if (matcher.matches()) {
		            m = matcher.group(2);
		            n = matcher.group(1);
		        }
		        sb.append(m);
		        sb.append(n);
		    }
		    return sb.toString();
		}
	}

	/**
     * Common logger for all navigation views
     */
    private Logger logger;

    /**
     * Scroll pane used for tree
     */
    private JScrollPane scrollPane;

    /**
     * @return the title of this view
     */
    public abstract String getTitle();

    /**
     * @return the icon of this view
     */
    public abstract ImageIcon getIcon();

    /**
     * @return the tooltip of this view in tabbed pane
     */
    public abstract String getTooltip();

    /**
     * @return the JTree that contains this view
     */
    public abstract JTree getTree();

    /**
     * Return decorators to be used in view
     * 
     * @return
     */
    protected abstract List<AbstractTreeCellDecorator> getTreeCellDecorators();

    /**
     * 
     * @return the tree popup menu of this view
     */
    public abstract JPopupMenu getTreePopupMenu();

    /**
     * 
     * @return the table popup menu of this view
     */
    public JPopupMenu getTablePopupMenu() {
        // By default table popup is the same of tree
        return getTreePopupMenu();
    }

    /**
     * Returns scroll pane of tree
     * 
     * @return
     */
    public final JScrollPane getTreeScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getTree());
        }
        return scrollPane;
    }

    /**
     * Returns the data to be shown in the view. It depends on the view mode
     * 
     * @param viewMode
     * @return
     */
    protected abstract Map<String, ?> getViewData(ViewMode viewMode);

    /**
     * Refreshes view
     * 
     * @param viewMode
     * @param treeFilter
     */
    public void refreshView(ViewMode viewMode, String treeFilter) {
        // Get selected rows before refresh
        List<AudioObject> selectedObjects = ((NavigationTableModel) getNavigationTable().getModel()).getAudioObjectsAt(getNavigationTable().getSelectedRows());

        // Call to refresh tree
        refreshTree(viewMode, treeFilter);

        // Set the same selected audio objects as before refreshing
        for (AudioObject audioObject : selectedObjects) {
            int indexOfAudioObject = ((NavigationTableModel) getNavigationTable().getModel()).getAudioObjects().indexOf(audioObject);
            if (indexOfAudioObject != -1) {
                getNavigationTable().addRowSelectionInterval(indexOfAudioObject, indexOfAudioObject);
            }
        }
    }

    /**
     * Refresh tree view
     * 
     * @param viewMode
     * @param treeFilter
     */
    protected abstract void refreshTree(ViewMode viewMode, String treeFilter);

    /**
     * Returns a list of audio object associated to a tree node
     * 
     * @param node
     * @param viewMode
     * @param treeFilter
     * @return
     */
    public abstract List<AudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter);

    /**
     * Returns <code>true</code> if the view supports organize information in
     * different view modes
     * 
     * @return
     */
    public abstract boolean isViewModeSupported();

    /**
     * Returns <code>true</code> if the view uses default navigator columns or
     * <code>false</code> if defines its own column set
     * 
     * @return
     */
    public abstract boolean isUseDefaultNavigatorColumnSet();

    /**
     * If <code>isUseDefaultNavigatorColumns</code> returns <code>false</code>
     * then this method must return a column set with columns
     * 
     * @return
     */
    public abstract AbstractColumnSet getCustomColumnSet();

    /**
     * Enables or disables tree popup menu items of this view
     * 
     * @param e
     */
    public final void updateTreePopupMenuWithTreeSelection(MouseEvent e) {
        List<DefaultMutableTreeNode> nodesSelected = new ArrayList<DefaultMutableTreeNode>();
        TreePath[] paths = getTree().getSelectionPaths();
        if (paths != null && paths.length > 0) {
            for (TreePath path : paths) {
                nodesSelected.add((DefaultMutableTreeNode) path.getLastPathComponent());
            }
        }
        updateTreePopupMenuItems(getTreePopupMenu(), getTree().isRowSelected(0), nodesSelected);
    }

    /**
     * Updates all actions of tree popup
     * 
     * @param menu
     * @param rootSelected
     * @param selection
     */
    private void updateTreePopupMenuItems(JPopupMenu menu, boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        for (Component c : menu.getComponents()) {
            Action action = null;
            if (c instanceof JMenuItem) {
                action = ((JMenuItem) c).getAction();
            }

            if (c instanceof JMenu) {
                updateTreeMenuItems((JMenu) c, rootSelected, selection);
            }

            if (action instanceof net.sourceforge.atunes.kernel.actions.AbstractAction) {
                boolean enabled = ((net.sourceforge.atunes.kernel.actions.AbstractAction) action).isEnabledForNavigationTreeSelection(rootSelected, selection);
                action.setEnabled(enabled);
            }
        }
    }

    /**
     * Updates all actions of tree menu
     * 
     * @param menu
     * @param rootSelected
     * @param selection
     */
    private void updateTreeMenuItems(JMenu menu, boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            // For some reason getItem can return null
            if (menuItem != null) {
                Action action = menuItem.getAction();

                if (menuItem instanceof JMenu) {
                    updateTreeMenuItems((JMenu) menuItem, rootSelected, selection);
                }

                if (action instanceof net.sourceforge.atunes.kernel.actions.AbstractAction) {
                    boolean enabled = ((net.sourceforge.atunes.kernel.actions.AbstractAction) action).isEnabledForNavigationTreeSelection(rootSelected, selection);
                    action.setEnabled(enabled);
                }
            }
        }
    }

    /**
     * Enables or disables table popup menu items of this view
     * 
     * @param table
     * @param e
     */
    public final void updateTablePopupMenuWithTableSelection(JTable table, MouseEvent e) {
        updateTablePopupMenuItems(getTablePopupMenu(), ((NavigationTableModel) getNavigationTable().getModel()).getAudioObjectsAt(table.getSelectedRows()));
    }

    /**
     * Updates all actions of table popup
     * 
     * @param menu
     * @param selection
     */
    private void updateTablePopupMenuItems(JPopupMenu menu, List<AudioObject> selection) {
        for (Component c : menu.getComponents()) {
            Action action = null;
            if (c instanceof JMenuItem) {
                action = ((JMenuItem) c).getAction();
            }

            if (c instanceof JMenu) {
                updateTableMenuItems((JMenu) c, selection);
            }

            if (action instanceof net.sourceforge.atunes.kernel.actions.AbstractAction) {
                boolean enabled = ((net.sourceforge.atunes.kernel.actions.AbstractAction) action).isEnabledForNavigationTableSelection(selection);
                action.setEnabled(enabled);
            }
        }
    }

    /**
     * Updates all actions of table menu
     * 
     * @param menu
     * @param selection
     */
    private void updateTableMenuItems(JMenu menu, List<AudioObject> selection) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            // For some reason getItem can return null
            if (menuItem != null) {
                Action action = menuItem.getAction();

                if (menuItem instanceof JMenu) {
                    updateTableMenuItems((JMenu) menuItem, selection);
                }

                if (action instanceof net.sourceforge.atunes.kernel.actions.AbstractAction) {
                    boolean enabled = ((net.sourceforge.atunes.kernel.actions.AbstractAction) action).isEnabledForNavigationTableSelection(selection);
                    action.setEnabled(enabled);
                }
            }
        }
    }

    /**
     * Method to log debug messages
     * 
     * @param objects
     */
    protected final void debug(Object... objects) {
        getLogger().debug(LogCategories.VIEW, objects);
    }

    /**
     * Give access to navigation table
     * 
     * @return
     */
    protected JTable getNavigationTable() {
        return ControllerProxy.getInstance().getNavigationController().getNavigationTablePanel().getNavigationTable();
    }

    /**
     * Returns the default comparator
     * 
     * @return the default comparator
     */
    public Comparator<String> getDefaultComparator() {
        return new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return getCollator().compare(s1.toLowerCase(), s2.toLowerCase());
            }
        };
    }

    /**
     * Return current view mode
     * 
     * @return
     */
    public ViewMode getCurrentViewMode() {
        return ApplicationState.getInstance().getViewMode();
    }

    /**
     * Return selected objects in this navigation view
     * 
     * @return
     */
    public List<AudioObject> getSelectedAudioObjects() {
        List<AudioObject> selectedInTable = ((NavigationTableModel) getNavigationTable().getModel()).getAudioObjectsAt(getNavigationTable().getSelectedRows());
        if (selectedInTable.isEmpty()) {
            TreePath[] paths = getTree().getSelectionPaths();
            List<AudioObject> audioObjectsSelected = new ArrayList<AudioObject>();
            if (paths != null) {
                for (TreePath path : paths) {
                    audioObjectsSelected.addAll(getAudioObjectForTreeNode((DefaultMutableTreeNode) path.getLastPathComponent(), getCurrentViewMode(), FilterHandler.getInstance()
                            .isFilterSelected(NavigationHandler.getInstance().getTreeFilter()) ? FilterHandler.getInstance().getFilter() : null));
                    AudioObjectComparator.sort(audioObjectsSelected);
                }
            }
            return audioObjectsSelected;
        }
        return selectedInTable;
    }

    /**
     * Returns a menu item with an action bound to view
     * 
     * @param clazz
     * @return
     */
    protected JMenuItem getMenuItemForAction(Class<? extends AbstractActionOverSelectedObjects<? extends AudioObject>> clazz) {
        return Actions.getMenuItemForAction(clazz, this);
    }

    /**
     * Returns a menu item with an action bound to view
     * 
     * @param clazz
     * @return
     */
    protected JMenuItem getMenuItemForAction(Class<? extends AbstractActionOverSelectedObjects<? extends AudioObject>> clazz, String actionId) {
        return Actions.getMenuItemForAction(clazz, actionId, this);
    }

    /**
     * Returns all TreeObject instances selected in a tree
     * 
     * @param tree
     * @return
     */
    protected static final List<TreeObject> getTreeObjectsSelected(JTree tree) {
        // Get objects selected before refreshing tree
        List<TreeObject> objectsSelected = new ArrayList<TreeObject>();
        TreePath[] pathsSelected = tree.getSelectionPaths();

        // If any node was selected
        if (pathsSelected != null) {
            for (TreePath pathSelected : pathsSelected) {
                Object obj = ((DefaultMutableTreeNode) pathSelected.getLastPathComponent()).getUserObject();
                if (obj instanceof TreeObject) {
                    objectsSelected.add((TreeObject) obj);
                }
            }
        }

        return objectsSelected;
    }

    /**
     * Returns all TreeObject instances expanded in a tree
     * 
     * @param tree
     * @param root
     * @return
     */
    protected static final List<TreeObject> getTreeObjectsExpanded(JTree tree, DefaultMutableTreeNode root) {
        // Get objects expanded before refreshing tree
        List<TreeObject> objectsExpanded = new ArrayList<TreeObject>();
        Enumeration<TreePath> enume = tree.getExpandedDescendants(new TreePath(root.getPath()));

        // If any node was expanded
        if (enume != null) {
            while (enume.hasMoreElements()) {
                TreePath p = enume.nextElement();
                Object obj = ((DefaultMutableTreeNode) p.getLastPathComponent()).getUserObject();
                if (obj instanceof TreeObject) {
                    objectsExpanded.add((TreeObject) obj);
                }
            }
        }

        return objectsExpanded;
    }

    /**
     * Expands a list of nodes of a tree
     * 
     * @param tree
     * @param nodesToExpand
     */
    protected static final void expandNodes(JTree tree, List<DefaultMutableTreeNode> nodesToExpand) {
        for (DefaultMutableTreeNode node : nodesToExpand) {
            tree.expandPath(new TreePath(node.getPath()));
        }
    }

    /**
     * Selects a list of nodes of a tree
     * 
     * @param tree
     * @param nodesToSelect
     */
    protected static final void selectNodes(JTree tree, List<DefaultMutableTreeNode> nodesToSelect) {
        if (nodesToSelect.isEmpty()) {
            tree.setSelectionRow(0);
        } else {
            TreePath[] pathsToSelect = new TreePath[nodesToSelect.size()];
            int i = 0;
            for (DefaultMutableTreeNode node : nodesToSelect) {
                pathsToSelect[i++] = new TreePath(node.getPath());
            }
            tree.setSelectionPaths(pathsToSelect);
        }
    }

    /**
     * Returns an action to show this view
     * 
     * @param index
     *            (index of this view in view's list, valid values are 1 to 9,
     *            others will not be added an accelerator)
     * @return
     */
    public final net.sourceforge.atunes.kernel.actions.AbstractAction getActionToShowView(int index) {
        net.sourceforge.atunes.kernel.actions.AbstractAction viewAction = new net.sourceforge.atunes.kernel.actions.AbstractAction(getTitle(), getIcon()) {

            private static final long serialVersionUID = 2895222205333520899L;

            @Override
            public void actionPerformed(ActionEvent e) {
                ControllerProxy.getInstance().getNavigationController().setNavigationView(AbstractNavigationView.this.getClass().getName());
            }
        };

        // The first 9 views will have an accelerator key ALT + index
        if (index > 0 && index < 10) {
            viewAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_0 + index, ActionEvent.ALT_MASK));
        }

        return viewAction;
    }

    /**
     * Returns tree renderer used
     * 
     * @return
     */
    protected final TreeCellRenderer getTreeRenderer() {
        return LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTreeCellRenderer(new AbstractTreeCellRendererCode() {

            @Override
            public Component getComponent(Component superComponent, JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean isHasFocus) {
                final Object content = ((DefaultMutableTreeNode) value).getUserObject();
                for (AbstractTreeCellDecorator decorator : getTreeCellDecorators()) {
                    decorator.decorateTreeCellComponent(superComponent, content);
                }
                return superComponent;
            }
        });
    }

    /**
     * @return New collator
     */
    protected Collator getCollator() {
        return Collator.getInstance(ApplicationState.getInstance().getLocale().getLocale());
    }

    /**
     * The smart comparator ignores a leading "The"
     * 
     * @return the smartComparator
     */
    protected Comparator<String> getSmartComparator() {
        return new Comparator<String>() {
            private String removeThe(String str) {
                if (str.toLowerCase().startsWith("the ") && str.length() > 4) {
                    return str.substring(4);
                }
                return str;
            }

            @Override
            public int compare(String s1, String s2) {
                return getCollator().compare(removeThe(s1).toLowerCase(), removeThe(s2).toLowerCase());
            }
        };
    }

    /**
     * @return the artistNamesComparator
     */
    protected Comparator<String> getArtistNamesComparator() {
        return new ArtistNamesComparator();
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}