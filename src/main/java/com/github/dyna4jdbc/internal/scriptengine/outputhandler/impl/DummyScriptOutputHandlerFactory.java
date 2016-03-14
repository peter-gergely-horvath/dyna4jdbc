package com.github.dyna4jdbc.internal.scriptengine.outputhandler.impl;

import com.github.dyna4jdbc.internal.scriptengine.ResultSetObjectIterable;
import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.SingleStringResultSet;
import com.github.dyna4jdbc.internal.scriptengine.outputhandler.MultiTypeScriptOutputHandler;
import com.github.dyna4jdbc.internal.scriptengine.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.scriptengine.outputhandler.SingleResultSetScriptOutputHandler;
import com.github.dyna4jdbc.internal.scriptengine.outputhandler.UpdateScriptOutputHandler;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DummyScriptOutputHandlerFactory implements ScriptOutputHandlerFactory {
	
	public DummyScriptOutputHandlerFactory() {
	}

    private static class DummyResultSetScriptOutputHandler implements SingleResultSetScriptOutputHandler, MultiTypeScriptOutputHandler {

        private final ObjectCapturingPrintWriter stdOut = new ObjectCapturingPrintWriter();


        private ResultSet processObjectListToResultSet() {
            StringBuilder sb = new StringBuilder();

            List<Object> objectList = stdOut.getUnmodifyAbleCapturedObjectList();

            for (Object objectWritten : objectList) {

                String stringToWrite;

                if (objectWritten == null) {
                    stringToWrite = null;
                } else {
                    Class<?> objectClass = objectWritten.getClass();
                    if (!objectClass.isArray()) {
                        stringToWrite = objectWritten.toString();
                    } else {
                        Class<?> componentType = objectClass.getComponentType();
                        if (!componentType.isPrimitive()) {
                            stringToWrite = Arrays.deepToString((Object[]) objectWritten);
                        } else
                            stringToWrite = Arrays.toString((char[]) objectWritten);
                    }
                }

                sb.append(stringToWrite);
            }

            String string = sb.toString();

            return new SingleStringResultSet(string, new ResultSetObjectIterable() {

                        @Override
                        public Iterator<Object> iterator() {
                            return objectList.iterator();
                        }
                    });
        }

        @Override
        public boolean isResultSets() {
            return true;
        }

        @Override
        public List<ResultSet> getResultSets() {
            return Arrays.asList(getResultSet());
        }

        @Override
        public int getUpdateCount() {
            return 0;
        }

        @Override
        public ResultSet getResultSet() {
            return processObjectListToResultSet();
        }

        @Override
        public PrintWriter getOutPrintWriter() {
            return stdOut;
        }

        @Override
        public PrintWriter getErrorPrintWriter() {
            return null;
        }
    }

    @Override
    public SingleResultSetScriptOutputHandler newSingleResultSetScriptOutputHandler(String script) {
        return new DummyResultSetScriptOutputHandler();
    }

    @Override
    public MultiTypeScriptOutputHandler newMultiTypeScriptOutputHandler(String script) {
        return new DummyResultSetScriptOutputHandler();
    }

    @Override
    public UpdateScriptOutputHandler newUpdateScriptOutputHandler(String script) {
        return new UpdateScriptOutputHandler() {
            @Override
            public int getUpdateCount() {
                return 0;
            }

            @Override
            public PrintWriter getOutPrintWriter() {
                return DisallowAllWritesPrintWriter.forMessage("Cannot write to stdout from update!");
            }

            @Override
            public PrintWriter getErrorPrintWriter() {
                return null;
            }
        };
    }
}
