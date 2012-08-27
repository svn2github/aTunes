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

package net.sourceforge.atunes;

import java.util.Collection;

import net.sourceforge.atunes.model.IBeanFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BeanFactory implements IBeanFactory, ApplicationContextAware {
	
	private ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}
	
	@Override
	public <T> T getBean(Class<T> beanType) {
		return context.getBean(beanType);
	}
	
	@Override
	public <T> T getBean(String name, Class<T> clazz) {
		return context.getBean(name, clazz);
	}

	@Override
	public <T> Collection<T> getBeans(Class<T> beanType) {
		return context.getBeansOfType(beanType).values();
	}
}
