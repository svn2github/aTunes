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

import net.sourceforge.atunes.model.IApplicationStateGenerator;
import net.sourceforge.atunes.model.IErrorReport;
import net.sourceforge.atunes.model.IErrorReportCreator;

/**
 * Creates error reports
 * 
 * @author alex
 * 
 */
public class ErrorReportCreator implements IErrorReportCreator {

    private IApplicationStateGenerator applicationStateGenerator;

    /**
     * @param applicationStateGenerator
     */
    public void setApplicationStateGenerator(
	    final IApplicationStateGenerator applicationStateGenerator) {
	this.applicationStateGenerator = applicationStateGenerator;
    }

    @Override
    public IErrorReport createReport(final String descriptionError,
	    final Throwable throwable) {
	IErrorReport result = new ErrorReport();
	result.setState(applicationStateGenerator.generateState());
	result.setThrowable(throwable);
	result.setErrorDescription(descriptionError);
	return result;
    }

}