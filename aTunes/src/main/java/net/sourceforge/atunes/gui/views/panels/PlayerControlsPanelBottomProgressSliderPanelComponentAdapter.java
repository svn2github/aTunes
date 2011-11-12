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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import net.sourceforge.atunes.gui.views.controls.playerControls.ProgressSlider;

/**
 * Controls size of player controls panel to move slider on top of controls or not
 * @author alex
 *
 */
final class PlayerControlsPanelBottomProgressSliderPanelComponentAdapter extends ComponentAdapter {
	
	private final ProgressSlider progressSlider;
	private final JPanel bottomProgressSliderPanel;
	private final JPanel topProgressSliderPanel;
	private Boolean showProgressOnTop = null;

	PlayerControlsPanelBottomProgressSliderPanelComponentAdapter(ProgressSlider progressSlider, JPanel bottomProgressSliderPanel, JPanel topProgressSliderPanel) {
		this.progressSlider = progressSlider;
		this.bottomProgressSliderPanel = bottomProgressSliderPanel;
		this.topProgressSliderPanel = topProgressSliderPanel;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		boolean showOnTop = bottomProgressSliderPanel.getWidth() < PlayerControlsPanel.PROGRESS_BAR_BOTTOM_MINIMUM_SIZE;

		if (showProgressOnTop == null || showProgressOnTop != showOnTop) {
			if (showOnTop) {
				bottomProgressSliderPanel.remove(progressSlider);
				progressSlider.setLayout();
				topProgressSliderPanel.add(progressSlider, BorderLayout.CENTER);
			} else {
				topProgressSliderPanel.remove(progressSlider);
				progressSlider.setLayout();
				bottomProgressSliderPanel.add(progressSlider, BorderLayout.CENTER);
			}
			showProgressOnTop = showOnTop;
		}
	}
}