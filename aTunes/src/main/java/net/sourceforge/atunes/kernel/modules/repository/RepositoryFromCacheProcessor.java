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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.dialogs.RepositorySelectionInfoDialog;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Processes repository read from cache
 * 
 * @author alex
 * 
 */
public class RepositoryFromCacheProcessor {

	private IStateRepository stateRepository;

	private IDialogFactory dialogFactory;

	private IRepositoryHandler repositoryHandler;

	private IBeanFactory beanFactory;

	private String userResponse;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	void setRepository(IRepository repository) {
		if (repository == null) {
			GuiUtils.callInEventDispatchThreadAndWait(new Runnable() {
				@Override
				public void run() {
					reloadExistingRepository();
				}
			});
		} else {
			if (repository.exists()) {
				beanFactory.getBean(RepositoryLoadedActions.class)
						.repositoryReadCompleted(repository);
			} else {
				askUser(repository);
			}
		}
	}

	/**
	 * If any repository was loaded previously, try to reload folders
	 */
	private void reloadExistingRepository() {
		List<String> lastRepositoryFolders = this.stateRepository
				.getLastRepositoryFolders();
		if (lastRepositoryFolders != null && !lastRepositoryFolders.isEmpty()) {
			List<File> foldersToRead = new ArrayList<File>();
			for (String f : lastRepositoryFolders) {
				foldersToRead.add(new File(f));
			}
			this.dialogFactory.newDialog(IMessageDialog.class).showMessage(
					I18nUtils.getString("RELOAD_REPOSITORY_MESSAGE"));
			beanFactory.getBean(RepositoryReader.class)
					.newRepositoryWithFoldersReloaded(foldersToRead);
		} else {
			RepositorySelectionInfoDialog dialog = this.dialogFactory
					.newDialog(RepositorySelectionInfoDialog.class);
			dialog.setVisible(true);
			if (dialog.userAccepted()) {
				this.repositoryHandler.addFolderToRepository();
			}
		}
	}

	private void askUser(final IRepository rep) {
		do {
			GuiUtils.callInEventDispatchThreadAndWait(new Runnable() {
				@Override
				public void run() {
					userResponse = showDialog(rep);
				}
			});
			if (userResponse != null
					&& userResponse.equals(I18nUtils.getString("EXIT"))) {
				this.beanFactory.getBean(IKernel.class).finish();
			} else if (userResponse == null
					|| userResponse.equals(I18nUtils.getString("IGNORE"))) {
				beanFactory.getBean(RepositoryLoadedActions.class)
						.repositoryReadCompleted(rep);
			} else if (userResponse != null
					&& userResponse.equals(I18nUtils
							.getString("SELECT_REPOSITORY"))) {
				repositoryHandler.addFolderToRepository();
			}
		} while (userResponse != null
				&& userResponse.equals(I18nUtils.getString("RETRY")));
	}

	/**
	 * Ask user when repository is not available
	 * 
	 * @param rep
	 */
	private String showDialog(final IRepository rep) {
		return (String) this.dialogFactory.newDialog(IMessageDialog.class)
				.showMessage(
						StringUtils.getString(
								I18nUtils.getString("REPOSITORY_NOT_FOUND"),
								": ", rep.getRepositoryFolders().get(0)),
						I18nUtils.getString("REPOSITORY_NOT_FOUND"),
						JOptionPane.WARNING_MESSAGE,
						new String[] { I18nUtils.getString("RETRY"),
								I18nUtils.getString("IGNORE"),
								I18nUtils.getString("SELECT_REPOSITORY"),
								I18nUtils.getString("EXIT") });
	}
}