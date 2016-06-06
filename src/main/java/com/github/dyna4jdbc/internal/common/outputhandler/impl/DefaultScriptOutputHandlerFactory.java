package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.common.outputhandler.*;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.sql.Statement;

public final class DefaultScriptOutputHandlerFactory implements ScriptOutputHandlerFactory {

    private final ColumnHandlerFactory columnHandlerFactory;
    private final Configuration configuration;

    public DefaultScriptOutputHandlerFactory(ColumnHandlerFactory columnHandlerFactory, Configuration configuration) {
        this.columnHandlerFactory = columnHandlerFactory;
        this.configuration = configuration;

    }

    @Override
    public SingleResultSetScriptOutputHandler newSingleResultSetQueryScriptOutputHandler(
            Statement statement,
            String script,
            SQLWarningSink warningSink) {

        return new DefaultSingleResultSetScriptOutputHandler(
                statement, columnHandlerFactory, configuration, warningSink);
    }

    @Override
    public MultipleResultSetScriptOutputHandler newUpdateOrQueryScriptOutputHandler(
            Statement statement,
            String script,
            SQLWarningSink warningSink) {

        return new DefaultMultipleResultSetScriptOutputHandler(
                statement, columnHandlerFactory, configuration, warningSink);
    }

    @Override
    public UpdateScriptOutputHandler newUpdateScriptOutputHandler(
            Statement statement,
            String script,
            SQLWarningSink warningSink) {

        return new DefaultUpdateScriptOutputHandler(this.configuration, warningSink);
    }


}
