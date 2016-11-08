# dyna4JDBC

A JDBC driver for running dynamic JVM languages and external programs.

## Introduction

dyna4JDBC is a JDBC driver implementation written in the Java programming language. It enables users to execute dynamic JVM languages or console-oriented external programs through the JDBC API. It captures the output generated and parses it to a standard JDBC ResultSet, which the caller application can process further. 

This approach combines the power of dynamic languages with the rich ecosystem of reporting and data visualisation tools, that support the JDBC standard. You can for example write a complex Groovy/Scala/JavaScript/Jython etc. JVM script or call any console application (Shell script, Python, R language, etc.) and visualize or process the results further from your favourite JDBC-compatible tool. 

## Documentation

For further information, please check out the 
 * [Official project home page] (http://dyna4jdbc.org/) 
 * [Project Wiki Page](https://github.com/peter-gergely-horvath/dyna4jdbc/wiki)

## Sample

NOTE: This is just a sample - the project's primary goal is empowering Java Reporting and ETL applications to call dynamic script langauges through the JDBC API, and NOT programmatic usage.

```java
package sample;

import java.sql.*;

public class HelloWorldSample {

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:dyna4jdbc:scriptengine:JavaScript";

        try (Connection connection = DriverManager.getConnection(url)) {

            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate(" var msg = 'Hello World'; ");
                ResultSet resultSet = statement.executeQuery(" print(msg); ");

                while (resultSet.next()) {
                    String string = resultSet.getString(1);

                    System.out.println(string);
                }
            }
        }
    }
}
```




## Download

Please visit [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.peter-gergely-horvath%22%20a%3A%22dyna4jdbc%22) or the the [release download section](https://github.com/peter-gergely-horvath/dyna4jdbc/releases) to download the binary version of dyna4jdbc JDBC driver. 

## Build Status

[![Build Status](https://travis-ci.org/peter-gergely-horvath/dyna4jdbc.svg?branch=master)](https://travis-ci.org/peter-gergely-horvath/dyna4jdbc)

Continuous integration platform is provided by [Travis CI](https://travis-ci.org/)
