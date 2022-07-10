package com.myasnykh.stepan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class UniqueIpCounter {
    private static final String POINT = "\\.";

    public static long readAndCount(String path) throws IOException {
        AtomicLong totalCounter = new AtomicLong(0);
        Map<String, Boolean>[][][] container = createContainer();
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            lines.parallel().forEach(s -> compute(totalCounter, container, s));
        }
        return totalCounter.getAcquire();
    }

    private static Map<String, Boolean>[][][] createContainer() {
        Map<String, Boolean>[][][] container = new ConcurrentHashMap[256][256][256];
        for (int i = 0; i < 256; i++) {
            Map<String, Boolean>[][] firstOctet = new ConcurrentHashMap[256][256];
            container[i] = firstOctet;
            for (int j = 0; j < 256; j++) {
                Map<String, Boolean>[] secondOctet = new ConcurrentHashMap[256];
                container[i][j] = secondOctet;
            }
        }
        return container;
    }

    private static void compute(AtomicLong totalCounter, Map<String, Boolean>[][][] container, String s) {
        String[] octets = s.split(POINT);
        Map<String, Boolean>[] secondOctet =  container[Integer.parseInt(octets[0])][Integer.parseInt(octets[1])];

        Map<String, Boolean> thirdOctet;
        synchronized (secondOctet) {
            thirdOctet = secondOctet[Integer.parseInt(octets[2])];
            if (thirdOctet == null) {
                thirdOctet = new ConcurrentHashMap<>();
                secondOctet[Integer.parseInt(octets[2])] = thirdOctet;
            }
        }

        Boolean exists = thirdOctet.get(octets[3]);
        if (exists == null) {
            thirdOctet.computeIfAbsent(octets[3].intern(), k -> {
                totalCounter.incrementAndGet();
                return true;
            });
        }
    }
}
