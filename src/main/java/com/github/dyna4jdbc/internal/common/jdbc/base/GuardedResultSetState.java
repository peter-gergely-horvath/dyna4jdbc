package com.github.dyna4jdbc.internal.common.jdbc.base;

import com.github.dyna4jdbc.internal.JDBCError;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


class GuardedResultSetState {

    enum State {
        AFTER_LAST,
        ITERATING_OVER_RESULTS(AFTER_LAST),
        BEFORE_FIRST(ITERATING_OVER_RESULTS, AFTER_LAST);

        private final Set<State> validTransitions;

        State(State... validTransitions) {
            this.validTransitions = Collections.unmodifiableSet(
                    new HashSet<>(Arrays.asList(validTransitions)));
        }
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
        if (!currentState.validTransitions.contains(newState)) {
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
