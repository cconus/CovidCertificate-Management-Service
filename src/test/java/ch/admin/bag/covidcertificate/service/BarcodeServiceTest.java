package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.digg.dgc.encoding.Barcode;
import se.digg.dgc.service.DGCBarcodeEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static ch.admin.bag.covidcertificate.api.Constants.CREATE_BARCODE_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_SIGNATURE_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarcodeServiceTest {
    @Mock
    private DGCBarcodeEncoder dgcBarcodeEncoder;

    @InjectMocks
    private BarcodeService barcodeService;

    @Test
    void whenCreateBarcode_thenOk() throws Exception {
        // given
        Barcode barcode = getBarcode();
        when(dgcBarcodeEncoder.encodeToBarcode(any(byte[].class), any(Instant.class)))
                .thenReturn(barcode);
        // when
        Barcode result = barcodeService.createBarcode("{\"hello\": \"world\"}");
        // then
        assertEquals(barcode, result);
    }

    @Test
    void givenCreateCertificateExceptionIsThrown_whenCreateBarcode_thenThrowsThisException() throws Exception {
        // given
        when(dgcBarcodeEncoder.encodeToBarcode(any(byte[].class), any(Instant.class)))
                .thenThrow(new CreateCertificateException(CREATE_SIGNATURE_FAILED));
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> barcodeService.createBarcode("{\"hello\": \"world\"}"));
        assertEquals(CREATE_SIGNATURE_FAILED, exception.getError());
    }

    @Test
    void givenExceptionIsThrown_whenCreateBarcode_thenThrowsBarcodeError() throws Exception {
        // given
        when(dgcBarcodeEncoder.encodeToBarcode(any(byte[].class), any(Instant.class)))
                .thenThrow(IOException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> barcodeService.createBarcode("{\"hello\": \"world\"}"));
        assertEquals(CREATE_BARCODE_FAILED, exception.getError());
    }

    private Barcode getBarcode() {
        return new Barcode(Barcode.BarcodeType.AZTEC,
                "Hello world.".getBytes(StandardCharsets.UTF_8),
                Barcode.ImageFormat.PNG,
                300,
                300,
                "Hello world.");
    }
}
