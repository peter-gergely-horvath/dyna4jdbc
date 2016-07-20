package com.github.dyna4jdbc.internal.sqlstate;

import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.stream.Stream;

import static org.testng.Assert.*;

/**
 * @author Peter Horvath
 */
public class SQLStateTest {

    private static final int NO_DATA_CODE = 02;

    @Test
    public void testNoCodeIsNull() throws SQLException {

        Stream.of(SQLState.values()).forEach(sqlState ->
                assertNotNull(sqlState.code, "Invalid code for: " + sqlState + ": " + sqlState.code)
        );
    }

    @Test
    public void testAllCodeIsOfLength5() throws SQLException {

        Stream.of(SQLState.values()).forEach(sqlState ->
                assertTrue(sqlState.code.length() == 5, "Invalid code for: " + sqlState + ": " + sqlState.code)
        );
    }

    @Test
    public void testSuccessCodesStartWith00() throws SQLException {

        Stream.of(SQLState.values()).filter( it -> it.category.equals(Category.SUCCESS)).forEach(sqlState ->
                assertTrue(sqlState.code.startsWith("00"), "Invalid code for: " + sqlState)
        );
    }

    @Test
    public void testWarningCodesStartWith01() throws SQLException {

        Stream.of(SQLState.values()).filter( it -> it.category.equals(Category.WARNING)).forEach(sqlState ->
                assertTrue(sqlState.code.startsWith("01"), "Invalid code for: " + sqlState)
        );
    }

    @Test
    public void testNoDataCodesStartWith02() throws SQLException {

        Stream.of(SQLState.values()).filter( it -> it.category.equals(Category.NO_DATA)).forEach(sqlState ->
                assertTrue(sqlState.code.startsWith("02"), "Invalid code for: " + sqlState)
        );
    }

    @Test
    public void testErrorCodesStartWithGreaterThan02() throws SQLException {

        Stream.of(SQLState.values()).filter( it -> it.category.equals(Category.ERROR)).forEach(sqlState -> {

                String firstTwoCharacters = sqlState.code.substring(0,2);

                if(firstTwoCharacters.matches("\\d+")) {
                    assertTrue(Integer.parseInt(firstTwoCharacters) > NO_DATA_CODE, "Incorrect code for: " + sqlState);
                }
        });
    }

}
