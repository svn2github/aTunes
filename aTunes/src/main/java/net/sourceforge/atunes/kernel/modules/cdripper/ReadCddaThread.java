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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

final class ReadCddaThread extends Thread {
	
	private final Cdda2wav cdda2wav;

    private String id = null;

    private String title;
	
    private String album;
    
    private String artist;
    
    private int tracks;
    
    private String totalDuration;

    private List<String> durations = new ArrayList<String>();
    
    private List<String> titles = new ArrayList<String>();
    
    private List<String> artists = new ArrayList<String>();
    
    private List<String> composers = new ArrayList<String>();
    
    private boolean cddbError = false;
    
	public ReadCddaThread(Cdda2wav cdda2wav) {
		super();
		this.cdda2wav = cdda2wav;
	}
	
	@Override
	public void run() {
	    BufferedReader stdInput = null;
	    try {
	        stdInput = new BufferedReader(new InputStreamReader(cdda2wav.getProcess().getErrorStream(), "ISO8859_1"));
	        Logger.info("Trying to read cdda2wav stream");

	        String s = null;
	        
	        //	read the output from the command
	        while ((s = stdInput.readLine()) != null) {
	            Logger.info(StringUtils.getString("While loop: ", s));
	            analyzeOutputLine(s);
	        }

	        // Write data to variable cd
	        if (!cddbError) {
	            artist = artist != null ? artist.replace("\\", "\'") : null;
	            album = album != null ? album.replace("\\", "\'") : null;
	        }

	        artist = artist != null ? artist.trim() : null;
	        album = album != null ? album.trim() : null;

	        fillCdInfo();
	    } catch (IOException e) {
	        Logger.error(e);
	    } finally {
	        ClosingUtils.close(stdInput);
	    }
	}
	
	/**
	 * Fills cd info object
	 */
	private void fillCdInfo() {
		CDInfo info = cdda2wav.getCdInfo();
        info.setTracks(tracks);
        info.setDurations(durations);
        info.setDuration(totalDuration);
        info.setId(id);
        if (album != null && !album.equals("")) {
        	info.setAlbum(album);
        }

        if (artist != null && !artist.equals("")) {
        	info.setArtist(artist);
        }

        info.setTitles(titles);
        info.setArtists(artists);
        info.setComposers(composers);
	}
	
	/**
	 * Analyzes each line of process output
	 * @param outputLine
	 */
	private void analyzeOutputLine(String outputLine) {
        // Used to detect if a CD is present. Don't know if this gets returned on
        // all drive, so may not work as expected. But if it does, this means a CD
        // is present and we don't have to wait until the disk info is read out. This 
        // means we can give the "no CD" error much faster!
        if (outputLine.contains("bytes buffer memory requested")) {
            cdda2wav.setCdLoaded(true);
        }

        // Sometimes cdda2wav gives an error message 
        // when a data CD is inserted
        if (outputLine.contains("This disk has no audio tracks")) {
        	cdda2wav.setCdLoaded(false);
        }

        if (outputLine.matches("Tracks:.*")) {
        	cdda2wav.setCdLoaded(true);
            tracks = Integer.parseInt(outputLine.substring(outputLine.indexOf(':') + 1, outputLine.indexOf(' ')));
            totalDuration = outputLine.substring(outputLine.indexOf(' ') + 1);
        } else if (outputLine.matches("CDDB discid.*")) {
        	cdda2wav.setCdLoaded(true);
            id = outputLine.substring(outputLine.indexOf('0'));
        }

        // We need to check if there was an connection error to avoid an exception
        // In this case aTunes will behave as previously (no Artist/Album info).
        if (outputLine.matches(".cddb connect failed.*")) {
            cddbError = true;
        }

        // Get album info (only if connection to cddb could be established)
        if (outputLine.matches("Album title:.*") && !cddbError) {
        	cdda2wav.setCdLoaded(true);
        	readAlbumAndArtist(outputLine);
        }

        // Get track info (track number, title name) - Data tracks get ignored.
        else if (outputLine.matches("T..:.*") && !outputLine.matches("......................data.*") && !cddbError) {
        	cdda2wav.setCdLoaded(true);
        	readTrackInfo(outputLine);
        }

        // If there is a data track do remove one track.
        if (outputLine.matches("......................data.*")) {
            tracks = tracks - 1;
        }
	}
	
	/**
	 * Gets album and artist from output line of process
	 * @param lineOutput
	 */
	private void readAlbumAndArtist(String lineOutput) {
        String line = lineOutput.trim();

        // Avoid '' sequences
        line = line.replaceAll("''", "' '");

        StringTokenizer albumInfoTokenizer = new java.util.StringTokenizer(line, "'");
        // The first part is not interesting, we look for the second token, thus the next line
        if (albumInfoTokenizer.hasMoreTokens()) {
            albumInfoTokenizer.nextToken();
        }
        if (albumInfoTokenizer.hasMoreTokens()) {
            album = albumInfoTokenizer.nextToken();
        }
        String token = null;
        if (albumInfoTokenizer.hasMoreElements()) {
            token = albumInfoTokenizer.nextToken();
        }
        // Album names can contain "'" so check if there is something left
        StringBuilder sb = new StringBuilder(album);
        while (albumInfoTokenizer.hasMoreElements() && token != null && !token.matches(" from ")) {
            sb.append(token);
            token = albumInfoTokenizer.nextToken();
        }
        album = sb.toString();
        if (albumInfoTokenizer.hasMoreTokens()) {
            artist = albumInfoTokenizer.nextToken();
        }
        // Artist names can contain "'" so check if there is something left
        sb = new StringBuilder(artist);
        while (albumInfoTokenizer.hasMoreTokens()) {
            token = albumInfoTokenizer.nextToken();
            sb.append(token);
        }
        artist = sb.toString();
	}
	
	/**
	 * Reads tracks information
	 * @param s
	 */
	private void readTrackInfo(String s) {
        String duration = s.substring(12, 18).trim();
        durations.add(duration);
        // If connection to cddb could be established do
        if (!cddbError) {
            String line = s.trim();

            // Avoid '' sequences
            line = line.replaceAll("''", "' '");

            StringTokenizer titleInfoTokenizer = new StringTokenizer(line, "'");
            // The first part is not interesting, we look for the second token, thus the next line
            if (titleInfoTokenizer.hasMoreElements()) {
                titleInfoTokenizer.nextToken();
            }
            if (titleInfoTokenizer.hasMoreElements()) {
                title = titleInfoTokenizer.nextToken();
            }
            String token = null;
            if (titleInfoTokenizer.hasMoreTokens()) {
                token = titleInfoTokenizer.nextToken();
            }
            // Album names can contain "'" so check if there is something left. Also, add "\" for Windows
            StringBuilder sb = new StringBuilder(title);
            while (titleInfoTokenizer.hasMoreTokens() && token != null && !token.matches(" from ")) {
                sb.append(token);
                token = titleInfoTokenizer.nextToken();
            }
            title = sb.toString();

            title = title != null ? title.trim() : null;

            title = title != null && !title.equals("") ? title.replace("\\", "\'") : null;

            if (title != null) {
                titles.add(title);
                //TODO add Song artist
                artists.add("");
                composers.add("");
            }
        }
	}
}