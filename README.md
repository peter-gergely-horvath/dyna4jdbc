# dyna4jdbc

## Introduction

dyna4jdbc is a JDBC driver implementation written in the Java programming language. It enables the user to execute dynamic JVM languages or console-oriented external programs through the JDBC API, captures the output generated and parses it to a standard JDBC ResultSet, which the caller application can process further. 

## How does it work?

Any dynamic JVM language (including JavaScript, Groovy, Scala, Jython etc.)  which supports the standard Java ScriptEngine API, or console-oriented external application (Shell-script, Perl, Python etc.) can be called via this driver. The output is assumed to be a TAB (\t) delimited tabular result, which can be retrieved as a Java JDBC ResultSet from any JDBC-enabled client application. This approach combines the power of dynamic languages with the rich ecosystem of reporting and data visualisation tools, that support the JDBC standard.     

## Status and availability

As of now, this library is under development and should be considered as "early beta", with many known issues and limitations. It is not yet promoted to any repository. Any potential user will have to clone the git repository and build it manually.  

## Dependencies

The driver is built into a self-contained JAR file. Any additional dependencies required to run a specific JVM language must be installed separately by the user. 

## System Requirements

### Java version

This driver was developed agains Oracle Java Development Kit, version 8 using Java 8 language features. Java 8 is required for both building and running.

### Requirements for building 

Git (to clone the repository), Maven 3 and Oracle Java 8 JDK are required to build this driver.

### Requirements for running

This driver requires Java Runtime Environment, version 8 or later. It should be compatible on any operating system, where Java 8 is officially available. 

## Samples

### Groovy 

#### Requirements 

In addition to the driver JAR itself, the full Groovy language pack has to be available on the classpath.

The following sample demonstrates fetching JSON data from Google Finance Internet service, transforming it into a tabular format 

Connect URL: `jdbc:dyna4jdbc:scriptengine:groovy`


```groovy
import groovy.json.JsonSlurper

def printRow(String... values) { println values.join("\t") }

def jsonData = new URL("http://www.google.com/finance/info?infotype=infoquoteall&q=NASDAQ:AAPL,IBM,MSFT,GOOG").text.replaceFirst("//", "")
def data = new JsonSlurper().parseText(jsonData)

printRow "Ticker::", "Name::", "Open::", "Close::", "Change::"
data.each { printRow it["t"], it["name"], it["op"], it["l_cur"], it["c"] }
```
