package com.myasnykh.stepan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class UniqueIpCounter {
    private static final String POINT = "\\.";

    public static long readAndCount(String path) throws IOException {
        AtomicLong totalCounter = new AtomicLong(0);
        boolean[][][][] container = new boolean[256][256][256][256];
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            lines.parallel().forEach(s -> compute(totalCounter, container, s));
        }
        return totalCounter.getAcquire();
    }

    private static void compute(AtomicLong totalCounter, boolean[][][][] container, String s) {
        String[] octets = s.split(POINT);
        boolean[] thirdOctet= container[Integer.parseInt(octets[0])][Integer.parseInt(octets[1])][Integer.parseInt(octets[2])];

        synchronized (thirdOctet) {
            if (!thirdOctet[Integer.parseInt(octets[3])]) {
                totalCounter.incrementAndGet();
                thirdOctet[Integer.parseInt(octets[3])] = true;
            }
        }
    }
}
