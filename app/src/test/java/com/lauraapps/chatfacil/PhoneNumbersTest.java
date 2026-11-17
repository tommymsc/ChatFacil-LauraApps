package com.lauraapps.chatfacil;

import org.junit.Test;
import static org.junit.Assert.*;

public class PhoneNumbersTest {
    @Test public void domesticDominican() { assertEquals("18095551234",PhoneNumbers.normalize("809-555-1234","1")); }
    @Test public void explicitInternational() { assertEquals("18095551234",PhoneNumbers.normalize("+1 (809) 555-1234","52")); }
    @Test public void internationalZeroZero() { assertEquals("34600123456",PhoneNumbers.normalize("0034 600 123 456","1")); }
    @Test public void rejectTooShort() { assertNull(PhoneNumbers.normalize("12345","1")); }
    @Test public void rejectLetters() { assertNull(PhoneNumbers.normalize("abc8095551234","1")); }
    @Test public void rejectExcess() { assertNull(PhoneNumbers.normalize("+1234567890123456789","1")); }
    @Test public void rejectMissing() { assertNull(PhoneNumbers.normalize("","1")); }
    @Test public void rejectInvalidCountry() { assertNull(PhoneNumbers.normalize("8095551234","abc")); }
}
