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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RepositoryStructure<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3802494024307120854L;

	private Map<String, T> structure;
	
	RepositoryStructure() {
		this.structure = new HashMap<String, T>();
	}
	
	int count() {
		return this.structure.size();
	}
	
	Collection<T> getAll() {
		return this.structure.values();
	}
	
	T get(String key) {
		return this.structure.get(key);
	}
	
	void put(String key, T value) {
		this.structure.put(key, value);
	}
	
	void remove(String key) {
		this.structure.remove(key);
	}
	
	Map<String, T> getStructure() {
		return this.structure;
	}
}