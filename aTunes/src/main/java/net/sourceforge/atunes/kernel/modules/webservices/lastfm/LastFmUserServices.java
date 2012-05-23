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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.UnknownObjectChecker;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmLovedTrack;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILovedTrack;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Result;
import de.umass.lastfm.Result.Status;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import de.umass.lastfm.scrobble.ScrobbleResult;

/**
 * Last.fm services related to user
 * @author alex
 *
 */
public class LastFmUserServices implements ApplicationContextAware {
	
    private static final int MIN_DURATION_TO_SUBMIT = 30;
    private static final int MAX_SUBMISSIONS = 50;
	
	private LastFmLogin lastFmLogin;
	
	private LastFmAPIKey lastFmAPIKey;
	
	private IStateContext stateContext;
	
	private LastFmCache lastFmCache;
	
	private IUnknownObjectChecker unknownObjectChecker;
	
	private ApplicationContext context;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
	}
	
	/**
	 * @param lastFmCache
	 */
	public void setLastFmCache(LastFmCache lastFmCache) {
		this.lastFmCache = lastFmCache;
	}
	
	/**
	 * @param stateContext
	 */
	public void setStateContext(IStateContext stateContext) {
		this.stateContext = stateContext;
	}
	
	/**
	 * @param lastFmAPIKey
	 */
	public void setLastFmAPIKey(LastFmAPIKey lastFmAPIKey) {
		this.lastFmAPIKey = lastFmAPIKey;
	}
	
	/**
	 * @param lastFmLogin
	 */
	public void setLastFmLogin(LastFmLogin lastFmLogin) {
		this.lastFmLogin = lastFmLogin;
	}

    /**
     * Adds a song to the list of banned tracks in Last.fm
     * 
     * @param song
     */
    void addBannedSong(IAudioObject song) {
        // Check all necessary conditions to submit request to Last.fm
        if (!lastFmLogin.checkCredentials() || !checkAudioFile(song) || !checkArtist(song)) {
            return;
        }

        Logger.info(StringUtils.getString("Trying to submit banned song to Last.fm: ", song.getArtist(), " - ", song.getTitle()));
        Result r = Track.ban(song.getArtist(), song.getTitle(), lastFmLogin.getSession());
        if (r.getStatus().equals(Status.OK)) {
            Logger.info(StringUtils.getString("Banned song submitted OK"));
        } else {
            Logger.error(StringUtils.getString("Error while submitting banned song"));
            // TODO: Add a cache to submit
        }
    }

    /**
     * Adds a song to the list of loved tracks in Last.fm
     * 
     * @param song
     */
    void addLovedSong(IAudioObject song) {
        // Check all necessary conditions to submit request to Last.fm
        if (!lastFmLogin.checkCredentials() || !checkAudioFile(song) || !checkArtist(song)) {
            return;
        }

        Logger.info(StringUtils.getString("Trying to submit loved song to Last.fm: ", song.getArtist(), " - ", song.getTitle()));
        Result r = Track.love(song.getArtist(), song.getTitle(), lastFmLogin.getSession());
        if (r.getStatus().equals(Status.OK)) {
            Logger.info(StringUtils.getString("Loved song submitted OK"));
        } else {
            Logger.error(StringUtils.getString("Error while submitting loved song"));
            // TODO: Add a cache to submit
        }
    }
    
    /**
     * Removes a song from the list of loved tracks in Last.fm
     * 
     * @param song
     */
    void removeLovedSong(IAudioObject song) {
        // Check all necessary conditions to submit request to Last.fm
        if (!lastFmLogin.checkCredentials() || !checkAudioFile(song) || !checkArtist(song)) {
            return;
        }

        Logger.info(StringUtils.getString("Trying to unlove song to Last.fm: ", song.getArtist(), " - ", song.getTitle()));
        Result r = Track.unlove(song.getArtist(), song.getTitle(), lastFmLogin.getSession());
        if (r.getStatus().equals(Status.OK)) {
            Logger.info(StringUtils.getString("Successfully unloved song"));
        } else {
            Logger.error(StringUtils.getString("Error while unloving song"));
            // TODO: Add a cache to submit
        }

    }

    /**
     * Submits song to Last.fm
     * 
     * @param file
     *            audio file
     * @param secondsPlayed
     *            seconds the audio file has already played
     * @throws ScrobblerException
     */
    void submit(IAudioObject file, long secondsPlayed) throws ScrobblerException {
        // Do all necessary checks
        if (!lastFmLogin.checkCredentials() || !checkArtist(file) || !checkTitle(file) || !checkDuration(file)) {
            return;
        }

        // Get started to play
        long startedToPlay = System.currentTimeMillis() / 1000 - secondsPlayed;

        Logger.info("Trying to submit song to Last.fm");
        ScrobbleResult result = Track.scrobble(file.getArtist(), file.getTitle(), (int) startedToPlay, lastFmLogin.getSession());

        if (result.isSuccessful() && !result.isIgnored()) {
        	Logger.info("Song submitted to Last.fm");
        } else {
        	lastFmCache.addSubmissionData(new net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData(file.getArtist(), file.getTitle(), (int) startedToPlay));
        	throw new ScrobblerException(result.getStatus().toString());
        }
    }

    /**
     * Check if parameter is a valid LocalAudioObject
     * 
     * @param ao
     * @return
     */
    private boolean checkAudioFile(IAudioObject ao) {
        if (!(ao instanceof ILocalAudioObject)) {
            return false;
        }
        return true;
    }

    /**
     * Submits now playing info to Last.fm
     * 
     * @param file
     *            audio file
     * @throws ScrobblerException
     */
    void submitNowPlayingInfo(ILocalAudioObject file) throws ScrobblerException {
        // Do all necessary checks
        if (!lastFmLogin.checkCredentials() || !checkArtist(file) || !checkTitle(file)) {
            return;
        }

        Logger.info("Trying to submit now playing info to Last.fm");
        ScrobbleResult status = Track.updateNowPlaying(file.getArtist(), file.getTitle(), lastFmLogin.getSession());
        if (status.isSuccessful() && !status.isIgnored()) {
        	Logger.info("Now playing info submitted to Last.fm");
        } else {
        	throw new ScrobblerException(status.getStatus().toString());
        }
    }

    /**
     * Returns a list of loved tracks from user profile
     * 
     * @return a list of loved tracks from user profile
     */
    List<ILovedTrack> getLovedTracks() {
        if (!StringUtils.isEmpty(stateContext.getLastFmUser())) {
        	List<ILovedTrack> lovedTracks = new ArrayList<ILovedTrack>();

        	int page = 1;
        	PaginatedResult<Track> paginatedResult = null;
        	do {
        		paginatedResult = User.getLovedTracks(stateContext.getLastFmUser(), page, lastFmAPIKey.getApiKey());
        		if (paginatedResult != null && paginatedResult.getPageResults() != null) {
        			for (Track t : paginatedResult.getPageResults()) {
        				lovedTracks.add(new LastFmLovedTrack(t.getArtist(), t.getName()));
        			}
        		}
        		page++;
        	} while (paginatedResult != null && page <= paginatedResult.getTotalPages());
        	
        	Logger.info("Returned ", lovedTracks.size(), " loved tracks from last.fm");
        	return lovedTracks;
        }
        return Collections.emptyList();
    }

    /**
     * Check artist
     * 
     * @param ao
     * @return
     */
    private boolean checkArtist(IAudioObject ao) {
        if (unknownObjectChecker.isUnknownArtist(ao.getArtist())) {
            Logger.debug("Don't submit to Last.fm: Unknown artist");
            return false;
        }
        return true;
    }

    /**
     * Check title
     * 
     * @param ao
     * @return
     */
    private boolean checkTitle(IAudioObject ao) {
        if (ao.getTitle().trim().equals("")) {
            Logger.debug("Don't submit to Last.fm: Unknown Title");
            return false;
        }
        return true;
    }

    /**
     * Check duration
     * 
     * @param ao
     * @return
     */
    private boolean checkDuration(IAudioObject ao) {
        if (ao.getDuration() < MIN_DURATION_TO_SUBMIT) {
            Logger.debug("Don't submit to Last.fm: Lenght < ", MIN_DURATION_TO_SUBMIT);
            return false;
        }
        return true;
    }

    /**
     * Submits Last.fm cache
     * @param service
     */
    void submitCacheToLastFm(ITaskService service) {
        if (stateContext.isLastFmEnabled()) {
        	service.submitNow("Submit Cache to Last.fm", new Runnable() {

                @Override
                public void run() {
                    try {
                        submitCache();
                    } catch (ScrobblerException e) {
                        if (e.getStatus() == 2) {
                            Logger.error("Authentication failure on Last.fm service");
                        } else {
                            Logger.error(e.getMessage());
                        }
                    }
                }
            });
        }
    }

    /**
     * Submits cache data to Last.fm
     * 
     * @throws ScrobblerException
     */
    private void submitCache() throws ScrobblerException {
        // Do all necessary checks
        if (!lastFmLogin.checkCredentials()) {
            return;
        }

        List<net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData> collectionWithSubmissionData = lastFmCache.getSubmissionData();
        if (!collectionWithSubmissionData.isEmpty()) {
            // More than MAX_SUBMISSIONS submissions at once are not allowed
            int size = collectionWithSubmissionData.size();
            if (size > MAX_SUBMISSIONS) {
                collectionWithSubmissionData = collectionWithSubmissionData.subList(size - MAX_SUBMISSIONS, size);
            }

            Logger.info("Trying to submit cache to Last.fm");
            ScrobbleResult result = null;
            boolean ok = true;
            for (net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData submissionData : collectionWithSubmissionData) {                	
            	result = Track.scrobble(submissionData.getArtist(), submissionData.getTitle(), submissionData.getStartTime(), lastFmLogin.getSession());
            	ok = ok || result.isSuccessful();
            }

            if (ok) {
            	lastFmCache.removeSubmissionData();
            	Logger.info("Cache submitted to Last.fm");
            } else {
            	throw new ScrobblerException(result.getStatus().toString());
            }

        }
    }
    
    /**
     * Submit song to Last.fm
     * 
     * @param audioFile
     * @param secondsPlayed
     * @param taskService
     */
    void submitToLastFm(final IAudioObject audioFile, final long secondsPlayed, ITaskService taskService) {
        if (stateContext.isLastFmEnabled()) {
        	SubmitToLastFmRunnable runnable = context.getBean(SubmitToLastFmRunnable.class);
        	runnable.setSecondsPlayed(secondsPlayed);
        	runnable.setAudioFile(audioFile);
        	taskService.submitNow("Submit to Last.fm", runnable);
        }
    }

    /**
     * Submit now playing info to Last.fm
     * 
     * @param audioFile
     *            the file
     */
    void submitNowPlayingInfoToLastFm(final ILocalAudioObject audioFile, ITaskService taskService) {
        if (stateContext.isLastFmEnabled()) {
        	SubmitNowPlayingInfoRunnable runnable = context.getBean(SubmitNowPlayingInfoRunnable.class);
        	runnable.setAudioFile(audioFile);
        	taskService.submitNow("Submit Now Playing to Last.fm", runnable);
        }
    }
}
