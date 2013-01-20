/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IProgressSlider;

/**
 * Listener for seek operation in progress slider
 * 
 * @author alex
 * 
 */
public class ProgressBarSeekListener extends MouseAdapter {

	private final IProgressSlider progressBar;

	private final IPlayerHandler playerHandler;

	/**
	 * @param progressBar
	 * @param playerHandler
	 */
	public ProgressBarSeekListener(final IProgressSlider progressBar,
			final IPlayerHandler playerHandler) {
		super();
		this.progressBar = progressBar;
		this.playerHandler = playerHandler;
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if (this.progressBar.isEnabled()) {

			// Progress bar width is greater than real slider width so calculate
			// value assuming 5 pixels in both left and right of track
			long temp = (long) this.progressBar.getMaximum() * (e.getX() - 5);
			int value = (int) (temp / (this.progressBar.getProgressBarWidth() - 10));

			float perCent = (((float) value) / this.playerHandler
					.getCurrentAudioObjectLength()) * 100.0f;

			// Force new value to avoid jump to next major tick
			this.progressBar.setValue(value);
			this.playerHandler.seekCurrentAudioObject(value,
					Math.round(perCent));
		}
	}
}
