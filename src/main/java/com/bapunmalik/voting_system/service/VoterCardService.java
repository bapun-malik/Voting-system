package com.bapunmalik.voting_system.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bapunmalik.voting_system.models.User;
import com.bapunmalik.voting_system.repository.UserRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class VoterCardService {
    
    @Autowired
    private UserRepository userRepository;
    
    public byte[] generateVoterCard(String epicNo) throws IOException, DocumentException {
        User voter = userRepository.findByVoterId(epicNo);
            
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Add header
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        Paragraph title = new Paragraph("ELECTION COMMISSION OF INDIA", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        header.addCell(getCell(title, PdfPCell.ALIGN_CENTER));
        document.add(header);
        
        // Main content table
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(100);
        
        // Left side - Photo and details
        PdfPCell leftCell = new PdfPCell();
        Path filePath = Paths.get("poster/" + voter.getPhotoFileName());
        if (filePath != null) {
            Image photo = Image.getInstance(filePath.toString());
            photo.scaleToFit(100, 120);
            leftCell.addElement(photo);
        }
        
        leftCell.addElement(new Paragraph("EPIC No: " + voter.getVoterId()));
        leftCell.addElement(new Paragraph("Name: " + voter.getFirstName()+" "+voter.getLastName()));
        leftCell.addElement(new Paragraph("Father's Name: " + voter.getFatherName()));
        leftCell.addElement(new Paragraph("Gender: " + voter.getSex()));
        leftCell.addElement(new Paragraph("Date of Birth: " + voter.getDob()));
        
        // Right side - QR code and address
        PdfPCell rightCell = new PdfPCell();
        BarcodeQRCode qrCode = new BarcodeQRCode(voter.getVoterId(), 100, 100, null);
        Image qrCodeImage = qrCode.getImage();
        rightCell.addElement(qrCodeImage);
        rightCell.addElement(new Paragraph("Address: " + voter.getDistrict()+","+voter.getPoliceStation()));
        
        mainTable.addCell(leftCell);
        mainTable.addCell(rightCell);
        document.add(mainTable);
        
        // Polling details table
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        
        detailsTable.addCell(getCell("Assembly Constituency:", PdfPCell.ALIGN_LEFT));
        detailsTable.addCell(getCell(voter.getPostOffice(), PdfPCell.ALIGN_LEFT));
        
        detailsTable.addCell(getCell("Part No.:", PdfPCell.ALIGN_LEFT));
        detailsTable.addCell(getCell(voter.getPoliceStation(), PdfPCell.ALIGN_LEFT));
        
        detailsTable.addCell(getCell("Polling Station:", PdfPCell.ALIGN_LEFT));
        detailsTable.addCell(getCell(voter.getPostOffice(), PdfPCell.ALIGN_LEFT));
        
        document.add(detailsTable);
        
        document.close();
        return baos.toByteArray();
    }
    
    private PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
    
    private PdfPCell getCell(Paragraph paragraph, int alignment) {
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
