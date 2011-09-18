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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Component;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 * Shows different types of error messages
 * @author alex
 *
 */
public class ErrorDialog implements IErrorDialog {

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IErrorDialog#showErrorDialog(net.sourceforge.atunes.model.IFrame, java.lang.String)
	 */
    @Override
	public void showErrorDialog(final IFrame frame, final String message) {
    	if (SwingUtilities.isEventDispatchThread()) {
    		JOptionPane.showMessageDialog(frame.getFrame(), message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
    	} else {
    		SwingUtilities.invokeLater(new Runnable() {
    			@Override
    			public void run() {
    				JOptionPane.showMessageDialog(frame.getFrame(), message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
    			}
    		});
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IErrorDialog#showErrorDialog(java.lang.String, java.awt.Component)
	 */
    @Override
	public void showErrorDialog(String message, Component parent) {
        JOptionPane.showMessageDialog(parent, message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IErrorDialog#showExceptionDialog(java.lang.String, java.lang.Exception)
	 */
    @Override
	public void showExceptionDialog(String message, Exception t) {
        showExceptionDialog(I18nUtils.getString("ERROR"), message, t);
    }

    /**
     * Shows a exception report dialog
     * 
     * @param title
     * @param message
     * @param t
     */
    private void showExceptionDialog(String title, String message, Exception t) {
        JXErrorPane pane = new JXErrorPane();
        StringBuilder sb = new StringBuilder();
        sb.append(t.getClass().getName());
        sb.append(": ");
        sb.append(t.getMessage());
        sb.append("<br/>");
        sb.append("<br/>");
        for (StackTraceElement s : t.getStackTrace()) {
            sb.append(s.toString());
            sb.append("<br/>");
        }
        pane.setErrorInfo(new ErrorInfo(title, message, sb.toString(), null, t, Level.SEVERE, null));
        JXErrorPane.showDialog(null, pane);
    }
}