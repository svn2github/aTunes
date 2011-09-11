/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class FileSelectionDialog.
 */
public final class FileSelectionDialog extends AbstractCustomDialog implements IFileSelectionDialog {

    private final class FileSystemListMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
		    File f = (File) fileSystemList.getSelectedValue();
		    setSelectionText(f);
		    if (e.getClickCount() == 2) {
		        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) fileSystemTree.getSelectionPath().getLastPathComponent();
		        TreePath path = new TreePath(parentNode.getPath());
		        fileSystemTree.expandPath(path);
		        int i = 0;
		        DefaultMutableTreeNode childToSelect = null;
		        while (i < parentNode.getChildCount() || childToSelect == null) {
		            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
		            if (((Directory) child.getUserObject()).file.equals(f)) {
		                childToSelect = child;
		            }
		            i++;
		        }
		        TreeNode[] newPath = new TreeNode[parentNode.getPath().length + 1];
		        for (int j = 0; j < parentNode.getPath().length; j++) {
		            newPath[j] = parentNode.getPath()[j];
		        }
		        newPath[parentNode.getPath().length] = childToSelect;

		        fileSystemTree.setSelectionPath(new TreePath(newPath));
		    }
		}
	}

	private final class FileSystemTreeTreeSelectionListener implements
			TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
		    DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		    Directory dir = (Directory) node.getUserObject();
		    setSelectionText(dir.file);
		    File[] files = getFiles(dir.file);
		    List<File> dirsList = new ArrayList<File>();
		    List<File> filesList = new ArrayList<File>();
		    for (File element : files) {
		        if (element.isDirectory()) {
		            dirsList.add(element);
		        } else {
		            filesList.add(element);
		        }
		    }
		    Collections.sort(dirsList);
		    Collections.sort(filesList);
		    dirsList.addAll(filesList);
		    fileSystemList.setListData(dirsList.toArray());
		}
	}

	private final class FileSystemTreeTreeWillExpandListener implements
			TreeWillExpandListener {
		@Override
		public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		    // Nothing to do
		}

		@Override
		public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		    DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) fileSystemTree.getSelectionPath().getLastPathComponent();
		    nodeSelected.removeAllChildren();
		    Directory dir = (Directory) nodeSelected.getUserObject();
		    File[] files = fsView.getFiles(dir.file, true);
		    Arrays.sort(files);
		    for (File f : files) {
		        if (fsView.isTraversable(f)) {
		            DefaultMutableTreeNode treeNode2 = new DefaultMutableTreeNode(new Directory(f));
		            nodeSelected.add(treeNode2);
		            treeNode2.add(new DefaultMutableTreeNode("Dummy node"));
		        }
		    }
		}
	}

	private final class OkButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
		    selectedDir = null;
		    selectedFiles = null;
		    if (directoryOnly) {
		        if (fileSystemList.getSelectedValue() != null) {
		            selectedDir = new File(((File) fileSystemList.getSelectedValue()).getAbsolutePath());
		        } else {
		            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileSystemTree.getSelectionPath().getLastPathComponent();
		            selectedDir = ((Directory) node.getUserObject()).file;
		        }
		    } else {
		        if (fileSystemList.getSelectedValues().length > 0) {
		            Object[] files = fileSystemList.getSelectedValues();
		            selectedFiles = new File[files.length];
		            System.arraycopy(files, 0, selectedFiles, 0, files.length);
		        } else {
		            selectedFiles = new File[1];
		            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileSystemTree.getSelectionPath().getLastPathComponent();
		            selectedFiles[0] = ((Directory) node.getUserObject()).file;
		        }
		    }
		    canceled = false;
		    setVisible(false);
		}
	}

	private static class FileSystemTreeCellRendererCode extends AbstractTreeCellRendererCode {
        @Override
        public JComponent getComponent(JComponent superComponent, JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean isHasFocus) {
            if (superComponent instanceof JLabel) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            	if (node.getUserObject() instanceof String) {
            		((JLabel) superComponent).setText(null);
            		return superComponent;
            	}

            	Directory content = (Directory) node.getUserObject();
            	JLabel icon = (JLabel) superComponent;
            	if (content != null) {
            		icon.setIcon(fsView.getSystemIcon(content.file));
            	}

            	return icon;
            }
            return superComponent;
        }
    }

    private static class FileSystemListCellRendererCode extends AbstractListCellRendererCode {
        @Override
        public JComponent getComponent(JComponent superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        	if (superComponent instanceof JLabel) {
        		JLabel icon = (JLabel) superComponent;
        		File f = (File) value;
        		icon.setText(fsView.getSystemDisplayName(f));
        		icon.setIcon(fsView.getSystemIcon(f));
        		icon.setHorizontalAlignment(GuiUtils.getComponentOrientationAsSwingConstant());
        		return icon;
        	}
        	return superComponent;
        }
    }

    /**
     * The Class Directory.
     */
    private static class Directory {

        /** The file. */
        public File file;

        /**
         * Instantiates a new directory.
         * 
         * @param file
         *            the file
         */
        Directory(File file) {
            this.file = file;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return fsView.getSystemDisplayName(file);
        }
    }

    private static final long serialVersionUID = -1612490779910952274L;

    /** The fs view. */
    static transient FileSystemView fsView = FileSystemView.getFileSystemView();

    /** The file system tree. */
    private JTree fileSystemTree;

    /** The file system list. */
    private JList fileSystemList;

    /** The selection. */
    private JLabel selection;

    /** The ok button. */
    private JButton okButton;

    /** The cancel button. */
    private JButton cancelButton;

    /** The dir only. */
    boolean directoryOnly;

    /** The canceled. */
    boolean canceled = true;

    /** The selected dir. */
    private File selectedDir;

    /** The selected files. */
    private File[] selectedFiles;

    /**
     * Instantiates a new file selection dialog.
     * @param frame
     */
    public FileSelectionDialog(IFrame frame) {
        super(frame, 660, 430, true, CloseAction.DISPOSE);
        add(getContent());
        setResizable(false);
    }
    
    @Override
    public void setDirectoryOnly(boolean dirOnly) {
		this.directoryOnly = dirOnly;
	}

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(null);

        fileSystemTree = new JTree();
        fileSystemTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane scroll1 = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTreeScrollPane(fileSystemTree);

        fileSystemList = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getList();
        setListRenderer();
        JScrollPane scroll2 = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getListScrollPane(fileSystemList);

        selection = new JLabel();

        okButton = new JButton(I18nUtils.getString("OK"));
        okButton.addActionListener(new OkButtonActionListener());
        cancelButton = new JButton(I18nUtils.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        scroll1.setSize(220, 320);
        scroll1.setLocation(10, 10);
        panel.add(scroll1);

        scroll2.setSize(400, 320);
        scroll2.setLocation(240, 10);
        panel.add(scroll2);

        selection.setSize(600, 20);
        selection.setLocation(10, 335);
        panel.add(selection);

        okButton.setSize(100, 25);
        okButton.setLocation(430, 360);
        panel.add(okButton);

        cancelButton.setSize(100, 25);
        cancelButton.setLocation(540, 360);
        panel.add(cancelButton);

        return panel;
    }

    /**
     * Gets the files.
     * 
     * @param f
     *            the f
     * 
     * @return the files
     */
    File[] getFiles(File f) {
        File[] files = fsView.getFiles(f, true);
        List<File> list = new ArrayList<File>();
        for (File element : files) {
            if (!directoryOnly) {
                list.add(element);
            } else if (element.isDirectory()) {
                list.add(element);
            }
        }
        Collections.sort(list);
        return list.toArray(new File[list.size()]);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IFileSelectionDialog#getSelectedDir()
	 */
    @Override
	public File getSelectedDir() {
        return selectedDir;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IFileSelectionDialog#getSelectedFiles()
	 */
    @Override
	public File[] getSelectedFiles() {
        return Arrays.copyOf(selectedFiles, selectedFiles.length);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IFileSelectionDialog#isCanceled()
	 */
    @Override
	public boolean isCanceled() {
        return canceled;
    }

    /**
     * Sets the list renderer.
     */
    private void setListRenderer() {
        fileSystemList.setCellRenderer(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getListCellRenderer(new FileSystemListCellRendererCode()));
    }

    /**
     * Sets the selection text.
     * 
     * @param f
     *            the new selection text
     */
    void setSelectionText(File f) {
        String displayName;
        if (!fsView.isFileSystem(f)) {
            displayName = fsView.getSystemDisplayName(f);
        } else {
            displayName = f.getAbsolutePath();
        }
        selection.setText(displayName);
    }

    /**
     * Sets the tree.
     */

    private void setTree() {
        File[] roots = fsView.getRoots();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();

        for (File f : roots) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(new Directory(f));
            root.add(treeNode);
            File[] files = fsView.getFiles(f, true);
            Arrays.sort(files);
            for (File f2 : files) {
                if (fsView.isTraversable(f2)) {
                    DefaultMutableTreeNode treeNode2 = new DefaultMutableTreeNode(new Directory(f2));
                    treeNode.add(treeNode2);
                    treeNode2.add(new DefaultMutableTreeNode("Dummy node"));
                }
            }
        }

        DefaultTreeModel model = new DefaultTreeModel(root);
        fileSystemTree.setModel(model);
        fileSystemTree.setRootVisible(false);
        fileSystemTree.expandRow(0);
        fileSystemTree.setSelectionRow(0);
        fileSystemList.setListData(getFiles(roots[0]));
        setSelectionText(roots[0]);
        setTreeRenderer();
        fileSystemTree.addTreeWillExpandListener(new FileSystemTreeTreeWillExpandListener());
        fileSystemTree.addTreeSelectionListener(new FileSystemTreeTreeSelectionListener());
        fileSystemList.addMouseListener(new FileSystemListMouseAdapter());
    }

    /**
     * Sets the tree renderer.
     */
    private void setTreeRenderer() {
        fileSystemTree.setCellRenderer(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTreeCellRenderer(new FileSystemTreeCellRendererCode()));
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IFileSelectionDialog#showDialog()
	 */
    @Override
	public void showDialog() {
        canceled = true;
        setTree();
        setVisible(true);
    }
}
