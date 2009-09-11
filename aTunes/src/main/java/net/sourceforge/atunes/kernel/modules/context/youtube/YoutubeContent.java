package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.substance.SubstanceContextImageJTable;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.desktop.DesktopHandler;
import net.sourceforge.atunes.kernel.modules.internetsearch.SearchFactory;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeVideoDownloader;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

import org.jfree.ui.ExtensionFileFilter;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * Content to show videos from Youtube
 * @author alex
 *
 */
public class YoutubeContent extends ContextPanelContent {
	
	private static final long serialVersionUID = 5041098100868186051L;
	
	private SubstanceContextImageJTable youtubeResultTable;

	public YoutubeContent() {
		super(new YoutubeDataSource());
	}
	
	@Override
	protected String getContentName() {
		return LanguageTool.getString("YOUTUBE_VIDEOS");
	}
	
	@Override
	protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(YoutubeDataSource.INPUT_AUDIO_OBJECT, audioObject);
		return parameters;
	}
	
	@Override
	protected void updateContentWithDataSourceResult(Map<String, ?> result) {
		if (result.containsKey(YoutubeDataSource.OUTPUT_VIDEOS)) {
			youtubeResultTable.setModel(new YoutubeResultTableModel((List<YoutubeResultEntry>)result.get(YoutubeDataSource.OUTPUT_VIDEOS)));
		}
	}
	
	@Override
	protected void clearContextPanelContent() {
		super.clearContextPanelContent();
		youtubeResultTable.setModel(new YoutubeResultTableModel(null));
	}
	
	@Override
	protected Component getComponent() {
		// Create components
        youtubeResultTable = new SubstanceContextImageJTable();
        youtubeResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        youtubeResultTable.setShowGrid(false);
        youtubeResultTable.getTableHeader().setReorderingAllowed(false);
        youtubeResultTable.setDefaultRenderer(YoutubeResultEntry.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 620892562731682118L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Color backgroundColor = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5).getBackground();

                return getPanelForTableRenderer(((YoutubeResultEntry) value).getImage(), StringUtils.getString("<html>", ((YoutubeResultEntry) value).getName(), "<br>(",
                        ((YoutubeResultEntry) value).getDuration(), ")</html>"), backgroundColor, Constants.CONTEXT_IMAGE_WIDTH,
                        Constants.CONTEXT_IMAGE_HEIGHT);
            }
        });
        youtubeResultTable.setColumnSelectionAllowed(false);        

        JMenuItem playMenuItem = new JMenuItem(LanguageTool.getString("PLAY_VIDEO_AT_YOUTUBE"));
        playMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playVideoAtYoutube();
			}
		});
        JMenuItem downloadMenuItem = new JMenuItem(LanguageTool.getString("DOWNLOAD_VIDEO"));
        downloadMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadVideo();
			}
		});
        final JPopupMenu youtubeTableMenu = new JPopupMenu();
        youtubeTableMenu.add(playMenuItem);
        youtubeTableMenu.add(downloadMenuItem);
        youtubeTableMenu.setInvoker(youtubeResultTable);
        
        youtubeResultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    playVideoAtYoutube();
                }
            }

            private void showPopup(MouseEvent e) {
            	youtubeResultTable.getSelectionModel().setSelectionInterval(youtubeResultTable.rowAtPoint(e.getPoint()),
            			youtubeResultTable.rowAtPoint(e.getPoint()));
                youtubeTableMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        return youtubeResultTable;
	}

    @Override
    protected List<Component> getOptions() {
    	List<Component> options = new ArrayList<Component>();
    	JMenuItem moreResults = new JMenuItem(LanguageTool.getString("SEE_MORE_RESULTS"));
    	moreResults.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchMoreResultsInYoutube();
			}
		});
    	JMenuItem openYoutube = new JMenuItem(LanguageTool.getString("GO_TO_YOUTUBE"));
    	openYoutube.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openYoutube();
			}
		});
    	options.add(moreResults);
    	options.add(openYoutube);
    	return options;
    }

    /**
     * Opens a dialog to select file and starts downloading video
     */
    protected void downloadVideo() {
        int selectedVideo = youtubeResultTable.getSelectedRow();
        if (selectedVideo != -1) {
            // get entry
            YoutubeResultEntry entry = ((YoutubeResultTableModel) youtubeResultTable.getModel()).getEntry(selectedVideo);

            // Open save dialog to select file
            JFileChooser dialog = new JFileChooser();
            dialog.setDialogType(JFileChooser.SAVE_DIALOG);
            dialog.setDialogTitle(LanguageTool.getString("SAVE_YOUTUBE_VIDEO"));
            dialog.setFileFilter(new ExtensionFileFilter("FLV", "FLV"));
            // Set default file name
            // for some reason dialog fails with files with [ or ] chars
            File defaultFileName = new File(FileNameUtils
                    .getValidFileName(entry.getName().replace("\\", "\\\\").replace("$", "\\$").replace('[', ' ').replace(']', ' ').toString()));
            dialog.setSelectedFile(defaultFileName);
            int returnValue = dialog.showSaveDialog(VisualHandler.getInstance().getFrame().getFrame());
            File selectedFile = dialog.getSelectedFile();
            if (selectedFile != null && JFileChooser.APPROVE_OPTION == returnValue) {
                downloadYoutubeVideo(entry, selectedFile);
            }
        }
    }

    /**
     * downloads the youtube video to the given file. Opens a ProgressDialog and
     * starts the download in a SwingWorker process.
     * 
     * @param entry
     * @param file
     */
    protected void downloadYoutubeVideo(final YoutubeResultEntry entry, final File file) {
        if (entry == null || entry.getUrl() == null)
            return;

        final YoutubeVideoDownloader downloader = new YoutubeVideoDownloader(entry, file);
        downloader.execute();
    }

    /**
     * Opens a web browser to play YouTube video
     */
    protected void playVideoAtYoutube() {
        int selectedVideo = youtubeResultTable.getSelectedRow();
        if (selectedVideo != -1) {
            // get entry
            YoutubeResultEntry entry = ((YoutubeResultTableModel)youtubeResultTable.getModel()).getEntry(selectedVideo);
            if (entry.getUrl() != null) {
                //open youtube url
                DesktopHandler.getInstance().openURL(entry.getUrl());
                // When playing a video in web browser automatically pause current song
                if (PlayerHandler.getInstance().isEnginePlaying()) {
                    PlayerHandler.getInstance().playCurrentAudioObject(true);
                }
            }
        }
    }
    
    /**
     * Searches for more results of the last search
     * 
     * @return
     */
    protected void searchMoreResultsInYoutube() {
    	String searchString = YoutubeService.getInstance().getSearchForAudioObject(ContextHandler.getInstance().getCurrentAudioObject());
    	if (searchString.length() > 0) {
    		final List<YoutubeResultEntry> result = YoutubeService.getInstance().searchInYoutube(searchString, youtubeResultTable.getRowCount() + 1);
    		((YoutubeResultTableModel)youtubeResultTable.getModel()).addEntries(result);
    	}
    }
    
    /**
     * Opens a web browser to show youtube results
     */
    protected void openYoutube() {
    	DesktopHandler.getInstance().openSearch(SearchFactory.getSearchForName("YouTube"), YoutubeService.getInstance().getSearchForAudioObject(ContextHandler.getInstance().getCurrentAudioObject()));
	}

}
