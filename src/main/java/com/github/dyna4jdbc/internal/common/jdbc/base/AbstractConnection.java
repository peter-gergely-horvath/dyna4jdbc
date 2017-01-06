/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.util.sqlwarning.SQLWarningContainer;


public abstract class AbstractConnection extends AbstractAutoCloseableJdbcObject implements java.sql.Connection {

    private static final Logger LOGGER = Logger.getLogger(AbstractConnection.class.getName());

    private static final int SUPPORTED_RESULT_SET_TYPE = ResultSet.TYPE_FORWARD_ONLY;
    private static final int SUPPORTED_RESULT_SET_CONCURRENCY = ResultSet.CONCUR_READ_ONLY;
    private static final int SUPPORTED_HOLDABILITY = ResultSet.HOLD_CURSORS_OVER_COMMIT;

    // --- properties used only to provide a sensible default JDBC interface implementation ---
    private LinkedHashMap<String, Class<?>> typeMap = new LinkedHashMap<>();
    private Properties clientInfo = new Properties();

    private final SQLWarningContainer warningContainer = new SQLWarningContainer();

    protected AbstractConnection() {
        super(null);
    }
    // ----------------------------------------------------------------------------------------

    @Override
    public final Statement createStatement() throws SQLException {
        checkNotClosed();

        /*
        To ensure, that Statements created from within this Connection
        are always registered, we introduced createStatementInternal()
        internal template method: all concrete subclasses have to
        implement that and can forget about registering the object
        completely.

        This method is final, hence it cannot be overridden accidentally.
         */
        Statement createdStatement = createStatementInternal();

        registerAsChild(createdStatement);

        return createdStatement;
    }

    protected abstract Statement createStatementInternal() throws SQLException;
    
    @Override
    public final Statement createStatement(
            int resultSetType,
            int resultSetConcurrency) throws SQLException {

        checkNotClosed();

        /*
        A client application can specify the requested ResultSet type and
        concurrency. We are prepared to handle cases, where the driver is
        inquired about its capabilities and this method is called with the
        supported type and concurrency combination: the call is delegated
        to the standard createStatement() method.

        Every other case is rejected as error JDBC_FEATURE_NOT_SUPPORTED.
        */

        if (resultSetType == SUPPORTED_RESULT_SET_TYPE
            && resultSetConcurrency == SUPPORTED_RESULT_SET_CONCURRENCY) {

            return createStatement();
        }

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "Creating non-forward-only or non read-only statements");
    }
    
       
    @Override
    public final Statement createStatement(
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {

        checkNotClosed();

        /*
        A client application can specify the requested ResultSet type,
        concurrency and holdability. We are prepared to handle cases,
        where the driver is inquired about its capabilities and this
        method is called with the supported type, concurrency
        holdability combination: the call is delegated to the standard
        createStatement() method.

        Every other case is rejected as error JDBC_FEATURE_NOT_SUPPORTED.
        */

        if (resultSetType == SUPPORTED_RESULT_SET_TYPE
                && resultSetConcurrency == SUPPORTED_RESULT_SET_CONCURRENCY
                && resultSetHoldability == SUPPORTED_HOLDABILITY) {

                return createStatement();
            }

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "Creating non-forward-only, non read-only or not over-commit held statements");
    }

    @Override
    public final PreparedStatement prepareStatement(String script) throws SQLException {
        checkNotClosed();

        /*
        To ensure, that PreparedStatement created from within this
        Connection are always registered, we introduced
        prepareStatementInternal() internal template method:
        all concrete subclasses have to implement that and can
        forget about registering the object completely.

        This method is final, hence it cannot be overridden accidentally.
         */
        PreparedStatement createdPreparedStatement = prepareStatementInternal(script);

        registerAsChild(createdPreparedStatement);

        return createdPreparedStatement;
    }

    protected abstract PreparedStatement prepareStatementInternal(String script) throws SQLException;

    @Override
    public final PreparedStatement prepareStatement(
            String sql,
            int resultSetType,
            int resultSetConcurrency) throws SQLException {

        checkNotClosed();

        /*
        A client application can specify the requested ResultSet type and
        concurrency. We are prepared to handle cases, where the driver is
        inquired about its capabilities and this method is called with the
        supported type and concurrency combination: the call is delegated
        to the standard prepareStatement() method.

        Every other case is rejected as error JDBC_FEATURE_NOT_SUPPORTED.
        */

        if (resultSetType == SUPPORTED_RESULT_SET_TYPE
            && resultSetConcurrency == SUPPORTED_RESULT_SET_CONCURRENCY) {

            return prepareStatement(sql);
        }

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "Creating non-forward-only or non read-only prepareStatement");
    }

    @Override
    public final PreparedStatement prepareStatement(
            String sql,
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {

        checkNotClosed();

        /*
        A client application can specify the requested ResultSet type,
        concurrency and holdability. We are prepared to handle cases,
        where the driver is inquired about its capabilities and this
        method is called with the supported type, concurrency
        holdability combination: the call is delegated to the standard
        prepareStatement() method.

        Every other case is rejected as error JDBC_FEATURE_NOT_SUPPORTED.
        */

        if (resultSetType == SUPPORTED_RESULT_SET_TYPE
                && resultSetConcurrency == SUPPORTED_RESULT_SET_CONCURRENCY
                && resultSetHoldability == SUPPORTED_HOLDABILITY) {

                return prepareStatement(sql);
            }

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "Creating non-forward-only, non read-only or not over-commit held prepareStatement");
    }

    @Override
    public final DatabaseMetaData getMetaData() throws SQLException {
        /*
        To make retrieval of DatabaseMetaDatas created from within
        this Connection consistent with createStatement and prepareStatement,
        we introduced getMetaDataInternal()  internal template method: all
        concrete subclasses have to implement and can forget about checking
        the connection state completely.

        This method is final, hence it cannot be overridden accidentally.
         */
        return getMetaDataInternal();
    }

    protected abstract DatabaseMetaData getMetaDataInternal() throws SQLException;

    @Override
    public final CallableStatement prepareCall(String sql) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final String nativeSQL(String sql) throws SQLException {
        checkNotClosed();
        return sql; // no change in the statement
    }

    @Override
    public final void setAutoCommit(boolean autoCommit) throws SQLException {
        checkNotClosed();
        if (!autoCommit) {
            throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                    "This driver can only handle autocommit mode");
        }
    }

    @Override
    public final boolean getAutoCommit() throws SQLException {
        checkNotClosed();
        return true;
    }

    @Override
    public final void commit() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This driver can only handle autocommit mode");
    }

    @Override
    public final void rollback() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This driver can only handle autocommit mode");
    }

    @Override
    public final Savepoint setSavepoint() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final Savepoint setSavepoint(String name) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final void rollback(Savepoint savepoint) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final void setReadOnly(boolean readOnly) throws SQLException {
        checkNotClosed();
        if (!readOnly) {
            throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                    "This driver can only handle read-only mode");
        }
    }

    @Override
    public final boolean isReadOnly() throws SQLException {
        checkNotClosed();
        return true;
    }

    @Override
    public final void setCatalog(String catalog) throws SQLException {
        checkNotClosed();
        // "If the driver does not support catalogs, it will silently ignore this request."
    }

    @Override
    public final String getCatalog() throws SQLException {
        checkNotClosed();
        return null; // "the current catalog name or null if there is none"
    }

    @Override
    public final void setTransactionIsolation(int level) throws SQLException {
        checkNotClosed();
        // we only accept if isolation level TRANSACTION_NONE is requested
        if (level != java.sql.Connection.TRANSACTION_NONE) {
            throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                    "This driver does not support transaction isolation");
        }
    }

    @Override
    public final int getTransactionIsolation() throws SQLException {
        checkNotClosed();
        return java.sql.Connection.TRANSACTION_NONE;
    }

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        checkNotClosed();
        return this.warningContainer.getWarnings();
    }

    @Override
    public final void clearWarnings() throws SQLException {
        checkNotClosed();
        this.warningContainer.clearWarnings();
    }

    protected final void addSQLWarning(SQLWarning warning) {

        this.warningContainer.addSQLWarning(warning);
    }



    @Override
    public final CallableStatement prepareCall(
            String sql,
            int resultSetType,
            int resultSetConcurrency) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final Map<String, Class<?>> getTypeMap() throws SQLException {
        checkNotClosed();
        return Collections.unmodifiableMap(typeMap);
    }

    @Override
    public final void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        checkNotClosed();
        this.typeMap = new LinkedHashMap<>(map);
    }

    @Override
    public final void setHoldability(int holdability) throws SQLException {
        checkNotClosed();

        if (holdability != ResultSet.HOLD_CURSORS_OVER_COMMIT
                && holdability != ResultSet.CLOSE_CURSORS_AT_COMMIT) {

            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Invalid holdability: " + holdability);
        }

        if (holdability != SUPPORTED_HOLDABILITY) {
            throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                    "Unsupported holdability: " + holdability);
        }
    }

    @Override
    public final int getHoldability() throws SQLException {
        checkNotClosed();

        return SUPPORTED_HOLDABILITY;
    }

    @Override
    public final CallableStatement prepareCall(
            String sql,
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final PreparedStatement prepareStatement(
            String sql, int autoGeneratedKeys) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final PreparedStatement prepareStatement(
            String sql, int[] columnIndexes) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final PreparedStatement prepareStatement(
            String sql, String[] columnNames) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final Clob createClob() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final Blob createBlob() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final NClob createNClob() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final SQLXML createSQLXML() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final boolean isValid(int timeout) throws SQLException {

        if (timeout < 0) {
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Negative timeout: " + timeout);
        }

        return !isClosed();
    }

    @Override
    public final void setClientInfo(
            String name, String value) throws SQLClientInfoException {

        this.clientInfo.setProperty(name, value);
    }

    @Override
    public final void setClientInfo(
            Properties properties) throws SQLClientInfoException {

        // Caution: Properties constructor sets the
        // *defaults* of the Properties
        this.clientInfo = new Properties();
        this.clientInfo.putAll(properties);
    }

    @Override
    public final String getClientInfo(String name) throws SQLException {
        checkNotClosed();
        return this.clientInfo.getProperty(name);
    }

    @Override
    public final Properties getClientInfo() throws SQLException {
        checkNotClosed();
        return new Properties(this.clientInfo);
    }

    @Override
    public final Array createArrayOf(
            String typeName, Object[] elements) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final Struct createStruct(
            String typeName, Object[] attributes) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final void setSchema(String schema) throws SQLException {
        checkNotClosed();
        // No-op: "If the driver does not support schemas, it will silently ignore this request."
    }

    @Override
    public final String getSchema() throws SQLException {
        checkNotClosed();
        // "the current schema name or null if there is none"
         return null;
    }

    @Override
    public final void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        checkNotClosed();
        if (milliseconds < 0) {
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "milliseconds cannot be less than zero: " + milliseconds);
        }
    }

    @Override
    public final int getNetworkTimeout() throws SQLException {
        checkNotClosed();
        return 0;
    }

    @Override
    public final void abort(Executor executor) throws SQLException {
        if (!isClosed()) {
            try {
                executor.execute(new CloseConnectionForAbortRunnable());
            } catch (RejectedExecutionException ree) {
                throw JDBCError.CLOSE_FAILED.raiseSQLException(ree,
                        this, "The close task has been rejected");
            }
        }
    }

    private final class CloseConnectionForAbortRunnable implements Runnable {
        @Override
        public void run() {
            try {
                close();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Async close failed: " + ex.getMessage(), ex);
                throw JDBCError.CLOSE_FAILED.raiseUncheckedException(ex,
                        AbstractConnection.this,
                        "Async close failed, examine exception "
                                + "stack trace for root cause");
            }
        }
    }
}
