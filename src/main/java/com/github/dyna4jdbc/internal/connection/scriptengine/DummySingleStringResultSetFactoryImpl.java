package com.github.dyna4jdbc.internal.connection.scriptengine;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DummySingleStringResultSetFactoryImpl implements ResultSetFactory {
	
	private final ScriptEngineConnection scriptEngineConnection;
	

	DummySingleStringResultSetFactoryImpl(ScriptEngineConnection scriptEngineConnection) {
		this.scriptEngineConnection = scriptEngineConnection;

	}
	
	@Override
	public List<ResultSet> newResultSets(ScriptEngineStatement ses, final List<Object> resultObjectList) {
	
		StringBuilder sb = new StringBuilder();
		
		for(Object objectWritten : resultObjectList) {
			
			String stringToWrite;
			
			if(objectWritten == null) {
				stringToWrite = null;
			} else {
				Class<?> objectClass = objectWritten.getClass();
				if(! objectClass.isArray()) {
					stringToWrite = objectWritten.toString();
				} else {
					Class<?> componentType = objectClass.getComponentType();
					if(! componentType.isPrimitive()) {
						stringToWrite = Arrays.deepToString((Object[]) objectWritten);
					} else
						stringToWrite = Arrays.toString((char[]) objectWritten);
				}
			}
				
			sb.append(stringToWrite);
		}
			
		String string = sb.toString();
		
		ResultSet resultSet = 
				new SingleStringResultSet(string, ses, resultObjectIterable(resultObjectList));
		
		
		return Arrays.asList(resultSet);
	}

	private ResultSetObjectIterable resultObjectIterable(final List<Object> resultObjectList) {
		return new ResultSetObjectIterable() {
			
				@Override
				public Iterator<Object> iterator() {
					return resultObjectList.iterator();
				}
			};
	}

}
