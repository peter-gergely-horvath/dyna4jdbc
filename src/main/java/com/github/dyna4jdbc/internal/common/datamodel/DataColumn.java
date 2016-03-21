package com.github.dyna4jdbc.internal.common.datamodel;


import java.util.Iterator;

public class DataColumn implements Iterable<String>  {

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
    public Iterator<String> iterator() {
        final Iterator<DataRow> delegate = dataTable.getRows().iterator();

        return new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public String next() {
                DataRow dataRow = delegate.next();
                return dataRow.getCell(columnNumber);
            }
        };
    }
}
