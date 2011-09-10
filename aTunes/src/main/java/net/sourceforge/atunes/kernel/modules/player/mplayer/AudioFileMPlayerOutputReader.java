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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.data.Format;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.ILocalAudioObject;

class AudioFileMPlayerOutputReader extends AbstractMPlayerOutputReader {

    private ILocalAudioObject audioFile;

    private boolean isMp3File;

    /**
     * Instantiates a new audio file m player output reader.
     * 
     * @param engine
     *            the engine
     * @param process
     *            the process
     * @param audioFile
     *            the audio file
     */
    AudioFileMPlayerOutputReader(MPlayerEngine engine, Process process, ILocalAudioObject audioFile) {
        super(engine, process);
        this.audioFile = audioFile;
        // Check audio file type only once and use calculated value in read method
        this.isMp3File = AudioFile.isValidAudioFile(audioFile.getFile(), Format.MP3);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void read(String line) {
        super.read(line);

        readAndApplyLength(audioFile, line, isMp3File);

        // MPlayer bug: Workaround (for audio files) for "mute bug" [1868482] 
        if (getEngine().isMute() && getLength() > 0 && getLength() - getTime() < 2000) {
            Logger.debug("MPlayer 'mute bug' workaround applied");
            getEngine().currentAudioObjectFinished(true);
            interrupt();
        }
    }

}
