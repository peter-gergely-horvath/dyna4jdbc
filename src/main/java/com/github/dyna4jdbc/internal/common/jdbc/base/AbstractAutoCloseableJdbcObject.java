/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.dyna4jdbc.internal.JDBCError;

/**
 * {@code AbstractAutoCloseableJdbcObject} is a base class
 * for any JDBC API implementation class, which supports 
 * the {@code close()} method defined by .
 * 
 * This class provides support to register child objects
 * (which logically belong to this object) on creation, 
 * and to cascade the close operation to live ones, when 
 * this object is closed. 
 * 
 * @author Peter G. Horvath
 */
public class AbstractAutoCloseableJdbcObject extends AbstractWrapper implements AutoCloseable {

	/**
	 * A thread-safe boolean container for the closed flag, used to prevent further use 
	 * of a concrete JDBC class implementation once {@link #close()} is called. 
	 * We use {@code AtomicBoolean} here so that a subclass can safely be closed from a 
	 * thread different than it was created from.
	 * 
	 * @see #close()
	 * @see #checkNotClosed()
	 */
	private final AtomicBoolean closed = new AtomicBoolean(false);
	
	/**
	 * <p>
	 * Closing a parent object should close all the derived resources: this 
	 * {@code Set} is used to maintain <b>live references</b> to objects created
	 * by the actual subclass. Under the hoods, we use a "weak" {@code Set}, which
	 * means objects registered here are not prevented from being garbage collected:
	 * any object discarded by the garbage collector will simply be removed from the 
	 * set. As a result, close operation will only be cascaded to live child objects.
	 * We wrap the "weak" {@code Set} so that operations will be thread-safe.</p>
	 * 
	 * <p>
	 * The intention here is to know all live objects related to {@code this}, but
	 * do NOT prevent them from being garbage collected in case they are no longer
	 * in use. </p>
	 * 
	 * @see #createNewWeakHashSet()
	 * @see #close()
	 */
	private final Set<AutoCloseable> children = Collections.synchronizedSet( createNewWeakHashSet() );

	/**
	 * Throws {@code SQLException} if {@code this} is closed. 
	 * This method is thread-safe: it shall consistently report 
	 * an object closed from one thread as closed from all other 
	 * threads as well, <i>without external synchronisation.</i>
	 * 
	 * @throws SQLException if {@code this} is closed. 
	 */
	protected final void checkNotClosed() throws SQLException {
		if (isClosed()) {
			throw JDBCError.OBJECT_CLOSED.raiseSQLException(this);
		}
	}
	
	/**
	 * Returns an indication whether {@code this} object is closed.
	 * This method is thread-safe: it shall consistently report 
	 * an object closed from one thread as closed from all other 
	 * threads as well, <i>without external synchronisation.</i>
	 *  
	 * @return {@code true} if {@code this} object is closed, {@code false} otherwise.
	 */
	public final boolean isClosed() {
		return closed.get();
	}
	
	/**
	 * Closes {@code this} object and all live registered closable child objects.
	 * This method is thread-safe: it shall consistently close 
	 * an object created from another thread. The results of the close
	 * operation shall be visible from all other threads as well, 
	 * <i>without external synchronisation.</i>
	 * 
	 * @throws SQLException in case closing {@code this} object or any of the the child object fails
	 */
	public final void close() throws SQLException {
		if(!closed.get()) {
			closed.set(true);
			
			SQLException internalCloseSQLException = null;
			SQLException childrenCloseSQLException = null;

			try {
				closeInternal();
			} catch(SQLException sqlEx) {
				internalCloseSQLException = sqlEx;
			}
						
			try {
				closeChildObjects();
			} catch(SQLException sqlEx) {
				childrenCloseSQLException = sqlEx;
			}

			if(internalCloseSQLException != null &&
					childrenCloseSQLException != null) {

				throw JDBCError.CLOSE_FAILED.raiseSQLExceptionWithSupressed(
						Arrays.asList(internalCloseSQLException, childrenCloseSQLException),
						this, "Multiple exceptions raised during close; see supressed");
			}

			if(internalCloseSQLException != null) {
				throw internalCloseSQLException;
			}

			if(childrenCloseSQLException != null) {
				throw childrenCloseSQLException;
			}
		}
	}

	/**
	 * <p>
	 * Executed when {@code this} object is closed. Concrete sub-classes might
	 * override this method to provide their own resource disposal logic.</p>
	 * 
	 * <b>NOTE: the implementation of this method MUST BE THREAD-SAFE!</b>
	 * 
	 * @throws SQLException in case closing the resource fails 
	 */
	protected void closeInternal() throws SQLException {
		// template method for subclasses to hook into close
	}

	/**
	 * Closes all child objects, even if some of them throw {@code Exception} when
	 * the close method is called on them. Any exceptions thrown by the child objects
	 * are later re-thrown, once closing of every child is completed.
	 * 
	 * @throws SQLException if one or more child objects throw exception one close method call
	 */
	private void closeChildObjects() throws SQLException {
		LinkedList<Exception> supressedThrowables = new LinkedList<>();

		for (AutoCloseable closeableObject : children) {

			try {
				closeableObject.close();
			} catch (Exception closeException) {
				supressedThrowables.add(closeException);
			}
		}
		
		children.clear();

		if (!supressedThrowables.isEmpty()) {
			
			if(supressedThrowables.size() == 1) {
				// closure of a single child has failed: propagate the root cause as cause
				throw JDBCError.CLOSE_FAILED.raiseSQLException(supressedThrowables.getFirst(),
						this, "Closing of child object caused exception");
				
			} else {
				// closure of multiple children has failed: propagate them as suppressed
				throw JDBCError.CLOSE_FAILED.raiseSQLExceptionWithSupressed(supressedThrowables,
						this, "Closing of child objects caused exceptions; see supressed");	
			}
		}
	}



	/**
	 * Registers the object as child object, which logically belongs to {@code this} object
	 * and hence, should also be closed (if still alive), when this {@code this} objects is 
	 * closed.

	 * @param closableObject the object to register
	 * 
	 * @throws SQLException if {@code this} is closed already
	 */
	protected final void registerAsChild(AutoCloseable closableObject) throws SQLException {
		checkNotClosed();

		if (closableObject == null) {
			JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException("closableObject to register cannot be null");
		}

		children.add(closableObject);
	}
	
	/**
	 * Registers all object supplied in the {@code Collection} as child objects, 
	 * which logically belong to {@code this} object and hence, should also be closed 
	 * (if still alive), when this {@code this} objects is closed.

	 * @param closableObjects a {@code Collection} of objects to register as children.
	 * 
	 * @throws SQLException if {@code this} is closed already
	 */
	protected final void registerAsChildren(Collection<? extends AutoCloseable> closableObjects) throws SQLException {
		for(AutoCloseable aClosableSQLObject : closableObjects) {
			registerAsChild(aClosableSQLObject);
		}
	}

	/**
	 * Construct a new "weak" {@code HashSet} according to the same specification
	 * as described for {@link WeakHashMap}.
	 *  
	 * @return a new "Weak" {@code HashSet} 
	 */
	private static <T> Set<T> createNewWeakHashSet() {

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

		return Collections.newSetFromMap(weakHashMap);
	}

}
