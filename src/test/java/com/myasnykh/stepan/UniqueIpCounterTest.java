package com.myasnykh.stepan;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UniqueIpCounterTest {

    @Test
    public void givenExistingFileWithIp_thenCorrectResultExpected() throws IOException {
        long count = UniqueIpCounter.readAndCount("src/test/resources/test.txt");

        assertEquals(4, count);

    }

    @Test
    public void givenNotExistingFile_thenExceptionExpected() {
        assertThrows(IOException.class, () -> UniqueIpCounter.readAndCount("wrongPath"));
    }

}