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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngineCapability;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.FileNameUtils;

/**
 * Engine for MPlayer
 */
public class MPlayerEngine extends AbstractPlayerEngine {



    /** Argument to not display more information than needed. */
    private static final String QUIET = "-quiet";
    /** Argument to control mplayer through commands. */
    private static final String SLAVE = "-slave";
    /** Argument to pass mplayer a play list. */
    private static final String PLAYLIST = "-playlist";
    /** Arguments to filter audio output. */
    private static final String AUDIO_FILTER = "-af";
    private static final String VOLUME_NORM = "volnorm";
    private static final String KARAOKE = "karaoke";
    private static final String EQUALIZER = "equalizer=";
    private static final String CACHE = "-cache";
    private static final String CACHE_SIZE = "500";
    private static final String CACHE_MIN = "-cache-min";
    private static final String CACHE_FILL_SIZE_IN_PERCENT = "7.0";
    private static final String PREFER_IPV4 = "-prefer-ipv4";

    private Process process;
    private MPlayerCommandWriter commandWriter;
    private AbstractMPlayerOutputReader mPlayerOutputReader;
    private MPlayerErrorReader mPlayerErrorReader;
    private MPlayerPositionThread mPlayerPositionThread;
    /** The current fade away process running */
    private FadeAwayRunnable currentFadeAwayRunnable = null;

    public MPlayerEngine() {
        commandWriter = new MPlayerCommandWriter(null);
    }

    @Override
    public boolean isEngineAvailable() {
    	InputStream in = null;
    	try {
    		Process p = new ProcessBuilder(OsManager.getPlayerEngineCommand(this)).start();
    		in = p.getInputStream();
    		byte[] buffer = new byte[4096];
    		while (in.read(buffer) >= 0) {
    			;
    		}

    		int code = p.waitFor();
    		if (code != 0) {
    			return false;
    		}
    		return true;
    	} catch (Exception e) {
    		getLogger().error(LogCategories.PLAYER, e);
    		return false;
    	} finally {
    		ClosingUtils.close(in);
    	}
    }

    @Override
    protected void pausePlayback() {
        commandWriter.sendPauseCommand();
    }

    @Override
    protected void resumePlayback() {
        commandWriter.sendResumeCommand();
        /*
         * Mplayer volume problem workaround If player was paused, set volume
         * again as it could be changed when paused
         */
        if (!isMuteEnabled()) {
            commandWriter.sendVolumeCommand(ApplicationState.getInstance().getVolume());
        }
        /*
         * End Mplayer volume problem workaround
         */
    }

    @Override
    protected void startPlayback(AudioObject audioObjectToPlay, AudioObject audioObject) {
        try {
            // If there is a fade away working, stop it inmediately
            if (currentFadeAwayRunnable != null) {
                currentFadeAwayRunnable.finish();
            }

            // Send stop command in order to try to avoid two mplayer
            // instaces are running at the same time
            commandWriter.sendStopCommand();

            // Start the play process
            process = getProcess(audioObjectToPlay);
            commandWriter = new MPlayerCommandWriter(process);
            // Output reader needs original audio object, specially when cacheFilesBeforePlaying is true, as
            // statistics must be applied over original audio object, not the cached one
            mPlayerOutputReader = AbstractMPlayerOutputReader.newInstance(this, process, audioObject);
            mPlayerErrorReader = new MPlayerErrorReader(this, process, mPlayerOutputReader, audioObjectToPlay);
            mPlayerOutputReader.start();
            mPlayerErrorReader.start();
            mPlayerPositionThread = new MPlayerPositionThread(this);
            mPlayerPositionThread.start();
            commandWriter.sendGetDurationCommand();

            setVolume(ApplicationState.getInstance().getVolume());

        } catch (Exception e) {
            stopCurrentAudioObject(false);
            handlePlayerEngineError(e);
        }
    }

    @Override
    protected void stopPlayback(boolean userStopped, boolean useFadeAway) {
        if (!isEnginePlaying()) {
            return;
        }

        if (useFadeAway && !isPaused()) {
            // If there is a fade away process working don't create
            // a new process
            if (currentFadeAwayRunnable != null) {
                return;
            }
            mPlayerErrorReader.interrupt();
            currentFadeAwayRunnable = new FadeAwayRunnable(process, ApplicationState.getInstance().getVolume(), this);
            Thread t = new Thread(currentFadeAwayRunnable);
            // Start fade away process
            t.start();
        } else {
            commandWriter.sendStopCommand();
            // If there is a fade away process stop inmediately
            if (currentFadeAwayRunnable != null) {
                currentFadeAwayRunnable.finish();
            } else {
                // This is already called from fade away runnable when finishing
                process = null;
                mPlayerErrorReader = null;
                mPlayerOutputReader = null;
                commandWriter.finishProcess();
                mPlayerPositionThread.interrupt();
                mPlayerPositionThread = null;
            }
            setCurrentAudioObjectPlayedTime(0);
        }
    }

    /**
     * Called when finished fade away
     */
    protected void finishedFadeAway() {
        // NOTE: interrupting output reader means closing standard input
        // of mplayer process, so process is finished
        mPlayerOutputReader.interrupt();
        process = null;
        mPlayerErrorReader = null;
        mPlayerOutputReader = null;
        mPlayerPositionThread.interrupt();
        mPlayerPositionThread = null;
        commandWriter.finishProcess();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setTime(0);
            }
        });
        // No fade away process working
        currentFadeAwayRunnable = null;
    }

    protected void setTime(int time) {
        super.setCurrentAudioObjectPlayedTime(time);
    }

    @Override
    protected void seekTo(long position) {
        commandWriter.sendSeekCommand(position);
    }

    @Override
    public void finishPlayer() {
        stopCurrentAudioObject(false);
        getLogger().info(LogCategories.PLAYER, "Stopping player");
    }

    @Override
    public boolean isEnginePlaying() {
        return process != null && !isPaused();
    }

    @Override
    public void applyMuteState(boolean mute) {
        commandWriter.sendMuteCommand();

        // volume must be applied again because of the volume bug
        setVolume(ApplicationState.getInstance().getVolume());

        // MPlayer bug: paused, demute, muted -> starts playing
        if (isPaused() && !mute) {
            commandWriter.sendPauseCommand();
            getLogger().debug(LogCategories.PLAYER, "MPlayer bug (paused, demute, muted -> starts playing) workaround applied");
        }
    }

    @Override
    public void setVolume(int volume) {
        // MPlayer bug: paused, volume change -> starts playing
        // If is paused, volume will be sent to mplayer when user resumes playback
        if (!isPaused() && !isMuteEnabled()) {
            commandWriter.sendVolumeCommand(volume);
        }
    }

    /**
     * Returns a mplayer process to play an audiofile.
     * 
     * @param audioObject
     *            audio object which should be played
     * 
     * @return mplayer process
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private Process getProcess(AudioObject audioObject) throws IOException {

        ProcessBuilder pb = new ProcessBuilder();
        List<String> command = new ArrayList<String>();

        boolean isRemoteAudio = !(audioObject instanceof LocalAudioObject || (audioObject instanceof PodcastFeedEntry
                && ApplicationState.getInstance().isUseDownloadedPodcastFeedEntries() && ((PodcastFeedEntry) audioObject).isDownloaded()));

        command.add(OsManager.getPlayerEngineCommand(this));
        command.addAll(OsManager.getPlayerEngineParameters(this));
        command.add(QUIET);
        command.add(SLAVE);

        // PREFER_IPV4 for radios and podcast entries
        if (isRemoteAudio) {
            command.add(PREFER_IPV4);
        }

        // If a radio has a playlist url add playlist command
        if (audioObject instanceof Radio && ((Radio) audioObject).hasPlaylistUrl(Proxy.getProxy(ApplicationState.getInstance().getProxy()))) {
            command.add(PLAYLIST);
        }

        // url
        boolean shortPathName = ApplicationState.getInstance().isUseShortPathNames() && OsManager.usesShortPathNames() && audioObject instanceof AudioFile;
        String url;
        if (audioObject instanceof PodcastFeedEntry && !isRemoteAudio) {
            url = PodcastFeedHandler.getInstance().getDownloadPath((PodcastFeedEntry) audioObject);
            if (ApplicationState.getInstance().isUseShortPathNames() && OsManager.usesShortPathNames()) {
                shortPathName = true;
            }
        } else {
            url = audioObject.getUrl();
        }
        if (shortPathName) {
            String shortPath = FileNameUtils.getShortPathNameW(url);
            command.add(shortPath != null && !shortPath.isEmpty() ? shortPath : url);
        } else {
            if (url.startsWith("http")) {

                // proxy
                StringBuilder proxy = new StringBuilder();
                ProxyBean proxyBean = ApplicationState.getInstance().getProxy();
                if (proxyBean != null && proxyBean.getType().equals(ProxyBean.HTTP_PROXY)) {
                    //String user = proxyBean.getUser();
                    //String password = proxyBean.getPassword();
                    String proxyUrl = proxyBean.getUrl();
                    int port = proxyBean.getPort();

                    proxy.append("http_proxy://");
                    //proxy.append(!user.isEmpty() ? user : "");
                    //proxy.append(!user.isEmpty() && !password.isEmpty() ? ":" : "");
                    //proxy.append(!user.isEmpty() && !password.isEmpty() ? password : "");
                    //proxy.append(!user.isEmpty() ? "@" : "");
                    proxy.append(proxyUrl);
                    proxy.append(port != 0 ? ":" : "");
                    proxy.append(port != 0 ? port : "");
                    proxy.append("/");
                }
                proxy.append(url);

                command.add(proxy.toString());
            } else {
                command.add(url);
            }
        }

        // Cache for radios and podcast entries
        if (isRemoteAudio) {
            command.add(CACHE);
            command.add(CACHE_SIZE);
            command.add(CACHE_MIN);
            command.add(CACHE_FILL_SIZE_IN_PERCENT);
        }

        boolean isKaraokeEnabled = ApplicationState.getInstance().isKaraoke();
        //float[] eualizer = getEqualizer();
        if ((audioObject instanceof LocalAudioObject && getEqualizer().getEqualizerValues() != null) || isSoundNormalizationEnabled() || isKaraokeEnabled) {
            command.add(AUDIO_FILTER);
        }

        // normalization
        if (isSoundNormalizationEnabled()) {
            command.add(VOLUME_NORM);
        }

        // Build equalizer command. Mplayer uses 10 bands
        if (audioObject instanceof LocalAudioObject && getEqualizer().getEqualizerValues() != null && !isKaraokeEnabled) {
            float[] equalizer = getEqualizer().getEqualizerValues();
            command.add(EQUALIZER + equalizer[0] + ":" + equalizer[1] + ":" + equalizer[2] + ":" + equalizer[3] + ":" + equalizer[4] + ":" + equalizer[5] + ":" + equalizer[6]
                    + ":" + equalizer[7] + ":" + equalizer[8] + ":" + equalizer[9]);
        }

        // karaoke
        if (isKaraokeEnabled) {
            command.add(KARAOKE);
        }

        getLogger().debug(LogCategories.PLAYER, (Object[]) command.toArray(new String[command.size()]));
        return pb.command(command).start();
    }

    /**
     * Gets the command writer.
     * 
     * @return the command writer
     */
    MPlayerCommandWriter getCommandWriter() {
        return commandWriter;
    }

    @Override
    public boolean supportsCapability(PlayerEngineCapability capability) {
        return EnumSet.of(PlayerEngineCapability.EQUALIZER, PlayerEngineCapability.EQUALIZER_CHANGE, PlayerEngineCapability.STREAMING, PlayerEngineCapability.PROXY,
                PlayerEngineCapability.KARAOKE, PlayerEngineCapability.NORMALIZATION).contains(capability);
    }

    @Override
    public void applyEqualization(float[] values) {
        // Mplayer does not support equalizer change
        // workaround:
        // we can stop/restart the current playing song to
        // its last position when users applied the EQ
        // test to avoid non desired startup of player
        if (isEnginePlaying()) {
            restartPlayback();
        }
    }

    @Override
    public void applyNormalization() {
        // same comment as above, but for normalization mode
        if (isEnginePlaying()) {
            restartPlayback();
        }
    }

    @Override
    public float[] transformEqualizerValues(float[] values) {
        return values;
    }

    protected void setCurrentLength(long currentDuration) {
        super.setCurrentAudioObjectLength(currentDuration);
    }

    /**
     * Checks if playback is paused.
     * 
     * @return true, if is paused
     */
    protected boolean isPlaybackPaused() {
        return isPaused();
    }

    protected void notifyRadioOrPodcastFeedEntry() {
        super.notifyRadioOrPodcastFeedEntryStarted();
    }

    protected boolean isMute() {
        return super.isMuteEnabled();
    }

    @Override
    protected String getEngineName() {
        return "MPlayer";
    }

    @Override
    protected void killPlayer() {
        commandWriter.sendStopCommand();
    }
}
