package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.exception.CsvException;
import ch.admin.bag.covidcertificate.api.request.CertificateType;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.response.CsvResponseDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvServiceTest {
    @InjectMocks
    private CsvService service;

    @Mock
    private CovidCertificateGenerationService covidCertificateGenerationService;
    @Mock
    private ServletJeapAuthorization jeapAuthorization;
    @Mock
    private KpiDataService kpiLogService;
    @Mock
    private ValueSetsService valueSetsService;

    private final JFixture fixture = new JFixture();
    private final File validRecoveryFile;
    private final File validTestFile;
    private final File validVaccinationFile;
    private final File emptyCsv;
    private final File invalidCsv;
    private final File invalidMultipleCsv;
    private final File validMultipleCsv;

    public CsvServiceTest() {
        validRecoveryFile = new File("src/test/resources/csv/recovery_csv_valid.csv");
        validTestFile = new File("src/test/resources/csv/test_csv_valid.csv");
        validVaccinationFile = new File("src/test/resources/csv/vaccination_csv_valid.csv");
        emptyCsv = new File("src/test/resources/csv/recovery_csv_empty.csv");
        invalidCsv = new File("src/test/resources/csv/recovery_csv_invalid.csv");
        invalidMultipleCsv = new File("src/test/resources/csv/vaccination_csv_multiple_invalid.csv");
        validMultipleCsv = new File("src/test/resources/csv/vaccination_csv_multiple_valid.csv");
    }

    @BeforeEach
    public void setUp() throws IOException {
        lenient().when(valueSetsService.getCountryCode(anyString(), anyString())).thenReturn(fixture.create(CountryCode.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(RecoveryCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(TestCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(VaccinationCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(fixture.create(JeapAuthenticationToken.class));
    }

    @Test
    public void testInvalidCertificateType() {
        var file = Mockito.mock(MultipartFile.class);
        var exception = assertThrows(CreateCertificateException.class,
                () -> service.handleCsvRequest(file, "blub"));
        assertEquals(INVALID_CERTIFICATE_TYPE, exception.getError());
    }

    @Test
    public void testEmptyCsv() throws Exception {
        var file = Mockito.mock(MultipartFile.class);
        var inputStream = new FileInputStream(emptyCsv);
        when(file.getInputStream()).thenReturn(inputStream);
        var exception = assertThrows(CreateCertificateException.class,
                () -> service.handleCsvRequest(file, CertificateType.recovery.name()));
        assertEquals(INVALID_CSV_SIZE, exception.getError());
    }

    @Test
    public void testInvalidCsv() throws Exception {
        var file = Mockito.mock(MultipartFile.class);
        var inputStream = new FileInputStream(invalidCsv);
        when(file.getInputStream()).thenReturn(inputStream);
        var exception = assertThrows(CsvException.class,
                () -> service.handleCsvRequest(file, CertificateType.recovery.name()));
        assertEquals(INVALID_CREATE_REQUESTS.getErrorCode(), exception.getError().getErrorCode());
    }

    @Test
    public void testMultipleInvalidCsv() throws Exception {
        var file = Mockito.mock(MultipartFile.class);
        var inputStream = new FileInputStream(invalidMultipleCsv);
        when(file.getInputStream()).thenReturn(inputStream);
        var exception = assertThrows(CsvException.class,
                () -> service.handleCsvRequest(file, CertificateType.vaccination.name()));
        assertEquals(INVALID_CREATE_REQUESTS.getErrorCode(), exception.getError().getErrorCode());
    }

    @Nested
    class GenerateRecoveryCertificateCsv {
        @Test
        void successful() throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(validRecoveryFile);
            when(file.getInputStream()).thenReturn(inputStream);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.recovery.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }

    }

    @Nested
    class GenerateTestCertificateCsv {
        @Test
        void successful() throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(validTestFile);
            when(file.getInputStream()).thenReturn(inputStream);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.test.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }

    }

    @Nested
    class GenerateVaccinationCertificateCsv {
        @Test
        void successful() throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(validVaccinationFile);
            when(file.getInputStream()).thenReturn(inputStream);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.vaccination.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }

        @Test
        void successfulMultiple() throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(validMultipleCsv);
            when(file.getInputStream()).thenReturn(inputStream);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.vaccination.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }
    }
}
