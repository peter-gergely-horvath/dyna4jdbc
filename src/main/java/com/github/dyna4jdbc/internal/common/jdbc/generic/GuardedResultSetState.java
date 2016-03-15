package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.SQLError;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class GuardedResultSetState {

    enum State {
        AFTER_LAST,
        ITERATING_OVER_RESULTS(AFTER_LAST),
        BEFORE_FIRST(ITERATING_OVER_RESULTS, AFTER_LAST);

        private final Set<State> validTransitions;

        State(State... validTransitions) {
            this.validTransitions = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(validTransitions)));
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
        if(!currentState.validTransitions.contains(newState)) {
            throw SQLError.DRIVER_BUG_UNEXPECTED_STATE.raiseException(
                    "Transitioning from " + this + " to " + newState + " is illegal");
        }

        this.currentState = newState;
    }

    void checkValidStateForRowAccess() throws SQLException {
        if(currentState != State.ITERATING_OVER_RESULTS) {
            throw SQLError.JDBC_API_USAGE_CALLER_ERROR.raiseException(
                    "Performing this operation in state " + currentState + " is illegal");
        }
    }

    @Override
    public String toString() {
        return this.currentState.toString();
    }
}