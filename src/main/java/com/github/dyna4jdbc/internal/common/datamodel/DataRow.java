package com.github.dyna4jdbc.internal.common.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class DataRow implements Iterable<String> {

    private ArrayList<String> dataCells;
    DataTable owner;


    void setOwner(DataTable dataTable) {
        this.owner = dataTable;
    }

    @Override
    public Iterator<String> iterator() {
        if (dataCells == null) {
            return Collections.emptyIterator();
        } else {
            return dataCells.iterator();
        }
    }

    public int cellNumber() {
        if (dataCells == null) {
            return 0;
        } else {
            return dataCells.size();
        }
    }

    public String getCell(int column) {
        if (dataCells == null) {
            return null;
        }
        if (column < dataCells.size()) {
            return dataCells.get(column);
        } else {
            return null;
        }
    }

    public String getCell(DataColumn column) {
        return getCell(column.columnNumber);
    }

    public void appendDataCell(String value) {
        if (dataCells == null) {
            dataCells = new ArrayList<>();
        }
        dataCells.add(value);
    }

    public boolean isValidIndex(int column) {
        if (column < 0) {
            return false;
        }

        if (dataCells == null || dataCells.isEmpty()) {
            return false;
        }

        return column < dataCells.size();
    }

    public boolean isEmpty() {
        return dataCells == null || dataCells.isEmpty();
    }

    public void clear() {
        if (dataCells != null) {
            dataCells.clear();
        }
    }
}
