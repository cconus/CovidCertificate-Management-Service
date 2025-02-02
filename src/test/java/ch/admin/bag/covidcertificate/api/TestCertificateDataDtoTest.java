package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestCertificateDataDtoTest {

    private final String manufacturerCode = "manufacturerCode";
    private final String typeCode = "";
    private final ZonedDateTime sampleDateTime = ZonedDateTime.of(LocalDateTime.of(2021, Month.APRIL, 4, 16, 25), SWISS_TIMEZONE);
    private final String testingCentreOrFacility = "testCenter";
    private final String memberStateOfTest = "CH";

    @Test
    public void testInvalidSampleOrResultDateTime() {
        TestCertificateDataDto testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                null,
                testingCentreOrFacility,
                memberStateOfTest
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_SAMPLE_OR_RESULT_DATE_TIME, exception.getError());

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                ZonedDateTime.now().plusMinutes(5),
                testingCentreOrFacility,
                memberStateOfTest
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_SAMPLE_OR_RESULT_DATE_TIME, exception.getError());

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                testingCentreOrFacility,
                memberStateOfTest
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testInvalidTestCenter() {
        TestCertificateDataDto testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                null,
                memberStateOfTest
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_TEST_CENTER, exception.getError());

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                "",
                memberStateOfTest
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_TEST_CENTER, exception.getError());

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                "lslksiueiufhflsleifhgoskeiusfdsafdrfhffdsafdsafdsafdsafvasfdsafgdsagfdadsafdsafdsa",
                memberStateOfTest
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_TEST_CENTER, exception.getError());

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                testingCentreOrFacility,
                memberStateOfTest
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testInvalidMemberStateOfTest() {
        TestCertificateDataDto testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                testingCentreOrFacility,
                null
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_MEMBER_STATE_OF_TEST, exception.getError());

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                testingCentreOrFacility,
                memberStateOfTest
        );
        assertDoesNotThrow(testee::validate);
    }
}
