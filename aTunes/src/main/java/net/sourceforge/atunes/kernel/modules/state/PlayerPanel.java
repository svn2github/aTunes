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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IHotkey;
import net.sourceforge.atunes.model.IHotkeyHandler;
import net.sourceforge.atunes.model.IHotkeysConfig;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;

public final class PlayerPanel extends AbstractPreferencesPanel {

	private final class HotkeyTableTableCellRendererCode extends AbstractTableCellRendererCode<JLabel, Object> {
		
		public HotkeyTableTableCellRendererCode(ILookAndFeel lookAndFeel) {
			super(lookAndFeel);
		}

		@Override
		public JLabel getComponent(JLabel c, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		    GuiUtils.applyComponentOrientation(c);
		    if (conflicts.contains(row) || notRecommendedKeys.contains(row)) {
		        c.setForeground(ColorDefinitions.WARNING_COLOR);
		    }
		    String keyWarnings = "";
		    if (conflicts.contains(row)) {
		        keyWarnings += I18nUtils.getString("DUPLICATE_HOTKEYS");
		    }
		    if (notRecommendedKeys.contains(row)) {
		        if (conflicts.contains(row)) {
		            keyWarnings += " ";
		        }
		        keyWarnings += I18nUtils.getString("NOT_RECOMMENDED_HOTKEYS");
		    }
		    c.setToolTipText(keyWarnings.isEmpty() ? null : keyWarnings);
		    return c;
		}
	}

	class HotkeyTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -5726677745418003289L;

        /** The data. */
        private IHotkeysConfig hotkeysConfig;

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            return column == 0 ? I18nUtils.getString("ACTION") : I18nUtils.getString("HOTKEY");
        }

        @Override
        public int getRowCount() {
            return hotkeysConfig != null ? hotkeysConfig.size() : 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (hotkeysConfig != null) {
                if (columnIndex == 0) {
                    return hotkeysConfig.getHotkeyByOrder(rowIndex).getDescription();
                } else if (columnIndex == 1) {
                    return hotkeysConfig.getHotkeyByOrder(rowIndex).getKeyDescription();
                }
            }
            return "";
        }

        public void setHotkeysConfig(IHotkeysConfig hotkeysConfig) {
            this.hotkeysConfig = hotkeysConfig;
            conflicts = hotkeysConfig.conflicts();
            notRecommendedKeys = hotkeysConfig.notRecommendedKeys();
            fireTableDataChanged();
        }

        public IHotkeysConfig getHotkeysConfig() {
            return hotkeysConfig;
        }

    }

    private static final long serialVersionUID = 4489293347321979288L;

    /** The play at startup. */
    private JCheckBox playAtStartup;

    /** The show ticks. */
    private JCheckBox showTicks;
    
    /** Show advanced player controls */
    private JCheckBox showAdvancedPlayerControls;
    
    /**
     * Show player controls on top
     */
    private JCheckBox showPlayerControlsOnTop;

    /** The use fade away. */
    private JCheckBox useFadeAway;

    /** The use short path names. */
    private JCheckBox useShortPathNames;

    /** The enable global hotkeys. */
    private JCheckBox enableGlobalHotkeys;

    /**
     * Scroll pane containing hotkeys
     */
    private JScrollPane hotkeyScrollPane;
    
    /** The hotkey table. */
    private JTable hotkeyTable;

    /** The table model. */
    private HotkeyTableModel tableModel = new HotkeyTableModel();

    private Set<Integer> conflicts = new HashSet<Integer>();
    private Set<Integer> notRecommendedKeys = new HashSet<Integer>();

    private JButton resetHotkeys;

    /** The cache files before playing. */
    private JCheckBox cacheFilesBeforePlaying;

    private JComboBox engineCombo;
    
    private IOSManager osManager;
    
    private IHotkeyHandler hotkeyHandler;

    /**
     * Instantiates a new player panel.
     * @param osManager
     * @param lookAndFeel
     * @param playerHandler
     * @param hotkeyHandler
     */
    public PlayerPanel(IOSManager osManager, ILookAndFeel lookAndFeel, IPlayerHandler playerHandler, IHotkeyHandler hotkeyHandler) {
        super(I18nUtils.getString("PLAYER"));
        this.osManager = osManager;
        this.hotkeyHandler = hotkeyHandler;
        Box engineBox = Box.createHorizontalBox();
        engineBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        engineBox.add(new JLabel(I18nUtils.getString("PLAYER_ENGINE")));
        engineBox.add(Box.createHorizontalStrut(6));
        String[] engineNames = playerHandler.getEngineNames();
        engineCombo = new JComboBox(engineNames);
        // Disable combo if no player engine available
        engineCombo.setEnabled(engineNames.length > 0);
        engineBox.add(engineCombo);
        engineBox.add(Box.createHorizontalGlue());
        playAtStartup = new JCheckBox(I18nUtils.getString("PLAY_AT_STARTUP"));
        useFadeAway = new JCheckBox(I18nUtils.getString("USE_FADE_AWAY"));
        showTicks = new JCheckBox(I18nUtils.getString("SHOW_TICKS"));
        showAdvancedPlayerControls = new JCheckBox(I18nUtils.getString("SHOW_ADVANCED_PLAYER_CONTROLS"));
        useShortPathNames = new JCheckBox(I18nUtils.getString("USE_SHORT_PATH_NAMES_FOR_MPLAYER"));
        enableGlobalHotkeys = new JCheckBox(I18nUtils.getString("ENABLE_GLOBAL_HOTKEYS"));
        showPlayerControlsOnTop = new JCheckBox(I18nUtils.getString("SHOW_PLAYER_CONTROLS_ON_TOP"));

        hotkeyTable = lookAndFeel.getTable();
        hotkeyTable.setModel(tableModel);
        hotkeyTable.getTableHeader().setReorderingAllowed(false);
        hotkeyTable.getTableHeader().setResizingAllowed(false);
        hotkeyTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hotkeyTable.setEnabled(hotkeyHandler.areHotkeysSupported());
        hotkeyTable.setDefaultRenderer(Object.class, lookAndFeel.getTableCellRenderer(new HotkeyTableTableCellRendererCode(lookAndFeel)));

        hotkeyTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int selectedRow = hotkeyTable.getSelectedRow();
                int modifiersEx = e.getModifiersEx();
                int keyCode = e.getKeyCode();
                if (selectedRow != -1 && keyCode != KeyEvent.VK_UNDEFINED && (modifiersEx & InputEvent.BUTTON1_DOWN_MASK) == 0 && (modifiersEx & InputEvent.BUTTON2_DOWN_MASK) == 0
                        && (modifiersEx & InputEvent.BUTTON3_DOWN_MASK) == 0) {
                    IHotkey hotkey = tableModel.getHotkeysConfig().getHotkeyByOrder(selectedRow);
                    hotkey.setMod(modifiersEx);
                    hotkey.setKey(keyCode);

                    conflicts = tableModel.getHotkeysConfig().conflicts();
                    notRecommendedKeys = tableModel.getHotkeysConfig().notRecommendedKeys();

                    tableModel.fireTableRowsUpdated(0, tableModel.getRowCount());
                }
            }

        });

        InputMap inputMap = hotkeyTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        for (KeyStroke keyStroke : inputMap.allKeys()) {
            inputMap.put(keyStroke, "none");
        }

        resetHotkeys = new JButton(I18nUtils.getString("RESET"));
        resetHotkeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setHotkeysConfig(PlayerPanel.this.hotkeyHandler.getDefaultHotkeysConfiguration());
                tableModel.fireTableDataChanged();
            }
        });

        hotkeyScrollPane = lookAndFeel.getTableScrollPane(hotkeyTable);
        hotkeyScrollPane.setMinimumSize(new Dimension(400, 200));
        cacheFilesBeforePlaying = new JCheckBox(I18nUtils.getString("CACHE_FILES_BEFORE_PLAYING"));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        Insets prevInsets = c.insets;
        c.insets = new Insets(0, 8, 0, 0);
        add(engineBox, c);
        c.insets = prevInsets;
        c.gridy = 1;
        add(playAtStartup, c);
        c.gridy = 2;
        add(useFadeAway, c);
        c.gridy = 3;
        add(showTicks, c);
        c.gridy = 4;
        add(showAdvancedPlayerControls, c);
        c.gridy = 5;
        add(showPlayerControlsOnTop, c);
        c.gridy = 6;
        add(useShortPathNames, c);
        c.gridy = 7;
        add(cacheFilesBeforePlaying, c);
        c.gridy = 8;
        add(enableGlobalHotkeys, c);
        c.gridy = 9;
        c.weightx = 0;
        c.insets = new Insets(10, 20, 5, 10);
        add(hotkeyScrollPane, c);
        c.gridy = 10;
        c.weighty = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 20, 0, 0);
        JPanel resetHotkeysPanel = new JPanel();
        resetHotkeysPanel.add(resetHotkeys);
        add(resetHotkeysPanel, c);
    }

    /**
     * Gets the enable global hotkeys.
     * 
     * @return the enable global hotkeys
     */
    private JCheckBox getEnableGlobalHotkeys() {
        return enableGlobalHotkeys;
    }

    @Override
    public boolean applyPreferences(IState state) {
        boolean needRestart = false;
        state.setPlayAtStartup(playAtStartup.isSelected());
        state.setUseFadeAway(useFadeAway.isSelected());
        state.setShowTicks(showTicks.isSelected());
        state.setShowAdvancedPlayerControls(showAdvancedPlayerControls.isSelected());
        state.setUseShortPathNames(useShortPathNames.isSelected());
        state.setEnableHotkeys(enableGlobalHotkeys.isSelected());
        state.setHotkeysConfig(tableModel.getHotkeysConfig());
        state.setCacheFilesBeforePlaying(cacheFilesBeforePlaying.isSelected());
        String engine = engineCombo.getSelectedItem() != null ? engineCombo.getSelectedItem().toString() : null;
        if (engine != null && !state.getPlayerEngine().equals(engine)) {
            state.setPlayerEngine(engine);
            needRestart = true;
        }

        boolean onTop = state.isShowPlayerControlsOnTop();
        state.setShowPlayerControlsOnTop(showPlayerControlsOnTop.isSelected());
        if (onTop != showPlayerControlsOnTop.isSelected()) {
        	needRestart = true;
        }
        
        return needRestart;
    }

    /**
     * Gets the use short path names.
     * 
     * @return the use short path names
     */
    private JCheckBox getUseShortPathNames() {
        return useShortPathNames;
    }

    /**
     * Sets the enable hotkeys.
     * 
     * @param enable
     *            the new enable hotkeys
     */
    private void setEnableHotkeys(boolean enable) {
        enableGlobalHotkeys.setSelected(enable);
    }

    /**
     * Sets the play at startup.
     * 
     * @param play
     *            the new play at startup
     */
    private void setPlayAtStartup(boolean play) {
        playAtStartup.setSelected(play);
    }

    /**
     * Sets the use short path names.
     * 
     * @param use
     *            the new use short path names
     */
    private void setUseShortPathNames(boolean use) {
        useShortPathNames.setSelected(use);
    }

    /**
     * Sets the use fade away.
     * 
     * @param useFadeAway
     *            the new use fade away
     */
    private void setUseFadeAway(boolean useFadeAway) {
        this.useFadeAway.setSelected(useFadeAway);
    }

    /**
     * Sets the show ticks.
     * 
     * @param showTicks
     *            the new show ticks
     */
    private void setShowTicks(boolean showTicks) {
        this.showTicks.setSelected(showTicks);
    }

    /**
     * Sets the show advanced player controls
     * 
	 * @param showAdvancedPlayerControls
	 */
	private void setShowAdvancedPlayerControls(boolean showAdvancedPlayerControls) {
		this.showAdvancedPlayerControls.setSelected(showAdvancedPlayerControls);
	}
	
	/**
	 * Sets the property to show player controls on top
	 * @param onTop
	 */
	private void setShowPlayerControlsOnTop(boolean onTop) {
		this.showPlayerControlsOnTop.setSelected(onTop);
	}
    
    /**
     * Sets the cache files before playing.
     * 
     * @param cacheFiles
     *            the new cache files before playing
     */
    private void setCacheFilesBeforePlaying(boolean cacheFiles) {
        this.cacheFilesBeforePlaying.setSelected(cacheFiles);
    }

    private void setHotkeysConfig(IHotkeysConfig hotkeysConfig) {
        this.tableModel.setHotkeysConfig(hotkeysConfig);
    }

    private void setPlayerEngine(String playerEngine) {
        engineCombo.setSelectedItem(playerEngine);
    }

    @Override
    public void updatePanel(IState state) {
        setPlayerEngine(state.getPlayerEngine());
        setPlayAtStartup(state.isPlayAtStartup());
        setUseFadeAway(state.isUseFadeAway());
        setShowTicks(state.isShowTicks());
        setShowAdvancedPlayerControls(state.isShowAdvancedPlayerControls());
        setShowPlayerControlsOnTop(state.isShowPlayerControlsOnTop());
        setCacheFilesBeforePlaying(state.isCacheFilesBeforePlaying());
        setEnableHotkeys(state.isEnableHotkeys());
        IHotkeysConfig hotkeysConfig = state.getHotkeysConfig();
        setHotkeysConfig(hotkeysConfig != null ? hotkeysConfig : hotkeyHandler.getHotkeysConfig());
        setUseShortPathNames(state.isUseShortPathNames());
        getUseShortPathNames().setEnabled(osManager.usesShortPathNames());
        
        boolean hotKeysSupported = hotkeyHandler.areHotkeysSupported();
        getEnableGlobalHotkeys().setVisible(hotKeysSupported);
        getHotkeyScrollPane().setVisible(hotKeysSupported);
        getResetHotkeys().setVisible(hotKeysSupported);
    }

    @Override
    public void resetImmediateChanges(IState state) {
        // Do nothing
    }

    @Override
    public void validatePanel() throws PreferencesValidationException {
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Use this method from PreferencesPanel instead of override setVisible
        // As Dialog uses CardLayout to show panels, that layout invokes setVisible(false) when creating dialog just before showing it
        // so it can execute both code blocks before showing dialog
        if (visible) {
            hotkeyHandler.disableHotkeys();
        } else {
            // Enable hotkeys again only if user didn't disable them
            if (getState().isEnableHotkeys()) {
                hotkeyHandler.enableHotkeys(getState().getHotkeysConfig());
            }
        }
    }

	/**
	 * @return hotkey scroll pane
	 */
	private JScrollPane getHotkeyScrollPane() {
		return hotkeyScrollPane;
	}

	/**
	 * @return the resetHotkeys
	 */
	private JButton getResetHotkeys() {
		return resetHotkeys;
	}

}