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

package net.sourceforge.atunes;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IExceptionDialog;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Captures unknown exceptions
 * 
 * @author alex
 * 
 */
public final class UncaughtExceptionHandler implements
		Thread.UncaughtExceptionHandler {

	private IDialogFactory dialogFactory;

	private List<KnownException> knownExceptions;

	/**
	 * @param knownExceptions
	 */
	public void setKnownExceptions(final List<KnownException> knownExceptions) {
		this.knownExceptions = knownExceptions;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		Logger.error(StringUtils.getString("Thread: ", t.getName()));
		Logger.error(e);

		if (!isKnownException(e)) {
			if (e instanceof InvocationTargetException
					&& ((InvocationTargetException) e).getCause() != null) {
				uncaughtException(t, ((InvocationTargetException) e).getCause());
			}

			GuiUtils.callInEventDispatchThread(new Runnable() {
				@Override
				public void run() {
					UncaughtExceptionHandler.this.dialogFactory.newDialog(
							IExceptionDialog.class).showExceptionDialog(e);
				}
			});
		}
	}

	private boolean isKnownException(final Throwable e) {
		if (!CollectionUtils.isEmpty(this.knownExceptions)) {
			for (KnownException exception : this.knownExceptions) {
				if (isKnownException(e, exception)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param e
	 * @param exception
	 * @return
	 */
	private boolean isKnownException(final Throwable e,
			final KnownException exception) {
		return e.getClass().getName()
				.equalsIgnoreCase(exception.getExceptionClass())
				&& e.getMessage().equalsIgnoreCase(exception.getMessage());
	}
}