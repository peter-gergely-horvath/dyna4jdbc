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

 
package com.github.dyna4jdbc.internal.common.util.sqlwarning;

import java.sql.SQLWarning;
import java.util.LinkedList;

import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;

public final class SQLWarningCollectorSink implements SQLWarningSink {

    private final LinkedList<SQLWarning> list = new LinkedList<>();

    @Override
    public void onSQLWarning(SQLWarning warning) {
        list.add(warning);
    }

    public LinkedList<SQLWarning> getWarnings() {
        return list; // defensive copy?
    }
}