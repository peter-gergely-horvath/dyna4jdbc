package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.common.datamodel.DataRow;
import com.github.dyna4jdbc.internal.common.datamodel.DataTable;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DataTableWriter extends CursorCellWriterOutputStream{

    private LinkedList<DataTable> dataTableList = new LinkedList<>();
    private DataRow currentRow = new DataRow();

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
        currentRow = new DataRow();
    }


    @Override
    protected void writeCellValue(String value) {
        currentRow.appendDataCell(value);
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

            DataRow lastRow = currentTable.getLastRow();
            if (lastRow.cellNumber() != currentRow.cellNumber()) {
                currentTable = new DataTable();
                dataTableList.addLast(currentTable);
            }
        }

        currentTable.appendRow(currentRow);
    }
}
