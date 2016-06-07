
def commandAndArguments = [
        "mvn",
        "clean",
        "clover:setup",
        "test",
        "clover:aggregate",
        "clover:clover",
        "-P clover,!default" ]

def process = new ProcessBuilder(commandAndArguments.collect { it.toString() })
        .redirectErrorStream(true)
        .start()

process.in.eachLine { println it }

if(process.waitFor() != 0) {
    System.err.println "Build failure detected! Return code: ${process.waitFor()}. Examine output for errors!"
    System.exit 1
}

java.awt.Desktop.desktop.open(new File(".", "target/site/clover/index.html"))