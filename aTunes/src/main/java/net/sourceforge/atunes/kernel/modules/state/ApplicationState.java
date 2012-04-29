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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.navigator.RepositoryNavigationView;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.tags.IncompleteTagsChecker;
import net.sourceforge.atunes.model.ArtistViewMode;
import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.IHotkeysConfig;
import net.sourceforge.atunes.model.ILocaleBean;
import net.sourceforge.atunes.model.ILyricsEngineInfo;
import net.sourceforge.atunes.model.IProxyBean;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.TagAttribute;
import net.sourceforge.atunes.model.ViewMode;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationState implements IState {

    private static final String UNCHECKED = "unchecked";

	/**
     * Component responsible of store state
     */
    private IStateStore stateStore;
    
    /**
     * Sets state store
     * @param store
     */
    public void setStateStore(IStateStore store) {
		this.stateStore = store;
	}

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowNavigationTable()
	 */
    @Override
	public boolean isShowNavigationTable() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_NAVIGATION_TABLE, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowNavigationTable(boolean)
	 */
    @Override
	public void setShowNavigationTable(boolean showNavigationTable) {
        this.stateStore.storePreference(Preferences.SHOW_NAVIGATION_TABLE, showNavigationTable);
    }
    
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getNavigationView()
	 */
    @Override
	public String getNavigationView() {
        return (String) this.stateStore.retrievePreference(Preferences.NAVIGATION_VIEW, RepositoryNavigationView.class.getName());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setNavigationView(java.lang.String)
	 */
    @Override
	public void setNavigationView(String navigationView) {
        this.stateStore.storePreference(Preferences.NAVIGATION_VIEW, navigationView);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getViewMode()
	 */
    @Override
	public ViewMode getViewMode() {
        return (ViewMode) this.stateStore.retrievePreference(Preferences.VIEW_MODE, ViewMode.ARTIST);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setViewMode(net.sourceforge.atunes.model.ViewMode)
	 */
    @Override
	public void setViewMode(ViewMode viewMode) {
        this.stateStore.storePreference(Preferences.VIEW_MODE, viewMode);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLocale()
	 */
    @Override
	public ILocaleBean getLocale() {
    	return (ILocaleBean) this.stateStore.retrievePreference(Preferences.LOCALE, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLocale(net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean)
	 */
    @Override
	public void setLocale(ILocaleBean locale) {
        this.stateStore.storePreference(Preferences.LOCALE, locale);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getOldLocale()
	 */
    @Override
	public ILocaleBean getOldLocale() {
        return (ILocaleBean) this.stateStore.retrievePreference(Preferences.OLD_LOCALE, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setOldLocale(net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean)
	 */
    @Override
	public void setOldLocale(ILocaleBean oldLocale) {
        this.stateStore.storePreference(Preferences.OLD_LOCALE, oldLocale);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDefaultSearch()
	 */
    @Override
	public String getDefaultSearch() {
        return (String) this.stateStore.retrievePreference(Preferences.DEFAULT_SEARCH, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDefaultSearch(java.lang.String)
	 */
    @Override
	public void setDefaultSearch(String defaultSearch) {
    	this.stateStore.storePreference(Preferences.DEFAULT_SEARCH, defaultSearch);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseContext()
	 */
    @Override
	public boolean isUseContext() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.USE_CONTEXT, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseContext(boolean)
	 */
    @Override
	public void setUseContext(boolean useContext) {
    	if (isUseContext() != useContext) {
    		this.stateStore.storePreference(Preferences.USE_CONTEXT, useContext);
    	}
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getSelectedContextTab()
	 */
    @Override
	public String getSelectedContextTab() {
        return (String) this.stateStore.retrievePreference(Preferences.SELECTED_CONTEXT_TAB, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSelectedContextTab(java.lang.String)
	 */
    @Override
	public void setSelectedContextTab(String selectedContextTab) {
    	if (getSelectedContextTab() == null || !getSelectedContextTab().equals(selectedContextTab)) {
    		this.stateStore.storePreference(Preferences.SELECTED_CONTEXT_TAB, selectedContextTab);
    	}
    }
    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getProxy()
	 */
    @Override
	public IProxyBean getProxy() {
        return (IProxyBean) this.stateStore.retrievePreference(Preferences.PROXY, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setProxy(net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean)
	 */
    @Override
	public void setProxy(IProxyBean proxy) {
        this.stateStore.storePreference(Preferences.PROXY, proxy);
    }
    
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isEnableAdvancedSearch()
	 */
    @Override
	public boolean isEnableAdvancedSearch() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.ENABLE_ADVANCED_SEARCH, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEnableAdvancedSearch(boolean)
	 */
    @Override
	public void setEnableAdvancedSearch(boolean enableAdvancedSearch) {
    	this.stateStore.storePreference(Preferences.ENABLE_ADVANCED_SEARCH, enableAdvancedSearch);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isEnableHotkeys()
	 */
    @Override
	public boolean isEnableHotkeys() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.ENABLE_HOTKEYS, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEnableHotkeys(boolean)
	 */
    @Override
	public void setEnableHotkeys(boolean enableHotkeys) {
    	this.stateStore.storePreference(Preferences.ENABLE_HOTKEYS, enableHotkeys);
    }
    
        
    
    

    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowFavoritesInNavigator()
	 */
    @Override
	public boolean isShowFavoritesInNavigator() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_FAVORITES_IN_NAVIGATOR, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowFavoritesInNavigator(boolean)
	 */
    @Override
	public void setShowFavoritesInNavigator(boolean showFavoritesInNavigator) {
    	this.stateStore.storePreference(Preferences.SHOW_FAVORITES_IN_NAVIGATOR, showFavoritesInNavigator);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseCdErrorCorrection()
	 */
    @Override
	public boolean isUseCdErrorCorrection() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_CD_ERROR_CORRECTION, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseCdErrorCorrection(boolean)
	 */
    @Override
	public void setUseCdErrorCorrection(boolean useCdErrorCorrection) {
    	this.stateStore.storePreference(Preferences.USE_CD_ERROR_CORRECTION, useCdErrorCorrection);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseSmartTagViewSorting()
	 */
    @Override
	public boolean isUseSmartTagViewSorting() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_SMART_TAG_VIEW_SORTING, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseSmartTagViewSorting(boolean)
	 */
    @Override
	public void setUseSmartTagViewSorting(boolean useSmartTagViewSorting) {
    	this.stateStore.storePreference(Preferences.USE_SMART_TAG_VIEW_SORTING, useSmartTagViewSorting);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUsePersonNamesArtistTagViewSorting()
	 */
    @Override
	public boolean isUsePersonNamesArtistTagViewSorting() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_PERSON_NAMES_ARTIST_TAG_SORTING, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUsePersonNamesArtistTagViewSorting(boolean)
	 */
    @Override
	public void setUsePersonNamesArtistTagViewSorting(boolean usePersonNamesArtistTagViewSorting) {
    	this.stateStore.storePreference(Preferences.USE_PERSON_NAMES_ARTIST_TAG_SORTING, usePersonNamesArtistTagViewSorting);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isSaveContextPicture()
	 */
    @Override
	public boolean isSaveContextPicture() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SAVE_CONTEXT_PICTURE, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSaveContextPicture(boolean)
	 */
    @Override
	public void setSaveContextPicture(boolean saveContextPicture) {
    	this.stateStore.storePreference(Preferences.SAVE_CONTEXT_PICTURE, saveContextPicture);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowExtendedTooltip()
	 */
    @Override
	public boolean isShowExtendedTooltip() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_EXTENDED_TOOLTIP, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowExtendedTooltip(boolean)
	 */
    @Override
	public void setShowExtendedTooltip(boolean showExtendedTooltip) {
    	this.stateStore.storePreference(Preferences.SHOW_EXTENDED_TOOLTIP, showExtendedTooltip);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getExtendedTooltipDelay()
	 */
    @Override
	public int getExtendedTooltipDelay() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.EXTENDED_TOOLTIP_DELAY, 1);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setExtendedTooltipDelay(int)
	 */
    @Override
	public void setExtendedTooltipDelay(int extendedTooltipDelay) {
    	this.stateStore.storePreference(Preferences.EXTENDED_TOOLTIP_DELAY, extendedTooltipDelay);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isLastFmEnabled()
	 */
    @Override
	public boolean isLastFmEnabled() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.LASTFM_ENABLED, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLastFmEnabled(boolean)
	 */
    @Override
	public void setLastFmEnabled(boolean lastFmEnabled) {
    	this.stateStore.storePreference(Preferences.LASTFM_ENABLED, lastFmEnabled);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLastFmUser()
	 */
    @Override
	public String getLastFmUser() {
    	return (String) this.stateStore.retrievePreference(Preferences.LASTFM_USER, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLastFmUser(java.lang.String)
	 */
    @Override
	public void setLastFmUser(String lastFmUser) {
    	this.stateStore.storePreference(Preferences.LASTFM_USER, lastFmUser);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLastFmPassword()
	 */
    @Override
	public String getLastFmPassword() {
		return this.stateStore.retrievePasswordPreference(Preferences.LASTFM_PASSWORD);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLastFmPassword(java.lang.String)
	 */
    @Override
	public void setLastFmPassword(String lastFmPassword) {
		this.stateStore.storePasswordPreference(Preferences.LASTFM_PASSWORD, lastFmPassword);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isAutoLoveFavoriteSong()
	 */
    @Override
	public boolean isAutoLoveFavoriteSong() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.AUTO_LOVE_FAVORITE_SONG, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAutoLoveFavoriteSong(boolean)
	 */
    @Override
	public void setAutoLoveFavoriteSong(boolean autoLoveFavoriteSong) {
    	this.stateStore.storePreference(Preferences.AUTO_LOVE_FAVORITE_SONG, autoLoveFavoriteSong);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLyricsEnginesInfo()
	 */
    @Override
	@SuppressWarnings(UNCHECKED)
	public List<ILyricsEngineInfo> getLyricsEnginesInfo() {
    	return (List<ILyricsEngineInfo>) this.stateStore.retrievePreference(Preferences.LYRICS_ENGINES_INFO, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLyricsEnginesInfo(java.util.List)
	 */
    @Override
	public void setLyricsEnginesInfo(List<ILyricsEngineInfo> lyricsEnginesInfo) {
    	this.stateStore.storePreference(Preferences.LYRICS_ENGINES_INFO, lyricsEnginesInfo);
    }
    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getEncoder()
	 */
    @Override
	public String getEncoder() {
    	return (String) this.stateStore.retrievePreference(Preferences.ENCODER, "OGG");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEncoder(java.lang.String)
	 */
    @Override
	public void setEncoder(String encoder) {
    	this.stateStore.storePreference(Preferences.ENCODER, encoder);
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getEncoderQuality()
	 */
    @Override
	public String getEncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.ENCODER_QUALITY, "5");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEncoderQuality(java.lang.String)
	 */
    @Override
	public void setEncoderQuality(String encoderQuality) {
    	this.stateStore.storePreference(Preferences.ENCODER_QUALITY, encoderQuality);
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getMp3EncoderQuality()
	 */
    @Override
	public String getMp3EncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.MP3_ENCODER_QUALITY, "medium");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setMp3EncoderQuality(java.lang.String)
	 */
    @Override
	public void setMp3EncoderQuality(String mp3EncoderQuality) {
    	this.stateStore.storePreference(Preferences.MP3_ENCODER_QUALITY, mp3EncoderQuality);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getFlacEncoderQuality()
	 */
    @Override
	public String getFlacEncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.FLAC_ENCODER_QUALITY, "-5");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setFlacEncoderQuality(java.lang.String)
	 */
    @Override
	public void setFlacEncoderQuality(String flacEncoderQuality) {
    	this.stateStore.storePreference(Preferences.FLAC_ENCODER_QUALITY, flacEncoderQuality);
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getCdRipperFileNamePattern()
	 */
    @Override
	public String getCdRipperFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.CD_RIPPER_FILENAME_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setCdRipperFileNamePattern(java.lang.String)
	 */
    @Override
	public void setCdRipperFileNamePattern(String cdRipperFileNamePattern) {
    	this.stateStore.storePreference(Preferences.CD_RIPPER_FILENAME_PATTERN, cdRipperFileNamePattern);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getColumns()
	 */
    @Override
	@SuppressWarnings(UNCHECKED)
	public Map<String, ColumnBean> getColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore.retrievePreference(Preferences.COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setColumns(java.util.Map)
	 */
    @Override
	public void setColumns(Map<String, ColumnBean> columns) {
    	if (getColumns() == null || !getColumns().equals(columns)) {
    		this.stateStore.storePreference(Preferences.COLUMNS, new HashMap<String, ColumnBean>(columns));
    	}
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getNavigatorColumns()
	 */
    @Override
	@SuppressWarnings(UNCHECKED)
	public Map<String, ColumnBean> getNavigatorColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore.retrievePreference(Preferences.NAVIGATOR_COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setNavigatorColumns(java.util.Map)
	 */
    @Override
	public void setNavigatorColumns(Map<String, ColumnBean> navigatorColumns) {
    	if (getNavigatorColumns() == null || !getNavigatorColumns().equals(navigatorColumns)) {
    		this.stateStore.storePreference(Preferences.NAVIGATOR_COLUMNS, navigatorColumns);
    	}
    }


    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDefaultDeviceLocation()
	 */
    @Override
	public String getDefaultDeviceLocation() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEFAULT_DEVICE_LOCATION, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDefaultDeviceLocation(java.lang.String)
	 */
    @Override
	public void setDefaultDeviceLocation(String defaultDeviceLocation) {
    	this.stateStore.storePreference(Preferences.DEFAULT_DEVICE_LOCATION, defaultDeviceLocation);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getPodcastFeedEntriesRetrievalInterval()
	 */
    @Override
	public long getPodcastFeedEntriesRetrievalInterval() {
    	return (Long) this.stateStore.retrievePreference(Preferences.PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL, PodcastFeedHandler.DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL);        
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setPodcastFeedEntriesRetrievalInterval(long)
	 */
    @Override
	public void setPodcastFeedEntriesRetrievalInterval(long podcastFeedEntriesRetrievalInterval) {
    	this.stateStore.storePreference(Preferences.PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL, podcastFeedEntriesRetrievalInterval);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getPodcastFeedEntryDownloadPath()
	 */
    @Override
	public String getPodcastFeedEntryDownloadPath() {
    	return (String) this.stateStore.retrievePreference(Preferences.PODCAST_FEED_ENTRY_DOWNLOAD_PATH, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setPodcastFeedEntryDownloadPath(java.lang.String)
	 */
    @Override
	public void setPodcastFeedEntryDownloadPath(String podcastFeedEntryDownloadPath) {
    	this.stateStore.storePreference(Preferences.PODCAST_FEED_ENTRY_DOWNLOAD_PATH, podcastFeedEntryDownloadPath);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseDownloadedPodcastFeedEntries()
	 */
    @Override
	public boolean isUseDownloadedPodcastFeedEntries() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_DOWNLOADED_PODCAST_FEED_ENTRIES, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseDownloadedPodcastFeedEntries(boolean)
	 */
    @Override
	public void setUseDownloadedPodcastFeedEntries(boolean useDownloadedPodcastFeedEntries) {
    	this.stateStore.storePreference(Preferences.USE_DOWNLOADED_PODCAST_FEED_ENTRIES, useDownloadedPodcastFeedEntries);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isRemovePodcastFeedEntriesRemovedFromPodcastFeed()
	 */
    @Override
	public boolean isRemovePodcastFeedEntriesRemovedFromPodcastFeed() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setRemovePodcastFeedEntriesRemovedFromPodcastFeed(boolean)
	 */
    @Override
	public void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(boolean removePodcastFeedEntriesRemovedFromPodcastFeed) {
    	this.stateStore.storePreference(Preferences.REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED, removePodcastFeedEntriesRemovedFromPodcastFeed);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isHighlightIncompleteTagElements()
	 */
    @Override
	public boolean isHighlightIncompleteTagElements() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setHighlightIncompleteTagElements(boolean)
	 */
    @Override
	public void setHighlightIncompleteTagElements(boolean highlightIncompleteTagElements) {
    	this.stateStore.storePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS, highlightIncompleteTagElements);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getHighlightIncompleteTagFoldersAttributes()
	 */
    @Override
	@SuppressWarnings(UNCHECKED)
	public List<TagAttribute> getHighlightIncompleteTagFoldersAttributes() {
    	return (List<TagAttribute> ) this.stateStore.retrievePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES, IncompleteTagsChecker.getDefaultTagAttributesToHighlightFolders());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setHighlightIncompleteTagFoldersAttributes(java.util.List)
	 */
    @Override
	public void setHighlightIncompleteTagFoldersAttributes(List<TagAttribute> highlightIncompleteTagFoldersAttributes) {
    	this.stateStore.storePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES, highlightIncompleteTagFoldersAttributes);
    }
    
       


    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isAutoScrollPlayListEnabled()
	 */
    @Override
	public boolean isAutoScrollPlayListEnabled() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.AUTO_SCROLL_PLAYLIST, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAutoScrollPlayListEnabled(boolean)
	 */
    @Override
	public void setAutoScrollPlayListEnabled(boolean autoScrollPlayListEnabled) {
    	this.stateStore.storePreference(Preferences.AUTO_SCROLL_PLAYLIST, autoScrollPlayListEnabled);
    }
    
    

    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLoadPlaylistPath()
	 */
    @Override
	public String getLoadPlaylistPath() {
    	return (String) this.stateStore.retrievePreference(Preferences.LOAD_PLAYLIST_PATH, null); 
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLoadPlaylistPath(java.lang.String)
	 */
    @Override
	public void setLoadPlaylistPath(String loadPlaylistPath) {
    	this.stateStore.storePreference(Preferences.LOAD_PLAYLIST_PATH, loadPlaylistPath);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getSavePlaylistPath()
	 */
    @Override
	public String getSavePlaylistPath() {
    	return (String) this.stateStore.retrievePreference(Preferences.SAVE_PLAYLIST_PATH, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSavePlaylistPath(java.lang.String)
	 */
    @Override
	public void setSavePlaylistPath(String savePlaylistPath) {
    	this.stateStore.storePreference(Preferences.SAVE_PLAYLIST_PATH, savePlaylistPath);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isStopPlayerOnPlayListSwitch()
	 */
    @Override
	public boolean isStopPlayerOnPlayListSwitch() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_SWITCH, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setStopPlayerOnPlayListSwitch(boolean)
	 */
    @Override
	public void setStopPlayerOnPlayListSwitch(boolean stopPlayerOnPlayListSwitch) {
    	this.stateStore.storePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_SWITCH, stopPlayerOnPlayListSwitch);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isStopPlayerOnPlayListClear()
	 */
    @Override
	public boolean isStopPlayerOnPlayListClear() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_CLEAR, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setStopPlayerOnPlayListClear(boolean)
	 */
    @Override
	public void setStopPlayerOnPlayListClear(boolean stopPlayerOnPlayListClear) {
    	this.stateStore.storePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_CLEAR, stopPlayerOnPlayListClear);
    }
    
    

    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDeviceFileNamePattern()
	 */
    @Override
	public String getDeviceFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEVICE_FILENAME_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDeviceFileNamePattern(java.lang.String)
	 */
    @Override
	public void setDeviceFileNamePattern(String deviceFileNamePattern) {
    	this.stateStore.storePreference(Preferences.DEVICE_FILENAME_PATTERN, deviceFileNamePattern);
    }
    
    
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDeviceFolderPathPattern()
	 */
    @Override
	public String getDeviceFolderPathPattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEVICE_FOLDER_PATH_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDeviceFolderPathPattern(java.lang.String)
	 */
    @Override
	public void setDeviceFolderPathPattern(String deviceFolderPathPattern) {
    	this.stateStore.storePreference(Preferences.DEVICE_FOLDER_PATH_PATTERN, deviceFolderPathPattern);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isAllowRepeatedSongsInDevice()
	 */
    @Override
	public boolean isAllowRepeatedSongsInDevice() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAllowRepeatedSongsInDevice(boolean)
	 */
    @Override
	public void setAllowRepeatedSongsInDevice(boolean allowRepeatedSongsInDevice) {
    	this.stateStore.storePreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE, allowRepeatedSongsInDevice);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getHotkeysConfig()
	 */
    @Override
	public IHotkeysConfig getHotkeysConfig() {
    	return (IHotkeysConfig) this.stateStore.retrievePreference(Preferences.HOTKEYS_CONFIG, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setHotkeysConfig(net.sourceforge.atunes.kernel.modules.hotkeys.HotkeysConfig)
	 */
    @Override
	public void setHotkeysConfig(IHotkeysConfig hotkeysConfig) {
    	this.stateStore.storePreference(Preferences.HOTKEYS_CONFIG, hotkeysConfig);
    }
    
    

    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getNotificationEngine()
	 */
    @Override
	public String getNotificationEngine() {
    	return (String) this.stateStore.retrievePreference(Preferences.NOTIFICATION_ENGINE, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setNotificationEngine(java.lang.String)
	 */
    @Override
	public void setNotificationEngine(String notificationEngine) {
    	this.stateStore.storePreference(Preferences.NOTIFICATION_ENGINE, notificationEngine);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowContextAlbumsInGrid()
	 */
    @Override
	public boolean isShowContextAlbumsInGrid() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_CONTEXT_ALBUMS_IN_GRID, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowContextAlbumsInGrid(boolean)
	 */
    @Override
	public void setShowContextAlbumsInGrid(boolean showContextAlbumsInGrid) {
    	this.stateStore.storePreference(Preferences.SHOW_CONTEXT_ALBUMS_IN_GRID, showContextAlbumsInGrid);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowNavigationTree()
	 */
    @Override
	public boolean isShowNavigationTree() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_NAVIGATION_TREE, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowNavigationTree(boolean)
	 */
    @Override
	public void setShowNavigationTree(boolean showNavigationTree) {
    	if (isShowNavigationTree() != showNavigationTree) {
    		this.stateStore.storePreference(Preferences.SHOW_NAVIGATION_TREE, showNavigationTree);
    	}
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getCustomNavigatorColumns()
	 */
    @Override
	@SuppressWarnings(UNCHECKED)
	public Map<String, Map<String, ColumnBean>> getCustomNavigatorColumns() {
    	// This map is not unmodifiable
    	return (Map<String, Map<String, ColumnBean>>) this.stateStore.retrievePreference(Preferences.CUSTOM_NAVIGATOR_COLUMNS, null); 
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setCustomNavigatorColumns(java.util.Map)
	 */
    @Override
	public void setCustomNavigatorColumns(Map<String, Map<String, ColumnBean>> customNavigatorColumns) {
    	this.stateStore.storePreference(Preferences.CUSTOM_NAVIGATOR_COLUMNS, customNavigatorColumns);
    }

    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getAlbumsColumns()
	 */
    @Override
	@SuppressWarnings(UNCHECKED)
    public Map<String, ColumnBean> getAlbumsColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore.retrievePreference(Preferences.ALBUM_COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAlbumColumns(java.util.Map)
	 */
    @Override
	public void setAlbumColumns(Map<String, ColumnBean> columnsConfiguration) {
    	this.stateStore.storePreference(Preferences.ALBUM_COLUMNS, columnsConfiguration);
    }

	@Override
	public void setPluginsEnabled(boolean pluginsEnabled) {
		this.stateStore.storePreference(Preferences.PLUGINS_ENABLED, pluginsEnabled);
		
	}

	@Override
	public boolean isPluginsEnabled() {
		return (Boolean) this.stateStore.retrievePreference(Preferences.PLUGINS_ENABLED, false);
	}
	
	@Override
	public void setArtistViewMode(ArtistViewMode artistViewMode) {
		this.stateStore.storePreference(Preferences.ARTIST_VIEW_MODE, artistViewMode);
		
	}

	@Override
	public ArtistViewMode getArtistViewMode() {
		return (ArtistViewMode) this.stateStore.retrievePreference(Preferences.ARTIST_VIEW_MODE, ArtistViewMode.BOTH);
	}
	
	
}
