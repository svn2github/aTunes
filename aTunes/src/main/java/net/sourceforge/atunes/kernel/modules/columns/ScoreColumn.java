/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.gui.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IRepositoryHandler;

/**
 * Column showing score
 * 
 * @author alex
 * 
 */
public class ScoreColumn extends AbstractColumn<Integer> {

	private final class ScoreColumnCellEditorRenderer extends
			AbstractListCellRendererCode<JLabel, Integer> {

		@Override
		public JComponent getComponent(final JLabel superComponent,
				final JList list, final Integer value, final int index,
				final boolean isSelected, final boolean cellHasFocus) {
			setLabel(superComponent, value);
			return superComponent;
		}
	}

	private static final long serialVersionUID = -2673502888298485650L;

	private transient ILookAndFeelManager lookAndFeelManager;

	private IIconFactory star1Icon;
	private IIconFactory star2Icon;
	private IIconFactory star3Icon;
	private IIconFactory star4Icon;
	private IIconFactory star5Icon;

	private static final Integer[] STARS = new Integer[] { 0, 1, 2, 3, 4, 5 };

	private IRepositoryHandler repositoryHandler;

	private final transient ScoreColumnCellEditorRenderer editor = new ScoreColumnCellEditorRenderer();

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param star1Icon
	 */
	public void setStar1Icon(final IIconFactory star1Icon) {
		this.star1Icon = star1Icon;
	}

	/**
	 * @param star2Icon
	 */
	public void setStar2Icon(final IIconFactory star2Icon) {
		this.star2Icon = star2Icon;
	}

	/**
	 * @param star3Icon
	 */
	public void setStar3Icon(final IIconFactory star3Icon) {
		this.star3Icon = star3Icon;
	}

	/**
	 * @param star4Icon
	 */
	public void setStar4Icon(final IIconFactory star4Icon) {
		this.star4Icon = star4Icon;
	}

	/**
	 * @param star5Icon
	 */
	public void setStar5Icon(final IIconFactory star5Icon) {
		this.star5Icon = star5Icon;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Default constructor
	 */
	public ScoreColumn() {
		super("SCORE");
		setWidth(100);
		setVisible(true);
		setEditable(true);
	}

	@Override
	public TableCellEditor getCellEditor() {
		JComboBox comboBox = new JComboBox(STARS);
		comboBox.setRenderer(this.lookAndFeelManager.getCurrentLookAndFeel()
				.getListCellRenderer(this.editor));
		return new DefaultCellEditor(comboBox);
	}

	@Override
	public TableCellRenderer getCellRenderer() {
		return this.lookAndFeelManager.getCurrentLookAndFeel()
				.getTableCellRenderer(
						new AbstractTableCellRendererCode<JLabel, Integer>() {

							@Override
							public JLabel getComponent(
									final JLabel superComponent,
									final JTable t, final Integer value,
									final boolean isSelected,
									final boolean hasFocus, final int row,
									final int column) {
								setLabel(superComponent, value);
								return superComponent;
							}
						});
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return -((Integer) ao1.getStars()).compareTo(ao2.getStars());
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return -ascendingCompare(ao1, ao2);
	}

	@Override
	public Integer getValueFor(final IAudioObject audioObject, final int row) {
		return audioObject.getStars();
	}

	@Override
	public void setValueFor(final IAudioObject audioObject, final Object value) {
		this.repositoryHandler.setStars((ILocalAudioObject) audioObject,
				(Integer) value);
	}

	/**
	 * Sets proper icon and text
	 * 
	 * @param label
	 * @param score
	 */
	private void setLabel(final JLabel label, final Integer score) {
		label.setText(" "); // Set to white space to display label
		IIconFactory icon = score != null ? getIcon(score) : null;
		// TODO: ICONOS Sacar a un renderer
		label.setIcon(icon != null ? icon.getIcon(this.lookAndFeelManager
				.getCurrentLookAndFeel().getPaintForColorMutableIcon(label,
						false)) : null);
	}

	/**
	 * Returns icon for score
	 * 
	 * @param score
	 * @return
	 */
	private IIconFactory getIcon(final int score) {
		switch (score) {
		case 1:
			return this.star1Icon;
		case 2:
			return this.star2Icon;
		case 3:
			return this.star3Icon;
		case 4:
			return this.star4Icon;
		case 5:
			return this.star5Icon;
		}
		return null;
	}
}
