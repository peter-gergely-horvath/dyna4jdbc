package com.github.dyna4jdbc.internal.common.datamodel;

import com.github.dyna4jdbc.internal.common.util.collection.ListIndexIterable;

public class DataColumn extends ListIndexIterable<String>  {

    DataColumn(DataTable dataTable, int columnNumber) {
        super(dataTable.getRows(), columnNumber);
    }
}
