/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.controllers.model;

import java.awt.Component;

import net.sourceforge.atunes.misc.log.LogCategories;

public abstract class SimpleController<T extends Component> extends Controller {

    private T componentControlled;

    /**
     * Instantiates a new controller.
     */
    public SimpleController(T componentControlled) {
        this.componentControlled = componentControlled;
        getLogger().debug(LogCategories.CONTROLLER, "Creating ", this.getClass().getSimpleName());
    }

    public T getComponentControlled() {
        return componentControlled;
    }

}
