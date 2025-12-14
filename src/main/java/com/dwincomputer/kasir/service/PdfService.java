package com.dwincomputer.kasir.nota.service;

import com.dwincomputer.kasir.nota.entity.NotaEntity;
import com.dwincomputer.kasir.nota.repository.NotaRepository;
import com.dwincomputer.kasir.nota.snapshot.NotaSnapshotEntity;
import com.dwincomputer.kasir.toko.entity.TokoBankEntity;
import com.dwincomputer.kasir.toko.entity.TokoEntity;
import com.dwincomputer.kasir.toko.repository.TokoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfService {

    private final TokoRepository tokoRepo;
    private final NotaRepository notaRepo;

    private final PDType1Font FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final PDType1Font FONT_PLAIN = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDType1Font FONT_OBLIQUE = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

    @Transactional(readOnly = true)
    public byte[] generateNotaPdf(NotaEntity notaParam) throws IOException {
        try {
            NotaEntity nota = notaRepo.findById(notaParam.getId())
                    .orElseThrow(() -> new RuntimeException("Nota tidak ditemukan"));
            nota.getSnapshots().size();

            TokoEntity toko = tokoRepo.findAll().stream().findFirst().orElse(new TokoEntity());
            if (toko.getDaftarRekening() != null) toko.getDaftarRekening().size();

            try (PDDocument document = new PDDocument()) {
                PDRectangle pageSize = new PDRectangle(9.5f * 72, 11f * 72);
                PDPage page = new PDPage(pageSize);
                document.addPage(page);

                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    float y = page.getMediaBox().getHeight() - 30;
                    float margin = 40;
                    float width = page.getMediaBox().getWidth() - 2 * margin;

                    // === LOGO & HEADER LAYOUT ===
                    float logoBottomY = y;
                    float textStartX = margin;
                    boolean hasLogo = false;

                    if (toko.getLogoBase64() != null && !toko.getLogoBase64().isEmpty()) {
                        try {
                            String base64Image = toko.getLogoBase64();
                            if (base64Image.contains(",")) base64Image = base64Image.split(",")[1];

                            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                            PDImageXObject image = PDImageXObject.createFromByteArray(document, imageBytes, "logo");

                            float logoHeight = 60;
                            float scale = logoHeight / image.getHeight();
                            float logoWidth = image.getWidth() * scale;

                            // Logo di kiri
                            content.drawImage(image, margin, y - logoHeight, logoWidth, logoHeight);
                            logoBottomY = y - logoHeight - 10;
                            textStartX = margin + logoWidth + 20; // Text mulai setelah logo
                            hasLogo = true;
                        } catch (Exception e) {
                            log.error("Gagal render logo", e);
                        }
                    }

                    // === HEADER TEXT ===
                    float textY = y;
                    String namaToko = sanitizeText(toko.getNamaToko());
                    if (!namaToko.isEmpty()) {
                        if (hasLogo) {
                            drawText(content, namaToko, textStartX, textY, 16, FONT_BOLD);
                            textY -= 15;
                        } else {
                            textY = drawCenterText(content, namaToko, textY, page, 16, FONT_BOLD);
                            textY -= 15;
                        }
                    }

                    String alamatToko = sanitizeText(toko.getAlamatToko());
                    if (!alamatToko.isEmpty()) {
                        if (hasLogo) {
                            drawText(content, alamatToko, textStartX, textY, 10, FONT_PLAIN);
                            textY -= 12;
                        } else {
                            textY = drawCenterText(content, alamatToko, textY, page, 10, FONT_PLAIN);
                            textY -= 12;
                        }
                    }

                    if (toko.getNoTelp() != null && !toko.getNoTelp().isEmpty()) {
                        String telp = "Telp: " + sanitizeText(toko.getNoTelp());
                        if (hasLogo) {
                            drawText(content, telp, textStartX, textY, 10, FONT_PLAIN);
                            textY -= 10;
                        } else {
                            textY = drawCenterText(content, telp, textY, page, 10, FONT_PLAIN);
                            textY -= 10;
                        }
                    }

                    y = Math.min(logoBottomY, textY);
                    y -= 10;

                    // Garis
                    content.setStrokingColor(Color.BLACK);
                    content.moveTo(margin, y);
                    content.lineTo(margin + width, y);
                    content.stroke();
                    y -= 20;

                    // === INFO NOTA ===
                    float col2X = margin + 350;
                    drawText(content, "No. Nota", margin, y, 10, FONT_BOLD);
                    drawText(content, ": " + sanitizeText(nota.getKodeNota()), margin + 60, y, 10, FONT_PLAIN);
                    drawText(content, "Kepada Yth.", col2X, y, 10, FONT_BOLD);
                    y -= 12;

                    drawText(content, "Tanggal", margin, y, 10, FONT_BOLD);
                    drawText(content, ": " + nota.getTanggal().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), margin + 60, y, 10, FONT_PLAIN);
                    drawText(content, sanitizeText(nota.getCustomerNama()), col2X, y, 10, FONT_PLAIN);
                    y -= 12;

                    drawText(content, "Kasir", margin, y, 10, FONT_BOLD);
                    drawText(content, ": " + sanitizeText(nota.getKasirNama()), margin + 60, y, 10, FONT_PLAIN);
                    if (nota.getCustomerAlamat() != null) {
                        drawText(content, sanitizeText(nota.getCustomerAlamat()), col2X, y, 10, FONT_PLAIN);
                    }
                    y -= 20;

                    // === TABEL ITEM ===
                    float[] colWidths = {30, 260, 60, 100, 120};
                    String[] headers = {"No", "Nama Barang", "Qty", "Harga", "Subtotal"};

                    content.setNonStrokingColor(new Color(230, 230, 230));
                    content.addRect(margin, y - 12, width, 16);
                    content.fill();
                    content.setNonStrokingColor(Color.BLACK);

                    float currentX = margin;
                    for (int i = 0; i < headers.length; i++) {
                        if (i >= 3) drawRightText(content, headers[i], currentX + colWidths[i] - 5, y - 10, 10, FONT_BOLD);
                        else drawText(content, headers[i], currentX + 5, y - 10, 10, FONT_BOLD);
                        currentX += colWidths[i];
                    }
                    y -= 16;

                    int no = 1;
                    NumberFormat rp = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

                    for (NotaSnapshotEntity item : nota.getSnapshots()) {
                        currentX = margin;

                        drawText(content, String.valueOf(no++), currentX + 5, y - 10, 10, FONT_PLAIN);
                        currentX += colWidths[0];

                        String namaBarang = sanitizeText(item.getNamaBarang());
                        if (namaBarang.length() > 50) namaBarang = namaBarang.substring(0, 50) + "...";
                        drawText(content, namaBarang, currentX + 5, y - 10, 10, FONT_PLAIN);
                        currentX += colWidths[1];

                        drawText(content, String.valueOf(item.getJumlah()), currentX + 5, y - 10, 10, FONT_PLAIN);
                        currentX += colWidths[2];

                        drawRightText(content, rp.format(item.getHargaSatuan()).replace("Rp", ""), currentX + colWidths[3] - 5, y - 10, 10, FONT_PLAIN);
                        currentX += colWidths[3];

                        drawRightText(content, rp.format(item.getTotalHarga()).replace("Rp", ""), currentX + colWidths[4] - 5, y - 10, 10, FONT_PLAIN);

                        y -= 14;
                        content.setStrokingColor(new Color(220, 220, 220));
                        content.moveTo(margin, y);
                        content.lineTo(margin + width, y);
                        content.stroke();
                        content.setStrokingColor(Color.BLACK);
                        y -= 4;
                    }
                    y -= 10;

                    // === FOOTER ===
                    float footerStartY = y;
                    float startTotalX = margin + width - 180;

                    drawText(content, "Total :", startTotalX, y, 12, FONT_BOLD);
                    drawRightText(content, rp.format(nota.getTotal()), margin + width, y, 12, FONT_BOLD);
                    y -= 15;

                    if (nota.getDp() != null && nota.getDp().compareTo(BigDecimal.ZERO) > 0) {
                        drawText(content, "DP/Bayar :", startTotalX, y, 10, FONT_PLAIN);
                        drawRightText(content, rp.format(nota.getDp()), margin + width, y, 10, FONT_PLAIN);
                        y -= 12;
                        drawText(content, "Sisa/Kembali :", startTotalX, y, 10, FONT_PLAIN);
                        drawRightText(content, rp.format(nota.getSisa()), margin + width, y, 10, FONT_PLAIN);
                        y -= 15;
                    }
                    float rightColumnLastY = y;

                    y = footerStartY;
                    if (toko.getDaftarRekening() != null) {
                        drawText(content, "Transfer Pembayaran:", margin, y, 9, FONT_BOLD);
                        y -= 10;
                        for (TokoBankEntity bank : toko.getDaftarRekening()) {
                            drawText(content, sanitizeText(bank.getNamaBank()) + " - " + sanitizeText(bank.getNoRekening()), margin, y, 9, FONT_PLAIN);
                            y -= 10;
                            drawText(content, "a.n " + sanitizeText(bank.getAtasNama()), margin, y, 9, FONT_OBLIQUE);
                            y -= 12;
                        }
                    }

                    y = Math.min(y, rightColumnLastY);
                    y -= 15;

                    drawCenterText(content, "PERHATIAN", y, page, 9, FONT_BOLD);
                    y -= 10;
                    String[] warnings = {
                            "Harap tunjukkan Nota ini saat pengambilan.",
                            "Garansi sesuai kerusakan yang sama (Non-Sparepart).",
                            "Kami tidak bertanggung jawab atas kehilangan data.",
                            "Barang tidak diambil > 1 bulan diluar tanggung jawab kami."
                    };
                    for (String warn : warnings) {
                        drawCenterText(content, warn, y, page, 8, FONT_PLAIN);
                        y -= 9;
                    }
                    y -= 20;

                    // === TANDA TANGAN ===
                    float ttdLabelY = y;
                    drawCenterText(content, "Diterima oleh,", margin + 50, ttdLabelY, 10, FONT_PLAIN);
                    drawCenterText(content, "Hormat Kami,", margin + width - 50, ttdLabelY, 10, FONT_PLAIN);

                    y -= 40;
                    String namaCust = nota.getCustomerNama() != null ? sanitizeText(nota.getCustomerNama()) : "(.......................)";
                    String namaKasir = nota.getKasirNama() != null ? sanitizeText(nota.getKasirNama()) : "(.......................)";
                    drawCenterText(content, "( " + namaCust + " )", margin + 50, y, 10, FONT_PLAIN);
                    drawCenterText(content, "( " + namaKasir + " )", margin + width - 50, y, 10, FONT_PLAIN);

                    y -= 20;
                    drawCenterText(content, "Terima kasih atas kunjungan Anda.", y, page, 9, FONT_OBLIQUE);
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                document.save(out);
                return out.toByteArray();
            }
        } catch (Throwable t) {
            log.error("CRITICAL ERROR PDF: ", t);
            throw new RuntimeException("Gagal membuat PDF: " + t.getMessage(), t);
        }
    }

    /**
     * Sanitize text to remove control characters that PDFBox cannot encode
     */
    private String sanitizeText(String text) {
        if (text == null) return "";
        return text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                .replaceAll("[\r\n\t]+", " ")
                .trim();
    }

    private void drawText(PDPageContentStream content, String text, float x, float y, int fontSize, PDType1Font font) throws IOException {
        text = sanitizeText(text);
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private void drawRightText(PDPageContentStream content, String text, float x, float y, int fontSize, PDType1Font font) throws IOException {
        text = sanitizeText(text);
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        drawText(content, text, x - textWidth, y, fontSize, font);
    }

    private float drawCenterText(PDPageContentStream content, String text, float y, PDPage page, int fontSize, PDType1Font font) throws IOException {
        text = sanitizeText(text);
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = (page.getMediaBox().getWidth() - textWidth) / 2;
        drawText(content, text, x, y, fontSize, font);
        return y;
    }

    private void drawCenterText(PDPageContentStream content, String text, float centerX, float y, int fontSize, PDType1Font font) throws IOException {
        text = sanitizeText(text);
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        drawText(content, text, centerX - (textWidth / 2), y, fontSize, font);
    }
}