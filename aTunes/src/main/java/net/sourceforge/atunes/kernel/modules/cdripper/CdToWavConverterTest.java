/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.cdripper;

import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IOSManager;

final class CdToWavConverterTest {
	
	private CdToWavConverterTest() {}

	/**
	 * Test cd to wav converter given the os manager
	 * @param applicationArguments
	 * @param osManager
	 * @return
	 */
	static boolean testTools(IApplicationArguments applicationArguments, IOSManager osManager) {
		if (applicationArguments.isSimulateCD()) {
			return true;
		}
		if (osManager.isMacOsX()) {
			return Cdparanoia.pTestTool(osManager);
		} else if (osManager.isWindows()) {
			return Cdda2wav.pTestTool(osManager);
		} else if (osManager.isLinux()) {
			if (Cdda2wav.pTestTool(osManager)) {
				return true;
			}
			return Cdparanoia.pTestTool(osManager);
		} else if (osManager.isSolaris()) {
			if (Cdda2wav.pTestTool(osManager)) {
				return true;
			}
			return Cdparanoia.pTestTool(osManager);
		}
		return false;
	}
}
