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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.dyna4jdbc.internal.JDBCError;

/**
 * {@code AbstractAutoCloseableJdbcObject} is a base class
 * for any JDBC API implementation class, which supports
 * the {@code close()} method defined by
 * {@code java.lang.AutoCloseable}.
 * <p>
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
     * by the actual subclass.</p>
     *
     * <p>
     * This {@code Set} only contains live children: child objects de-register
     * themselves when they are closed.</p>
     *
     * <p>
     * Note: We use wrap {@code HashSet} to {@code synchronizedSet} so that
     * operations will be thread-safe.</p>
     *
     * @see #close()
     */
    private final Set<AutoCloseable> children = Collections.synchronizedSet(new HashSet<>());

    /**
     * We maintain a reference to the parent object, so that we can
     * un-register from the parent in case {@code this} object is closed.
     */
    private final AbstractAutoCloseableJdbcObject parent;

    protected AbstractAutoCloseableJdbcObject(Object parent) {
        //CHECKSTYLE.OFF: AvoidInlineConditionals : inline conditional is required in this case
        this(parent instanceof AbstractAutoCloseableJdbcObject
                ? (AbstractAutoCloseableJdbcObject) parent : null);
        //CHECKSTYLE.ON
    }

    protected AbstractAutoCloseableJdbcObject(AbstractAutoCloseableJdbcObject parent) {
        this.parent = parent;
    }

    /**
     * <p>
     * Throws {@code SQLException} if {@code this} is closed.
     * This method is thread-safe: it shall consistently report
     * an object closed from one thread as closed from all other
     * threads as well, <i>without external synchronisation.</i></p>
     *
     * <p>
     * <b>NOTE:</b> no guarantee is made whether underlying resources
     * have already been freed. The value returned simply indicate
     * whether {@link #close()} method has been called or not, but
     * does not state whether its execution is finished (the call
     * has returned already or not). A thread might well see an
     * object as closed while the one initiating the close operation
     * is still waiting for the {@link #close()} to return after
     * executing the cleanup operations.</p>
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
        final boolean closedFlagSet = markClosedInternal();
        if (closedFlagSet) {
            unRegisterFromParent();

            closeChildObjects();
        }
    }

    /**
     * Sets the closed flag, if it is not yet set and returns and indication,
     * whether it was set already. It does not perform any actual resource closure.
     * Must not be used except the infrastructure code use to handle
     * closing and resources.
     *
     * @return {@code true} if {@code this} object was successfully marked as closed,
     *          {@code false} if it was marked as closed previously.
     */
    private boolean markClosedInternal() {
        return closed.compareAndSet(false, true);
    }

    /**
     * Removes this object from the parent's children registry: once a child is closed,
     * the parent will no longer maintain any reference to it or try to close it when
     * the parent itself is closed.
     */
    private void unRegisterFromParent() {
        if (parent != null) {
            parent.children.remove(this);
        }
    }

    /**
     * Closes all child objects, even if some of them throw {@code Throwable} when
     * the close method is called on them. Any {@code Throwable}s thrown by the
     * child objects are later re-thrown, once closing of every child is completed.
     *
     * @throws SQLException if one or more child objects throw {@code Throwable} on close method call
     */
    private void closeChildObjects() throws SQLException {
        LinkedList<Throwable> caughtThrowables = new LinkedList<>();

        for (AutoCloseable closeableObject : children) {

            try {
                closeableObject.close();
            } catch (Throwable closeThrowable) {
                /* Normally, any Throwable caught here should be a
                 * SQLException, however, we catch all Throwables
                 * so as to handle offending implementations properly:
                 * if a RuntimeException or Error is thrown, we still
                 * catch it and wrap into a SQLException
                 */
                caughtThrowables.add(closeThrowable);
            }
        }

        children.clear(); // closing also includes discarding references

        switch (caughtThrowables.size()) {
            case 0:
                return; // no Throwable caught: normal completion

            case 1:
                // closure of a single child has failed: propagate it as the root cause
                throw JDBCError.CLOSE_FAILED.raiseSQLException(caughtThrowables.getFirst(),
                        this, "Closing of child object caused exception");

            default:
                // closure of multiple children has failed: propagate them as suppressed
                throw JDBCError.CLOSE_FAILED.raiseSQLExceptionWithSupressed(caughtThrowables,
                        this, "Closing of child objects caused exceptions; see supressed");

        }
    }


    /**
     * Registers the object as child object, which logically belongs to {@code this} object
     * and hence, should also be closed (if still alive), when this {@code this} objects is
     * closed.
     *
     * @param closableObject the object to register
     * @throws RuntimeException if {@code this} is closed already
     */
    protected final void registerAsChild(AutoCloseable closableObject) {
        if (isClosed()) {
            JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    "Attempt to register a child to a closed object");
        }

        if (closableObject == null) {
            JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    "closableObject to register cannot be null");
        }

        children.add(closableObject);
    }

    /**
     * Registers all object supplied in the {@code Collection} as child objects,
     * which logically belong to {@code this} object and hence, should also be closed
     * (if still alive), when this {@code this} objects is closed.
     *
     * @param closableObjects a {@code Collection} of objects to register as children.
     * @throws SQLException if {@code this} is closed already
     */
    public final void registerAsChildren(Collection<? extends AutoCloseable> closableObjects) throws SQLException {
        for (AutoCloseable aClosableSQLObject : closableObjects) {
            registerAsChild(aClosableSQLObject);
        }
    }
}
