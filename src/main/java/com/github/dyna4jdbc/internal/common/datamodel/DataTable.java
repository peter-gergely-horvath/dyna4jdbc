package com.github.dyna4jdbc.internal.common.datamodel;

import java.util.*;

public class DataTable {

    private LinkedList<DataRow> rows = new LinkedList<>();
    private ArrayList<DataColumn> columns = new ArrayList<>();
    private int columnCount = 0;

    public DataRow appendRow(DataRow row) {
        rows.addLast(row);
        row.owner = this;
        columnCount = Math.max(columnCount, row.cellNumber());

        return row;
    }

    public DataRow getLastRow() {
        return rows.getLast();
    }
    
	public Iterable<DataColumn> columnIterable() {
		return new Iterable<DataColumn>() {
			
			@Override
			public Iterator<DataColumn> iterator() {
				return new Iterator<DataColumn>() {
					
					private int index = 0;
					
					@Override
					public DataColumn next() {
						return new DataColumn(DataTable.this, index++);
					}
					
					@Override
					public boolean hasNext() {
						return index < columnCount;
					}
				};
			}
		};
	}

    public int getColumnCount() {
        return columnCount;
    }

    public Iterator<DataRow> rowIterator() {
        return rows.iterator();
    }

    LinkedList<DataRow> getRows() {
        return rows;
    }

    public void clear() {
        columns.clear();
        rows.forEach(theRow -> theRow.clear());
        rows.clear();
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }


}
