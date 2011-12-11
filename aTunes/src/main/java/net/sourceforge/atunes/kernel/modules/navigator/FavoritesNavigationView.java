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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.CopyToDeviceAction;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.ExtractPictureAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromFavoritesAction;
import net.sourceforge.atunes.kernel.actions.RenameAudioFileInNavigationTableAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAtAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

public final class FavoritesNavigationView extends AbstractNavigationView {

    private JTree favoritesTree;

    /** The favorite tree menu. */
    private JPopupMenu favoriteTreeMenu;

    /** The favorite table menu. */
    private JPopupMenu favoriteTableMenu;
    
    private IFavoritesHandler favoritesHandler;
    
    private IRepositoryHandler repositoryHandler;
    
    private CachedIconFactory favoriteIcon;
    
    /**
     * @param favoriteIcon
     */
    public void setFavoriteIcon(CachedIconFactory favoriteIcon) {
		this.favoriteIcon = favoriteIcon;
	}

    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
    
    @Override
    public IColorMutableImageIcon getIcon() {
    	return favoriteIcon.getColorMutableIcon();
    }

    @Override
    public String getTitle() {
        return I18nUtils.getString("FAVORITES");
    }

    @Override
    public String getTooltip() {
        return I18nUtils.getString("FAVORITES_TAB_TOOLTIP");
    }

    @Override
    public JTree getTree() {
        if (favoritesTree == null) {
            favoritesTree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("FAVORITES"))));
            favoritesTree.setToggleClickCount(0);
            favoritesTree.setCellRenderer(getTreeRenderer());
        }
        return favoritesTree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (favoriteTreeMenu == null) {
            favoriteTreeMenu = new JPopupMenu();
            
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = Context.getBean("addToPlayListFromFavoritesNavigationView", AddToPlayListAction.class);
            addToPlayListAction.setAudioObjectsSource(this);
            favoriteTreeMenu.add(addToPlayListAction);
            
            SetAsPlayListAction setAsPlayListAction = Context.getBean("setAsPlaylistFromFavoritesNavigationView", SetAsPlayListAction.class);
            setAsPlayListAction.setAudioObjectsSource(this);
            favoriteTreeMenu.add(setAsPlayListAction);
            
            favoriteTreeMenu.add(new JSeparator());
            
            OpenFolderFromNavigatorAction openFolderFromNavigatorAction = Context.getBean("openFolderFromFavoritesNavigationView", OpenFolderFromNavigatorAction.class);
            openFolderFromNavigatorAction.setAudioObjectsSource(this);
            favoriteTreeMenu.add(openFolderFromNavigatorAction);
            
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(new EditTagMenu(false, this));
            favoriteTreeMenu.add(Context.getBean(EditTitlesAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(Context.getBean(RemoveFromDiskAction.class));
            favoriteTreeMenu.add(new JSeparator());
            
            AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = Context.getBean("copyToDeviceFromFavoritesNavigationView", CopyToDeviceAction.class);
            copyToDeviceAction.setAudioObjectsSource(this);
            favoriteTreeMenu.add(copyToDeviceAction);
            
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(Context.getBean(RemoveFromFavoritesAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(Context.getBean(SearchArtistAction.class));
            favoriteTreeMenu.add(Context.getBean(SearchArtistAtAction.class));
        }
        return favoriteTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (favoriteTableMenu == null) {
            favoriteTableMenu = new JPopupMenu();
            
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = Context.getBean("addToPlayListFromFavoritesNavigationView", AddToPlayListAction.class);
            addToPlayListAction.setAudioObjectsSource(this);
            favoriteTableMenu.add(addToPlayListAction);
            
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = Context.getBean("addToPlayListAfterCurrentAudioObjectFromFavoritesNavigationView", AddToPlayListAfterCurrentAudioObjectAction.class);
            addToPlayListAfterCurrentAudioObjectAction.setAudioObjectsSource(this);
            favoriteTableMenu.add(addToPlayListAfterCurrentAudioObjectAction);
            
            SetAsPlayListAction setAsPlayListAction = Context.getBean("setAsPlaylistFromFavoritesNavigationView", SetAsPlayListAction.class);
            setAsPlayListAction.setAudioObjectsSource(this);
            favoriteTableMenu.add(setAsPlayListAction);
            
            favoriteTableMenu.add(Context.getBean(PlayNowAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(Context.getBean(ShowNavigatorTableItemInfoAction.class));
            favoriteTableMenu.add(new JSeparator());
            
            OpenFolderFromNavigatorAction openFolderFromNavigatorAction = Context.getBean("openFolderFromFavoritesNavigationView", OpenFolderFromNavigatorAction.class);
            openFolderFromNavigatorAction.setAudioObjectsSource(this);
            favoriteTableMenu.add(openFolderFromNavigatorAction);
            
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(new EditTagMenu(false, this));
            
            ExtractPictureAction extractPictureAction = Context.getBean("extractPictureFromFavoritesNavigationView", ExtractPictureAction.class);
            extractPictureAction.setAudioObjectsSource(this);
            favoriteTableMenu.add(extractPictureAction);
            
            
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(Context.getBean(RemoveFromDiskAction.class));
            favoriteTableMenu.add(Context.getBean(RenameAudioFileInNavigationTableAction.class));
            favoriteTableMenu.add(new JSeparator());
            
            AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = Context.getBean("copyToDeviceFromFavoritesNavigationView", CopyToDeviceAction.class);
            copyToDeviceAction.setAudioObjectsSource(this);
            favoriteTableMenu.add(copyToDeviceAction);
            
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(Context.getBean(RemoveFromFavoritesAction.class));
        }
        return favoriteTableMenu;
    }

    @Override
    public Map<String, ?> getViewData(ViewMode viewMode) {
        Map<String, Map<?, ?>> data = new HashMap<String, Map<?, ?>>();
        data.put("ARTISTS", favoritesHandler.getFavoriteArtistsInfo());
        data.put("ALBUMS", favoritesHandler.getFavoriteAlbumsInfo());
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing ", this.getClass().getName());

        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Get objects selected before refreshing tree
        List<ITreeObject<? extends IAudioObject>> objectsSelected = getTreeObjectsSelected(getTree());
        // Get objects expanded before refreshing tree
        List<ITreeObject<? extends IAudioObject>> objectsExpanded = getTreeObjectsExpanded(getTree(), root);

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        Map<String, ?> data = getViewData(viewMode);

        root.setUserObject(I18nUtils.getString("FAVORITES"));
        root.removeAllChildren();

        DefaultMutableTreeNode artistsNode = new DefaultMutableTreeNode();
        artistsNode.setUserObject(I18nUtils.getString("ARTISTS"));
        nodesToExpand.add(artistsNode);
        addArtistNodes(artistsNode, treeFilter, (Map<String, IArtist>) data.get("ARTISTS"), objectsSelected, objectsExpanded, nodesToSelect, nodesToExpand);
        root.add(artistsNode);

        DefaultMutableTreeNode albumsNode = new DefaultMutableTreeNode();
        albumsNode.setUserObject(I18nUtils.getString("ALBUMS"));
        nodesToExpand.add(albumsNode);
        addAlbumNodes(albumsNode, treeFilter, (Map<String, IAlbum>) data.get("ALBUMS"), objectsSelected, objectsExpanded, nodesToSelect, nodesToExpand);
        root.add(albumsNode);

        DefaultMutableTreeNode songsNode = new DefaultMutableTreeNode();
        songsNode.setUserObject(I18nUtils.getString("SONGS"));
        root.add(songsNode);

        treeModel.reload();

        // Expand nodes
        NavigationViewHelper.expandNodes(getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        NavigationViewHelper.selectNodes(getTree(), nodesToSelect);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<ILocalAudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<ILocalAudioObject> songs = null;

        if (node.isRoot()) {
            songs = new ArrayList<ILocalAudioObject>();
            songs.addAll(repositoryHandler.getAudioFilesForArtists(favoritesHandler.getFavoriteArtistsInfo()));
            songs.addAll(repositoryHandler.getAudioFilesForAlbums(favoritesHandler.getFavoriteAlbumsInfo()));
            songs.addAll(favoritesHandler.getFavoriteSongsInfo().values());
        } else {
            if (node.getUserObject() instanceof ITreeObject) {
                songs = ((ITreeObject<ILocalAudioObject>) node.getUserObject()).getAudioObjects();
            } else {
                songs = new ArrayList<ILocalAudioObject>();
                if (node.getUserObject().toString().equals(I18nUtils.getString("ARTISTS"))) {
                    songs.addAll(repositoryHandler.getAudioFilesForArtists(favoritesHandler.getFavoriteArtistsInfo()));
                } else if (node.getUserObject().toString().equals(I18nUtils.getString("ALBUMS"))) {
                    songs.addAll(repositoryHandler.getAudioFilesForAlbums(favoritesHandler.getFavoriteAlbumsInfo()));
                } else {
                    songs.addAll(new ArrayList<ILocalAudioObject>(favoritesHandler.getFavoriteSongsInfo().values()));
                }
            }
        }
        return songs;
    }

    /**
     * Adds the album nodes.
     * 
     * @param root
     *            the root
     * @param albums
     *            the albums
     */
    private static void addAlbumNodes(DefaultMutableTreeNode root, String currentFilter, Map<String, IAlbum> albums, List<ITreeObject<? extends IAudioObject>> objectsSelected, List<ITreeObject<? extends IAudioObject>> objectsExpanded, List<DefaultMutableTreeNode> nodesToSelect, List<DefaultMutableTreeNode> nodesToExpand) {
        List<String> albumsNamesList = new ArrayList<String>(albums.keySet());
        Collections.sort(albumsNamesList);

        for (int i = 0; i < albumsNamesList.size(); i++) {
            IAlbum album = albums.get(albumsNamesList.get(i));
            if (currentFilter == null || album.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);

                // If node was selected before refreshing...
                if (objectsSelected.contains(albumNode.getUserObject())) {
                    nodesToSelect.add(albumNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(albumNode.getUserObject())) {
                    nodesToExpand.add(albumNode);
                }

                root.add(albumNode);
            }
        }
    }

    /**
     * Adds the artist nodes.
     * 
     * @param root
     *            the root
     * @param artists
     *            the artists
     */
    private static void addArtistNodes(DefaultMutableTreeNode root, String currentFilter, Map<String, IArtist> artists, List<ITreeObject<? extends IAudioObject>> objectsSelected, List<ITreeObject<? extends IAudioObject>> objectsExpanded, List<DefaultMutableTreeNode> nodesToSelect, List<DefaultMutableTreeNode> nodesToExpand) {
        List<String> artistNamesList = new ArrayList<String>(artists.keySet());
        Collections.sort(artistNamesList);

        for (int i = 0; i < artistNamesList.size(); i++) {
            IArtist artist = artists.get(artistNamesList.get(i));
            if (currentFilter == null || artist.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);

                // If node was selected before refreshing...
                if (objectsSelected.contains(artistNode.getUserObject())) {
                    nodesToSelect.add(artistNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(artistNode.getUserObject())) {
                    nodesToExpand.add(artistNode);
                }

                // If an artist fits current filter we want to show all albums so put filter to null
                addAlbumNodes(artistNode, null, artist.getAlbums(), objectsSelected, objectsExpanded, nodesToSelect, nodesToExpand);

                root.add(artistNode);
            }
        }
    }

    @Override
    public boolean isUseDefaultNavigatorColumnSet() {
        return true;
    }

    @Override
    public IColumnSet getCustomColumnSet() {
        // Returns null since uses default column set
        return null;
    }

    @Override
    public boolean isViewModeSupported() {
        return false;
    }
    
    public void setFavoritesHandler(IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}
}
