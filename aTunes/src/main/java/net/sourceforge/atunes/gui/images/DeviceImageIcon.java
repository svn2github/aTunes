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

package net.sourceforge.atunes.gui.images;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;

public class DeviceImageIcon {

	private static final int WIDTH = 18;
	private static final int HEIGHT = 18;
	
	private static ImageIcon icon;
	
	public static ImageIcon getIcon() {
		if (icon == null) {
			icon = getIcon(null);	
		}
		return icon;
	}
	
	public static ImageIcon getIcon(Paint color) {
		int marginX = 4;
		int marginY = 2;
		int arc = 4;

		int internalMarginX = 6;
		int internalMarginY = 4;
		
		int circleDiameter = HEIGHT / 4;
		
		BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(color != null ? color : LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintFor(null));
        
       	g.fill(new RoundRectangle2D.Float(marginX, marginY, WIDTH - 2 * marginX, HEIGHT - 2 * marginY, arc, arc));
       	g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
       	g.fill(new Rectangle(internalMarginX, internalMarginY, WIDTH - 2 * internalMarginX, HEIGHT / 2 - internalMarginY - 1));
       	g.fill(new Ellipse2D.Float(WIDTH / 2 - circleDiameter / 2, HEIGHT - internalMarginY - circleDiameter, circleDiameter, circleDiameter));
       	
        g.dispose();
        return new ImageIcon(bi);
	}
}
