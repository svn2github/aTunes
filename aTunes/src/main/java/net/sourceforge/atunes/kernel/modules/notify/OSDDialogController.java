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

package net.sourceforge.atunes.kernel.modules.notify;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.WindowFader;
import net.sourceforge.atunes.gui.views.dialogs.OSDDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectGenericImageFactory;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.ImageSize;

final class OSDDialogController extends AbstractSimpleController<OSDDialog> {

	private WindowFader windowFader;
	private Timer timer;
	private final IAudioObjectGenericImageFactory audioObjectGenericImageFactory;
	private final ILookAndFeelManager lookAndFeelManager;
	private final IAudioObjectImageLocator audioObjectImageLocator;

	private final IStateUI stateUI;

	private final IUnknownObjectChecker unknownObjectChecker;

	/**
	 * Instantiates a new oSD dialog controller.
	 * @param dialogControlled
	 * @param stateUI
	 * @param audioObjectGenericImageFactory
	 * @param lookAndFeelManager
	 * @param audioObjectImageLocator
	 * @param unknownObjectChecker
	 */
	OSDDialogController(final OSDDialog dialogControlled, final IStateUI stateUI, final IAudioObjectGenericImageFactory audioObjectGenericImageFactory, final ILookAndFeelManager lookAndFeelManager, final IAudioObjectImageLocator audioObjectImageLocator, final IUnknownObjectChecker unknownObjectChecker) {
		super(dialogControlled);
		this.stateUI = stateUI;
		addBindings();
		this.audioObjectGenericImageFactory = audioObjectGenericImageFactory;
		this.windowFader = new WindowFader(dialogControlled, 50);
		this.lookAndFeelManager = lookAndFeelManager;
		this.audioObjectImageLocator = audioObjectImageLocator;
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	public void addBindings() {
		MouseListener listener = new OSDDialogMouseListener(getComponentControlled(), this);
		getComponentControlled().addMouseListener(listener);
	}

	/**
	 * Stops animation and disposes osd dialog
	 */
	void stopAnimation() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
		windowFader.clear();
	}

	/**
	 * Show osd.
	 * 
	 * @param audioObject
	 *            the audio object
	 */
	void showOSD(final IAudioObject audioObject) {
		if (audioObject == null) {
			return;
		}

		// If the OSD is already visible stop animation
		stopAnimation();

		windowFader = new WindowFader(getComponentControlled(), 50);

		int x = 0;
		if (stateUI.getOsdHorizontalAlignment() == SwingConstants.CENTER) {
			x = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - stateUI.getOsdWidth() / 2;
		} else if (stateUI.getOsdHorizontalAlignment() == SwingConstants.LEFT) {
			x = 50;
		} else {
			x = Toolkit.getDefaultToolkit().getScreenSize().width - stateUI.getOsdWidth() - 50;
		}
		x = Math.max(x, 0);

		int y = 0;
		if (stateUI.getOsdVerticalAlignment() == SwingConstants.CENTER) {
			y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getComponentControlled().getHeight() / 2;
		} else if (stateUI.getOsdVerticalAlignment() == SwingConstants.TOP) {
			y = 20;
		} else {
			y = Toolkit.getDefaultToolkit().getScreenSize().height - 20 - getComponentControlled().getHeight();
		}

		Point location = new Point(x, y);

		ImageIcon i = audioObjectImageLocator.getImage(audioObject, ImageSize.SIZE_MAX);
		if (i == null) {
			i = audioObjectGenericImageFactory.getGenericImage(audioObject, GenericImageSize.MEDIUM).getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls());
		}
		getComponentControlled().setImage(i);
		getComponentControlled().setLine1(audioObject.getTitleOrFileName());
		getComponentControlled().setLine2(audioObject.getAlbum(unknownObjectChecker));
		getComponentControlled().setLine3(audioObject.getArtist(unknownObjectChecker));

		getComponentControlled().setLocation(location);
		GuiUtils.setWindowOpacity(getComponentControlled(), 0);
		getComponentControlled().setRoundedBorders(true);

		getComponentControlled().setVisible(true);
		// see bug 1864517
		getComponentControlled().repaint();

		windowFader.fadeIn();
		timer = new Timer(stateUI.getOsdDuration() * 1000, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				windowFader.fadeOut();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
}
