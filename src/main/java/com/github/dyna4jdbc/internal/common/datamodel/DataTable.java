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

    public DataColumn getColumn(int index) {

        return new DataColumn(this, index);
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Iterator<DataRow> rowIterator() {
        return rows.iterator();
    }

    public Iterator<DataColumn> columnIterator() {
        return columns.iterator();
    }

    public List<DataColumn> getColumns() {
        for(int i=0; i<columnCount; i++) {
            getColumn(i);
        }
        return Collections.unmodifiableList(columns);
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
