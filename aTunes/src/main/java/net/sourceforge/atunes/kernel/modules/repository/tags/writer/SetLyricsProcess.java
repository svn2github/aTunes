/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.kernel.modules.repository.tags.writer;

import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;

/**
 * The Class SetLyricsProcess.
 */
public class SetLyricsProcess extends ChangeTagProcess {

    /**
     * Writes lyrics to files if any is found. Context handler is used for
     * finding lyrics
     * 
     * @param files
     *            to which the lyrics should be written
     */
    SetLyricsProcess(List<AudioFile> files) {
        super(files);
    }

    @Override
    protected void changeTag(AudioFile file) throws IOException {
        // Check if no lyrics is present and we have enough info for a querry
        if (file.getLyrics().isEmpty() && !file.getArtist().isEmpty() && !file.getTitle().isEmpty()) {
            Lyrics lyrics = null;// TODO: CONTEXT: Change thisContextHandler.getInstance().getLyrics(file.getArtist(), file.getTitle());
            String lyricsString = lyrics != null ? lyrics.getLyrics().trim() : "";
            if (!lyricsString.isEmpty()) {
                TagModifier.setLyrics(file, lyricsString);
            }
        }
    }
}
