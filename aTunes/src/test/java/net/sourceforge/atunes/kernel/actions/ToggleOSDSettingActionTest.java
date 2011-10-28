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

package net.sourceforge.atunes.kernel.actions;

import javax.swing.AbstractAction;

import net.sourceforge.atunes.model.AbstractStateMock;
import net.sourceforge.atunes.model.IState;

import org.junit.Assert;
import org.junit.Test;

public class ToggleOSDSettingActionTest {

	@Test
	public void test() {
		ToggleOSDSettingAction sut = new ToggleOSDSettingAction();
		IState state = new AbstractStateMock() {
			private boolean showOSD;
			
			public boolean isShowOSD() { 
				return showOSD;
			}
			
			@Override
			public void setShowOSD(boolean showOSD) {
				this.showOSD = showOSD;
			}
		};
		sut.setState(state);

		state.setShowOSD(false);
		sut.putValue(AbstractAction.SELECTED_KEY, true);
		sut.executeAction();
		Assert.assertTrue(state.isShowOSD());
		sut.putValue(AbstractAction.SELECTED_KEY, false);
		sut.executeAction();
		Assert.assertFalse(state.isShowOSD());
	}
}
