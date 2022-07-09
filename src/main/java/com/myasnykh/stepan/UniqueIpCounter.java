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
        Map<String, Map<String, Map<String, Map<String, Boolean>>>> container = new ConcurrentHashMap<>();
        Stream<String> lines = Files.lines(Paths.get(path));
        lines.parallel().forEach(s -> compute(totalCounter, container, s));
        lines.close();
        return totalCounter.getAcquire();
    }

    private static void compute(AtomicLong totalCounter, Map<String, Map<String, Map<String, Map<String, Boolean>>>> container, String s) {
        String[] octets = s.split(POINT);
        Map<String, Map<String, Map<String, Boolean>>> firstOctet = container.get(octets[0]);
        if (firstOctet == null) {
            firstOctet = container.computeIfAbsent(octets[0].intern(), k -> new ConcurrentHashMap<>());
        }

        Map<String, Map<String, Boolean>> secondOctet = firstOctet.get(octets[1]);
        if (secondOctet == null) {
            secondOctet = firstOctet.computeIfAbsent(octets[1].intern(), k -> new ConcurrentHashMap<>());
        }

        Map<String, Boolean> thirdOctet = secondOctet.get(octets[2]);
        if (thirdOctet == null) {
            thirdOctet = secondOctet.computeIfAbsent(octets[2].intern(), k -> new ConcurrentHashMap<>());
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
