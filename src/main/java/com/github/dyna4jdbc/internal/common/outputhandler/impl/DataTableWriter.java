package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.github.dyna4jdbc.internal.common.datamodel.DataTable;

public class DataTableWriter extends CursorCellWriterOutputStream{

    private LinkedList<DataTable> dataTableList = new LinkedList<>();
    private List<String> currentRow = new ArrayList<String>();

    public DataTableWriter() {
        dataTableList.addLast(new DataTable());

    }


    public List<DataTable> getDataTableList() {
        return Collections.unmodifiableList(dataTableList);
    }

    @Override
    protected void nextCell() {
        // no-op
    }

    @Override
    protected void nextRow() {
        appendRow();
        currentRow = new ArrayList<String>();
    }


    @Override
    protected void writeCellValue(String value) {
        currentRow.add(value);
    }

    @Override
    public void close() throws IOException {
        super.close(); // ensure all pending content is written to currentRow

        if(! currentRow.isEmpty()) {
            appendRow();
        }
    }

    private void appendRow() {
        DataTable currentTable = dataTableList.getLast();

        if (!currentTable.isEmpty()) {

            List<String> lastRow = currentTable.getLastRow();
            if (lastRow.size() != currentRow.size()) {
                currentTable = new DataTable();
                dataTableList.addLast(currentTable);
            }
        }

        currentTable.appendRow(currentRow);
    }
}
