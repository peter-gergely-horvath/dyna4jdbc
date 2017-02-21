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

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.ResultSetScriptOutputHandlerConstants;
import com.github.dyna4jdbc.internal.common.util.collection.BoundedIterator;
import com.github.dyna4jdbc.internal.common.util.collection.RemoveRetrievedElementIterator;

final class RowIteratorFactory {
    
    private RowIteratorFactory() {
        // no external instances
    }
    
    private static final RowIteratorFactory INSTANCE = new RowIteratorFactory();
    
    static RowIteratorFactory getInstance() {
        return INSTANCE;
    }


    <T> Iterator<T> getRowIterator(List<T> dataRows, Statement statement) {
        try {
            int maxRows;

            if (statement != null) {
                maxRows = statement.getMaxRows();
            } else {
                maxRows = ResultSetScriptOutputHandlerConstants.MAX_ROWS_NO_BOUNDS;
            }

            Iterator<T> actualRowIterator =
                    new RemoveRetrievedElementIterator<T>(dataRows.iterator());

            if (maxRows != ResultSetScriptOutputHandlerConstants.MAX_ROWS_NO_BOUNDS) {
                return new BoundedIterator<>(actualRowIterator, maxRows);
            } else {
                return actualRowIterator;
            }

        } catch (SQLException e) {
            // should not happen, since the statement is active when
            // we create this object
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    e, "Caught SQLException");
        }
    }
}
