package com.github.dyna4jdbc.integrationtests;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.JDBCError;

/**
 * @author Peter Horvath
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

            Future<Boolean> future = executorService.submit(() ->

                statement.execute(longRunningScript)

            );

            Thread.sleep(2_000);

            statement.cancel();

            try {
                future.get();

                fail("Should have thrown an exception");
            } catch (ExecutionException eex) {

                String exceptionMessage = eex.getMessage();
                assertTrue(exceptionMessage.contains(JDBCError.EXECUTION_ABORTED_AT_CLIENT_REQUEST.name()),
                        exceptionMessage);
            }
        }
    }
}
