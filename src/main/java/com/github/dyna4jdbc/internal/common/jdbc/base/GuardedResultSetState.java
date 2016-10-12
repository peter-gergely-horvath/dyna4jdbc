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
