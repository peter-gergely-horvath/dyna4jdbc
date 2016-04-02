package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.dyna4jdbc.internal.JDBCError;

public class BasicSQLObject extends AbstractWrapper {

	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final Set<BasicSQLObject> children = createNewSynchronizedWeakHashSet();

	protected void checkNotClosed() throws SQLException {
		checkNotClosed(this.toString());
	}

	protected final void checkNotClosed(String objectIdentifier) throws SQLException {
		if (closed.get()) {
			throw JDBCError.OBJECT_CLOSED.raiseSQLException(objectIdentifier);
		}
	}

	public void close() throws SQLException {
		if(!closed.get()) {
		
			closed.set(true);

			closeChildObjects(children);
			
			children.clear();
		}
	}

	private static void closeChildObjects(Iterable<BasicSQLObject> childSQLObjects) throws SQLException {
		LinkedList<SQLException> supressedThrowables = new LinkedList<>();

		for (BasicSQLObject sqlObject : childSQLObjects) {

			try {
				sqlObject.close();
			} catch (SQLException sqle) {
				supressedThrowables.add(sqle);
			}
		}

		if (!supressedThrowables.isEmpty()) {

			throw JDBCError.CLOSE_FAILED.raiseSQLExceptionWithSupressed(
					"Closing of child object(s) caused exception(s); see supressed", supressedThrowables);
		}
	}

	public final boolean isClosed() throws SQLException {
		return closed.get();
	}

	protected final void registerAsChild(BasicSQLObject sqlObject) throws SQLException {
		checkNotClosed();

		if (sqlObject == null) {
			JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException("sqlObject to register cannot be null");
		}

		children.add(sqlObject);
	}

	private static <T> Set<T> createNewSynchronizedWeakHashSet() {

		/*
		 * From the JavaDoc: "An entry in a WeakHashMap will automatically be
		 * removed when its key is no longer in ordinary use"
		 */
		WeakHashMap<T, Boolean> weakHashMap = new WeakHashMap<>();

		/*
		 * java.util.Collections.newSetFromMap(Map) creates a set VIEW of the
		 * supplied Map's KEY SET ==> Technically, it is a "WeakHashSet":
		 * 
		 * From the JavaDoc: "Each method invocation on the set returned by this
		 * method results in exactly one method invocation on the backing map or
		 * its keySet view, with one exception. The addAll method is implemented
		 * as a sequence of put invocations on the backing map."
		 */

		Set<T> weakHashSet = Collections.newSetFromMap(weakHashMap);

		return Collections.synchronizedSet(weakHashSet);
	}

}
