package com.github.dyna4jdbc.internal.common.datamodel;

import java.util.*;

public class DataTable implements Iterable<List<String>> {

    private LinkedList<List<String>> rows = new LinkedList<>();
    private int columnCount = 0;

    public List<String> appendRow(List<String> row) {
        rows.addLast(row);
        columnCount = Math.max(columnCount, row.size());

        return row;
    }

    public List<String> getLastRow() {
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

    LinkedList<List<String>> getRows() {
        return rows;
    }

    public void clear() {
        rows.forEach(theRow -> theRow.clear());
        rows.clear();
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

	@Override
	public Iterator<List<String>> iterator() {
		return rows.iterator();
	}


}
