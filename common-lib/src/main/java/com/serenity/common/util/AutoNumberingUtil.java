package com.serenity.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AutoNumberingUtil {

    private static final String FORMAT = "%s-%06d";

    /**
     * Generates a formatted auto-number from a prefix and sequence value.
     * The sequence is zero-padded to 6 digits.
     * <p>
     * Example: generate("MBR", 1) → "MBR-000001"
     * Example: generate("PAY", 42) → "PAY-000042"
     * Example: generate("CTR", 1234567) → "CTR-1234567"
     *
     * @param prefix    the prefix for the generated number
     * @param sequence  the sequential number
     * @return the formatted number string
     */
    public String generate(String prefix, long sequence) {
        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException("Prefix must not be null or blank");
        }
        if (sequence < 0) {
            throw new IllegalArgumentException("Sequence must be non-negative");
        }
        return String.format(FORMAT, prefix.toUpperCase(), sequence);
    }
}
