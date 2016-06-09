# dyna4jdbc 

A JDBC driver for running dynamic JVM languages and external programs.

## Introduction

dyna4jdbc is a JDBC driver implementation written in the Java programming language. It enables users to execute dynamic JVM languages or console-oriented external programs through the JDBC API. It captures the output generated and parses it to a standard JDBC ResultSet, which the caller application can process further. 

This approach combines the power of dynamic languages with the rich ecosystem of reporting and data visualisation tools, that support the JDBC standard. You can for example write a complex Groovy/Scala/JavaScript/Jython etc. JVM script or call any console application (Shell script, Python, R language, etc.) and visualize or process the results further from your favourite JDBC-compatible tool. 

This is the branch for the web site of the project.

For further information, please take a look at the [master branch](https://github.com/peter-gergely-horvath/dyna4jdbc/tree/master) and the [project Wiki Page](https://github.com/peter-gergely-horvath/dyna4jdbc/wiki).
