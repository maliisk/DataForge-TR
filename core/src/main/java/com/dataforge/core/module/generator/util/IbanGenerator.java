package com.dataforge.core.module.generator.util;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public final class IbanGenerator {

    private IbanGenerator() {}

    public static String generateTrIban(String bankCode) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        StringBuilder accountNo = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            accountNo.append(random.nextInt(10));
        }

        String trNumeric = "2927";
        String reserveBit = "0";

        String baseNumberStr = bankCode + reserveBit + accountNo.toString() + trNumeric + "00";

        BigInteger baseNumber = new BigInteger(baseNumberStr);
        BigInteger mod97 = new BigInteger("97");

        int remainder = baseNumber.remainder(mod97).intValue();
        int checksum = 98 - remainder;

        String checksumStr = (checksum < 10) ? "0" + checksum : String.valueOf(checksum);

        return "TR" + checksumStr + bankCode + reserveBit + accountNo.toString();
    }
}