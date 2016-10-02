package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata.Nullability;
import com.github.dyna4jdbc.internal.config.Configuration;

final class EmptyColumnMetadataFactory implements ColumnMetadataFactory {

    private static final int DEFAULT_COLUMN_DISPLAY_SIZE = 4;

    private static final EmptyColumnMetadataFactory INSTANCE = new EmptyColumnMetadataFactory();

    static EmptyColumnMetadataFactory getInstance(Configuration configuration) {
        return INSTANCE;
    }

    private EmptyColumnMetadataFactory() {
    }

    @Override
    public ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValues) {
        
        final int sqlColumnIndex = columnIndex + 1;
        
        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        metadata.setConsumesFirstRow(false);
        metadata.setCurrency(false);
        metadata.setNullability(Nullability.UNKNOWN);
        metadata.setSigned(false);
        metadata.setColumnDisplaySize(DEFAULT_COLUMN_DISPLAY_SIZE);
        metadata.setColumnLabel(String.valueOf(sqlColumnIndex));
        metadata.setColumnName(String.valueOf(sqlColumnIndex));
        metadata.setPrecision(0);
        metadata.setScale(0);
        metadata.setColumnType(SQLDataType.VARCHAR);

        return metadata;
    }

}
