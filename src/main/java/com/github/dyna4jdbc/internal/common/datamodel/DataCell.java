package com.github.dyna4jdbc.internal.common.datamodel;

public class DataCell {

    private final DataRow dataRow;

    private final String value;
    private final int columnIndex;

    DataCell(DataRow dataRow, String value, int columnIndex) {
        this.dataRow = dataRow;
        this.value = value;
        this.columnIndex = columnIndex;
    }

    public String getValue() {
        return value;
    }


    public int getColumnIndex() {
        return columnIndex;
    }
}