package com.github.dyna4jdbc.internal.common.typeconverter;


public interface ColumnHandlerFactory {

    ColumnHandler newColumnHandler(int columnIndex, Iterable<String> columnValues);
}
