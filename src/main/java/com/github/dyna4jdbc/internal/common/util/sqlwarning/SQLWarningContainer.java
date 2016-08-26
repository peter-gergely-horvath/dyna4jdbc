package com.github.dyna4jdbc.internal.common.util.sqlwarning;

import java.sql.SQLWarning;

/**
 * @author Peter Horvath
 */
public final class SQLWarningContainer {

    private SQLWarning currentSQLWarning;
    
    public void addSQLWarning(SQLWarning newSQLWarning) {

        if (currentSQLWarning == null) {
            currentSQLWarning = newSQLWarning;
            
        } else {

            SQLWarning last = currentSQLWarning;
            while (true) {

                SQLWarning next = last.getNextWarning();
                if (next == null) {
                    break;
                }

                last = next;
            }

            last.setNextWarning(newSQLWarning);
        }
    }

    
    
    public void clearWarnings() {
        this.currentSQLWarning = null;
    }
    
    public SQLWarning getWarnings() {
        return this.currentSQLWarning;
    }

}
