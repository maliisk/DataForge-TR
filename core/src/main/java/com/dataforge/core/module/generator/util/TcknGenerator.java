package com.dataforge.core.module.generator.util;

import java.util.concurrent.ThreadLocalRandom;

public final class TcknGenerator {

    private TcknGenerator() {}

    public static String generateValidTckn() {
        int[] digits = new int[11];
        ThreadLocalRandom random = ThreadLocalRandom.current();

        digits[0] = random.nextInt(1, 10);

        for (int i = 1; i < 9; i++) {
            digits[i] = random.nextInt(10);
        }

        int oddSum = digits[0] + digits[2] + digits[4] + digits[6] + digits[8];
        int evenSum = digits[1] + digits[3] + digits[5] + digits[7];

        int digit10 = ((oddSum * 7) - evenSum) % 10;
        if (digit10 < 0) digit10 += 10;
        digits[9] = digit10;

        int first10Sum = 0;
        for (int i = 0; i < 10; i++) {
            first10Sum += digits[i];
        }
        digits[10] = first10Sum % 10;

        StringBuilder tckn = new StringBuilder(11);
        for (int digit : digits) {
            tckn.append(digit);
        }

        return tckn.toString();
    }
}