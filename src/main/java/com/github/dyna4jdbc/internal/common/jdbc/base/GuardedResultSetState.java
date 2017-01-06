/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
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

import com.github.dyna4jdbc.internal.JDBCError;

import java.sql.SQLException;


class GuardedResultSetState {

    enum State {
        AFTER_LAST {
            @Override
            protected boolean canTransitionTo(State newState) {
                return false;
            }
        },
        ITERATING_OVER_RESULTS {
            @Override
            protected boolean canTransitionTo(State newState) {
                return newState == AFTER_LAST;
            }
        },
        BEFORE_FIRST {
            @Override
            protected boolean canTransitionTo(State newState) {
                return newState == ITERATING_OVER_RESULTS || newState == AFTER_LAST;
            }
        };

        protected abstract boolean canTransitionTo(State newState);
    }

    private State currentState;

    boolean isInState(GuardedResultSetState.State state) {
        return currentState == state;
    }

    State getCurrentState() {
        return currentState;
    }

    GuardedResultSetState() {
        this.currentState = GuardedResultSetState.State.BEFORE_FIRST;
    }

    void transitionTo(State newState) throws SQLException {
        if (!currentState.canTransitionTo(newState)) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
                    "Transitioning from " + currentState + " to " + newState + " is illegal");
        }

        this.currentState = newState;
    }

    @Override
    public String toString() {
        return this.currentState.toString();
    }
}
