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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationView;

import org.junit.Test;

public class ExpandTreesActionTest {

    @Test
    public void test() {
	ExpandTreesAction sut = new ExpandTreesAction();
	INavigationHandler navigationHandler = mock(INavigationHandler.class);
	INavigationView nv2 = mock(INavigationView.class);
	INavigationView nv1 = mock(INavigationView.class);
	when(nv1.getTree()).thenReturn(mock(INavigationTree.class));
	when(nv2.getTree()).thenReturn(mock(INavigationTree.class));
	List<INavigationView> nvs = new ArrayList<INavigationView>();
	nvs.add(nv1);
	nvs.add(nv2);
	when(navigationHandler.getNavigationViews()).thenReturn(nvs);
	sut.setNavigationHandler(navigationHandler);

	sut.executeAction();

	verify(nv1).getTree();
	verify(nv2).getTree();
    }
}
