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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.dyna4jdbc.internal.JDBCError;

/**
 * {@code AbstractAutoCloseableJdbcObject} is a base class
 * for any JDBC API implementation class, which supports
 * the {@code close()} method defined by
 * {@code java.lang.AutoCloseable}.
 * <p>
 * This class provides support for
 * <ul>
 *     <li>registering child objects (which logically belong to this object) on creation, and</li>
 *     <li>cascading the close operation to alive children, when this object is closed.</li>
 * </ul>
 *
 *
 * @author Peter G. Horvath
 */
public class AbstractAutoCloseableJdbcObject extends AbstractWrapper implements AutoCloseable {

    /**
     * <p>
     * A thread-safe boolean container for the closed flag, used to prevent further use
     * of a concrete JDBC class implementation once {@link #close()} is called.
     * We use {@code AtomicBoolean} here so that a subclass can safely be closed from a
     * thread different than it was created from.</p>
     *
     * @see #close()
     * @see #checkNotClosed()
     */
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * <p>
     * Closing a parent object should close all the derived resources: this
     * {@code Set} is used to maintain references to <b>live (not closed)</b>
     * objects created by the actual subclass.</p>
     *
     * <p>
     * This {@code Set} only contains live children: child objects de-register
     * themselves when they are closed.</p>
     *
     * <p>
     * Note: this {@code HashSet} is NOT thread-safe: all operations the access
     * it are MANDATED to use external locking.</p>
     *
     * @see #close()
     */
    private final Set<AutoCloseable> children = new HashSet<>();

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
     * have already been freed. The value returned simply indicates
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
        
        /* The close operation is de-duplicated: even if requested multiple times, 
         * it will actually be executed only one time!
         */
        final boolean closedFlagWasSetDuringThisMethodCall = closed.compareAndSet(false, true);
        if (closedFlagWasSetDuringThisMethodCall) {

            // close children first, since they might require this.parent to be around... 
            LinkedList<Throwable> caughtThrowables = closeChildObjects();

            // Notice for threading correctness: this.parent is FINAL
            if (this.parent != null) {
                try {
                    this.parent.unregisterChild(this);
                } catch (Throwable thowable) {
                    /* Normally, any Throwable caught here should be a
                     * SQLException, however, we catch all Throwables
                     * so as to handle offending implementations properly:
                     * if a RuntimeException or Error is thrown, we still
                     * catch and pass it to method #closeChildObjects(Throwable)
                     */
                    caughtThrowables.add(thowable);
                }
            }

            switch (caughtThrowables.size()) {
                case 0:
                    return; // no Throwable caught: normal completion

                case 1:
                    // one Throwable caught during close: propagate it as the root cause
                    throw JDBCError.CLOSE_FAILED.raiseSQLException(caughtThrowables.getFirst(),
                            this, "Closing of dependent object caused exception");

                default:
                    // closure caused multiple Throwables to be thrown: propagate them as suppressed
                    throw JDBCError.CLOSE_FAILED.raiseSQLExceptionWithSuppressed(caughtThrowables,
                            this, "Closing of dependent objects caused exceptions; see supressed");

            }
        }
    }

    /**
     * Closes all child objects, even if some of them throw {@code Throwable} when
     * the close method is called on them. Any {@code Throwable}s thrown during closing
     * the child objects are collected into the {@code LinkedList} returned by this method
     *
     * @return a {@code LinkedList} of {@code Throwable}s thrown by children
     *          (if any), or an <b>empty</b> {@code LinkedList}, but <b>never {@code null}</b>
     */
    private LinkedList<Throwable> closeChildObjects() throws SQLException {

        synchronized (this.children) {
            LinkedList<Throwable> caughtThrowables = new LinkedList<>();

            // we have the monitor: synchronized (this.children)!
            for (AutoCloseable closeableObject : new HashSet<>(this.children)) {

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

            // closing also includes discarding references
            children.clear(); // we have the monitor: synchronized (this.children)!

            return caughtThrowables;

        } // end of synchronized (this.children) block
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

        if (closableObject == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    "closableObject to register cannot be null");
        }

        /* We do not even try to acquire the monitor of this.children
         * in case this object is closed. This prevents potentially
         * unnecessary wait on the monitor if the registration cannot
         * be performed anyways. However, the check HAS TO BE EXECUTED
         * once again, when the monitor is actually held.
         */
        if (isClosed()) {
            throw JDBCError.OBJECT_CLOSED.raiseUncheckedException(this);
        }

        synchronized (this.children) {
            /* Note that isClosed() MIGHT change to true while we are waiting
             * for monitor of this.children, so we HAVE TO check it again!
             */

            if (isClosed()) {
                throw JDBCError.OBJECT_CLOSED.raiseUncheckedException(this);
            }

            children.add(closableObject); // we have the monitor: synchronized (this.children)!
        } // end of synchronized(this.children) block
    }

    /**
     * Registers all objects supplied in the {@code Collection} as child objects,
     * which logically belong to {@code this} object and hence, should also be closed
     * (if still alive), when this {@code this} objects is closed.
     *
     * @param closableObjects a {@code Collection} of objects to register as children.
     * @throws SQLException if {@code this} is closed already
     */
    protected final void registerAsChildren(Collection<? extends AutoCloseable> closableObjects) throws SQLException {
        for (AutoCloseable aClosableSQLObject : closableObjects) {
            registerAsChild(aClosableSQLObject);
        }
    }

    /**
     * Unregisters a child object from {@code this} object. If there is no more
     * child element is registered after the removal of the supplied object, then
     * the template method {@link #onLastChildRemoved()} is also invoked.
     *
     * @param childObject the child object to remove from registration
     *
     * @throws SQLException in case {@link #onLastChildRemoved()} was invoked and it threw an exception
     */
    private void unregisterChild(AutoCloseable childObject) throws SQLException {

        synchronized (this.children) {
            boolean setContainedTheElement = this.children.remove(childObject);
            if (!setContainedTheElement) {
                /* Added out of paranoia: should NEVER happen.
                 * Something is fundamentally wrong, if this occurs: we are
                 * attempting to un-register a child, which was never ours?
                 * Fail fast by throwing an exception!
                 */
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                        "Child to unregister was not found as a child.");
            }

            if (this.children.isEmpty()) {
                /* NOTE: we invoke the callback while holding the monitor of
                 * this.children. Since this is mutually exclusive with the
                 * addition of a new child, the callback is guaranteed to
                 * see a state, where this.children.isEmpty() is true
                 */
                onLastChildRemoved();
            }
        } // end of synchronized(this.children) block
    }

    protected void onLastChildRemoved() throws SQLException {
        // template method for sub-classes to override
    }
}
