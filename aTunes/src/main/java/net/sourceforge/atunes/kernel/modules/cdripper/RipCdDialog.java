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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

/**
 * The dialog for ripping cds
 */
public final class RipCdDialog extends AbstractCustomDialog {

    private static final long serialVersionUID = 1987727841297807350L;

    private JTable table;
    private JTextField artistTextField;
    private JTextField albumTextField;
    private JTextField yearTextField;
    private JComboBox genreComboBox;
    private JButton titlesButton;
    private JComboBox format;
    private JComboBox quality;
    private JComboBox filePattern;
    private JTextField folderName;
    private JButton folderSelectionButton;
    private JCheckBox useCdErrorCorrection;
    private JButton ok;
    private JButton cancel;
    private CdInfoTableModel tableModel;

    /**
     * Instantiates a new rip cd dialog.
     * 
     * @param owner
     *            the owner
     */
    public RipCdDialog(IFrame frame, ILookAndFeelManager lookAndFeelManager) {
        super(frame, 750, 540, true, CloseAction.DISPOSE, lookAndFeelManager.getCurrentLookAndFeel());
        setTitle(I18nUtils.getString("RIP_CD"));
        add(getContent(lookAndFeelManager.getCurrentLookAndFeel()));
    }

    /**
     * Gets the album text field.
     * 
     * @return the album text field
     */
    public JTextField getAlbumTextField() {
        return albumTextField;
    }

    /**
     * Gets the amazon button.
     * 
     * @return the amazon button
     */
    public JButton getTitlesButton() {
        return titlesButton;
    }

    /**
     * Gets the artist names.
     * 
     * @return the artist names
     */
    public List<String> getArtistNames() {
        return tableModel.artistNames;
    }

    /**
     * Gets the artist text field.
     * 
     * @return the artist text field
     */
    public JTextField getArtistTextField() {
        return artistTextField;
    }

    /**
     * Gets the cancel.
     * 
     * @return the cancel
     */
    public JButton getCancel() {
        return cancel;
    }

    /**
     * Gets the composer names.
     * 
     * @return the composer names
     */
    public List<String> getComposerNames() {
        return tableModel.composerNames;
    }

    /**
     * Defines the content of the dialog box.
     * @param iLookAndFeel 
     * 
     * @return the content
     */
    private JPanel getContent(ILookAndFeel iLookAndFeel) {
        JPanel panel = new JPanel(new GridBagLayout());

        tableModel = new CdInfoTableModel();
        table = iLookAndFeel.getTable();
        table.setModel(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(20);
        table.getColumnModel().getColumn(4).setMaxWidth(50);
        JCheckBox checkBox = new JCheckBox();
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(checkBox));
        JTextField textfield1 = new CustomTextField();
        JTextField textfield2 = new CustomTextField();
        JTextField textfield3 = new CustomTextField();
        textfield1.setBorder(BorderFactory.createEmptyBorder());
        textfield2.setBorder(BorderFactory.createEmptyBorder());
        textfield3.setBorder(BorderFactory.createEmptyBorder());
        GuiUtils.applyComponentOrientation(textfield1);
        GuiUtils.applyComponentOrientation(textfield2);
        GuiUtils.applyComponentOrientation(textfield3);

        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(textfield1));
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(textfield2));
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(textfield3));

        table.setDefaultRenderer(String.class, iLookAndFeel.getTableCellRenderer(
                GuiUtils.getComponentOrientationTableCellRendererCode(iLookAndFeel)));

        JScrollPane scrollPane = iLookAndFeel.getTableScrollPane(table);
        JLabel artistLabel = new JLabel(I18nUtils.getString("ALBUM_ARTIST"));
        artistTextField = new CustomTextField();
        JLabel albumLabel = new JLabel(I18nUtils.getString("ALBUM"));
        albumTextField = new CustomTextField();
        JLabel yearLabel = new JLabel();
        yearLabel.setText(I18nUtils.getString("YEAR"));
        yearTextField = new CustomTextField();
        JLabel genreLabel = new JLabel(I18nUtils.getString("GENRE"));

        genreComboBox = new JComboBox();
        genreComboBox.setEditable(true);
        titlesButton = new JButton(I18nUtils.getString("GET_TITLES"));
        JLabel formatLabel = new JLabel(I18nUtils.getString("ENCODE_TO"));

        format = new JComboBox();
        JLabel qualityLabel = new JLabel(I18nUtils.getString("QUALITY"));

        quality = new JComboBox(new String[] {});
        quality.setMinimumSize(new Dimension(150, 20));
        JLabel filePatternLabel = new JLabel(I18nUtils.getString("FILEPATTERN"));

        filePattern = new JComboBox();
        JLabel dir = new JLabel(I18nUtils.getString("FOLDER"));

        folderName = new CustomTextField();
        folderSelectionButton = new JButton(I18nUtils.getString("SELECT_FOLDER"));

        // Explain what the file name pattern means
        JLabel explainPatterns = new JLabel(StringUtils.getString("%A=", I18nUtils.getString("ARTIST"), "  -  %L=", I18nUtils.getString("ALBUM"), "  -  %N=", I18nUtils
                .getString("TRACK"), "  -  %T=", I18nUtils.getString("TITLE")));

        useCdErrorCorrection = new JCheckBox(I18nUtils.getString("USE_CD_ERROR_CORRECTION"));
        ok = new JButton(I18nUtils.getString("OK"));
        cancel = new JButton(I18nUtils.getString("CANCEL"));

        JPanel auxPanel = new JPanel();
        auxPanel.setOpaque(false);
        auxPanel.add(ok);
        auxPanel.add(cancel);

        // Here we define the cd ripper dialog display layout
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridwidth = 4;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(20, 20, 10, 20);
        panel.add(scrollPane, c);
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 20, 5, 20);
        c.anchor = GridBagConstraints.WEST;
        panel.add(artistLabel, c);
        c.gridx = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(artistTextField, c);
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        panel.add(albumLabel, c);
        c.gridx = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(albumTextField, c);
        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        panel.add(genreLabel, c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(genreComboBox, c);
        c.gridx = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        panel.add(yearLabel, c);
        c.gridx = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(yearTextField, c);
        c.gridx = 1;
        c.gridwidth = 3;
        c.gridy = 4;
        panel.add(titlesButton, c);
        c.gridx = 0;
        c.gridy = 5;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        panel.add(formatLabel, c);
        c.gridx = 1;
        c.weightx = 0.3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(format, c);
        c.gridx = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(qualityLabel, c);
        c.gridx = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(quality, c);
        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 3;
        panel.add(useCdErrorCorrection, c);
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        panel.add(dir, c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 3;
        panel.add(folderName, c);
        c.gridx = 0;
        c.gridy = 8;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        panel.add(filePatternLabel, c);
        c.gridx = 1;
        c.weightx = 0.3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(filePattern, c);
        c.gridx = 2;
        c.gridwidth = 2;
        panel.add(folderSelectionButton, c);
        c.gridx = 1;
        c.gridy = 9;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 3;
        panel.add(explainPatterns, c);
        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 8;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(auxPanel, c);

        return panel;
    }

    /**
     * Returns the filename pattern selected in the CD ripper dialog.
     * 
     * @return Filename pattern
     */
    public String getFileNamePattern() {
        return (String) filePattern.getSelectedItem();
    }

    /**
     * Gets the file pattern.
     * 
     * @return the file pattern
     */
    public JComboBox getFilePattern() {
        return filePattern;
    }

    /**
     * Gets the folder name.
     * 
     * @return the folder name
     */
    public JTextField getFolderName() {
        return folderName;
    }

    /**
     * Gets the folder selection button.
     * 
     * @return the folder selection button
     */
    public JButton getFolderSelectionButton() {
        return folderSelectionButton;
    }

    /**
     * Gets the format.
     * 
     * @return the format
     */
    public JComboBox getFormat() {
        return format;
    }

    /**
     * Gets the genre combo box.
     * 
     * @return the genre combo box
     */
    public JComboBox getGenreComboBox() {
        return genreComboBox;
    }

    /**
     * Gets the ok.
     * 
     * @return the ok
     */
    public JButton getOk() {
        return ok;
    }

    /**
     * Gets the quality.
     * 
     * @return the quality
     */
    public String getQuality() {
        return (String) quality.getSelectedItem();
    }

    /**
     * Gets the quality combo box.
     * 
     * @return the quality combo box
     */
    public JComboBox getQualityComboBox() {
        return quality;
    }

    /**
     * Gets the track names.
     * 
     * @return the track names
     */
    public List<String> getTrackNames() {
        return tableModel.trackNames;
    }

    /**
     * Gets the tracks selected.
     * 
     * @return the tracks selected
     */

    public List<Integer> getTracksSelected() {
        List<Boolean> tracks = tableModel.getTracksSelected();
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i)) {
                result.add(i + 1);
            }
        }
        return result;
    }

    /**
     * Gets the CD error correction checkbox
     * 
     * @return The cd error correction checkbox
     */
    public JCheckBox getUseCdErrorCorrection() {
        return useCdErrorCorrection;
    }

    /**
     * Gets the year text field.
     * 
     * @return the year text field
     */
    public JTextField getYearTextField() {
        return yearTextField;
    }

    /**
     * Sets the table data.
     * 
     * @param cdInfo
     *            the new table data
     */
    public void setTableData(CDInfo cdInfo) {
        tableModel.setCdInfo(cdInfo);
        tableModel.fireTableDataChanged();
    }

    /**
     * Update artist names.
     * 
     * @param names
     *            the names
     */
    public void updateArtistNames(CDInfo cdInfo) {
        // Fill names of artists
        // Each track has an artist which can be "" if it's the same artist of all CD tracks
        List<String> names = new ArrayList<String>();
        for (String artist : cdInfo.getArtists()) {
            if (!artist.trim().equals("")) {
                names.add(artist);
            } else {
                // TODO if cdda2wav is modified for detecting song artist modify here
                if (cdInfo.getArtist() != null && !cdInfo.getArtist().trim().equals("")) {
                    names.add(cdInfo.getArtist());
                } else {
                    names.add(UnknownObjectCheck.getUnknownArtist());
                }
            }
        }

        tableModel.setArtistNames(names);
        tableModel.fireTableDataChanged();
    }

    /**
     * Update composer names.
     * 
     * @param names
     *            the names
     */
    public void updateComposerNames(List<String> names) {
        tableModel.setComposerNames(names);
        tableModel.fireTableDataChanged();
    }

    /**
     * Update track names.
     * 
     * @param names
     *            the names
     */
    public void updateTrackNames(List<String> names) {
        tableModel.setTrackNames(names);
        tableModel.fireTableDataChanged();
    }

}