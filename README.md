# dyna4jdbc

Note: both development and documentation is in progress!

## Introduction

dyna4jdbc is a JDBC driver implementation written in the Java programming language. It enables the user to execute dynamic JVM languages or console-oriented external programs through the JDBC API, captures the output generated and parses it to a standard JDBC ResultSet, which the caller application can process further. 

This approach combines the power of dynamic languages with the rich ecosystem of reporting and data visualisation tools, that support the JDBC standard. You can for example write a complex Groovy/Scala/JavaScript/Jython etc. JVM script or call any console application (Shell script, Python, R language, etc.) and visualize or process the results further from your favourite JDBC-compatible tool. 

## How does it work?

Any dynamic JVM language (including JavaScript, Groovy, Scala, Jython etc.)  which supports the standard Java ScriptEngine API, or console-oriented external application (Shell-script, Perl, Python etc.) can be called via this driver. The output is captured and is assumed to be a TAB (\t) delimited tabular result, which is parsed by the driver can be retrieved as a Java JDBC ResultSet from any JDBC-enabled client application. 

## Status and availability

As of now, this library is under development and should be considered as "development" or "early alpha", with many known issues and limitations. It is not yet promoted to any library repository. Any potential user will have to clone the git repository and build it manually.  

## Dependencies

The driver is built into a self-contained JAR file. Any additional dependencies required to run a specific JVM language must be installed separately by the user. 

## System Requirements

### Java version

This driver was developed agains Oracle Java Development Kit, version 8 using Java 8 language features. Java 8 is required for both building and running.

### Requirements for building 

Git (to clone the repository), Maven 3 and Oracle Java 8 JDK are required to build this driver.

### Requirements for running

This driver requires Java Runtime Environment, version 8 or later. It should be compatible on any operating system, where Java 8 is officially available. 

## User Manual

Documentation is in progress: this section contains only the essential basics required to get started. 

### Adding data to the result set

Simply *echo (print)* your results to the standard output using the host language. Each line emitted represents a row in the result set. By default, TAB (`\t`) separates cells (this can be customized in the driver configuration). The driver captures the values and makes reasonable efforts to heuristically guess the correct SQL type corresponding to the value. 

### Defining the JDBC headers

Column headers can either be automatically generated from the index of the column or defined by the first output line of the scipt. In the latter case, the script must emit a special _formatting header_, wich contains three fields separated by a colon (':') character.

`<Column Header> : <SQL type definition> : <additional flags>`

1. Column Header: The human-readable name assigned to the column (see `java.sql.ResultSetMetaData.getColumnLabel(int)`)
2. SQL type definition(optional): the SQL type definition of the column, auto-detected if not present
3. Additional flags(optional): additional formatting flags for the column

**Important: The _formatting header_ MUST ALWAYS CONTAIN TWO COLONS, even if a field is not used!**

A first line, which does not match this criteria will be interpreted as part of the result set and the column headers will be generated automatically:

Examples for the first line output:

1. `FOO\tBAR` ==> Columns are named as 1 and 2, while 'FOO' and 'BAR' appear in result set as the first entry.
2. `FOO::\tBAR::` ==> Columns named as 'FOO' and 'BAR', values from the second output row appear in the result set as first row. 
3. `FOO:\tBAR:` ==> Columns are named as 1 and 2, while 'FOO:' and 'BAR:' appear in result set as the first entry.
4. `FOO:\tBAR:` ==> Columns are named as 1 and 2, while 'FOO:' and 'BAR:' appear in result set as the first entry.
5. `FOO::\tBAR:` ==> Error condition detected by the driver, error INCONSISTENT_HEADER_SPECIFICATION is emitted.
6. `FOO:\tBAR::` ==> Error condition detected by the driver, error INCONSISTENT_HEADER_SPECIFICATION is emitted.
7. `FOO\tBAR::` ==> Error condition detected by the driver, error INCONSISTENT_HEADER_SPECIFICATION is emitted.
8. `FOO::\tBAR:` ==> Error condition detected by the driver, error INCONSISTENT_HEADER_SPECIFICATION is emitted.

Check the samples on details regarding how to generate output properly.

## Samples

### Groovy 

#### Requirements 

In addition to the driver JAR itself, the full Groovy language pack has to be available on the classpath.

The following sample demonstrates fetching JSON data from Google Finance Internet service and transforming it into a tabular format. (NOTE: This is just a *technical sample*: please consule Google Use of Service Conditions before you actually use any data from their API!) 

JDBC Connect URL: `jdbc:dyna4jdbc:scriptengine:groovy`

Executed Groovy script:

```groovy
import groovy.json.JsonSlurper

def printRow(String... values) { println values.join("\t") }

def jsonData = new URL("http://www.google.com/finance/info?infotype=infoquoteall&q=NASDAQ:AAPL,IBM,MSFT,GOOG").text.replaceFirst("//", "")
def data = new JsonSlurper().parseText(jsonData)

printRow "Ticker::", "Name::", "Open::", "Close::", "Change::"
data.each { printRow it["t"], it["name"], it["op"], it["l_cur"], it["c"] }
```

Full Java source code (Remember: both driver JAR and Groovy JARs have to be on the classpath):

```java
package samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
public class GroovyDemo {

    private static final String GROOVY_SCRIPT = ""
            + "	import groovy.json.JsonSlurper														\n"
            + "																						\n"
            + " def printRow(String... values) { println values.join(\"\t\") }						\n"
            + " def jsonData = new URL('http://www.google.com/finance/info?"
                + "infotype=infoquoteall&q=NASDAQ:AAPL,IBM,MSFT,GOOG').text.replaceFirst('//', '')	\n"
            + " def data = new JsonSlurper().parseText(jsonData)									\n"
            + " printRow 'Ticker::', 'Name::', 'Open::', 'Close::', 'Change::'						\n"
            + " data.each { printRow it['t'], it['name'], it['op'], it['l_cur'], it['c'] } 			\n";

    public static void main(String[] args) throws SQLException {

        try (Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:groovy")) {

            try (Statement statement = connection.createStatement()) {
                boolean results = statement.execute(GROOVY_SCRIPT);
                while (results) {
                    try (ResultSet rs = statement.getResultSet()) {

                        printResultSetWithHeaders(rs);
                    }

                    results = statement.getMoreResults();
                }
            }
        }
    }

    private static void printResultSetWithHeaders(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        final int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnLabel = metaData.getColumnLabel(i);
            int columnDisplaySize = metaData.getColumnDisplaySize(i);
            String formatStr = "%" + columnDisplaySize + "s | ";
            System.out.format(formatStr, columnLabel);
        }
        System.out.println();

        for (int i = 1; i <= columnCount; i++) {
            int columnDisplaySize = metaData.getColumnDisplaySize(i);
            String formatStr = "%" + columnDisplaySize + "s | ";
            System.out.format(String.format(formatStr, "").replace(' ', '-'));
        }
        System.out.println();

        while (rs.next()) {

            for (int i = 1; i <= columnCount; i++) {
                int columnDisplaySize = metaData.getColumnDisplaySize(i);
                String formatStr = "%" + columnDisplaySize + "s | ";
                System.out.format(formatStr, rs.getString(i));
            }
            System.out.println();
        }
    }

}
```

