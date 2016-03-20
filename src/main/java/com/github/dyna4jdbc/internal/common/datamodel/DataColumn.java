package com.github.dyna4jdbc.internal.common.datamodel;


import java.util.Iterator;

public class DataColumn implements Iterable<DataCell>  {

    private final DataTable dataTable;
    final int columnNumber;

    DataColumn(DataTable dataTable, int columnNumber) {
        this.dataTable = dataTable;
        this.columnNumber = columnNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    @Override
    public Iterator<DataCell> iterator() {
        final Iterator<DataRow> delegate = dataTable.getRows().iterator();

        return new Iterator<DataCell>() {
            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public DataCell next() {
                DataRow dataRow = delegate.next();
                return dataRow.getCell(columnNumber);
            }
        };
    }

    public Iterable<String> valueIterable() {
    	
    	return new Iterable<String>() {

			@Override
			public Iterator<String> iterator() {
		        Iterator<DataCell> delegate = DataColumn.this.iterator();

		        return new Iterator<String>() {
		            @Override
		            public boolean hasNext() {
		                return delegate.hasNext();
		            }

		            @Override
		            public String next() {
		                DataCell cell = delegate.next();
		                return cell.getValue();
		            }
		        };
			}
    		
    	};
    }
}
