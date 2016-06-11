package com.github.dyna4jdbc.integrationtests;

import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.executeScriptForResultSetString;
import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.newLineSeparated;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ProcessRunnerTest {

    private static final String TEST_DATA = 
            newLineSeparated("Poem::", 
                    "Mary had a little lamb,", 
                    "His fleece was white as snow,", 
                    "And everywhere that Mary went,", 
                    "The lamb was sure to go.");

    private static final String EXPECTED_RESULT_SET_REPRESENTATION = 
            newLineSeparated("RESULT SET #1 ",
                    "                          Poem | ",
                    "-------------------------------|-",
                    "       Mary had a little lamb, | ",
                    " His fleece was white as snow, | ",
                    "And everywhere that Mary went, | ",
                    "      The lamb was sure to go. | ");
    
            
    
    private File tempFile;
    private String commandToExecute;

    @BeforeMethod
    public void beforeMethod() throws IOException {

        tempFile = File.createTempFile("processIntegrationTest", "tmp");
        tempFile.deleteOnExit();

        String fileSeparator = System.getProperty("file.separator");

        switch (fileSeparator) {
        case "/":
            commandToExecute = String.format("cat %s", tempFile.getAbsolutePath());
            break;

        case "\\":
            commandToExecute = String.format("CMD /C \"TYPE %s \"", tempFile.getAbsolutePath());
            break;

        default:
            throw new IllegalStateException("Unexpected file.separator: '" + fileSeparator + "'");
        }

        Files.write(tempFile.toPath(), TEST_DATA.getBytes("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);

    }

    @Test
    public void testContent() throws SQLException {

        String resultSetString = executeScriptForResultSetString("jdbc:dyna4jdbc:process-runner", commandToExecute);

        assertNotNull(resultSetString);

        assertEquals(resultSetString, EXPECTED_RESULT_SET_REPRESENTATION);
    }

}
