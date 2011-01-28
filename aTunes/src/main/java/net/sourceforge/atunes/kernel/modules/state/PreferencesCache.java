/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.state;

import java.io.IOException;
import java.security.GeneralSecurityException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sourceforge.atunes.misc.AbstractCache;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

class PreferencesCache extends AbstractCache {

    private Logger logger;

    protected PreferencesCache() {
        super(PreferencesCache.class.getResource("/settings/ehcache-preferences.xml"));
    }

    /**
     * Clears the cache.
     * 
     * @return If an Exception occurred during clearing
     */
    public synchronized boolean clearCache() {
        boolean exception = false;
        try {
            getCache().removeAll();
        } catch (Exception e) {
            getLogger().info(LogCategories.FILE_DELETE, "Could not delete all files from preferences cache");
            exception = true;
        }
        return exception;
    }

    /**
     * Retrieves a Preference value from cache.
     * 
     * @param preference
     * @param defaultValue
     * @return Value
     */
    public synchronized Object retrievePreference(Preferences preferenceId, Object defaultValue) {
        Element element = getCache().get(preferenceId.toString());
        if (element == null) {
            return defaultValue;
        } else {
        	Preference preference = (Preference) element.getValue();
            return preference != null ? preference.getValue() : null;
        }
    }

    /**
     * Stores a Preference at cache.
     * @param preferenceId
     * @param value
     */
    public synchronized void storePreference(Preferences preferenceId, Object value) {
        if (preferenceId == null) {
            return;
        }

        Element element = new Element(preferenceId.toString(), value != null ? new Preference(value) : value);
        getCache().put(element);
        getCache().flush();
        getLogger().debug(LogCategories.PREFERENCES, "Stored Preference: ", preferenceId, " Value: ", value != null ? value.toString() : null);
    }
    
    /**
     * Retrieves a Password Preference from cache
     * @param preferenceId
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public synchronized String retrievePasswordPreference(Preferences preferenceId) {
    	Element element = getCache().get(preferenceId.toString());
    	if (element == null) {
    		return null;
    	} else {
    		PasswordPreference preferences = (PasswordPreference) element.getValue();
    		return preferences.getPassword();
    	}
    }
    
    /**
     * Stores a Password Preference at cache.
     * @param preferenceId
     * @param value
     */
    public synchronized void storePasswordPreference(Preferences preferenceId, String value) {
        if (preferenceId == null) {
            return;
        }

        Element element = new Element(preferenceId.toString(), value != null ? new PasswordPreference(value) : null);
        getCache().put(element);
        getCache().flush();
        getLogger().debug(LogCategories.PREFERENCES, "Stored Password Preference: ", preferenceId, " Value: ", value.toString());
    }

    private Cache getCache() {
        return getCache("preferences");
    }

    public void shutdown() {
        getCache().dispose();
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }
}
