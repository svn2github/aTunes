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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.IProxy;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.NetworkUtils;

/**
 * The podcast feed entry downloader downloads podcast feed entries from the
 * internet to a local file.
 */
public class PodcastFeedEntryDownloader extends SwingWorker<Boolean, Void> {

    private IPodcastFeedEntry podcastFeedEntry;
    /*
     * Additional Bean properties
     */
    private volatile long totalBytes;
    private volatile long byteProgress;
    private volatile boolean failed;
    
    private IProxy proxy;
    
    private ITable navigationTable;
    
    private IPodcastFeedHandler podcastFeedHandler;

    /**
     * Instantiates a new podcast feed entry downloader.
     * 
     * @param podcastFeedEntry
     * @param proxy
     * @param navigationTable
     * @param podcastFeedHandler
     */
    public PodcastFeedEntryDownloader(IPodcastFeedEntry podcastFeedEntry, IProxy proxy, ITable navigationTable, IPodcastFeedHandler podcastFeedHandler) {
        this.podcastFeedEntry = podcastFeedEntry;
        this.proxy = proxy;
        this.navigationTable = navigationTable;
        this.podcastFeedHandler = podcastFeedHandler;
    }

    @Override
    protected Boolean doInBackground() throws Exception {

        Logger.info("Downloading PodcastEntry: " + podcastFeedEntry.getUrl());

        OutputStream out = null;
        InputStream in = null;

        String podcastFeedEntryFileName = podcastFeedHandler.getDownloadPath(podcastFeedEntry);
        Logger.info("Downloading to: " + podcastFeedEntryFileName);
        File localFile = new File(podcastFeedEntryFileName);

        try {
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            URLConnection conn = NetworkUtils.getConnection(podcastFeedEntry.getUrl(), ExtendedProxy.getProxy(proxy));
            in = conn.getInputStream();
            setTotalBytes(conn.getContentLength());

            byte[] buffer = new byte[1024];
            int numRead;
            long numWritten = 0;
            int bytesRead = 0;

            while ((numRead = in.read(buffer)) != -1 && !isCancelled()) {
                bytesRead = numRead + bytesRead;
                out.write(buffer, 0, numRead);
                numWritten += numRead;
                setByteProgress(bytesRead);
                out.flush();
            }
            return !isCancelled();
        } catch (FileNotFoundException e) {
            Logger.info("file not found");
            setFailed(true);
            return false;
        } catch (IOException e) {
            Logger.info("Connection to " + podcastFeedEntry.getUrl() + " failed");
            setFailed(true);
            return false;
        } finally {
            ClosingUtils.close(out);
            ClosingUtils.close(in);
        }
    }

    /**
     * Sets the total bytes.
     * 
     * @param totalBytes
     *            the new total bytes
     */

    private void setTotalBytes(long totalBytes) {
        if (totalBytes == this.totalBytes) {
            return;
        }
        long oldTotalBytes = this.byteProgress;
        this.totalBytes = totalBytes;
        if (getPropertyChangeSupport().hasListeners("totalBytes")) {
            firePropertyChange("totalBytes", oldTotalBytes, this.totalBytes);
        }
    }

    /**
     * Sets the byte progress.
     * 
     * @param byteProgress
     *            the new byte progress
     */

    private void setByteProgress(long byteProgress) {
        if (byteProgress == this.byteProgress) {
            return;
        }
        long oldByteProgress = this.byteProgress;
        this.byteProgress = byteProgress;
        if (getPropertyChangeSupport().hasListeners("byteProgress")) {
            firePropertyChange("byteProgress", oldByteProgress, this.byteProgress);
        }
        // we want to update progress on byteProgress change
        int progress = (int) (((double) byteProgress / (double) totalBytes) * 100);
        setProgress(progress);
    }

    /**
     * Sets the failed.
     * 
     * @param failed
     *            the new failed
     */

    private void setFailed(boolean failed) {
        if (failed == this.failed) {
            return;
        }
        boolean oldFailed = this.failed;
        this.failed = failed;
        if (getPropertyChangeSupport().hasListeners("failed")) {
            firePropertyChange("failed", oldFailed, this.failed);
        }
    }

    /**
     * Gets the total bytes.
     * 
     * @return the total bytes
     */
    public long getTotalBytes() {
        return totalBytes;
    }

    @Override
    protected void done() {
        try {
            if (!isCancelled() && get()) {
                Logger.info("Download of " + podcastFeedEntry.getUrl() + " finished.");
                podcastFeedEntry.setDownloaded(true);
                navigationTable.repaint();
            }
        } catch (InterruptedException e) {
            Logger.error(e);
        } catch (ExecutionException e) {
            Logger.error(e);
        }
    }

    /**
     * Gets the podcast feed entry.
     * 
     * @return the podcast feed entry
     */
    public IPodcastFeedEntry getPodcastFeedEntry() {
        return podcastFeedEntry;
    }
}
