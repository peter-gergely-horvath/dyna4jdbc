package com.github.dyna4jdbc.internal.scriptengine;

import java.util.*;

public class DataTable implements Iterable<DataTable.Row> {

    private LinkedList<Row> rows = new LinkedList<>();

    public Row appendRow(Row row) {
        rows.addLast(row);
        return row;
    }

    public Row appendRow() {
        Row row = new Row();
        rows.addLast(row);
        return row;
    }

    public Row getLastRow() {
        if (rows.isEmpty()) {
            appendRow();
        }
        return rows.getLast();
    }

    @Override
    public Iterator<Row> iterator() {
        return rows.iterator();
    }

    public class Row implements Iterable<DataTable.Row.Cell> {

        @Override
        public Iterator<Cell> iterator() {
            if (cells == null) {
                return Collections.emptyIterator();
            } else {
                return cells.iterator();
            }
        }


        public class Cell {
            private String value;

            private Cell(String value) {
                this.value = value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }

            public Column getColumn() {
                return new Column(cells.indexOf(Cell.this));
            }
        }

        private ArrayList<DataTable.Row.Cell> cells;

        public int getNumberOfCells() {
            if (cells == null) {
                return 0;
            } else {
                return cells.size();
            }
        }

        public Cell getCell(int column) {
            if (cells == null) {
                return null;
            }
            if (column < cells.size()) {
                return cells.get(column);
            } else {
                return null;
            }
        }

        public Cell getCell(Column column) {
            return getCell(column.columnNumber);
        }

        public void appendCell(String value) {
            if (cells == null) {
                cells = new ArrayList<>();
            }
            cells.add(new Cell(value));
        }

        public boolean isValidIndex(int column) {
            if (column < 0) {
                return false;
            }

            if (cells == null || cells.isEmpty()) {
                return false;
            }

            return column < cells.size();
        }

        public boolean isEmpty() {
            return cells == null || cells.isEmpty();
        }

        public void clear() {
            if (cells != null) {
                cells.clear();
            }
        }
    }

    public class Column implements Iterable<DataTable.Row.Cell> {

        final int columnNumber;

        private Column(int columnNumber) {
            this.columnNumber = columnNumber;
        }


        @Override
        public Iterator<Row.Cell> iterator() {
            Iterator<Row> delegate = rows.iterator();

            return new Iterator<Row.Cell>() {
                @Override
                public boolean hasNext() {
                    return delegate.hasNext();
                }

                @Override
                public Row.Cell next() {
                    return delegate.next().getCell(columnNumber);
                }
            };
        }
    }

    public void clear() {
        rows.forEach(row -> row.clear());
        rows.clear();
    }

    public boolean getAllRowsAreOfSameLength() {

        int numberOfCells = -1;
        for(Row row : this.rows) {
            if (numberOfCells == -1) {
                numberOfCells = row.getNumberOfCells();
            } else {
                if (numberOfCells != row.getNumberOfCells()) {
                    return false;
                }
            }
        }

        return true;
    }

    public List<DataTable> partitionByRowLengthDifferences() {

        LinkedList<DataTable> result = new LinkedList<>();

        DataTable outputDataTable = new DataTable();
        result.addLast(outputDataTable);


        Row previousRow = null;

        Iterator<Row> iterator = this.rows.iterator();
        while (iterator.hasNext()) {

            Row thisRow = iterator.next();

            if (previousRow != null &&
                    previousRow.getNumberOfCells() != thisRow.getNumberOfCells()) {

                outputDataTable = new DataTable();
                result.addLast(outputDataTable);
            }

            outputDataTable.appendRow(thisRow);
            previousRow = thisRow;
        }

        return Collections.unmodifiableList(result);
    }


}
