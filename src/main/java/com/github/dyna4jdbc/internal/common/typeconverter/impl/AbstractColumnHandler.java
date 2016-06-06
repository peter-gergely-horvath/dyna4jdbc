package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;

public abstract class AbstractColumnHandler implements ColumnHandler {

    //CHECKSTYLE.OFF: VisibilityModifier
    protected final ColumnMetadata columnMetadata;
    //CHECKSTYLE.ON: VisibilityModifier

    protected AbstractColumnHandler(ColumnMetadata columnMetadata) {
        this.columnMetadata = columnMetadata;
    }

    @Override
    public final ColumnMetadata getColumnMetadata() {
        return columnMetadata;
    }

}
