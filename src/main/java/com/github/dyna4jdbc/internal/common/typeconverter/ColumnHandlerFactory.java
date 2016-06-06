package com.github.dyna4jdbc.internal.common.typeconverter;


public interface ColumnHandlerFactory {

    ColumnHandler newTypeHandler(int columnIndex, Iterable<String> columnIterable);
}
