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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.List;

import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IAudioObject;

public class FullScreenController extends AbstractSimpleController<FullScreenWindow> {

	private FullScreenWindowFactory fullScreenWindowFactory;
	
	/**
	 * @param fullScreenWindowFactory
	 */
	public void setFullScreenWindowFactory(FullScreenWindowFactory fullScreenWindowFactory) {
		this.fullScreenWindowFactory = fullScreenWindowFactory;
	}
	
	/**
	 * Initializes controller
	 */
	public void initialize() {
		final FullScreenWindow window = fullScreenWindowFactory.getFullScreenWindow();
        setComponentControlled(window);
        
        KeyListener keyAdapter = new FullScreenKeyAdapter(window);
        
        window.addKeyListener(keyAdapter);
        window.getOptions().addKeyListener(keyAdapter);
        
        MouseListener clickListener = new FullScreenMouseListener(window);
        window.addMouseListener(clickListener);
        setClickListener(clickListener);
        
        MouseMotionListener moveListener = new FullScreenMouseMotionAdapter(window);
        window.addMouseMotionListener(moveListener);
        window.getCovers().addMouseMotionListener(moveListener);

        window.getSelectBackground().addActionListener(new SelectBackgroundActionListener(this));
        
        window.getRemoveBackground().addActionListener(new RemoveBackgroundActionListener(window, getState()));

        FullScreenShowMenuMouseAdapter optionsAdapter = new FullScreenShowMenuMouseAdapter(window.getOptions());
        window.getBackgroundPanel().addMouseListener(optionsAdapter);
        window.getCovers().addMouseListener(optionsAdapter);

        window.getExitFullScreen().addActionListener(new ExitFullScreenActionListener(window));

        setBackground();

	}

	private void setBackground() {
        File backgroundFile = null;
        if (getState().getFullScreenBackground() != null) {
            backgroundFile = new File(getState().getFullScreenBackground());
            if (!backgroundFile.exists()) {
                backgroundFile = null;
            }
        }
        getComponentControlled().setBackground(backgroundFile);
	}
	
	void setBackground(File file) {
		getComponentControlled().setBackground(file);
		getState().setFullScreenBackground(file.getAbsolutePath());
	}
	
	/**
	 * @param clickListener
	 */
	private void setClickListener(MouseListener clickListener) {
		getComponentControlled().getCovers().addMouseListener(clickListener);
		getComponentControlled().getOptions().addMouseListener(clickListener);
		getComponentControlled().getPreviousButton().addMouseListener(clickListener);
		getComponentControlled().getPlayButton().addMouseListener(clickListener);
		getComponentControlled().getNextButton().addMouseListener(clickListener);
	}

	/**
	 * Shows or hides full screen
	 */
	void toggleVisibility() {
		getComponentControlled().setVisible(!getComponentControlled().isVisible());
	}
	
	/**
	 * Sets the audio object.
	 * @param objects
	 */
	void setAudioObjects(List<IAudioObject> objects) {
		getComponentControlled().setAudioObjects(objects);
	}

	/**
	 * Sets the playing
	 * @param playing
	 */
	void setPlaying(boolean playing) {
		getComponentControlled().setPlaying(playing);
	}

	/**
	 * Returns true if full screen is visible
	 * @return
	 */
	boolean isVisible() {
		return getComponentControlled().isVisible();
	}
}
