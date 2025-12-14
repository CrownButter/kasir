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
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfService {

    private final TokoRepository tokoRepo;
    private final NotaRepository notaRepo;

    // Font Standar
    private final PDType1Font FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final PDType1Font FONT_PLAIN = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDType1Font FONT_OBLIQUE = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

    @Transactional(readOnly = true)
    public byte[] generateNotaPdf(NotaEntity notaParam) throws IOException {
        try {
            // 1. Re-fetch Nota & Trigger Snapshots (Solusi Lazy Error)
            NotaEntity nota = notaRepo.findById(notaParam.getId())
                    .orElseThrow(() -> new RuntimeException("Nota tidak ditemukan"));
            nota.getSnapshots().size();

            // 2. Fetch Toko
            TokoEntity toko = tokoRepo.findAll().stream().findFirst().orElse(new TokoEntity());
            if (toko.getDaftarRekening() != null) toko.getDaftarRekening().size();

            // 3. Generate PDF
            try (PDDocument document = new PDDocument()) {
                // Kertas 9.5 x 11 Inch
                PDRectangle pageSize = new PDRectangle(9.5f * 72, 11f * 72);
                PDPage page = new PDPage(pageSize);
                document.addPage(page);

                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    float y = page.getMediaBox().getHeight() - 30; // Start Y
                    float margin = 40;
                    float width = page.getMediaBox().getWidth() - 2 * margin;

                    // === [FIX LAYOUT HEADER ANTI-OVERLAP] ===
                    float logoBottomY = y;
                    float usedLogoWidth = 0; // Lebar yang terpakai oleh logo

                    // 1. GAMBAR LOGO DULU
                    if (toko.getLogoBase64() != null && !toko.getLogoBase64().isEmpty()) {
                        try {
                            String base64Image = toko.getLogoBase64();
                            if (base64Image.contains(",")) base64Image = base64Image.split(",")[1];

                            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                            PDImageXObject image = PDImageXObject.createFromByteArray(document, imageBytes, "logo");

                            float logoHeight = 45; // Tinggi Logo Fixed
                            float scale = logoHeight / image.getHeight();
                            float logoWidth = image.getWidth() * scale;

                            // Gambar Logo di Kiri
                            content.drawImage(image, margin, y - logoHeight + 10, logoWidth, logoHeight);

                            logoBottomY = y - logoHeight;
                            usedLogoWidth = logoWidth + 15; // Tambah padding 15px safe zone
                        } catch (Exception e) {
                            log.error("Gagal render logo", e);
                        }
                    }

                    // 2. TULIS TEXT (Di Samping Logo)
                    // Hitung titik tengah SISA ruangan di sebelah kanan logo
                    float remainingWidth = width - usedLogoWidth;
                    float textCenterX = margin + usedLogoWidth + (remainingWidth / 2);

                    float textY = y;

                    String namaToko = toko.getNamaToko() != null ? toko.getNamaToko() : "";
                    if (!namaToko.isEmpty()) {
                        // Gunakan textCenterX yang baru (Relative Center)
                        textY = drawCenterText(content, namaToko, textCenterX, textY, 16, FONT_BOLD);
                        textY -= 15;
                    }

                    String alamatToko = toko.getAlamatToko() != null ? toko.getAlamatToko() : "";
                    if (!alamatToko.isEmpty()) {
                        textY = drawCenterText(content, alamatToko, textCenterX, textY, 10, FONT_PLAIN);
                        textY -= 12;
                    }

                    if (toko.getNoTelp() != null && !toko.getNoTelp().isEmpty()) {
                        textY = drawCenterText(content, "Telp: " + toko.getNoTelp(), textCenterX, textY, 10, FONT_PLAIN);
                    }

                    // Update Y ke posisi paling bawah antara Logo atau Teks
                    y = Math.min(logoBottomY, textY);
                    y -= 10;

                    // Garis Pemisah
                    content.setStrokingColor(Color.BLACK);
                    content.moveTo(margin, y);
                    content.lineTo(margin + width, y);
                    content.stroke();
                    y -= 20;

                    // === INFO NOTA ===
                    float col2X = margin + 350;
                    drawText(content, "No. Nota", margin, y, 10, FONT_BOLD);
                    drawText(content, ": " + nota.getKodeNota(), margin + 60, y, 10, FONT_PLAIN);
                    drawText(content, "Kepada Yth.", col2X, y, 10, FONT_BOLD);
                    y -= 12;

                    drawText(content, "Tanggal", margin, y, 10, FONT_BOLD);
                    drawText(content, ": " + nota.getTanggal().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), margin + 60, y, 10, FONT_PLAIN);
                    drawText(content, nota.getCustomerNama(), col2X, y, 10, FONT_PLAIN);
                    y -= 12;

                    drawText(content, "Kasir", margin, y, 10, FONT_BOLD);
                    drawText(content, ": " + nota.getKasirNama(), margin + 60, y, 10, FONT_PLAIN);
                    if (nota.getCustomerAlamat() != null) {
                        drawText(content, nota.getCustomerAlamat(), col2X, y, 10, FONT_PLAIN);
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

                        String namaBarang = item.getNamaBarang();
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
                            drawText(content, bank.getNamaBank() + " - " + bank.getNoRekening(), margin, y, 9, FONT_PLAIN);
                            y -= 10;
                            drawText(content, "a.n " + bank.getAtasNama(), margin, y, 9, FONT_OBLIQUE);
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
                    String namaCust = nota.getCustomerNama() != null ? nota.getCustomerNama() : "(.......................)";
                    String namaKasir = nota.getKasirNama() != null ? nota.getKasirNama() : "(.......................)";
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

    // --- HELPER METHODS ---
    private String sanitize(String text) {
        if (text == null) return "";
        return text.replaceAll("[\\n\\r]", " ").replace("\t", " ");
    }

    private void drawText(PDPageContentStream content, String text, float x, float y, int fontSize, PDType1Font font) throws IOException {
        content.beginText(); content.setFont(font, fontSize); content.newLineAtOffset(x, y); content.showText(sanitize(text)); content.endText();
    }
    private void drawRightText(PDPageContentStream content, String text, float x, float y, int fontSize, PDType1Font font) throws IOException {
        String safeText = sanitize(text);
        float textWidth = font.getStringWidth(safeText) / 1000 * fontSize;
        drawText(content, safeText, x - textWidth, y, fontSize, font);
    }

    // Helper Asli (Center seluruh halaman)
    private float drawCenterText(PDPageContentStream content, String text, float y, PDPage page, int fontSize, PDType1Font font) throws IOException {
        String safeText = sanitize(text);
        float textWidth = font.getStringWidth(safeText) / 1000 * fontSize;
        float x = (page.getMediaBox().getWidth() - textWidth) / 2;
        drawText(content, safeText, x, y, fontSize, font);
        return y;
    }

    // Helper Baru (Center berdasarkan titik tertentu) -> Untuk Header Toko
    private float drawCenterText(PDPageContentStream content, String text, float centerX, float y, int fontSize, PDType1Font font) throws IOException {
        String safeText = sanitize(text);
        float textWidth = font.getStringWidth(safeText) / 1000 * fontSize;
        drawText(content, safeText, centerX - (textWidth / 2), y, fontSize, font);
        return y;
    }
}