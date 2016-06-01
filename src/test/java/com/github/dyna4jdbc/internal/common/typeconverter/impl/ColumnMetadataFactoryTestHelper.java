package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;

/**
 * @author Peter Horvath
 */
class ColumnMetadataFactoryTestHelper {

    private ColumnMetadataFactoryTestHelper() {
        // static utility class
    }

    static final String TEST_COLUMN_INDEX = "1";

    static ColumnMetadata varcharColumnMetadata(
            int scale, String header, ColumnMetadata.Nullability nullability) {

        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        metadata.setConsumesFirstRowValue(false);
        metadata.setColumnDisplaySize(scale);
        metadata.setNullability(nullability);
        metadata.setSigned(false);
        metadata.setColumnDisplaySize(scale);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(0);
        metadata.setScale(scale);
        metadata.setColumnType(SQLDataType.VARCHAR);
        metadata.setFormatString(null);

        return metadata;
    }

    static ColumnMetadata integerColumnMetadata(
            int scale, String header, ColumnMetadata.Nullability nullability) {

        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        metadata.setConsumesFirstRowValue(false);
        metadata.setColumnDisplaySize(scale);
        metadata.setNullability(nullability);
        metadata.setSigned(true);
        metadata.setColumnDisplaySize(scale);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(0);
        metadata.setScale(scale);
        metadata.setColumnType(SQLDataType.INTEGER);
        metadata.setFormatString(null);

        return metadata;
    }

    static ColumnMetadata doubleColumnMetadata(
            int scale, int precision, String header, ColumnMetadata.Nullability nullability) {

        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        final int expectedColumnDisplaySize;
        if(precision == 0) {
            expectedColumnDisplaySize = scale;
        } else {
            // include decimal point in ColumnDisplaySize
            expectedColumnDisplaySize =  scale + 1;
        }

        metadata.setConsumesFirstRowValue(false);
        metadata.setNullability(nullability);
        metadata.setSigned(true);
        metadata.setColumnDisplaySize(expectedColumnDisplaySize);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(precision);
        metadata.setScale(scale);
        metadata.setColumnType(SQLDataType.DOUBLE);
        metadata.setFormatString(null);

        return metadata;
    }

    static ColumnMetadata timestampColumnMetadata(
            String header, ColumnMetadata.Nullability nullability) {

        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        metadata.setConsumesFirstRowValue(false);
        metadata.setNullability(nullability);
        metadata.setSigned(true);
        metadata.setColumnDisplaySize(23);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(0);
        metadata.setScale(0);
        metadata.setColumnType(SQLDataType.TIMESTAMP);
        metadata.setFormatString(null);

        return metadata;
    }
}
