package com.github.dyna4jdbc.internal.common.datamodel;

import java.util.*;

public final class DataTable implements Iterable<List<String>>, AutoCloseable {

    private final LinkedList<List<String>> rows = new LinkedList<>();
    private int columnCount = 0;

    public List<String> appendRow(List<String> row) {
        if (row == null) {
            throw new NullPointerException("argument row cannot be null");
        }

        rows.addLast(row);
        columnCount = Math.max(columnCount, row.size());

        return row;
    }

    public List<String> getLastRow() {
        return rows.getLast();
    }

    public Iterable<DataColumn> columnIterable() {
        return () -> new Iterator<DataColumn>() {

            private int index = 0;

            @Override
            public DataColumn next() {
                if (!hasNext()) {
                    throw new NoSuchElementException(Integer.toString(index));
                }

                return new DataColumn(DataTable.this, index++);
            }

            @Override
            public boolean hasNext() {
                return index < columnCount;
            }
        };
    }

    public int getColumnCount() {
        return columnCount;
    }

    public LinkedList<List<String>> getRows() {
        return rows;
    }

    public void clear() {
        /*
        Technically, clearing the content of the nested lists is "unnecessary",
        but we still perform it as a simple optimisation since we assume that
        a DataTable might hold _significant_ amount of data.

        This approach
            - helps JVM implementations, which use generational garbage
              collection in case the nested Lists inhabit more than one
              GC generation. E.g. a list reference that made its way to
              Tenured Generation will not prevent collection of nested
              elements present in Eden or Survivor Generations only.

            - allows freeing memory right after a Result Set is closed,
              and not only after the containing Result Set is collected.
         */
        rows.forEach(List::clear);
        rows.clear();
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    @Override
    public Iterator<List<String>> iterator() {
        return rows.iterator();
    }


    @Override
    public void close() throws Exception {
        /*
        Discard contained data immediately on close and
        do not wait until garbage collection happens.
        */
        this.clear();
    }
}
