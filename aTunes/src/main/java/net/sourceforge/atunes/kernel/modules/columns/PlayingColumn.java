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

package net.sourceforge.atunes.kernel.modules.columns;

import net.sourceforge.atunes.model.IAudioObject;

/**
 * Column to show play / pause / stop icon
 * @author alex
 *
 */
public class PlayingColumn extends AbstractColumn<Integer> {

    
    private static final long serialVersionUID = -5604736587749167043L;

    /**
     * Constructor
     */
    public PlayingColumn() {
        super("PLAYING");
        setResizable(false);
        setWidth(16);
        setVisible(true);
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
        return 0;
    }
    
    @Override
    protected int descendingCompare(IAudioObject ao1, IAudioObject ao2) {
    	return 0;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    @Override
    public Integer getValueFor(IAudioObject audioObject) {
        return null;
    }

    @Override
    public String getHeaderText() {
        return "";
    }
}
