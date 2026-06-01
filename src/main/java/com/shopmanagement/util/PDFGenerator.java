package com.shopmanagement.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.shopmanagement.model.Order;
import com.shopmanagement.model.OrderItem;

import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class PDFGenerator {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(44, 62, 80));
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static final Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font TOTAL_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(44, 62, 80));

    public static void generateBill(Order order, OutputStream out) throws Exception {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, out);
        document.open();

        // Title
        Paragraph title = new Paragraph("SHOP MANAGEMENT SYSTEM", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("Tax Invoice / Bill of Sale", BODY_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);

        // Separator
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(new BaseColor(52, 152, 219));
        document.add(new Chunk(separator));
        document.add(Chunk.NEWLINE);

        // Order details
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(15);

        addInfoRow(infoTable, "Invoice No:", "#INV-" + String.format("%05d", order.getId()));
        addInfoRow(infoTable, "Date:", sdf.format(order.getOrderDate()));
        addInfoRow(infoTable, "Customer:", order.getCustomerName());
        if (order.getCustomerPhone() != null && !order.getCustomerPhone().isEmpty()) {
            addInfoRow(infoTable, "Phone:", order.getCustomerPhone());
        }
        addInfoRow(infoTable, "Status:", order.getStatus());

        document.add(infoTable);

        // Items table
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 4, 2, 2, 2});
        table.setSpacingAfter(15);

        BaseColor headerColor = new BaseColor(52, 73, 94);
        addHeaderCell(table, "#", headerColor);
        addHeaderCell(table, "Product", headerColor);
        addHeaderCell(table, "Price", headerColor);
        addHeaderCell(table, "Qty", headerColor);
        addHeaderCell(table, "Total", headerColor);

        int index = 1;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                BaseColor rowColor = (index % 2 == 0) ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
                addBodyCell(table, String.valueOf(index++), rowColor);
                addBodyCell(table, item.getProductName(), rowColor);
                addBodyCell(table, String.format("Rs. %.2f", item.getUnitPrice()), rowColor);
                addBodyCell(table, String.valueOf(item.getQuantity()), rowColor);
                addBodyCell(table, String.format("Rs. %.2f", item.getUnitPrice() * item.getQuantity()), rowColor);
            }
        }

        document.add(table);

        // Total
        Paragraph total = new Paragraph("Grand Total: Rs. " + String.format("%.2f", order.getTotalAmount()), TOTAL_FONT);
        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingBefore(10);
        document.add(total);

        // Payment QR Code
        document.add(Chunk.NEWLINE);
        try {
            byte[] qrBytes = QRCodeGenerator.generatePaymentQRBytes(order.getId(), order.getTotalAmount());
            Image qrImage = Image.getInstance(qrBytes);
            qrImage.scaleAbsolute(150, 150);
            qrImage.setAlignment(Element.ALIGN_CENTER);

            Paragraph qrTitle = new Paragraph("Scan to Pay via UPI", BOLD_FONT);
            qrTitle.setAlignment(Element.ALIGN_CENTER);
            qrTitle.setSpacingBefore(10);
            document.add(qrTitle);

            document.add(qrImage);

            Paragraph qrHint = new Paragraph("Google Pay | PhonePe | Paytm | BHIM", 
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY));
            qrHint.setAlignment(Element.ALIGN_CENTER);
            qrHint.setSpacingAfter(10);
            document.add(qrHint);
        } catch (Exception e) {
            // QR generation failed — continue without it
            e.printStackTrace();
        }

        // Footer
        document.add(Chunk.NEWLINE);
        document.add(new Chunk(separator));
        Paragraph footer = new Paragraph("Thank you for your purchase! Visit again.", BODY_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        document.add(footer);

        document.close();
    }

    private static void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(3);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, BODY_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(3);
        table.addCell(valueCell);
    }

    private static void addHeaderCell(PdfPTable table, String text, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBorderColor(BaseColor.WHITE);
        table.addCell(cell);
    }

    private static void addBodyCell(PdfPTable table, String text, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BODY_FONT));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        cell.setBorderColor(new BaseColor(220, 220, 220));
        table.addCell(cell);
    }
}
