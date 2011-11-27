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

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.dialogs.RepositorySelectionInfoDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IMessageDialogFactory;
import net.sourceforge.atunes.model.IMultiFolderSelectionDialog;
import net.sourceforge.atunes.model.IMultiFolderSelectionDialogFactory;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;
import net.sourceforge.atunes.model.IRepositoryProgressDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.Repository;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

public class RepositoryReader implements IRepositoryLoaderListener {

	// Used to retrieve covers and show in progress dialog
	private String lastArtistRead;
	
	private String lastAlbumRead;
	
	private SwingWorker<Image, Void> coverWorker;
	
    private int filesLoaded;

    private boolean backgroundLoad = false;

    private Repository repositoryRetrievedFromCache = null;

    private IMessageDialogFactory messageDialogFactory;
    
    private IMultiFolderSelectionDialogFactory multiFolderSelectionDialogFactory;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private IRepositoryProgressDialog progressDialog;
    
    private RepositoryLoader currentLoader;
    
	private Repository repository;
	
	private IFrame frame;
	
	private IState state;
	
	private RepositoryHandler repositoryHandler;

	private INavigationHandler navigationHandler;
	
	private IWebServicesHandler webServicesHandler;
	
	private RepositoryActionsHelper repositoryActions;
	
	private ShowRepositoryDataHelper showRepositoryDataHelper;
	
	/**
	 * @param showRepositoryDataHelper
	 */
	public void setShowRepositoryDataHelper(ShowRepositoryDataHelper showRepositoryDataHelper) {
		this.showRepositoryDataHelper = showRepositoryDataHelper;
	}
	
	/**
	 * @param repositoryActions
	 */
	public void setRepositoryActions(RepositoryActionsHelper repositoryActions) {
		this.repositoryActions = repositoryActions;
	}
	
	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
	
	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
	
	/**
	 * @param state
	 */
	public void setState(IState state) {
		this.state = state;
	}
	
	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(RepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
	
	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}

    /**
     * @param multiFolderSelectionDialogFactory
     */
    public void setMultiFolderSelectionDialogFactory(IMultiFolderSelectionDialogFactory multiFolderSelectionDialogFactory) {
		this.multiFolderSelectionDialogFactory = multiFolderSelectionDialogFactory;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * @param messageDialogFactory
     */
    public void setMessageDialogFactory(IMessageDialogFactory messageDialogFactory) {
		this.messageDialogFactory = messageDialogFactory;
	}
    
    /**
     * @param repositoryRetrievedFromCache
     */
    void setRepositoryRetrievedFromCache(Repository repositoryRetrievedFromCache) {
		this.repositoryRetrievedFromCache = repositoryRetrievedFromCache;
	}

    /**
     * Sets the repository from the one read from cache
     * @param askUser
     */
    void applyRepositoryFromCache() {
        Repository rep = repositoryRetrievedFromCache;
        if (rep != null && rep.exists()) {
        	applyExistingRepository(rep);
        }
        if (repository == null) {
    		applyRepository();
    	}
    }

	void testRepositoryRetrievedFromCache() {
    	IRepository rep = repositoryRetrievedFromCache;
    	if (rep == null) {
    		reloadExistingRepository();
    	}
	}
	
    /**
     * Sets the repository.
     * @param askUser
     */
    private void applyRepository() {
        Repository rep = repositoryRetrievedFromCache;
        // Try to read repository cache. If fails or not exists, should be selected again
        if (rep != null) {
        	if (!rep.exists()) {
        		askUserForRepository(rep);
        		if (!rep.exists() && !selectRepository(true)) {
        			// select "old" repository if repository was not found and no new repository was selected
        			repository = rep;
        		} else if (rep.exists()) {
        			// repository exists
        			applyExistingRepository(rep);
        		}
        	} else {
        		// repository exists
        		applyExistingRepository(rep);
        	}
        }
    }

    /**
     * Test if repository exists and show message until repository exists or
     * user doesn't press "RETRY"
     * 
     * @param rep
     */
    private void askUserForRepository(final IRepository rep) {
        Object selection;
        do {
            selection = messageDialogFactory.getDialog().
            	showMessage(frame, StringUtils.getString(I18nUtils.getString("REPOSITORY_NOT_FOUND"), ": ", rep.getRepositoryFolders().get(0)),
                    I18nUtils.getString("REPOSITORY_NOT_FOUND"), JOptionPane.WARNING_MESSAGE,
                    new String[] { I18nUtils.getString("RETRY"), I18nUtils.getString("SELECT_REPOSITORY"), I18nUtils.getString("EXIT") });

            if (selection.equals(I18nUtils.getString("EXIT"))) {
                SwingUtilities.invokeLater(new ExitWhenRepositoryNotFoundRunnable());
            }
        } while (I18nUtils.getString("RETRY").equals(selection) && !rep.exists());
    }

    private void applyExistingRepository(Repository rep) {
        repository = rep;
        repositoryReadCompleted();
    }

    /**
     * If any repository was loaded previously, try to reload folders
     */
    private void reloadExistingRepository() {
        List<String> lastRepositoryFolders = state.getLastRepositoryFolders();
        if (lastRepositoryFolders != null && !lastRepositoryFolders.isEmpty()) {
            List<File> foldersToRead = new ArrayList<File>();
            for (String f : lastRepositoryFolders) {
                foldersToRead.add(new File(f));
            }
            messageDialogFactory.getDialog().showMessage(I18nUtils.getString("RELOAD_REPOSITORY_MESSAGE"), frame);
            retrieve(foldersToRead);
        } else {
        	RepositorySelectionInfoDialog dialog = new RepositorySelectionInfoDialog(frame.getFrame(), lookAndFeelManager);
        	dialog.setVisible(true);
        	if (dialog.userAccepted()) {
        		repositoryHandler.selectRepository();
        	}
        }
    }

    /**
     * Select repository.
     * 
     * @param repositoryNotFound
     *            the repository not found
     * 
     * @return true, if successful
     */
    boolean selectRepository(boolean repositoryNotFound) {
    	IMultiFolderSelectionDialog dialog = multiFolderSelectionDialogFactory.getDialog();
        dialog.setTitle(I18nUtils.getString("SELECT_REPOSITORY"));
        dialog.setText(I18nUtils.getString("SELECT_REPOSITORY_FOLDERS"));
        dialog.showDialog((repository != null && !repositoryNotFound) ? repository.getRepositoryFolders() : null);
        if (!dialog.isCancelled()) {
            List<File> folders = dialog.getSelectedFolders();
            if (!folders.isEmpty()) {
                this.retrieve(folders);
                return true;
            }
        }
        return false;
    }

	private boolean retrieve(List<File> folders) {
		repositoryActions.enableRepositoryActions(false);
        progressDialog = Context.getBean(IRepositoryProgressDialog.class);
        // Start with indeterminate dialog
        progressDialog.showProgressDialog();
        progressDialog.setProgressBarIndeterminate(true);
        frame.getProgressBar().setIndeterminate(true);
        filesLoaded = 0;
        try {
            if (folders == null || folders.isEmpty()) {
                repository = null;
                return false;
            }
            readRepository(folders);
            return true;
        } catch (Exception e) {
            repository = null;
            Logger.error(e);
            return false;
        }
    }
	
    /**
     * Read repository.
     * 
     * @param folders
     *            the folders
     */
    private void readRepository(List<File> folders) {
        backgroundLoad = false;
        Repository oldRepository = repository;
        repository = new Repository(folders, state);
        repositoryHandler.setRepository(repository);
        currentLoader = new RepositoryLoader(state, new RepositoryTransaction(repository, repositoryHandler), folders, oldRepository, repository, false);
        currentLoader.addRepositoryLoaderListener(this);
        currentLoader.start();
    }

    @Override
    public void notifyCurrentPath(final String dir) {
        if (progressDialog != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                	if (progressDialog != null) {
                		progressDialog.setCurrentFolder(dir);
                	}
                }
            });
        }
    }

    @Override
    public void notifyFileLoaded() {
        this.filesLoaded++;
        // Update GUI every 50 files
        if (this.filesLoaded % 50 == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.setProgressText(Integer.toString(filesLoaded));
                        progressDialog.setProgressBarValue(filesLoaded);
                    }
                    frame.getProgressBar().setValue(filesLoaded);
                }
            });
        }
    }

    @Override
    public void notifyFilesInRepository(int totalFiles) {
        // When total files has been calculated change to determinate progress bar
        if (progressDialog != null) {
            progressDialog.setProgressBarIndeterminate(false);
            progressDialog.setTotalFiles(totalFiles);
        }
        frame.getProgressBar().setIndeterminate(false);
        frame.getProgressBar().setMaximum(totalFiles);
    }

    @Override
    public void notifyFinishRead(RepositoryLoader loader) {
        if (progressDialog != null) {
            progressDialog.setButtonsEnabled(false);
            progressDialog.setCurrentTask(I18nUtils.getString("STORING_REPOSITORY_INFORMATION"));
            progressDialog.setProgressText("");
            progressDialog.setCurrentFolder("");
        }

        // Save folders: if repository config is lost application can reload data without asking user to select folders again
        List<String> repositoryFolders = new ArrayList<String>();
        for (File folder : repository.getRepositoryFolders()) {
            repositoryFolders.add(folder.getAbsolutePath());
        }
        state.setLastRepositoryFolders(repositoryFolders);

        if (backgroundLoad) {
        	frame.hideProgressBar();
        }

        repositoryReadCompleted();
    }

    @Override
    public void notifyFinishRefresh(RepositoryLoader loader) {
    	repositoryActions.enableRepositoryActions(true);

        frame.hideProgressBar();
        showRepositoryDataHelper.showRepositoryAudioFileNumber(repository.getFiles().size(), repository.getTotalSizeInBytes(), repository.getTotalDurationInSeconds());
        navigationHandler.repositoryReloaded();
        Logger.info("Repository refresh done");

        currentLoader = null;
    }

    @Override
    public void notifyRemainingTime(final long millis) {
        if (progressDialog != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                	if (progressDialog != null) {
                		progressDialog.setRemainingTime(StringUtils.getString(I18nUtils.getString("REMAINING_TIME"), ":   ", StringUtils.milliseconds2String(millis)));
                	}
                }
            });
        }
    }

    @Override
    public void notifyReadProgress() {
        try {
        	// Use invoke and wait in this case to avoid concurrent problems while reading repository and showing nodes in navigator tree (ConcurrentModificationException)
			SwingUtilities.invokeAndWait(new Runnable() {
			    @Override
			    public void run() {
			    	navigationHandler.repositoryReloaded();
			    	showRepositoryDataHelper.showRepositoryAudioFileNumber(repository.getFiles().size(), repository.getTotalSizeInBytes(), repository.getTotalDurationInSeconds());
			    }
			});
		} catch (InterruptedException e) {
			Logger.error(e);
		} catch (InvocationTargetException e) {
			Logger.error(e);
		}
    }

    /**
     * Refresh.
     */
    void refresh() {
        Logger.info("Refreshing repository");
        filesLoaded = 0;
        Repository oldRepository = repository;
        repository = new Repository(oldRepository.getRepositoryFolders(), state);
        repositoryHandler.setRepository(repository);
        currentLoader = new RepositoryLoader(state, new RepositoryTransaction(repository, repositoryHandler), oldRepository.getRepositoryFolders(), oldRepository, repository, true);
        currentLoader.addRepositoryLoaderListener(this);
        currentLoader.start();
    }
    
	@Override
	public void notifyCurrentAlbum(final String artist, final String album) {
		if (progressDialog != null && progressDialog.isVisible()) {
			if (lastArtistRead == null || lastAlbumRead == null || !lastArtistRead.equals(artist) || !lastAlbumRead.equals(album)) {
				lastArtistRead = artist;
				lastAlbumRead = album;
				if (coverWorker == null || coverWorker.isDone()) {
					// Try to find cover and set in progress dialog
					coverWorker = new SwingWorker<Image, Void>() {
						@Override
						protected Image doInBackground() throws Exception {
							return webServicesHandler.getAlbumImage(artist, album);
						}

						@Override
						protected void done() {
							super.done();
							if (progressDialog != null) {
								try {
									progressDialog.setImage(get());
								} catch (InterruptedException e) {
									progressDialog.setImage(null);
								} catch (ExecutionException e) {
									progressDialog.setImage(null);
								}
							}
						}
					};
					coverWorker.execute();
				}
			}
		}
	}

	public void doInBackground() {
        if (currentLoader != null) {
            backgroundLoad = true;
            currentLoader.setPriority(Thread.MIN_PRIORITY);
            if (progressDialog != null) {
                progressDialog.hideProgressDialog();
            }
            frame.showProgressBar(false, StringUtils.getString(I18nUtils.getString("LOADING"), "..."));
            frame.getProgressBar().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    backgroundLoad = false;
                    currentLoader.setPriority(Thread.MAX_PRIORITY);
                    frame.hideProgressBar();
                    if (progressDialog != null) {
                        progressDialog.showProgressDialog();
                    }
                    frame.getProgressBar().removeMouseListener(this);
                };
            });
        }

    }

	void notifyCancel() {
        currentLoader.interruptLoad();
        repository = currentLoader.getOldRepository();
        repositoryReadCompleted();
    }
	
    /**
     * Notify finish repository read.
     */
    private void repositoryReadCompleted() {
    	repositoryHandler.setRepository(repository);
    	repositoryActions.enableRepositoryActions(true);
        if (progressDialog != null) {
            progressDialog.hideProgressDialog();
            progressDialog = null;
        }
        navigationHandler.repositoryReloaded();
        showRepositoryDataHelper.showRepositoryAudioFileNumber(repository.getFiles().size(), repository.getTotalSizeInBytes(), repository != null ? repository.getTotalDurationInSeconds() : 0);

        currentLoader = null;
    }

    /**
     * Returns <code>true</code>if there is a loader reading or refreshing
     * repository
     * 
     * @return
     */
    protected boolean isWorking() {
        return currentLoader != null;
    }


}