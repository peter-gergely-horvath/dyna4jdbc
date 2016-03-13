package com.github.dyna4jdbc.internal.connection.scriptengine;

import java.sql.ResultSet;
import java.util.List;

public interface ResultSetFactory {

	List<ResultSet> newResultSets(ScriptEngineStatement ses, final List<Object> resultObjectList);

}