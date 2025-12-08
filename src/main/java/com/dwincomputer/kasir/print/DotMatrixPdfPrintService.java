package com.dwincomputer.kasir.print;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.io.File;

@Service
public class DotMatrixPdfPrintService {

    private static final String PRINTER_NAME = "EPSON LQ"; // ganti sesuai printer Anda

    public void printPdf(String pdfPath) {
        try {
            File file = new File(pdfPath);

            // PDFBOX 3.x CARA BARU LOAD PDF
            try (PDDocument document = Loader.loadPDF(file)) {

                PrinterJob job = PrinterJob.getPrinterJob();

                // CARI PRINTER YANG COCOK
                PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
                PrintService selected = null;

                for (PrintService ps : services) {
                    if (ps.getName().toLowerCase().contains(PRINTER_NAME.toLowerCase())) {
                        selected = ps;
                        break;
                    }
                }

                if (selected == null) {
                    throw new RuntimeException("Printer dot matrix tidak ditemukan: " + PRINTER_NAME);
                }

                job.setPrintService(selected);

                // PRINT PDF DALAM MODE GRAFIS SESUAI FORMAT PDF
                job.setPageable(new PDFPageable(document));

                job.print();
            }

        } catch (Exception e) {
            throw new RuntimeException("Gagal print PDF ke printer dot matrix", e);
        }
    }
}
