package com.github.dyna4jdbc.internal.common.jdbc.generic;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractResultSet;

public class EmptyResultSet extends AbstractResultSet<List<String>> {

	public EmptyResultSet() {
		super(new LinkedList<>(), null);
	}

	@Override
	public boolean wasNull() throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public String getCursorName() throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return new EmptyResultSetMetaData();
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
	}


   

}
