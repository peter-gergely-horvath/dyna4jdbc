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

 
package com.github.dyna4jdbc.integrationtests;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class NodeJSTest extends IntegrationTestBase {

    protected NodeJSTest() {
        super("jdbc:dyna4jdbc:nodejs");
    }

    @Test
    @Override
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "console.log(\"Hello World\")";

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    @Override
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = "msg = \"Hello World\"; ";

        String printVariableScript = new StringBuilder()
                .append("console.log(\"Message::\");\n ")
                .append("console.log(msg); \n")
                .toString();

        assertVariableDeclaredInStatementVisibleFromAnotherStatement(variableDeclarationScript, printVariableScript);

    }

    @Test
    @Override
    public void testHeadersNotSpecified() throws Exception {

        String script = "console.log(\"A:\tB:\");\n console.log(\"First A\tFirst B\");\n console.log(\"Second A\tSecond B\");\n";

        assertHeadersNotSpecifiedCausesNumbersToBeUsed(script);
    }

    @Test
    @Override
    public void testHeadersSpecified() throws Exception {

        String script = "console.log(\"A::\tB::\");\n console.log(\"First A\tFirst B\");\n console.log(\"Second A\tSecond B\");";

        assertIfHeadersAreSpecifiedThenHeadersAreUsed(script);
    }

    @Test
    @Override
    public void testStatementMaxRowsHandlingWithHeaders() throws Exception {

        String script = new StringBuilder()
                .append("console.log(\"A::\tB::\");\n ")
                .append("console.log(\"First A\tFirst B\");\n ")
                .append("console.log(\"Second A\tSecond B\");")
                .append("console.log(\"Third A\tThird B\");")
                .append("console.log(\"Fourth A\tFourth B\");")
                .append("console.log(\"Fifth A\tFifth B\");")
                .toString();

        assertYieldsFirstTwoRowsOnlyWithHeaders(script);
    }

    @Test
    @Override
    public void testStatementMaxRowsHandlingNoHeaders() throws Exception {

        String script = new StringBuilder()
                .append("console.log(\"A:\tB:\");\n ")
                .append("console.log(\"First A\tFirst B\");\n ")
                .append("console.log(\"Second A\tSecond B\");")
                .append("console.log(\"Third A\tThird B\");")
                .append("console.log(\"Fourth A\tFourth B\");")
                .append("console.log(\"Fifth A\tFifth B\");")
                .toString();

        assertYieldsFirstTwoRowsOnlyNoHeaders(script);
    }



    @Test
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "console.log(\"Message::\");\n console.log(parameter1)";

        assertPreparedStatementQueryReturnsParameter(script);
    }

    @Test
    public void testPreparedStatementBindsObjectAsString() throws Exception {

        Object testObject = new Object();

        String script = "console.log(parameter1)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, testObject);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getObject(1);
                    assertEquals(result, testObject.toString());
                }
            }
        }
    }

    @Test(enabled = false)
    public void testPreparedStatementBindsNull() throws Exception {

        String script = "console.log(parameter1)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setNull(1, Types.VARCHAR);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getObject(1);
                    assertNull(result);
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsString() throws Exception {

        String script = "console.log(parameter1)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setString(1, "FOOBAR");

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getObject(1);
                    assertEquals(result, "FOOBAR");
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsCharacter() throws Exception {

        String script = "console.log(parameter1)";

        Character expectedValue = new Character('A');

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, expectedValue);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getObject(1);
                    assertEquals(result, expectedValue.toString());
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsBooleanTrue() throws Exception {

        String script = "console.log(parameter1)";

        Boolean expectedValue = Boolean.TRUE;

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, expectedValue);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getBoolean(1);
                    assertEquals(result, expectedValue);
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsBooleanFalse() throws Exception {

        String script = "console.log(parameter1)";

        Boolean expectedValue = Boolean.FALSE;

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, expectedValue);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getBoolean(1);
                    assertEquals(result, expectedValue);
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsInteger() throws Exception {

        String script = "console.log(parameter1)";

        Integer expectedValue = new Integer(42);

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, expectedValue);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getInt(1);
                    assertEquals(result, expectedValue);
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsLong() throws Exception {

        String script = "console.log(parameter1)";

        Long expectedValue = new Long(42);

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, expectedValue);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getLong(1);
                    assertEquals(result, expectedValue);
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsByte() throws Exception {

        String script = "console.log(parameter1)";

        Byte expectedValue = new Byte((byte)42);

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, expectedValue);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getByte(1);
                    assertEquals(result, expectedValue);
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsShort() throws Exception {

        String script = "console.log(parameter1)";

        Short expectedValue = new Short((short)42);

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, expectedValue);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getShort(1);
                    assertEquals(result, expectedValue);
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsFloat() throws Exception {

        String script = "console.log(parameter1)";

        Float expectedValue = new Float(42f);

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, expectedValue);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getFloat(1);
                    assertEquals(result, expectedValue);
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsDouble() throws Exception {

        String script = "console.log(parameter1)";

        Double expectedValue = new Double(12.345);

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, expectedValue);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object result = resultSet.getDouble(1);
                    assertEquals(result, expectedValue);
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsDate() throws Exception {

        String script = new StringBuilder()
                .append("console.log(\"TheTimeStamp:TIMESTAMP:format=dd-MM-yyyy HH:mm\");\n")
                .append("console.log((\"0\" + parameter1.getDate()).slice(-2) + \"-\" + " +
                        "(\"0\"+(parameter1.getMonth()+1)).slice(-2) + \"-\" +\n" +
                        "    parameter1.getFullYear() + \" \" + " +
                        "(\"0\" + parameter1.getHours()).slice(-2) + \":\" + " +
                        "(\"0\" + parameter1.getMinutes()).slice(-2));\n")
                .toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        Date expectedValue = new Date(new java.util.Date().getTime());

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, expectedValue);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Date result = resultSet.getDate(1);
                    assertEquals(simpleDateFormat.format(result), simpleDateFormat.format(expectedValue));
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsList() throws Exception {

        String headerScript =
                "console.log(parameter1[0] + '\t' + parameter1[1] + '\t' + parameter1[2] + '\t' + parameter1[3] +'\n');";
        String dataScript = "console.log(parameter2[0] + '\t' + parameter2[1] + '\t' + parameter2[2] + '\t' + parameter2[3] +'\n');";

        String script = String.format("%s \n %s", headerScript, dataScript);

        List<String> headers = Arrays.asList("textcol::", "decimalcol::", "intcol1::", "intcol2::");
        List<String> data = Arrays.asList("foobar", "12.34", "56", "789");

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, headers);
                statement.setObject(2, data);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    assertEquals("foobar", resultSet.getString("textcol"));
                    assertEquals(12.34, resultSet.getDouble("decimalcol"));
                    assertEquals(56, resultSet.getInt("intcol1"));
                    assertEquals(789, resultSet.getInt("intcol2"));
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsArray() throws Exception {

        String headerScript =
                "console.log(parameter1[0] + '\t' + parameter1[1] + '\t' + parameter1[2] + '\t' + parameter1[3] +'\n');";
        String dataScript = "console.log(parameter2[0] + '\t' + parameter2[1] + '\t' + parameter2[2] + '\t' + parameter2[3] +'\n');";

        String script = String.format("%s \n %s", headerScript, dataScript);

        String[] headers = new String[] {"textcol::", "decimalcol::", "intcol1::", "intcol2::"};
        String[] data = new String[] { "foobar", "12.34", "56", "789"};

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, headers);
                statement.setObject(2, data);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    assertEquals("foobar", resultSet.getString("textcol"));
                    assertEquals(12.34, resultSet.getDouble("decimalcol"));
                    assertEquals(56, resultSet.getInt("intcol1"));
                    assertEquals(789, resultSet.getInt("intcol2"));
                }
            }
        }
    }

    @Test
    public void testPreparedStatementBindsMap() throws Exception {

        String headerScript =
                "console.log('textcol::\tdecimalcol::\tintcol1::\tintcol2::\n');";
        String dataScript = "console.log(" +
                "  parameter1['textcol'] + '\t' " +
                "+ parameter1['decimalcol'] + '\t' " +
                "+ parameter1['intcol1'] + '\t' " +
                "+ parameter1['intcol2'] +'\n');";

        String script = String.format("%s \n %s", headerScript, dataScript);

        Map<String, Object> headerToData = new LinkedHashMap<String, Object>();
        headerToData.put("textcol", "foobar");
        headerToData.put("decimalcol", 12.34);
        headerToData.put("intcol1", 56);
        headerToData.put("intcol2", 789);

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setObject(1, headerToData);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    assertEquals("foobar", resultSet.getString("textcol"));
                    assertEquals(12.34, resultSet.getDouble("decimalcol"));
                    assertEquals(56, resultSet.getInt("intcol1"));
                    assertEquals(789, resultSet.getInt("intcol2"));
                }
            }
        }
    }




    @Test
    public void testDatabaseMetaDataProductInformation() throws Exception {

        Process process = new ProcessBuilder()
            .command("node", "--version")
            .redirectErrorStream(true)
            .start();
        
        String expectedVersionReported;
        
        try (BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(
                process.getInputStream(), StandardCharsets.UTF_8))) {
            
            List<String> outputLines = processOutputReader.lines().collect(Collectors.toList());
            
            Assert.assertEquals(outputLines.size(), 1);
            
            String singleLine = outputLines.get(0);
            
            /* Remove non-numeric/dot characters from output
             * e.g. "v7.4.0" --> "7.4.0"
             */
            expectedVersionReported = singleLine.replaceAll("[^\\d\\.]", "");
        }

        process.waitFor();
        
        assertDataBaseMetadataReturns("Node.js", expectedVersionReported);
    }

}
