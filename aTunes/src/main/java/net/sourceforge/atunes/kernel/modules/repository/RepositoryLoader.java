/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.repository.model.Folder;
import net.sourceforge.atunes.kernel.modules.repository.model.Genre;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.kernel.modules.statistics.StatisticsHandler;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.Timer;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Class for loading audiofiles into repository.
 */
public class RepositoryLoader extends Thread {

    private static Logger logger = new Logger();

    // Some attributes to speed up populate info process
    LoaderListener listener;
    private List<File> folders;
    private boolean refresh;
    private boolean interrupt;
    private Repository oldRepository;
    private Repository repository;
    int totalFilesToLoad;
    private int filesLoaded;
    private long startReadTime;
    private String fastRepositoryPath;
    private int fastFirstChar;

    /**
     * Instantiates a new repository loader.
     * 
     * @param folders
     *            the folders
     * @param refresh
     *            the refresh
     */
    public RepositoryLoader(List<File> folders, Repository oldRepository, Repository repository, boolean refresh) {
        this.refresh = refresh;
        this.folders = folders;
        this.oldRepository = oldRepository;
        this.repository = repository;
        setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Add files to repository.
     * 
     * @param rep
     *            Repository to which files should be added
     * @param files
     *            Files to add
     */
    protected static void addToRepository(Repository rep, List<File> files) {
    	// This operation changes repository, so mark it as dirty
    	rep.setDirty(true);
    	
        // Get folders where files are
        Set<File> folders = new HashSet<File>();
        for (File file : files) {
            folders.add(file.getParentFile());
        }

        for (File folder : folders) {
            String repositoryPath = getRepositoryFolderContaining(rep, folder).getAbsolutePath().replace('\\', '/');
            if (repositoryPath.endsWith("/")) {
                repositoryPath = repositoryPath.substring(0, repositoryPath.length() - 2);
            }
            int firstChar = repositoryPath.length() + 1;

            File[] list = folder.listFiles();
            List<File> pictures = new ArrayList<File>();
            if (list != null) {
                for (File element : list) {
                    if (element.getName().toUpperCase().endsWith("JPG")) {
                        pictures.add(element);
                    }
                }
            }

            Map<String, AudioFile> repositoryFiles = rep.getAudioFiles();
            for (File f : files) {
                if (f.getParentFile().equals(folder)) {
                    AudioFile audioFile = null;
                    audioFile = new AudioFile(f.getAbsolutePath());
                    audioFile.setExternalPictures(pictures);
                    repositoryFiles.put(audioFile.getUrl(), audioFile);

                    String pathToFile = audioFile.getUrl().replace('\\', '/');
                    int lastChar = pathToFile.lastIndexOf('/') + 1;
                    String relativePath;
                    if (firstChar < lastChar) {
                        relativePath = pathToFile.substring(firstChar, lastChar);
                    } else {
                        relativePath = ".";
                    }

                    populateInfo(rep, audioFile);
                    populateFolderTree(rep, getRepositoryFolderContaining(rep, folder), relativePath, audioFile);
                    populateGenreTree(rep, audioFile);
                    
                    rep.setTotalSizeInBytes(rep.getTotalSizeInBytes() + audioFile.getFile().length());                    
                    rep.addDurationInSeconds(audioFile.getDuration());
                }
            }
        }
    }

    /**
     * Count files.
     * 
     * @param dir
     *            the dir
     * 
     * @return the int
     */
    private static int countFiles(File dir) {
        int files = 0;
        File[] list = dir.listFiles();
        if (list == null) {
            return files;
        }
        for (File element : list) {
            if (AudioFile.isValidAudioFile(element)) {
                files++;
            } else if (element.isDirectory()) {
                files = files + countFiles(element);
            }
        }
        return files;
    }

    /**
     * Count files in repository.
     * 
     * @param rep
     *            the rep
     * 
     * @return the int
     */
    protected static int countFilesInRepository(Repository rep) {
        int files = 0;
        for (File dir : rep.getFolders()) {
            files = files + countFiles(dir);
        }
        return files;
    }

    /**
     * Gets the repository folder containing.
     * 
     * @param rep
     *            the rep
     * @param folder
     *            the folder
     * 
     * @return the repository folder containing
     */
    private static File getRepositoryFolderContaining(Repository rep, File folder) {
        String path = folder.getAbsolutePath();
        for (File f : rep.getFolders()) {
            if (path.startsWith(f.getAbsolutePath())) {
                return f;
            }
        }
        return null;
    }

    /**
     * Gets the songs for dir.
     * 
     * @param folder
     *            the dir
     * 
     * @return the songs for dir
     */
    public static List<AudioFile> getSongsForFolder(File folder) {
        List<AudioFile> result = new ArrayList<AudioFile>();

        File[] list = folder.listFiles();
        List<File> pictures = new ArrayList<File>();
        List<File> files = new ArrayList<File>();
        List<File> dirs = new ArrayList<File>();
        if (list != null) {
            //First find pictures, audio and files
            for (File element : list) {
                if (AudioFile.isValidAudioFile(element)) {
                    files.add(element);
                } else if (element.isDirectory()) {
                    dirs.add(element);
                } else if (element.getName().toUpperCase().endsWith("JPG")) {
                    pictures.add(element);
                }
            }

            for (int i = 0; i < files.size(); i++) {
                AudioFile mp3 = null;
                mp3 = new AudioFile(files.get(i).getAbsolutePath());
                mp3.setExternalPictures(pictures);
                result.add(mp3);
            }

            for (int i = 0; i < dirs.size(); i++) {
                result.addAll(getSongsForFolder(dirs.get(i)));
            }
        }
        return result;
    }

    /**
     * Gets the songs of a list of folders
     * 
     * @param folers
     * @return
     */
    public static List<AudioFile> getSongsForFolders(List<File> folders) {
        List<AudioFile> result = new ArrayList<AudioFile>();
        for (File folder : folders) {
            result.addAll(getSongsForFolder(folder));
        }
        return result;
    }

    /**
     * Populate folder tree.
     * 
     * @param repository
     *            the repository
     * @param relativeTo
     *            the relative to
     * @param relativePath
     *            the relative path
     * @param file
     *            the file
     */
    private static void populateFolderTree(Repository repository, File relativeTo, String relativePath, AudioFile file) {
        Folder relativeFolder = null;
        if (repository.getStructure().getFolderStructure().containsKey(relativeTo.getAbsolutePath())) {
            relativeFolder = repository.getStructure().getFolderStructure().get(relativeTo.getAbsolutePath());
        } else {
            relativeFolder = new Folder(relativeTo.getAbsolutePath());
            repository.getStructure().getFolderStructure().put(relativeFolder.getName(), relativeFolder);
        }

        StringTokenizer st = new StringTokenizer(relativePath, "/");
        Folder parentFolder = relativeFolder;
        while (st.hasMoreTokens()) {
            String folderName = st.nextToken();
            Folder f;
            if (parentFolder != null) {
                if (parentFolder.containsFolder(folderName)) {
                    f = parentFolder.getFolder(folderName);
                } else {
                    f = new Folder(folderName);
                    parentFolder.addFolder(f);
                }
            } else {
                if (repository.getStructure().getFolderStructure().containsKey(folderName)) {
                    f = repository.getStructure().getFolderStructure().get(folderName);
                } else {
                    f = new Folder(folderName);
                    repository.getStructure().getFolderStructure().put(f.getName(), f);
                }
            }
            parentFolder = f;
        }
        if (parentFolder == null) {
            parentFolder = new Folder(".");
        }
        parentFolder.addFile(file);

    }

    /**
     * Populate genre tree.
     * 
     * @param repository
     *            the repository
     * @param audioFile
     *            the audio file
     */
    private static void populateGenreTree(Repository repository, AudioFile audioFile) {
        try {
            Tag tag = audioFile.getTag();
            String albumArtist = null;
            String artist = null;
            String album = null;
            String genre = null;
            if (tag != null) {
                albumArtist = tag.getAlbumArtist();
                artist = tag.getArtist();
                album = tag.getAlbum();
                genre = tag.getGenre();
            }

            /*
             * FIX: Bug 1894536. Some formats do not support album artist field
             * and can cause problems. If album artist field does not contain
             * expected data just set it to following to avoid issues.
             */
            if (albumArtist == null || albumArtist.isEmpty() || albumArtist.length() == 0) {
                albumArtist = "";
            }
            if ((artist == null || artist.equals("")) && albumArtist.equals("")) {
                artist = Artist.getUnknownArtist();
            }
            if (album == null || album.equals("")) {
                album = Album.getUnknownAlbum();
            }
            if (genre == null || genre.equals("") || genre.isEmpty() || genre.length() == 0) {
                genre = Genre.getUnknownGenre();
            }

            if (repository.getStructure().getGenreStructure().containsKey(genre)) {
                Genre g = repository.getStructure().getGenreStructure().get(genre);
                Artist art = null;
                art = g.getArtist(albumArtist);
                if (art == null) {
                    art = g.getArtist(artist);
                }

                // Artist not present in genre list, so lets add him
                if (art == null && !"".equals(albumArtist)) {
                    art = new Artist(albumArtist);
                    g.addArtist(art);
                } else if (art == null && artist != null && !artist.equals("")) {
                    art = new Artist(artist);
                    g.addArtist(art);
                }
                if (art != null) {
                    Album alb = art.getAlbum(album);
                    if (alb != null) {
                        alb.addSong(audioFile);
                    } else {
                        alb = new Album(album);
                        alb.addSong(audioFile);
                        alb.setArtist(art);
                        art.addAlbum(alb);
                    }
                } else {
                    art = new Artist(albumArtist);
                    Album alb = new Album(album);
                    alb.addSong(audioFile);
                    alb.setArtist(art);
                    art.addAlbum(alb);
                    g.addArtist(art);
                }
            } else {
                Genre g = new Genre(genre);
                Artist a = null;
                if (albumArtist.equals("")) {
                    a = new Artist(artist);
                } else {
                    a = new Artist(albumArtist);
                }
                Album alb = new Album(album);
                alb.addSong(audioFile);
                alb.setArtist(a);
                a.addAlbum(alb);
                g.addArtist(a);
                repository.getStructure().getGenreStructure().put(genre, g);
            }
        } catch (Exception e) {
            logger.error(LogCategories.FILE_READ, e.getMessage());
        }
    }

    /**
     * Organizes the tag view.
     * 
     * @param repository
     *            Repository to use
     * @param audioFile
     *            AudioFile to display
     */
    private static void populateInfo(Repository repository, AudioFile audioFile) {
        try {
            Tag tag = audioFile.getTag();
            String artist = null;
            String album = null;
            if (tag != null) {
                // First see if album artist is present
                artist = tag.getAlbumArtist();
                if (artist == null || artist.equals("")) {
                    // If not, use artist field
                    artist = tag.getArtist();
                }
                album = tag.getAlbum();
            }
            if (artist == null || artist.equals("")) {
                artist = Artist.getUnknownArtist();
            }
            if (album == null || album.equals("")) {
                album = Album.getUnknownAlbum();
            }

            if (repository.getStructure().getArtistStructure().containsKey(artist)) {
                Artist a = repository.getStructure().getArtistStructure().get(artist);
                Album alb = a.getAlbum(album);
                if (alb != null) {
                    alb.addSong(audioFile);
                } else {
                    alb = new Album(album);
                    alb.addSong(audioFile);
                    alb.setArtist(a);
                    a.addAlbum(alb);
                }
            } else {
                Artist a = new Artist(artist);
                Album alb = new Album(album);
                alb.addSong(audioFile);
                alb.setArtist(a);
                a.addAlbum(alb);
                repository.getStructure().getArtistStructure().put(artist, a);
            }
        } catch (Exception e) {
            logger.error(LogCategories.FILE_READ, e.getMessage());
        }
    }

    /**
     * Refresh file
     * 
     * @param repository
     *            the repository
     * @param file
     *            the file
     */
    protected static void refreshFile(Repository repository, AudioFile file) {
    	// This operation changes repository, so mark it as dirty
    	repository.setDirty(true);

        try {        	
            Tag oldTag = file.getTag();
            String albumArtist = null;
            String artist = null;
            String album = null;
            String genre = null;
            if (oldTag != null) {
                albumArtist = oldTag.getAlbumArtist();
                artist = oldTag.getArtist();
                album = oldTag.getAlbum();
                genre = oldTag.getGenre();
            }
            if (artist == null || artist.equals("")) {
                artist = Artist.getUnknownArtist();
            }
            if (album == null || album.equals("")) {
                album = Album.getUnknownAlbum();
            }
            if (genre == null || genre.equals("")) {
                genre = Genre.getUnknownGenre();
            }

            // Remove from tree structure if necessary
            boolean albumArtistPresent = true;
            Artist a = repository.getStructure().getArtistStructure().get(albumArtist);
            if (a == null) {
                a = repository.getStructure().getArtistStructure().get(artist);
                albumArtistPresent = false;
            }
            if (a != null) {
                Album alb = a.getAlbum(album);
                if (alb != null) {
                    if (alb.getAudioObjects().size() == 1) {
                        a.removeAlbum(alb);
                    } else {
                        alb.removeAudioFile(file);
                    }

                    if (a.getAudioObjects().size() <= 0) {
                        repository.getStructure().getArtistStructure().remove(a.getName());
                    }
                }
                // If album artist field is present, audiofile might still be present under artist name so check
                if (albumArtistPresent) {
                    a = repository.getStructure().getArtistStructure().get(artist);
                    if (a != null) {
                        alb = a.getAlbum(album);
                        if (alb != null) {
                            if (alb.getAudioObjects().size() == 1) {
                                a.removeAlbum(alb);
                            } else {
                                alb.removeAudioFile(file);
                            }
                            // Maybe needs to be set to 0 in case node gets deleted
                            if (a.getAudioObjects().size() <= 1) {
                                repository.getStructure().getArtistStructure().remove(a.getName());
                            }
                        }
                    }
                }
            }

            // Remove from genre structure if necessary
            Genre g = repository.getStructure().getGenreStructure().get(genre);
            if (g != null) {
                Artist art = g.getArtist(artist);
                if (art != null) {
                    Album alb = art.getAlbum(album);
                    if (alb != null) {
                        if (alb.getAudioObjects().size() == 1) {
                            art.removeAlbum(alb);
                        } else {
                            alb.removeAudioFile(file);
                        }
                    }

                    if (art.getAudioObjects().size() <= 1) {
                        g.removeArtist(art);
                    }
                }

                if (g.getAudioObjects().size() <= 1) {
                    repository.getStructure().getGenreStructure().remove(genre);
                }
            }

            // Update tag
            file.refreshTag();
            populateInfo(repository, file);
            populateGenreTree(repository, file);
            // There is no need to update folder as audio file is in the same folder
            
            // Compare old tag with new tag
            Tag newTag = file.getTag();            
            if (newTag != null) {
            	boolean artistChanged = !oldTag.getArtist().equals(newTag.getArtist());
            	boolean albumChanged = !oldTag.getAlbum().equals(newTag.getAlbum());
            	boolean oldArtistRemoved = false;
            	if (artistChanged) {
            		Artist oldArtist = repository.getStructure().getArtistStructure().get(oldTag.getArtist());
            		if (oldArtist == null) {
            			// Artist has been renamed -> Update statistics
            			StatisticsHandler.getInstance().updateArtist(oldTag.getArtist(), newTag.getArtist());
            			oldArtistRemoved = true;
            		}
            	}
            	if (albumChanged) {
            		Artist artistWithOldAlbum = oldArtistRemoved ? repository.getStructure().getArtistStructure().get(newTag.getArtist()) :
            													   repository.getStructure().getArtistStructure().get(oldTag.getArtist());
            		Album oldAlbum = artistWithOldAlbum.getAlbum(oldTag.getAlbum());
            		if (oldAlbum == null) {
            			// Album has been renamed -> Update statistics
            			StatisticsHandler.getInstance().updateAlbum(artistWithOldAlbum.getName(), oldTag.getAlbum(), newTag.getAlbum());
            		}
            	}
            }
            
            
        } catch (Exception e) {
            logger.error(LogCategories.FILE_READ, e.getMessage());
        }
    }

    /**
     * Adds the repository loader listener.
     * 
     * @param l
     *            the l
     */
    public void addRepositoryLoaderListener(LoaderListener l) {
        this.listener = l;
    }

    /**
     * Count files in dir.
     * 
     * @param dir
     *            the dir
     * 
     * @return the int
     */
    private int countFilesInDir(File dir) {
        int files = 0;
        if (!interrupt) {
            File[] list = dir.listFiles();
            if (list == null) {
                return files;
            }
            for (File element : list) {
                if (AudioFile.isValidAudioFile(element)) {
                    files++;
                } else if (element.isDirectory()) {
                    files = files + countFilesInDir(element);
                }
            }
        }
        return files;
    }

    /**
     * Count files in dir.
     * 
     * @param folders1
     *            the folders1
     * 
     * @return the int
     */
    private int countFilesInDir(List<File> folders1) {
        int files = 0;
        for (File f : folders1) {
            files = files + countFilesInDir(f);
        }
        return files;
    }

    /**
     * Interrupt load.
     */
    protected void interruptLoad() {
        logger.info(LogCategories.REPOSITORY, "Load interrupted");
        interrupt = true;
    }

    /**
     * Load repository.
     */
    private void loadRepository() {
        totalFilesToLoad = countFilesInDir(folders);
        if (listener != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.notifyFilesInRepository(totalFilesToLoad);
                }
            });
        }
        startReadTime = System.currentTimeMillis();
        try {
            for (File folder : folders) {
                fastRepositoryPath = folder.getAbsolutePath().replace('\\', '/');
                if (fastRepositoryPath.endsWith("/")) {
                    fastRepositoryPath = fastRepositoryPath.substring(0, fastRepositoryPath.length() - 2);
                }
                fastFirstChar = fastRepositoryPath.length() + 1;

                navigateDir(folder, folder);
            }

        } catch (FileNotFoundException e) {
            logger.error(LogCategories.REPOSITORY, e.getMessage());
        }
    }

    /**
     * Navigate dir.
     * 
     * @param relativeTo
     *            the relative to
     * @param dir
     *            the dir
     * 
     * @throws FileNotFoundException
     *             the file not found exception
     */
    private void navigateDir(File relativeTo, File dir) throws FileNotFoundException {
        logger.debug(LogCategories.REPOSITORY, StringUtils.getString("Reading dir ", dir.getAbsolutePath()));

        if (!interrupt) {
            String pathToFile = dir.getAbsolutePath().replace('\\', '/');

            int lastChar = pathToFile.lastIndexOf('/') + 1;
            final String relativePath;
            if (fastFirstChar <= lastChar) {
                relativePath = pathToFile.substring(fastFirstChar);
            } else {
                relativePath = ".";
            }

            if (listener != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        listener.notifyCurrentPath(relativePath);
                    }
                });
            }

            File[] list = dir.listFiles();
            List<File> pictures = new ArrayList<File>();
            List<File> audioFiles = new ArrayList<File>();
            List<File> dirs = new ArrayList<File>();
            if (list != null) {
                //First find pictures, audio and files
                for (File element : list) {
                    if (element.isDirectory()) {
                        dirs.add(element);
                    } else if (AudioFile.isValidAudioFile(element)) {
                        audioFiles.add(element);
                    } else if (AudioFilePictureUtils.isValidPicture(element)) {
                        pictures.add(element);
                    }
                }

                Map<String, AudioFile> repositoryFiles = repository.getAudioFiles();
                // Use a List<AudioFile> here to deal with more than one cue track found within one cue sheet file
                List<AudioFile> audioFilesList = new ArrayList<AudioFile>();
                for (int i = 0; i < audioFiles.size() && !interrupt; i++) {
                    AudioFile audio = null;

                    // If a previous repository exists, check if file already was loaded.
                    // If so, compare modification date. If modification date is equal to last repository load
                    // don't read file again

                    if (oldRepository != null) {
                        AudioFile oldAudioFile = oldRepository.getFile(audioFiles.get(i).getAbsolutePath());
                        if (oldAudioFile != null && oldAudioFile.isUpToDate()) {
                            audio = oldAudioFile;
                            audioFilesList.add(audio);
                        } else {
                            audio = new AudioFile(audioFiles.get(i).getAbsolutePath());
                            audioFilesList.add(audio);
                        }
                    } else {
                        audio = new AudioFile(audioFiles.get(i).getAbsolutePath());
                        audioFilesList.add(audio);
                    }
                }

                for (AudioFile audioFile : audioFilesList) {
                    audioFile.setExternalPictures(pictures);
                    if (listener != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                listener.notifyFileLoaded();
                            }
                        });
                    }
                    filesLoaded++;
                    repositoryFiles.put(audioFile.getUrl(), audioFile);
                    populateInfo(repository, audioFile);
                    populateFolderTree(repository, relativeTo, relativePath, audioFile);
                    populateGenreTree(repository, audioFile);
                    repository.setTotalSizeInBytes(repository.getTotalSizeInBytes() + audioFile.getFile().length());
                    repository.addDurationInSeconds(audioFile.getDuration());

                    if (filesLoaded % 25 == 0) {
                        long t1 = System.currentTimeMillis();
                        final long remainingTime = filesLoaded != 0 ? (totalFilesToLoad - filesLoaded) * (t1 - startReadTime) / filesLoaded : 0;

                        if (listener != null) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    listener.notifyRemainingTime(remainingTime);
                                }
                            });
                            if (!refresh) {
                            	SwingUtilities.invokeLater(new Runnable() {
                            		@Override
                            		public void run() {
                            			listener.notifyReadProgress();
                            		}
                            	});
                            }
                        }
                    }
                }

                for (int i = 0; i < dirs.size(); i++) {
                    navigateDir(relativeTo, dirs.get(i));
                }
            }
        }
    }

    /**
     * Notify finish.
     */
    private void notifyFinish() {
    	// After every read or refresh mark repository as dirty
    	repository.setDirty(true);
    	
        if (listener == null) {
            return;
        }
        if (refresh) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.notifyFinishRefresh(RepositoryLoader.this);
                }
            });
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.notifyFinishRead(RepositoryLoader.this);
                }
            });
        }
    }

    @Override
    public void run() {
        logger.info(LogCategories.REPOSITORY, "Starting repository read");
        Timer timer = new Timer();
        timer.start();
        if (!folders.isEmpty()) {
            loadRepository();
        } else {
            logger.error(LogCategories.REPOSITORY, "No folders selected for repository");
        }
        if (!interrupt) {
            double time = timer.stop();
            long files = repository.countFiles();
            double averageFileTime = time / files;
            logger.info(LogCategories.REPOSITORY, StringUtils.getString("Read repository process DONE (", files, " files, ", time, " seconds, ", StringUtils.toString(
                    averageFileTime, 4), " seconds / file)"));

            notifyFinish();
        }
    }

	/**
	 * @return the oldRepository
	 */
	protected Repository getOldRepository() {
		return oldRepository;
	}
	
    /**
     * Adds the external picture for album.
     * 
     * @param artistName
     *            the artist name
     * @param albumName
     *            the album name
     * @param picture
     *            the picture
     */
	protected static void addExternalPictureForAlbum(Repository repository, String artistName, String albumName, File picture) {    	
        if (repository != null) {        	
            Artist artist = repository.getStructure().getArtistStructure().get(artistName);
            if (artist == null) {
                return;
            }
            Album album = artist.getAlbum(albumName);
            if (album == null) {
                return;
            }
            
        	// This operation changes repository, so mark it as dirty
            repository.setDirty(true);
            
            List<AudioFile> audioFiles = album.getAudioFiles();
            for (AudioFile af : audioFiles) {
                af.addExternalPicture(picture);
            }
        }
    }
	
    /**
     * Permanently deletes an audio file from the repository metainformation
     * 
     * @param file
     *            File to be removed permanently
     */
    protected static void deleteFile(AudioFile file) {
        String albumArtist = file.getAlbumArtist();
        String artist = file.getArtist();
        String album = file.getAlbum();
        String genre = file.getGenre();

        // Only do this if file is in repository
        if (getFolderForFile(file) != null) {
        	// This operation changes repository, so mark it as dirty
            RepositoryHandler.getInstance().getRepository().setDirty(true);

            // Remove from file structure
            Folder f = getFolderForFile(file);
            if (f != null) {
                f.removeFile(file);
                // If folder is empty, remove too
                if (f.isEmpty()) {
                    f.getParentFolder().removeFolder(f);
                }
            }

            // Remove from tree structure
            Artist a = RepositoryHandler.getInstance().getArtistStructure().get(albumArtist);
            if (a == null) {
                a = RepositoryHandler.getInstance().getArtistStructure().get(artist);
            }
            if (a != null) {
                Album alb = a.getAlbum(album);
                if (alb != null) {
                    if (alb.getAudioObjects().size() == 1) {
                        a.removeAlbum(alb);
                    } else {
                        alb.removeAudioFile(file);
                    }

                    if (a.getAudioObjects().size() <= 1) {
                    	RepositoryHandler.getInstance().getArtistStructure().remove(a.getName());
                    }
                }
            }

            // Remove from genre structure
            Genre g = RepositoryHandler.getInstance().getGenreStructure().get(genre);
            if (g != null) {
                Artist art = g.getArtist(artist);
                if (art != null) {
                    Album alb = art.getAlbum(album);
                    if (alb != null) {
                        if (alb.getAudioObjects().size() == 1) {
                            art.removeAlbum(alb);
                        } else {
                            alb.removeAudioFile(file);
                        }
                    }

                    if (art.getAudioObjects().size() <= 1) {
                        g.removeArtist(art);
                    }
                }

                if (g.getAudioObjects().size() <= 1) {
                	RepositoryHandler.getInstance().getGenreStructure().remove(genre);
                }
            }

            // Remove from file list
            RepositoryHandler.getInstance().getRepository().getAudioFiles().remove(file.getUrl());

            // Update repository size
            RepositoryHandler.getInstance().getRepository().setTotalSizeInBytes(RepositoryHandler.getInstance().getRepository().getTotalSizeInBytes() - file.getFile().length());

            // Update repository duration
            RepositoryHandler.getInstance().getRepository().removeDurationInSeconds(file.getDuration());
        }
        // File is on a device
        else if (DeviceHandler.getInstance().isDevicePath(file.getUrl())) {
        	RepositoryHandler.getRepositoryHandlerLogger().info(LogCategories.REPOSITORY, StringUtils.getString("Deleted file ", file, " from device"));
        }
    }

    /**
     * Finds folder that contains file.
     * 
     * @param file
     *            Audio file for which the folder is wanted
     * 
     * @return Either folder or null if file is not in repository
     */
    private static Folder getFolderForFile(AudioFile file) {
        // Get repository folder where file is
        File repositoryFolder = RepositoryHandler.getInstance().getRepositoryFolderContainingFile(file);
        // If the file is not in the repository, return null
        if (repositoryFolder == null) {
            return null;
        }

        // Get root folder
        Folder rootFolder = RepositoryHandler.getInstance().getRepository().getStructure().getFolderStructure().get(repositoryFolder.getAbsolutePath());

        // Now navigate through folder until find folder that contains file
        String path = file.getFile().getParentFile().getAbsolutePath();
        path = path.replace(repositoryFolder.getAbsolutePath(), "");

        Folder f = rootFolder;
        StringTokenizer st = new StringTokenizer(path, SystemProperties.FILE_SEPARATOR);
        while (st.hasMoreTokens()) {
            String folderName = st.nextToken();
            f = f.getFolder(folderName);
        }
        return f;
    }

    /**
     * Renames a file in repository
     * @param audioFile
     * @param oldFile
     * @param newFile
     */
    protected static void renameFile(AudioFile audioFile, File oldFile, File newFile) {
    	// This operation changes repository, so mark it as dirty
    	RepositoryHandler.getInstance().getRepository().setDirty(true);

    	audioFile.setFile(newFile);
    	RepositoryHandler.getInstance().getRepository().getAudioFiles().remove(oldFile.getAbsolutePath());
        RepositoryHandler.getInstance().getRepository().getAudioFiles().put(newFile.getAbsolutePath(), audioFile);
    }

}
