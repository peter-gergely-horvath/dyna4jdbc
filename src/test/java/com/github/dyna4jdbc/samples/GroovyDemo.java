package com.github.dyna4jdbc.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class GroovyDemo {
	
	private static final String GROOVY_SCRIPT = ""
			+ "	import groovy.json.JsonSlurper													\n"
			+ "																					\n"
			+ " def printRow(String... values) { println values.join(\"\t\") }					\n"
			+ " def jsonData = new URL('http://www.google.com/finance/info?infotype=infoquoteall&q=NASDAQ:AAPL,IBM,MSFT,GOOG').text.replaceFirst('//', '')	\n"
			+ " def data = new JsonSlurper().parseText(jsonData)								\n"
			+ " printRow 'Ticker::', 'Name::', 'Open::', 'Close::', 'Change::'					\n"
			+ " data.each { printRow it['t'], it['name'], it['op'], it['l_cur'], it['c'] } 		\n";

	public static void main(String[] args) throws SQLException {

		try (Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:Groovy")) {

			try (Statement statement = connection.createStatement()) {
				boolean results = statement.execute(GROOVY_SCRIPT);
				do {
					if (results) {
						try (ResultSet rs = statement.getResultSet()) {

							printResultSetWithHeaders(rs);
						}
					}

					results = statement.getMoreResults();
				} while (results);
			}
		}
	}

	private static void printResultSetWithHeaders(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		final int columnCount = metaData.getColumnCount();

		for(int i=1; i<=columnCount; i++ ) {
			String columnLabel = metaData.getColumnLabel(i);
			int columnDisplaySize = metaData.getColumnDisplaySize(i);
			String formatStr = "%" + columnDisplaySize + "s | ";
			System.out.format(formatStr, columnLabel);
		}
		System.out.println();
		
		for(int i=1; i<=columnCount; i++ ) {
			int columnDisplaySize = metaData.getColumnDisplaySize(i);
			String formatStr = "%" + columnDisplaySize + "s | ";
			System.out.format(String.format(formatStr, "").replace(' ', '-'));
		}
		System.out.println();
		
		while (rs.next()) {

		    for(int i=1; i<=columnCount; i++ ) {
		    	int columnDisplaySize = metaData.getColumnDisplaySize(i);
		    	String formatStr = "%" + columnDisplaySize + "s | ";
		    	System.out.format(formatStr, rs.getString(i));
		    }
		    System.out.println();
		}
	}

}
