def commandAndArguments = [
        "mvn",
        "clean",
        "install" ]

def process = new ProcessBuilder(commandAndArguments.collect { it.toString() })
        .redirectErrorStream(true)
        .start()

process.in.eachLine { println it }

if(process.waitFor() != 0) {
    System.err.println "Build failure detected! Return code: ${process.waitFor()}. Examine output for errors!"
    System.exit 1
}