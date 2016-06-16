package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;


import com.github.dyna4jdbc.internal.JDBCError;

/**
 * <p>
 * A superclass of the actual {@code ResultSet} implementation we
 * expose in the JDBC driver, which rejects all attempts to change
 * data by throwing an {@code SQLException} with the code
 * {@code JDBC_FEATURE_NOT_SUPPORTED} in all of the methods
 * related to updating or changing data represented by the
 * {@code ResultSet}.</p>
 *
 * <p>
 * Why do not we allow changes in {@code ResultSet}s? Our JDBC
 * driver implementation captures output of scripts and external
 * programs. Changing the captured data simply does not
 * make any sense; hence the actual {@code ResultSet} class
 * extends this class and inherits the behaviour from it.
 * </p>
 *
 * @param <T> the type of rows managed by the {@code ResultSet}
 */
public abstract class AbstractReadOnlyResultSet<T> extends AbstractResultSet<T> {

    public AbstractReadOnlyResultSet(Statement statement) {
        super(statement);
    }

    @Override
    public final boolean rowUpdated() throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final boolean rowInserted() throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final boolean rowDeleted() throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }


    @Override
    public final void insertRow() throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateRow() throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void deleteRow() throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void cancelRowUpdates() throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void moveToInsertRow() throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void moveToCurrentRow() throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    // -- update*(...) methods --
    @Override
    public final void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateClob(int columnIndex, Reader reader) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateClob(String columnLabel, Reader reader) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNClob(int columnIndex, Reader reader) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNClob(String columnLabel, Reader reader) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNull(int columnIndex) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBoolean(int columnIndex, boolean x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateByte(int columnIndex, byte x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateShort(int columnIndex, short x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateInt(int columnIndex, int x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateLong(int columnIndex, long x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateFloat(int columnIndex, float x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateDouble(int columnIndex, double x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateString(int columnIndex, String x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBytes(int columnIndex, byte[] x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateDate(int columnIndex, Date x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateTime(int columnIndex, Time x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateObject(int columnIndex, Object x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNull(String columnLabel) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBoolean(String columnLabel, boolean x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateByte(String columnLabel, byte x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateShort(String columnLabel, short x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateInt(String columnLabel, int x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateLong(String columnLabel, long x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateFloat(String columnLabel, float x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateDouble(String columnLabel, double x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateString(String columnLabel, String x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBytes(String columnLabel, byte[] x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateDate(String columnLabel, Date x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateTime(String columnLabel, Time x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateObject(String columnLabel, Object x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateRef(int columnIndex, Ref x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateRef(String columnLabel, Ref x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBlob(int columnIndex, Blob x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateBlob(String columnLabel, Blob x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateClob(int columnIndex, Clob x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateClob(String columnLabel, Clob x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateArray(int columnIndex, Array x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateArray(String columnLabel, Array x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateRowId(int columnIndex, RowId x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateRowId(String columnLabel, RowId x) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNString(int columnIndex, String nString) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNString(String columnLabel, String nString) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateObject(int columnIndex, Object x,
                              SQLType targetSqlType, int scaleOrLength)  throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateObject(String columnLabel, Object x,
                              SQLType targetSqlType, int scaleOrLength) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final void updateObject(String columnLabel, Object x,
                              SQLType targetSqlType) throws SQLException {
        checkNotClosed();

        // We implement result set concurrency CONCUR_READ_ONLY,
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }
}
