/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.gui.model.ColumnSetTableModel;
import net.sourceforge.atunes.gui.model.CommonColumnModel;
import net.sourceforge.atunes.kernel.modules.columns.Column;
import net.sourceforge.atunes.model.AudioObject;

public class ColumnSetRowSorter {

	private JTable table;
	private ColumnSetTableModel model;
	private CommonColumnModel columnModel;
	
	private Column lastColumnSorted;
	
	public ColumnSetRowSorter(JTable table, ColumnSetTableModel model, CommonColumnModel columnModel) {
		this.table = table;
		this.model = model;
		this.columnModel = columnModel;
		setListeners();
	}
	
	private void setListeners() {
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					int columnClickedIndex = table.getTableHeader().getColumnModel().getColumnIndexAtX(e.getX());
					if (columnClickedIndex != -1) {
						// Get column
						Column columnClicked = ColumnSetRowSorter.this.columnModel.getColumnObject(columnClickedIndex);
						if (lastColumnSorted != null && !lastColumnSorted.equals(columnClicked)) {
							lastColumnSorted.setColumnSort(null);
						}
						lastColumnSorted = columnClicked;
						if (columnClicked.isSortable()) {
							sort(columnClicked.getComparator());
						}
					}
				}
			}
		});
	}	

	/**
	 * Method to sort a column set. It must sort the underlying data
	 * @param comparator
	 */
	protected void sort(Comparator<AudioObject> comparator) {
		
		// Sort model
		this.model.sort(comparator);
		
        // Refresh model
        model.refresh(TableModelEvent.UPDATE);        
	}
}
