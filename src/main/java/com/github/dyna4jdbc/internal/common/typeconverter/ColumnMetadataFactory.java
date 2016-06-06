package com.github.dyna4jdbc.internal.common.typeconverter;

public interface ColumnMetadataFactory {

    ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValues);
}
