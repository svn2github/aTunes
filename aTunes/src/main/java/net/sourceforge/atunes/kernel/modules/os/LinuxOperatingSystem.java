/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.os;

import java.io.File;

import net.sourceforge.atunes.kernel.OperatingSystem;
import net.sourceforge.atunes.kernel.modules.hotkeys.AbstractHotkeys;
import net.sourceforge.atunes.kernel.modules.hotkeys.X11Hotkeys;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.utils.StringUtils;

public class LinuxOperatingSystem extends OperatingSystemAdapter {

	/**
     * Name of the Linux command
     */
    private static final String COMMAND_LINUX = "aTunes.sh";
    
    /**
     * Command to be executed on Linux systems to launch mplayer. Mplayer should
     * be in $PATH
     */
    private static String MPLAYER_LINUX_COMMAND = "mplayer";

    public LinuxOperatingSystem(OperatingSystem systemType) {
		super(systemType);
	}

	@Override
	public String getAppDataFolder() {
		return StringUtils.getString(getUserHome(), "/.atunes");
	}

	@Override
	public String getLaunchCommand() {
		return new File(StringUtils.getString("./", COMMAND_LINUX)).getAbsolutePath();
	}
	
	@Override
	public Class<? extends AbstractHotkeys> getHotkeysListener() {
		return X11Hotkeys.class;
	}

	@Override
	public boolean isPlayerEngineSupported(AbstractPlayerEngine engine) {
		return true; // all supported
	}
	
	@Override
	public String getPlayerEngineCommand(AbstractPlayerEngine engine) {
		return engine instanceof MPlayerEngine ? MPLAYER_LINUX_COMMAND : null;
	}
}