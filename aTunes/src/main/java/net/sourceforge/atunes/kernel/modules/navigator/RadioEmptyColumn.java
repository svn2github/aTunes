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

package net.sourceforge.atunes.kernel.modules.navigator;

import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.model.IAudioObject;

final class RadioEmptyColumn extends AbstractColumn {
    /**
	 * 
	 */
    private static final long serialVersionUID = 3613237620716484881L;

    RadioEmptyColumn(String name, Class<?> columnClass) {
        super(name, columnClass);
    }

    @Override
    protected int ascendingCompare(IAudioObject o1, IAudioObject o2) {
        return 0;
    }

    @Override
    public Object getValueFor(IAudioObject audioObject) {
        return Property.NO_PROPERTIES;
    }
}