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

package net.sourceforge.atunes.kernel.modules.player;

import java.io.File;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.kernel.PlaybackStateListeners;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IExceptionDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.ITemporalDiskStorage;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.model.SubmissionState;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class has common logic for all player engines.
 */
/**
 * @author alex
 *
 */
public abstract class AbstractPlayerEngine implements IPlayerEngine {

	/**
	 * Setting this attribute to <code>true</code> avoid calling play back state listeners
	 */
	private boolean callToPlaybackStateListenersDisabled = false;

	/**
	 * Paused property: <code>true</code> if audio object play back is paused
	 */
	private boolean paused;

	/**
	 * Audio object
	 */
	private IAudioObject audioObject;

	/**
	 * Length of the current audio object
	 */
	private long currentAudioObjectLength;

	/**
	 * This attribute is used when caching files option is enabled. When caching
	 * a new file, file pointed by this object is removed from temp folder
	 */
	private File lastFileCached;

	/**
	 * The submission state of the current audio object
	 */
	private SubmissionState submissionState = SubmissionState.NOT_SUBMITTED;

	/**
	 * The time the current audio object has already played
	 */
	private long currentAudioObjectPlayedTime;

	private IFrame frame;

	private IOSManager osManager;

	private IPlayListHandler playListHandler;

	private INavigationHandler navigationHandler;

	private ITemporalDiskStorage temporalDiskStorage;

	private IPlayerHandler playerHandler;

	private PlayListEventListeners playListEventListeners;

	private ILocalAudioObjectFactory localAudioObjectFactory;

	private INavigationView podcastNavigationView;

	private Volume volumeController;

	private IStatePlayer statePlayer;

	private IDialogFactory dialogFactory;

	private PlaybackStateListeners playbackStateListeners;

	private IWebServicesHandler webServicesHandler;

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param playbackStateListeners
	 */
	public void setPlaybackStateListeners(
			final PlaybackStateListeners playbackStateListeners) {
		this.playbackStateListeners = playbackStateListeners;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	protected IStatePlayer getStatePlayer() {
		return statePlayer;
	}

	/**
	 * @param volumeController
	 */
	public void setVolumeController(final Volume volumeController) {
		this.volumeController = volumeController;
	}

	/**
	 * @param podcastNavigationView
	 */
	public void setPodcastNavigationView(final INavigationView podcastNavigationView) {
		this.podcastNavigationView = podcastNavigationView;
	}

	/**
	 * @return play list handler
	 */
	public IPlayListHandler getPlayListHandler() {
		return playListHandler;
	}

	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(final ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}

	/**
	 * A thread invoking play in engine
	 */
	private Thread playAudioObjectThread;

	/**
	 * @param playAudioObjectThread
	 */
	protected void setPlayAudioObjectThread(final Thread playAudioObjectThread) {
		this.playAudioObjectThread = playAudioObjectThread;
	}


	/**
	 * Calls all playback listeners with new state and current audio object When
	 * audio object changes this method must be called after change to call
	 * listeners with new audio object
	 * 
	 * @param newState
	 * @param currentAudioObject
	 */
	final void callPlaybackStateListeners(final PlaybackState newState) {
		if (!isCallToPlaybackStateListenersDisabled()) {
			playbackStateListeners.playbackStateChanged(newState, audioObject);
		}
	}

	/**
	 * Starts playing or pauses current audio object from play list
	 */
	@Override
	public final void playCurrentAudioObject() {
		if (isEnginePlaying()) { // Pause
			playButtonCausesPause();
		} else { // Resume
			playButtonCausesResume();
		}
	}

	@Override
	public void playAudioObjectInPlayListPositionOfVisiblePlayList(final int position) {
		playAudioObjectFromPlayList(position, playListHandler.getVisiblePlayList());
	}

	@Override
	public void playAudioObjectInPlayListPositionOfActivePlayList(final int position) {
		playAudioObjectFromPlayList(position, playListHandler.getActivePlayList());
	}

	private void playAudioObjectFromPlayList(final int position, final IPlayList playList) {
		IAudioObject audioObject = playList.get(position);
		if (audioObject != null) {
			if (isEnginePlaying() || paused) {
				stopCurrentAudioObject(false);
			}

			// We need to update current object and active play list first
			if (!playListHandler.getActivePlayList().equals(playList)) {
				// Only change active play list if audio object is from visible playlist and not from active play list
				playListHandler.setVisiblePlayListActive();
			}
			playListEventListeners.selectedAudioObjectHasChanged(audioObject);

			playAudioObject(audioObject);
		}
	}

	/**
	 * 
	 */
	private void playButtonCausesResume() {
		if (!playListHandler.getActivePlayList().isEmpty()) {
			paused = false;
			resumePlayback();
			callPlaybackStateListeners(PlaybackState.RESUMING);
			Logger.info("Resumed paused song");
		}
	}

	/**
	 * 
	 */
	private void playButtonCausesPause() {
		try {
			paused = true;
			pausePlayback();
			Logger.info("Pause");
			callPlaybackStateListeners(PlaybackState.PAUSED);
		} catch (Exception e) {
			handlePlayerEngineError(e);
			stopCurrentAudioObject(false);
		}
	}

	/**
	 * Stops playing current audio object
	 * 
	 * @param userStopped
	 *            <code>true</code> if user has stopped playback
	 * 
	 */
	@Override
	public final void stopCurrentAudioObject(final boolean userStopped) {
		try {
			boolean activateFadeAway = userStopped && statePlayer.isUseFadeAway() && !paused;
			if (paused) {
				paused = false;
			}

			stopPlayback(userStopped, activateFadeAway);
			callPlaybackStateListeners(PlaybackState.STOPPED);
			Logger.info("Stop");
		} catch (Exception e) {
			handlePlayerEngineError(e);
		}

	}

	/**
	 * Starts playing previous audio object from play list
	 */
	@Override
	public final void playPreviousAudioObject() {
		// Call listeners to notify playback was interrupted
		callPlaybackStateListeners(PlaybackState.PLAY_INTERRUPTED);

		switchPlaybackTo(playListHandler.getPreviousAudioObject(), true, false);
	}

	/**
	 * Starts playing next audio object from play list
	 * 
	 * @param audioObjectFinished
	 *            <code>true</code> if this method is called because current
	 *            audio object has finished, <code>false</code> if this method
	 *            is called because user has pressed the "NEXT" button
	 * 
	 */
	@Override
	public final void playNextAudioObject(final boolean audioObjectFinished) {
		if (!audioObjectFinished) {
			// Call listeners to notify playback was interrupted
			callPlaybackStateListeners(PlaybackState.PLAY_INTERRUPTED);
		}
		switchPlaybackTo(playListHandler.getNextAudioObject(), true, audioObjectFinished);
	}

	/**
	 * This method must be called by engine when audio object finishes its
	 * playback
	 * 
	 * @param ok
	 *            <code>true</code> if playback finishes ok, <code>false</code>
	 *            otherwise
	 * @param messages
	 *            Messages when playback finishes with error
	 */
	@Override
	public final void currentAudioObjectFinished() {
		// Call listeners to notify playback finished
		callPlaybackStateListeners(PlaybackState.PLAY_FINISHED);
		Logger.info("Playback finished");

		// Move to the next audio object
		playNextAudioObject(true);
	}

	@Override
	public void currentAudioObjectFinishedWithError(final Exception exception) {
		// Call listeners to notify playback finished
		callPlaybackStateListeners(PlaybackState.PLAY_FINISHED);
		Logger.info(StringUtils.getString("Playback finished with errors: ", exception.toString()));
		handlePlayerEngineError(exception);
	}

	@Override
	public final void seekCurrentAudioObject(final long milliseconds, final int perCent) {
		// If paused first resume and then seek
		if (paused) {
			paused = false;
			if (!playListHandler.getActivePlayList().isEmpty()) {
				callPlaybackStateListeners(PlaybackState.RESUMING);
				Logger.info("Resumed paused song");
			}
		}

		seekTo(milliseconds, perCent);
	}

	/**
	 * Lower volume
	 */
	@Override
	public final void volumeDown() {
		volumeController.setVolume(statePlayer.getVolume() - 5);
	}

	/**
	 * Raise volume
	 */
	@Override
	public final void volumeUp() {
		volumeController.setVolume(statePlayer.getVolume() + 5);
	}

	/**
	 * Called when a exception is thrown related with player engine
	 * 
	 * @param e
	 *            The exception thrown
	 */
	@Override
	public final void handlePlayerEngineError(final Exception e) {
		Logger.error(StringUtils.getString("Player Error: ", e));
		Logger.error(e);
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				dialogFactory.newDialog(IExceptionDialog.class).showExceptionDialog(e);
			}
		});
		// Force a stop to finish all player engine processes
		stopCurrentAudioObject(false);
	}

	/**
	 * Checks if mute is enabled (<code>true</code>) or not (<code>false</code>)
	 * 
	 * @return <code>true</code> if mute is enabled
	 */
	protected final boolean isMuteEnabled() {
		return statePlayer.isMuteEnabled();
	}

	/**
	 * Sets the length of the current audio object
	 * 
	 * @param currentLength
	 *            The length of the current audio object in milliseconds (ms)
	 * 
	 */
	protected final void setCurrentAudioObjectLength(final long currentLength) {
		this.currentAudioObjectLength = currentLength;
		// Update sliders with max length
		playerHandler.setAudioObjectLength(currentLength);
	}

	/**
	 * Sets the time played for the current audio object as playback advances
	 * 
	 * @param playedTime
	 *            played time in milliseconds (ms)
	 */
	@Override
	public final void setCurrentAudioObjectPlayedTime(final long playedTime) {
		long actualPlayedTime = playedTime;
		this.currentAudioObjectPlayedTime = actualPlayedTime;
		playerHandler.setCurrentAudioObjectTimePlayed(actualPlayedTime, currentAudioObjectLength);

		// Conditions to submit an object:
		// - Not submitted before
		// - Length of object is not 0
		// - Played time is greater than half the length of the object OR is greater than 4 minutes
		if (submissionState == SubmissionState.NOT_SUBMITTED &&
				currentAudioObjectLength > 0 &&
				(actualPlayedTime > currentAudioObjectLength / 2 || actualPlayedTime >= 240000)) {
			submissionState = SubmissionState.PENDING;
		}
	}

	@Override
	public final boolean isEnginePaused() {
		return paused;
	}

	/**
	 * Notifies the handler that the radio or podcast feed entry has started
	 * playing (MPlayer bug workaround).
	 */
	protected final void notifyRadioOrPodcastFeedEntryStarted() {
		Logger.debug("radio or podcast feed entry has started playing");
		// send volume command
		setVolume(statePlayer.getVolume());
		// if muted set mute again
		if (statePlayer.isMuteEnabled()) {
			applyMuteState(true);
		}
		Logger.debug("MPlayer bug (ignoring muting and volume settings after streamed file starts playing) workaround applied");
	}

	/**
	 * Starts playing an audio object.
	 * 
	 * @param audioObject
	 */
	private void playAudioObject(final IAudioObject audioObject) {
		Logger.info(StringUtils.getString("Started play of file ", audioObject));

		if (statePlayer.isCacheFilesBeforePlaying()) {

			PlayAudioObjectRunnable r = new PlayAudioObjectRunnable(this, audioObject, frame, temporalDiskStorage);

			// NOTE: This thread was initially a SwingWorker but as number of concurrent SwingWorkers is limited if context panel SwingWorker were working
			// this one was blocked so use a Thread to avoid blocking no matters if a SwingWorker is active or not
			playAudioObjectThread = new Thread(r);
			playAudioObjectThread.start();
		} else {
			playAudioObjectAfterCache(audioObject, audioObject);
		}
	}

	/**
	 * Caches audio object
	 * @param audioObject
	 * @param temporalDiskStorage
	 * @return
	 */
	IAudioObject cacheAudioObject(final IAudioObject audioObject, final ITemporalDiskStorage temporalDiskStorage) {
		IAudioObject audioObjectToPlay = null;

		// If cacheFilesBeforePlaying is true and audio object is an audio file, copy it to temp folder
		// and start player process from this copied file
		if (audioObject instanceof ILocalAudioObject && statePlayer.isCacheFilesBeforePlaying()) {

			Logger.debug("Start caching file: ", audioObject.getUrl());

			// Remove previous cached file
			if (lastFileCached != null) {
				temporalDiskStorage.removeFile(lastFileCached);
			}

			File tempFile = temporalDiskStorage.addFile(((ILocalAudioObject) audioObject).getFile());
			if (tempFile != null) {
				audioObjectToPlay = localAudioObjectFactory.getLocalAudioObject(tempFile);
				lastFileCached = tempFile;
			} else {
				lastFileCached = null;
			}

			Logger.debug("End caching file: ", audioObject.getUrl());
		} else {
			audioObjectToPlay = audioObject;
		}

		return audioObjectToPlay;
	}

	/**
	 * Calls to play an audio object after being cached if necessary
	 * @param audioObjectToPlay Cached audio object
	 * @param audioObject real audio object
	 */
	void playAudioObjectAfterCache(final IAudioObject audioObjectToPlay, final IAudioObject audioObject) {
		// This audio object has not been listened yet
		submissionState = SubmissionState.NOT_SUBMITTED;

		// If we are playing a podcast, mark entry as listened
		if (audioObject instanceof IPodcastFeedEntry) {
			((IPodcastFeedEntry) audioObject).setListened(true);
			// Update pod cast navigator
			navigationHandler.refreshView(podcastNavigationView);
		}

		// Send Now Playing info to Last.fm
		if (audioObject instanceof ILocalAudioObject) {
			webServicesHandler.submitNowPlayingInfo(audioObject);
		}

		this.audioObject = audioObject;

		// Add audio object to playback history
		playListHandler.addToPlaybackHistory(audioObject);

		startPlayback(audioObjectToPlay, audioObject);

		// Setting volume and balance
		if (statePlayer.isMuteEnabled()) {
			applyMuteState(true);
		} else {
			setVolume(statePlayer.getVolume());
		}

		// Call listeners
		callPlaybackStateListeners(PlaybackState.PLAYING);
	}

	/**
	 * Starts playing audio object passed as parameter
	 * 
	 * @param audioObjectToSwitchTo
	 *            The audio object to play
	 * @param resetIfNoObject
	 *            If <code>true</code> reset play list position if audio object
	 *            is null
	 * @param autoNext
	 *            <code>true</code> if this method is called because of previous
	 *            audio object has finished playing or <code>false</code> if
	 *            it's called because of an action of the user (previous, next,
	 *            ...)
	 */
	private void switchPlaybackTo(final IAudioObject audioObjectToSwitchTo, final boolean resetIfNoObject, final boolean autoNext) {
		if (audioObjectToSwitchTo != null) {
			try {
				if (isEnginePlaying() || isEnginePaused() || autoNext) {
					stopCurrentAudioObject(false);
					if (!isEnginePaused()) {
						playAudioObject(audioObjectToSwitchTo);
					}
				}
			} catch (Exception e) {
				handlePlayerEngineError(e);
				stopCurrentAudioObject(false);
			}
		} else {
			if (resetIfNoObject) {
				stopCurrentAudioObject(false);
				playListHandler.resetCurrentPlayList();
				playListEventListeners.selectedAudioObjectHasChanged(playListHandler.getCurrentAudioObjectFromCurrentPlayList());
			}
		}
	}

	/**
	 * Restarts playback (stops and starts playback, seeking to previous position)
	 * This is normally used after apply a change in configuration (normalization, equalization)
	 */
	protected void restartPlayback() {
		long position = getCurrentAudioObjectPlayedTime();
		// Disable playback state listeners while restarting playback
		setCallToPlaybackStateListenersDisabled(true);

		finishPlayer();
		playCurrentAudioObject();
		float perCent = (((float) playerHandler.getCurrentAudioObjectPlayedTime()) / playerHandler.getCurrentAudioObjectLength()) * 100.0f;
		seekCurrentAudioObject(position, Math.round(perCent));

		// Enable playback state listeners again
		setCallToPlaybackStateListenersDisabled(false);
	}

	@Override
	public IAudioObject getAudioObject() {
		return audioObject;
	}

	@Override
	public long getCurrentAudioObjectPlayedTime() {
		return currentAudioObjectPlayedTime;
	}

	@Override
	public long getCurrentAudioObjectLength() {
		return currentAudioObjectLength;
	}

	/**
	 * @return the callToPlaybackStateListenersDisabled
	 */
	private boolean isCallToPlaybackStateListenersDisabled() {
		return callToPlaybackStateListenersDisabled;
	}

	/**
	 * @param callToPlaybackStateListenersDisabled the callToPlaybackStateListenersDisabled to set
	 */
	private void setCallToPlaybackStateListenersDisabled(
			final boolean callToPlaybackStateListenersDisabled) {
		this.callToPlaybackStateListenersDisabled = callToPlaybackStateListenersDisabled;
	}

	/**
	 * Interrupts playing thread
	 */
	@Override
	public final void interruptPlayAudioObjectThread() {
		if (playAudioObjectThread != null) {
			playAudioObjectThread.interrupt();
		}
	}

	/**
	 * @return the submissionState
	 */
	@Override
	public SubmissionState getSubmissionState() {
		return submissionState;
	}

	/**
	 * @param submissionState the submissionState to set
	 */
	@Override
	public void setSubmissionState(final SubmissionState submissionState) {
		this.submissionState = submissionState;
	}

	/**
	 * @return
	 */
	protected IFrame getFrame() {
		return frame;
	}

	/**
	 * @return the osManager
	 */
	protected IOSManager getOsManager() {
		return osManager;
	}

	/**
	 * @param frame
	 */
	public void setFrame(final IFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param temporalDiskStorage
	 */
	public void setTemporalDiskStorage(final ITemporalDiskStorage temporalDiskStorage) {
		this.temporalDiskStorage = temporalDiskStorage;
	}

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
	 * @param playListEventListeners
	 */
	public void setPlayListEventListeners(final PlayListEventListeners playListEventListeners) {
		this.playListEventListeners = playListEventListeners;
	}

}
