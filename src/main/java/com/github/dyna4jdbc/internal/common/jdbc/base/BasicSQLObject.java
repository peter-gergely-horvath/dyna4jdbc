package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.util.List;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.dyna4jdbc.internal.JDBCError;

public class BasicSQLObject extends AbstractWrapper {

	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final Set<AutoCloseable> children = createNewSynchronizedWeakHashSet();

	protected void checkNotClosed() throws SQLException {
		checkNotClosed(this.toString());
	}

	protected final void checkNotClosed(String objectIdentifier) throws SQLException {
		if (closed.get()) {
			throw JDBCError.OBJECT_CLOSED.raiseSQLException(objectIdentifier);
		}
	}

	public final void close() throws SQLException {
		if(!closed.get()) {
			closed.set(true);
			
			SQLException exceptionRaisedDuringCloseInternal = null;

			try {
				closeInternal();
			} catch(SQLException sqlEx) {
				exceptionRaisedDuringCloseInternal = sqlEx;
			}
						
			try {
				closeChildObjects(children);
			} catch(SQLException exceptionRaisedDuringClosingChildren) {
				
				if(exceptionRaisedDuringCloseInternal == null) {
					throw exceptionRaisedDuringClosingChildren;
				} else {
					throw JDBCError.CLOSE_FAILED.raiseSQLExceptionWithSupressed(
							Arrays.asList(exceptionRaisedDuringCloseInternal, exceptionRaisedDuringClosingChildren),
							this, "Multiple exceptions raised during close; see supressed");
				}
			}
		}
	}

	protected void closeInternal() throws SQLException {
		// template method for subclasses to hook into close
	}

	private void closeChildObjects(Iterable<AutoCloseable> objectsToClose) throws SQLException {
		LinkedList<Exception> supressedThrowables = new LinkedList<>();

		for (AutoCloseable sqlObject : objectsToClose) {

			try {
				sqlObject.close();
			} catch (Exception sqle) {
				supressedThrowables.add(sqle);
			}
		}
		
		children.clear();

		if (!supressedThrowables.isEmpty()) {
			
			if(supressedThrowables.size() == 1) {
				// closure of a single child has failed: propagate the root cause as cause
				throw JDBCError.CLOSE_FAILED.raiseSQLException(supressedThrowables.getFirst(),
						this, "Closing of child object caused exception");
				
			} else {
				// closure of multiple children has failed: propagate them as supressed
				throw JDBCError.CLOSE_FAILED.raiseSQLExceptionWithSupressed(supressedThrowables,
						this, "Closing of child objects caused exceptions; see supressed");	
			}
		}
	}

	public final boolean isClosed() throws SQLException {
		return closed.get();
	}

	protected final void registerAsChild(AutoCloseable closableSQLObject) throws SQLException {
		checkNotClosed();

		if (closableSQLObject == null) {
			JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException("closableSQLObject to register cannot be null");
		}

		children.add(closableSQLObject);
	}
	
	protected final void registerAsChildren(List<? extends AutoCloseable> closableSQLObjects) throws SQLException {
		for(AutoCloseable closableSQLObject : closableSQLObjects) {
			registerAsChild(closableSQLObject);
		}
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
