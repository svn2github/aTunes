/*
 * aTunes 3.1.0
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

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.LocalAudioObjectFormat;
import net.sourceforge.atunes.utils.Logger;

class AudioFileMPlayerOutputReader extends AbstractMPlayerOutputReader {

	private final ILocalAudioObject audioFile;

	private final boolean isMp3File;

	/**
	 * Instantiates a new audio file m player output reader.
	 * 
	 * @param engine
	 * @param process
	 * @param audioFile
	 * @param localAudioObjectValidator
	 */
	AudioFileMPlayerOutputReader(final MPlayerEngine engine, final Process process, final ILocalAudioObject audioFile, final ILocalAudioObjectValidator localAudioObjectValidator) {
		super(engine, process);
		this.audioFile = audioFile;
		// Check audio file type only once and use calculated value in read method
		this.isMp3File = localAudioObjectValidator.isOneOfTheseFormats(audioFile.getFile().getName(), LocalAudioObjectFormat.MP3);
	}

	@Override
	protected void init() {
	}

	@Override
	protected void read(final String line) {
		super.read(line);

		readAndApplyLength(audioFile, line, isMp3File);

		// MPlayer bug: Workaround (for audio files) for "mute bug" [1868482]
		if (getEngine().isMute() && getLength() > 0 && getLength() - getTime() < 2000) {
			Logger.debug("MPlayer 'mute bug' workaround applied");
			getEngine().currentAudioObjectFinished();
			interrupt();
		}
	}

}
