package com.shopmanagement.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating QR codes for payment.
 * Generates UPI-compatible QR codes that can be scanned by any UPI app
 * (Google Pay, PhonePe, Paytm, etc.).
 */
public class QRCodeGenerator {

    private static final int DEFAULT_WIDTH = 350;
    private static final int DEFAULT_HEIGHT = 350;

    // Customize these for your shop
    private static final String UPI_ID = "shopmanagement@upi";
    private static final String PAYEE_NAME = "Shop Management System";

    /**
     * Generates a UPI payment QR code and writes it to the output stream as a PNG image.
     *
     * @param orderId    The order ID for the transaction reference
     * @param amount     The payment amount
     * @param out        The output stream to write the PNG image to
     */
    public static void generatePaymentQR(int orderId, double amount, OutputStream out)
            throws WriterException, IOException {
        String upiString = buildUPIString(orderId, amount);
        generateQRCode(upiString, DEFAULT_WIDTH, DEFAULT_HEIGHT, out);
    }

    /**
     * Generates a UPI payment QR code and returns it as a byte array (PNG).
     *
     * @param orderId The order ID for the transaction reference
     * @param amount  The payment amount
     * @return byte array of the PNG image
     */
    public static byte[] generatePaymentQRBytes(int orderId, double amount)
            throws WriterException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        generatePaymentQR(orderId, amount, baos);
        return baos.toByteArray();
    }

    /**
     * Builds a UPI deep-link string compatible with all major UPI apps.
     * Format: upi://pay?pa=<UPI_ID>&pn=<NAME>&am=<AMOUNT>&tn=<NOTE>&cu=INR
     */
    private static String buildUPIString(int orderId, double amount) {
        return String.format(
            "upi://pay?pa=%s&pn=%s&am=%.2f&tn=%s&cu=INR",
            UPI_ID,
            PAYEE_NAME.replace(" ", "%20"),
            amount,
            "Payment%20for%20Order%20%23" + orderId
        );
    }

    /**
     * Generates a QR code from arbitrary text content.
     */
    public static void generateQRCode(String content, int width, int height, OutputStream out)
            throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        // Use dark blue on white for a professional look
        MatrixToImageConfig config = new MatrixToImageConfig(
            0xFF1A1A2E,  // Dark navy foreground
            0xFFFFFFFF   // White background
        );

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out, config);
    }
}
