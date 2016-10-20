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

 
package com.github.dyna4jdbc.integrationtests;

import com.github.dyna4jdbc.internal.JDBCError;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.dyna4jdbc.CommonTestUtils.assertThrowsSQLExceptionWithJDBCError;

/**
 * @author Peter G. Horvath
 */
public class AbortSupportTest {

    private ExecutorService executorService;

    @BeforeMethod
    public void beforeTest() {
        executorService = Executors.newSingleThreadExecutor();

    }

    @AfterMethod
    public void afterTest() {
        executorService.shutdownNow();
    }

    @Test
    public void testAbortHandling()
            throws SQLException, ExecutionException, InterruptedException, BrokenBarrierException {

        String longRunningScript =
                 "for(var i=java.lang.Integer.MIN_VALUE; i<java.lang.Integer.MAX_VALUE; i++) { print(i); }";

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:JavaScript")) {

            Statement statement = connection.createStatement();

            executorService.submit(() -> {

                Thread.sleep(2_000);

                statement.cancel();

                return null;
            });

            assertThrowsSQLExceptionWithJDBCError(JDBCError.EXECUTION_ABORTED_AT_CLIENT_REQUEST, () ->

                    statement.execute(longRunningScript)
            );
        }
    }
}
