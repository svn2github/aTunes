package net.sourceforge.atunes.gui.model;

import net.sourceforge.atunes.kernel.modules.columns.Column;
import net.sourceforge.atunes.kernel.modules.columns.ColumnSet;
import net.sourceforge.atunes.utils.I18nUtils;

public abstract class ColumnSetTableModel extends CommonTableModel {
	
	private ColumnSet columnSet;
	
	public ColumnSetTableModel(ColumnSet columnSet) {
		super();
		this.columnSet = columnSet;		
	}
	
    /**
     * Returns column data class.
     * 
     * @param colIndex
     *            the col index
     * 
     * @return the column class
     */
    @Override
    public Class<?> getColumnClass(int colIndex) {
        return getColumn(colIndex).getColumnClass();
    }
    
    /**
     * Return column count.
     * 
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return columnSet.getVisibleColumnCount();
    }

    /**
     * Return column name.
     * 
     * @param colIndex
     *            the col index
     * 
     * @return the column name
     */
    @Override
    public String getColumnName(int colIndex) {
        return I18nUtils.getString(getColumn(colIndex).getHeaderText());
    }

    /**
     * Returns column in given index
     * @param colIndex
     * @return
     */
    protected Column getColumn(int colIndex) {
    	return columnSet.getColumn(columnSet.getColumnId(colIndex));
    }


	

}
