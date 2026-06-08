package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void parsesConnectStringAndExternalCommand() {
        App.CommandLineOptions options = App.CommandLineOptions.parse(new String[] {
            "--zk", "localhost:2181,localhost:2182,localhost:2183",
            "--app", "open", "-a", "Calculator"
        });

        assertEquals("localhost:2181,localhost:2182,localhost:2183", options.connectString());
        assertEquals(List.of("open", "-a", "Calculator"), options.externalCommand());
    }

    @Test
    void rejectsMissingZkConnectString() {
        assertThrows(
                IllegalArgumentException.class,
                () -> App.CommandLineOptions.parse(new String[] { "--app", "xcalc" }));
    }

    @Test
    void parsesQuotedExternalCommand() {
        App.CommandLineOptions options = App.CommandLineOptions.parse(new String[] {
            "--zk", "localhost:2181",
            "--app", "\"open -a Calculator\""
        });

        assertEquals(List.of("open", "-a", "Calculator"), options.externalCommand());
    }
}
