package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata.Nullability;
import com.github.dyna4jdbc.internal.config.Configuration;

public final class EmptyColumnMetadataFactory implements ColumnMetadataFactory {

    private static final EmptyColumnMetadataFactory INSTANCE = new EmptyColumnMetadataFactory();

    static EmptyColumnMetadataFactory getInstance(Configuration configuration) {
        return INSTANCE;
    }

    private EmptyColumnMetadataFactory() {
    }

    @Override
    public ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValuesIterable) {
        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        metadata.setTakesFirstRowValue(false);
        metadata.setCurrency(false);
        metadata.setNullability(Nullability.UNKNOWN);
        metadata.setSigned(false);
        metadata.setColumnDisplaySize(4);
        metadata.setColumnLabel("");
        metadata.setColumnName("");
        metadata.setPrecision(0);
        metadata.setScale(0);
        metadata.setColumnType(SQLDataType.VARCHAR);

        return metadata;
    }

}
