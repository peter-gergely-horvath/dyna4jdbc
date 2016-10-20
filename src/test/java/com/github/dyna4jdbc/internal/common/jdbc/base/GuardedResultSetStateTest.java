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

import com.github.dyna4jdbc.internal.common.jdbc.base.GuardedResultSetState.State;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

/**
 * @author Peter G. Horvath
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
