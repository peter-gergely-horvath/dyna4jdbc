package com.github.dyna4jdbc.internal.scriptengine.outputhandler.impl;

import com.github.dyna4jdbc.internal.scriptengine.DataTable;

public class DataTableWriter extends CursorCellWriterOutputStream{

    private DataTable dataTable = new DataTable();
    private boolean shouldStartNewRow = false;

    public DataTable getDataTable() {
        return dataTable;
    }

    @Override
    protected void nextCell() {
        // no-op
    }

    @Override
    protected void nextRow() {
        shouldStartNewRow = true;
    }


    @Override
    protected void writeCellValue(String value) {
        DataTable.Row currentRow;

        if(shouldStartNewRow) {
            currentRow = dataTable.appendRow();
            shouldStartNewRow = false;

        } else {
            currentRow = dataTable.getLastRow();

        }
        currentRow.appendCell(value);
    }
}
