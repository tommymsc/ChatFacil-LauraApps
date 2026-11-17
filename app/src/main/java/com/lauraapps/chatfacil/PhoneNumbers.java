package com.lauraapps.chatfacil;

/** International number normalization. Does not require reading phone contacts. */
public final class PhoneNumbers {
    private PhoneNumbers() {}
    public static String normalize(String raw, String callingCode) {
        if (raw == null) return null;
        String cleaned = raw.trim().replaceAll("[\\s().\\-]", "");
        if (!cleaned.matches("(\\+|00)?[0-9]{7,17}")) return null;
        String number;
        if (cleaned.startsWith("+")) number = cleaned.substring(1);
        else if (cleaned.startsWith("00")) number = cleaned.substring(2);
        else {
            if (callingCode == null || !callingCode.matches("[0-9]{1,3}")) return null;
            // Allow a user to paste a fully qualified number without a + sign when identifiable.
            // Do not guess ambiguous numbers; user can always add + for international numbers.
            number = callingCode + cleaned;
        }
        if (!number.matches("[1-9][0-9]{7,14}")) return null;
        return number;
    }
}
