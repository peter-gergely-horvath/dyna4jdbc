package com.github.dyna4jdbc.internal;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.sqlstate.Category;

public class JDBCErrorTest {

    private static final int NO_DATA_CODE = 02;

    @Test
    public void testNoCodeIsNull() throws SQLException {

        Stream.of(JDBCError.values()).forEach(
                jdbcError -> assertNotNull(jdbcError.getSqlStateAsString(), "Null sqlState for: " + jdbcError));
    }

    @Test
    public void testAllCodeIsOfLength5() throws SQLException {

        Stream.of(JDBCError.values()).forEach(jdbcError -> {
            String sqlState = jdbcError.getSqlStateAsString();

            assertTrue(sqlState.length() == 5, "Invalid code for: " + jdbcError + ": " + sqlState);
        });
    }

    @Test
    public void testSuccessCodesStartWith00() throws SQLException {

        getWithCategory(Category.SUCCESS).forEach(jdbcError -> {

            String sqlState = jdbcError.getSqlStateAsString();

            assertTrue(jdbcError.getSqlStateAsString().startsWith("00"), "Invalid code for: " + sqlState);
        });
    }

    @Test
    public void testWarningCodesStartWith01() throws SQLException {

        getWithCategory(Category.WARNING).forEach(jdbcError -> {

            String sqlState = jdbcError.getSqlStateAsString();

            assertTrue(jdbcError.getSqlStateAsString().startsWith("01"), "Invalid code for: " + sqlState);
        });
    }

    @Test
    public void testNoDataCodesStartWith02() throws SQLException {

        getWithCategory(Category.NO_DATA).forEach(jdbcError -> {

            String sqlState = jdbcError.getSqlStateAsString();

            assertTrue(jdbcError.getSqlStateAsString().startsWith("02"), "Invalid code for: " + sqlState);
        });
    }

    @Test
    public void testErrorCodesStartWithGreaterThan02() throws SQLException {

        getWithCategory(Category.NO_DATA).forEach(jdbcError -> {

            String sqlState = jdbcError.getSqlStateAsString();

            String firstTwoCharacters = sqlState.substring(0, 2);

            if (firstTwoCharacters.matches("\\d+")) {
                assertTrue(Integer.parseInt(firstTwoCharacters) > NO_DATA_CODE, "Incorrect code for: " + sqlState);
            }

            assertTrue(jdbcError.getSqlStateAsString().startsWith("02"), "Invalid code for: " + sqlState);
        });
    }

    private static List<JDBCError> getWithCategory(Category wantedCategory) {

        return Stream.of(JDBCError.values())
                .filter(jdbcError -> jdbcError.sqlStateClass.category == wantedCategory)
                .collect(Collectors.toList());
    }

}
