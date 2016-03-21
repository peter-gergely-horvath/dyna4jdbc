package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet;
import com.github.dyna4jdbc.internal.common.outputhandler.MultiTypeScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.SingleResultSetScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.UpdateScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;

public class DefaultScriptOutputHandlerFactory implements ScriptOutputHandlerFactory {

	// TODO: cleanup

	private final TypeHandlerFactory typeHandlerFactory;

	public DefaultScriptOutputHandlerFactory(TypeHandlerFactory typeHandlerFactory) {
		this.typeHandlerFactory = typeHandlerFactory;

	}

	@Override
	public SingleResultSetScriptOutputHandler newSingleResultSetScriptOutputHandler(
			Statement statement, String script) {
		
		return new DefaultResultSetScriptOutputHandler(statement, typeHandlerFactory);
	}

	@Override
	public MultiTypeScriptOutputHandler newMultiTypeScriptOutputHandler(
			Statement statement, String script) {
		
		return new DefaultResultSetScriptOutputHandler(statement, typeHandlerFactory);
	}

	@Override
	public UpdateScriptOutputHandler newUpdateScriptOutputHandler(
			Statement statement, String script) {
		
		return new DefaultUpdateScriptOutputHandler();
	}

	private static final class DefaultUpdateScriptOutputHandler implements UpdateScriptOutputHandler {
		@Override
		public int getUpdateCount() {
			return 0;
		}

		@Override
		public PrintWriter getOutPrintWriter() {
			return new DisallowAllWritesPrintWriter("Writing to to stdout from update is not allowed");
		}

		@Override
		public PrintWriter getErrorPrintWriter() {
			return null;
		}
	}

	private static class DefaultResultSetScriptOutputHandler
			implements SingleResultSetScriptOutputHandler, MultiTypeScriptOutputHandler {

		private final Statement statement;
		private final DataTableWriter stdOut = new DataTableWriter();
		private final PrintWriter printWriter = new PrintWriter(stdOut);
		private final TypeHandlerFactory typeHandlerFactory;

		public DefaultResultSetScriptOutputHandler(Statement statement, TypeHandlerFactory typeHandlerFactory) {
			this.statement = statement;
			this.typeHandlerFactory = typeHandlerFactory;
		}

		private List<ResultSet> processObjectListToResultSet() {

			return stdOut.getDataTableList().stream()
					.map(dataTable -> new DataTableHolderResultSet(statement, dataTable, typeHandlerFactory))
					.collect(Collectors.<ResultSet> toList());
		}

		@Override
		public boolean isResultSets() {
			return true;
		}

		@Override
		public List<ResultSet> getResultSets() {
			return processObjectListToResultSet();
		}

		@Override
		public int getUpdateCount() {
			return 0;
		}

		@Override
		public ResultSet getResultSet() throws SQLException {
			List<ResultSet> resultSets = getResultSets();

			if (resultSets.size() > 1) {

				throw SQLError.RESULT_SET_MULTIPLE_EXPECTED_ONE.raiseException(resultSets.size());

			} else {
				return resultSets.get(0);
			}

		}

		@Override
		public PrintWriter getOutPrintWriter() {
			return printWriter;
		}

		@Override
		public PrintWriter getErrorPrintWriter() {
			return null;
		}
	}
}
