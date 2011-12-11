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

package net.sourceforge.atunes.api;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IRepositoryHandler;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public final class RepositoryApi {

    private RepositoryApi() {

    }

    /**
     * Returns artist information for the given artist name or <code>null</code>
     * if there is no information about
     * 
     * @param artistName
     * @return
     */
    public static IArtist getArtist(String artistName) {
        return Context.getBean(IRepositoryHandler.class).getArtist(artistName);
    }
}
