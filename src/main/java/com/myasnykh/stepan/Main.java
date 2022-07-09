package com.myasnykh.stepan;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter absolute path to file");
        String path = sc.nextLine();

        System.out.println("Calculating uniq ips... Started at " + LocalDateTime.now());
        long result = UniqueIpCounter.readAndCount(path);
        System.out.println("Finish calculating at " + LocalDateTime.now());
        System.out.println("Result = " + result);

    }
}
