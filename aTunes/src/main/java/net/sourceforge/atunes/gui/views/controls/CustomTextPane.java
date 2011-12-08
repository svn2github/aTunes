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

package net.sourceforge.atunes.gui.views.controls;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * A CustomTextPane is a JTextPane using the fonts and colors configured for all
 * application
 * 
 * @author fleax
 * 
 */
public class CustomTextPane extends JTextPane implements ILookAndFeelChangeListener {

    private static final long serialVersionUID = -3601855261867415475L;

    private Integer alignment;

    /**
     * @param lookAndFeelManager
     */
    public CustomTextPane(ILookAndFeelManager lookAndFeelManager) {
    	this(null, lookAndFeelManager);
    }
    
    /**
     * @param alignment
     * @param lookAndFeelManager
     */
    public CustomTextPane(Integer alignment, ILookAndFeelManager lookAndFeelManager) {
        super();
        this.alignment = alignment;
        updateStyle(false);
        new EditionPopUpMenu(this);
        // Register look and feel change listener
        lookAndFeelManager.addLookAndFeelChangeListener(this);
    }
    
    @Override
    public void lookAndFeelChanged() {
    	updateStyle(true);
    }
    
    /**
     * Updates style of text pane
     * @param forceUpdate
     *            if <code>true</code> will update component to use new style
     */
    private void updateStyle(boolean forceUpdate) {
        MutableAttributeSet mainStyle = new SimpleAttributeSet();
       	StyleConstants.setAlignment(mainStyle, alignment != null ? alignment : GuiUtils.getComponentOrientationAsTextStyleConstant());
        StyleConstants.setFontFamily(mainStyle, UIManager.getFont("Label.font").getFamily());
        StyleConstants.setFontSize(mainStyle, UIManager.getFont("Label.font").getSize());
        StyleConstants.setForeground(mainStyle, UIManager.getColor("Label.foreground"));
        getStyledDocument().setParagraphAttributes(0, 0, mainStyle, true);
        if (forceUpdate) {
        	// Setting text again to use new style
        	setText(getText());
        }
    }
}
