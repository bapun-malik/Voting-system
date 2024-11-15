package com.bapunmalik.voting_system.service;

import com.bapunmalik.voting_system.models.User;
import com.bapunmalik.voting_system.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;

@Service
public class PdfGeneratorService {

    @Autowired
    private UserRepository userRepository;

    public byte[] generatePdf(String voterId) throws IOException, DocumentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        User user = userRepository.findByVoterId(voterId);  // Retrieve user data from the database
        if (user == null) {
            throw new IllegalArgumentException("User not found with voter ID: " + voterId);
        }

        // Create a new Document
        Document document = new Document(PageSize.A4);

        // Initialize PDF writer
        PdfWriter.getInstance(document, byteArrayOutputStream);

        // Open the document
        document.open();

       // Add Title
        // Add Title with correct font and alignment
            Paragraph title = new Paragraph("ELECTION COMMISSION OF INDIA", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);


        document.add(Chunk.NEWLINE);

        // Create a table for the card details
        PdfPTable cardTable = new PdfPTable(2);
        cardTable.setWidthPercentage(100);
        cardTable.setWidths(new int[]{3, 3}); // Adjust the column widths

        // Fetch image from Cloudinary once
        Image photo = fetchImageFromCloudinary(user.getPhotoFileName());
        photo.setWidthPercentage(30);

        Image signature = fetchImageFromCloudinary(user.getSignatureFileName());
        signature.setWidthPercentage(20);

        // Create a cell for the photo and signature
        PdfPCell imageCell = new PdfPCell();
        imageCell.addElement(createImageTable(photo, signature));

        cardTable.addCell(imageCell);

        // Create a cell for the QR code and other details
        PdfPCell detailsCell = new PdfPCell();
        detailsCell.setBorder(Rectangle.NO_BORDER);

        Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        detailsCell.addElement(new Paragraph("Epic No: " + user.getVoterId(), boldFont));
        detailsCell.addElement(new Paragraph("Name: " + user.getFirstName() + " " + user.getMiddleName() + user.getLastName(), boldFont));
        detailsCell.addElement(new Paragraph("Father's Name: " + user.getFatherName(), boldFont));
        detailsCell.addElement(new Paragraph("Gender: " + user.getSex(), boldFont));
        detailsCell.addElement(new Paragraph("Date of Birth: " + user.getDob(), boldFont));

        // Generate and add QR code image
        detailsCell.addElement(generateQrCodeImage("Epic No: " + user.getVoterId() + "\nAssembly Constituency:" + user.getDistrict(), 100, 100));

        cardTable.addCell(detailsCell);

        document.add(cardTable);

        document.add(Chunk.NEWLINE);

        // Create a table for additional details
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new int[]{3, 2}); // Adjust the column widths

        addDetailRow(detailsTable, "Serial No:", user.getId().toString());
        addDetailRow(detailsTable, "Assembly Constituency Name:", user.getDistrict());
        addDetailRow(detailsTable, "Address:", user.getHouseNumber() + "," + user.getVillageName() + "," + user.getPostOffice() + "," + user.getPoliceStation() + "," + user.getDistrict() + "," + user.getState());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        addDetailRow(detailsTable, "Download Date:", formattedDateTime);

        document.add(detailsTable);

        // Close the document
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private PdfPCell createCell(String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private void addDetailRow(PdfPTable table, String header, String value) {
        table.addCell(createCell(header));
        table.addCell(createCell(value));
    }

    private PdfPTable createImageTable(Image photo, Image signature) {
        PdfPTable imageTable = new PdfPTable(1);
        imageTable.setWidthPercentage(100);
        imageTable.addCell(new PdfPCell(photo, true));
        imageTable.addCell(new PdfPCell(signature, true));
        return imageTable;
    }

    private Image fetchImageFromCloudinary(String url) throws IOException, DocumentException {
        try (InputStream in = new URL(url).openStream()) {
            return Image.getInstance(in.readAllBytes());
        }catch (IOException e) {
            // Handle potential exceptions when fetching or processing the image
            throw new DocumentException("Error fetching image from Cloudinary", e);
        }
    }

    private Image generateQrCodeImage(String content, int width, int height) throws IOException, DocumentException {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFF);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] qrImageData = baos.toByteArray();

            return Image.getInstance(qrImageData);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DocumentException("Error generating QR Code");
        }
    }
}
