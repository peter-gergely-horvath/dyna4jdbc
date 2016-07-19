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
