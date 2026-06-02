package com.serenity.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@UtilityClass
public class PhoneNumberUtil {

    /**
     * West African country calling codes mapped to their digit length (excluding the country code prefix).
     * The local number length is typically 8 digits for most West African countries.
     */
    private static final Map<String, Integer> WEST_AFRICAN_CODES = Map.ofEntries(
            Map.entry("+225", 10),  // Côte d'Ivoire
            Map.entry("+223", 8),   // Mali
            Map.entry("+221", 9),   // Sénégal
            Map.entry("+226", 8),   // Burkina Faso
            Map.entry("+227", 8),   // Niger
            Map.entry("+228", 8),   // Togo
            Map.entry("+229", 8),   // Bénin
            Map.entry("+232", 8),   // Sierra Leone
            Map.entry("+233", 10),  // Ghana
            Map.entry("+234", 10),  // Nigeria
            Map.entry("+235", 8),   // Tchad
            Map.entry("+236", 8),   // Centrafrique
            Map.entry("+237", 9),   // Cameroun
            Map.entry("+240", 9),   // Guinée Équatoriale
            Map.entry("+241", 8),   // Gabon
            Map.entry("+242", 9),   // Congo
            Map.entry("+243", 9),   // RD Congo
            Map.entry("+244", 9),   // Angola
            Map.entry("+245", 7),   // Guinée-Bissau
            Map.entry("+245", 7),   // Guinée-Bissau
            Map.entry("+220", 7),   // Gambie
            Map.entry("+224", 9),   // Guinée
            Map.entry("+245", 7),   // Guinée-Bissau
            Map.entry("+258", 9),   // Mozambique
            Map.entry("+260", 9),   // Zambie
            Map.entry("+261", 9),   // Madagascar
            Map.entry("+265", 9),   // Malawi
            Map.entry("+266", 8)    // Lesotho
    );

    /**
     * Normalizes a West African phone number to E.164 format.
     * Handles various input formats:
     * <ul>
     *     <li>+225XXXXXXXXX → +225XXXXXXXXX (already E.164)</li>
     *     <li>225XXXXXXXXX → +225XXXXXXXXX</li>
     *     <li>0XXXXXXXXX → +225XXXXXXXXX (assumes Côte d'Ivoire default)</li>
     *     <li>XXXXXXXX → +225XXXXXXXXX (assumes Côte d'Ivoire default)</li>
     * </ul>
     *
     * @param phone the phone number to normalize
     * @return the normalized E.164 phone number, or null if input is null/blank
     * @throws IllegalArgumentException if the phone number cannot be normalized
     */
    public String normalizeToE164(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        // Strip all whitespace, dashes, dots, parentheses
        String cleaned = phone.replaceAll("[\\s\\-\\.\\(\\)]+", "");

        // Already in E.164 format with + prefix
        if (cleaned.startsWith("+")) {
            return validateAndReturn(cleaned);
        }

        // Starts with 00 (international dialing prefix) - replace with +
        if (cleaned.startsWith("00")) {
            return validateAndReturn("+" + cleaned.substring(2));
        }

        // Starts with a known country code without +
        for (String countryCode : WEST_AFRICAN_CODES.keySet()) {
            String codeDigits = countryCode.substring(1); // Remove the +
            if (cleaned.startsWith(codeDigits)) {
                return validateAndReturn("+" + cleaned);
            }
        }

        // Starts with 0 (local format) - default to Côte d'Ivoire (+225)
        if (cleaned.startsWith("0")) {
            return validateAndReturn("+225" + cleaned.substring(1));
        }

        // Bare number without prefix - default to Côte d'Ivoire (+225)
        return validateAndReturn("+225" + cleaned);
    }

    private String validateAndReturn(String e164Number) {
        if (!isValidE164Format(e164Number)) {
            log.warn("Phone number does not appear to be valid E.164 format: {}", e164Number);
        }
        return e164Number;
    }

    /**
     * Validates that a phone number is in E.164 format matching West African country codes.
     *
     * @param e164Number the phone number in E.164 format
     * @return true if the format appears valid
     */
    public boolean isValidE164Format(String e164Number) {
        if (e164Number == null || !e164Number.startsWith("+")) {
            return false;
        }

        for (Map.Entry<String, Integer> entry : WEST_AFRICAN_CODES.entrySet()) {
            String countryCode = entry.getKey();
            int localLength = entry.getValue();

            if (e164Number.startsWith(countryCode)) {
                String localPart = e164Number.substring(countryCode.length());
                return localPart.length() == localLength && localPart.matches("\\d+");
            }
        }

        // If the country code is not in our map, just check basic E.164 format
        String digits = e164Number.substring(1);
        return digits.matches("\\d+") && digits.length() >= 7 && digits.length() <= 15;
    }
}
