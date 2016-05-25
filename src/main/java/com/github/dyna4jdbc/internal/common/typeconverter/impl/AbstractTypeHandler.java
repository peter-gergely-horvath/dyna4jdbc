package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandler;

public abstract class AbstractTypeHandler implements TypeHandler {

    //CHECKSTYLE.OFF: VisibilityModifier
    protected final ColumnMetadata columnMetadata;
    //CHECKSTYLE.ON: VisibilityModifier

    protected AbstractTypeHandler(ColumnMetadata columnMetadata) {
        this.columnMetadata = columnMetadata;
    }

    @Override
    public final ColumnMetadata getColumnMetadata() {
        return columnMetadata;
    }

}
