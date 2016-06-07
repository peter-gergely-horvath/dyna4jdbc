package com.github.dyna4jdbc.internal.common.jdbc.base;

import com.github.dyna4jdbc.internal.common.jdbc.base.GuardedResultSetState.State;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

/**
 * @author Peter Horvath
 */
public class GuardedResultSetStateTest {

    private GuardedResultSetState guardedResultSetState;

    @BeforeMethod
    public void beforeMethod() {
        guardedResultSetState = new GuardedResultSetState();
    }

    @Test(expectedExceptions = SQLException.class)
    public void testBeforeFirstToBeforeFirst() throws SQLException {

        assertEquals(guardedResultSetState.getCurrentState(), State.BEFORE_FIRST);

        guardedResultSetState.transitionTo(State.BEFORE_FIRST);
    }

    @Test
    public void testBeforeFirstToIterating() throws SQLException {

        assertEquals(guardedResultSetState.getCurrentState(), State.BEFORE_FIRST);

        guardedResultSetState.transitionTo(State.ITERATING_OVER_RESULTS);

        assertEquals(guardedResultSetState.getCurrentState(), State.ITERATING_OVER_RESULTS);
    }

    @Test
    public void testBeforeFirstToAfterLast() throws SQLException {

        assertEquals(guardedResultSetState.getCurrentState(), State.BEFORE_FIRST);

        guardedResultSetState.transitionTo(State.AFTER_LAST);

        assertEquals(guardedResultSetState.getCurrentState(), State.AFTER_LAST);
    }

    @Test(expectedExceptions = SQLException.class)
    public void testIteratingToIterating() throws SQLException {

        assertEquals(guardedResultSetState.getCurrentState(), State.BEFORE_FIRST);

        guardedResultSetState.transitionTo(State.ITERATING_OVER_RESULTS);

        assertEquals(guardedResultSetState.getCurrentState(), State.ITERATING_OVER_RESULTS);

        guardedResultSetState.transitionTo(State.ITERATING_OVER_RESULTS);

        assertEquals(guardedResultSetState.getCurrentState(), State.ITERATING_OVER_RESULTS);
    }

    @Test(expectedExceptions = SQLException.class)
    public void testIteratingToBeforeFirst() throws SQLException {

        assertEquals(guardedResultSetState.getCurrentState(), State.BEFORE_FIRST);

        guardedResultSetState.transitionTo(State.ITERATING_OVER_RESULTS);

        assertEquals(guardedResultSetState.getCurrentState(), State.ITERATING_OVER_RESULTS);

        guardedResultSetState.transitionTo(State.BEFORE_FIRST);
    }

    @Test
    public void testIteratingToAfterLast() throws SQLException {

        assertEquals(guardedResultSetState.getCurrentState(), State.BEFORE_FIRST);

        guardedResultSetState.transitionTo(State.ITERATING_OVER_RESULTS);

        assertEquals(guardedResultSetState.getCurrentState(), State.ITERATING_OVER_RESULTS);

        guardedResultSetState.transitionTo(State.AFTER_LAST);

        assertEquals(guardedResultSetState.getCurrentState(), State.AFTER_LAST);
    }

    @Test(expectedExceptions = SQLException.class)
    public void testAfterLastToAfterLast() throws SQLException {

        assertEquals(guardedResultSetState.getCurrentState(), State.BEFORE_FIRST);

        guardedResultSetState.transitionTo(State.ITERATING_OVER_RESULTS);

        assertEquals(guardedResultSetState.getCurrentState(), State.ITERATING_OVER_RESULTS);

        guardedResultSetState.transitionTo(State.AFTER_LAST);

        assertEquals(guardedResultSetState.getCurrentState(), State.AFTER_LAST);

        guardedResultSetState.transitionTo(State.AFTER_LAST);

    }

    @Test(expectedExceptions = SQLException.class)
    public void testAfterLastToBeforeFirst() throws SQLException {

        assertEquals(guardedResultSetState.getCurrentState(), State.BEFORE_FIRST);

        guardedResultSetState.transitionTo(State.ITERATING_OVER_RESULTS);

        assertEquals(guardedResultSetState.getCurrentState(), State.ITERATING_OVER_RESULTS);

        guardedResultSetState.transitionTo(State.AFTER_LAST);

        assertEquals(guardedResultSetState.getCurrentState(), State.AFTER_LAST);

        guardedResultSetState.transitionTo(State.BEFORE_FIRST);
    }

    @Test(expectedExceptions = SQLException.class)
    public void testAfterLastToIterating() throws SQLException {

        assertEquals(guardedResultSetState.getCurrentState(), State.BEFORE_FIRST);

        guardedResultSetState.transitionTo(State.ITERATING_OVER_RESULTS);

        assertEquals(guardedResultSetState.getCurrentState(), State.ITERATING_OVER_RESULTS);

        guardedResultSetState.transitionTo(State.AFTER_LAST);

        assertEquals(guardedResultSetState.getCurrentState(), State.AFTER_LAST);

        guardedResultSetState.transitionTo(State.ITERATING_OVER_RESULTS);
    }

    @Test
    public void testToString() throws SQLException {

        assertEquals(guardedResultSetState.toString(), State.BEFORE_FIRST.name());

        guardedResultSetState.transitionTo(State.ITERATING_OVER_RESULTS);
        assertEquals(guardedResultSetState.toString(), State.ITERATING_OVER_RESULTS.name());

        guardedResultSetState.transitionTo(State.AFTER_LAST);
        assertEquals(guardedResultSetState.toString(), State.AFTER_LAST.name());
    }


}
