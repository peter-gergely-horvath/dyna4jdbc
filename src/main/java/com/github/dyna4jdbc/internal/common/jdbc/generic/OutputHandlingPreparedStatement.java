package com.github.dyna4jdbc.internal.common.jdbc.generic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;

public final class OutputHandlingPreparedStatement<T extends java.sql.Connection>
        extends OutputHandlingStatement<T>
        implements java.sql.PreparedStatement {

    private final String script;

    private final HashMap<String, Object> executionContext = new HashMap<>();

    public OutputHandlingPreparedStatement(String script, T connection,
            ScriptOutputHandlerFactory scriptOutputHandlerFactory,
            OutputCapturingScriptExecutor outputCapturingScriptExecutor) {

        super(connection, scriptOutputHandlerFactory, outputCapturingScriptExecutor);
        this.script = script;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return super.executeQuery(script);
    }

    @Override
    public int executeUpdate() throws SQLException {
        return super.executeUpdate(script);
    }

    @Override
    public boolean execute() throws SQLException {
        return super.execute(script);
    }

    @Override
    protected void executeScriptUsingOutputHandler(
            String scriptToExecute,
            ScriptOutputHandler scriptOutputHandler) throws ScriptExecutionException, IOException {

        OutputStream outOutputStream = scriptOutputHandler.getOutOutputStream();
        OutputStream errorOutputStream = scriptOutputHandler.getErrorOutputStream();

        OutputCapturingScriptExecutor scriptExecutor = getOutputCapturingScriptExecutor();


        scriptExecutor.executeScriptUsingStreams(scriptToExecute,
                Collections.unmodifiableMap(executionContext),
                outOutputStream, errorOutputStream);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return new EmptyParameterMetaData();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.PreparedStatement.getMetaData()");
    }

    private void setParameter(int parameterIndex, Object value) {
        String key = String.format("parameter%s", parameterIndex);

        executionContext.put(key, value);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, null);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @SuppressWarnings("deprecation")
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void clearParameters() throws SQLException {
        checkNotClosed();

        executionContext.clear();

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void addBatch() throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.PreparedStatement.addBatch()");
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED
                .raiseSQLException("java.sql.PreparedStatement.setCharacterStream(int, Reader, int)");

    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.PreparedStatement.setRef(int, Ref)");
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, null);

    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, value);

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED
                .raiseSQLException("java.sql.PreparedStatement.setNCharacterStream(int, Reader, length)");

    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, value);

    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED
                .raiseSQLException("java.sql.PreparedStatement.setClob(int, Reader, long)");

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED
                .raiseSQLException("java.sql.PreparedStatement.setBlob(int, InputStream, long)");

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED
                .raiseSQLException("java.sql.PreparedStatement.setNClob(int, Reader, long)");

    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, xmlObject);

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED
                .raiseSQLException("java.sql.PreparedStatement.setObject(int, Object, int, int)");
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED
                .raiseSQLException("java.sql.PreparedStatement.setAsciiStream(int, InputStream, long)");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED
                .raiseSQLException("java.sql.PreparedStatement.setBinaryStream(int, InputStream, long)");
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED
                .raiseSQLException("java.sql.PreparedStatement.setCharacterStream(int, Reader, long)");

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, x);

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, reader);

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, value);

    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, reader);

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, inputStream);

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        checkNotClosed();

        setParameter(parameterIndex, reader);

    }
}
