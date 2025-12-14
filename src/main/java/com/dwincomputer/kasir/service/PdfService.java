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
            // 1. Ambil Data (Anti-Lazy Load)
            NotaEntity nota = notaRepo.findById(notaParam.getId())
                    .orElseThrow(() -> new RuntimeException("Nota tidak ditemukan"));
            nota.getSnapshots().size(); // Trigger fetch items

            TokoEntity toko = tokoRepo.findAll().stream().findFirst().orElse(new TokoEntity());
            if (toko.getDaftarRekening() != null) toko.getDaftarRekening().size(); // Trigger fetch bank

            // 2. Setup PDF (Ukuran 9.5 x 11 Inch)
            try (PDDocument document = new PDDocument()) {
                PDRectangle pageSize = new PDRectangle(9.5f * 72, 11f * 72);
                PDPage page = new PDPage(pageSize);
                document.addPage(page);

                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    // Setting Margin
                    float marginTop = 40;
                    float y = page.getMediaBox().getHeight() - marginTop;
                    float margin = 40;
                    float width = page.getMediaBox().getWidth() - 2 * margin;

                    // ==========================================
                    // HEADER SECTION (Logo & Teks Anti-Overlap)
                    // ==========================================
                    float logoBottomY = y;
                    float usedLogoWidth = 0;
                    float logoHeight = 50; // Tinggi Logo Sedikit Diperbesar agar pas

                    // A. Render Logo (Kiri)
                    if (toko.getLogoBase64() != null && !toko.getLogoBase64().isEmpty()) {
                        try {
                            String base64Image = toko.getLogoBase64();
                            if (base64Image.contains(",")) base64Image = base64Image.split(",")[1];

                            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                            PDImageXObject image = PDImageXObject.createFromByteArray(document, imageBytes, "logo");

                            float scale = logoHeight / image.getHeight();
                            float logoWidth = image.getWidth() * scale;

                            // Gambar logo
                            content.drawImage(image, margin, y - logoHeight, logoWidth, logoHeight);

                            logoBottomY = y - logoHeight;
                            usedLogoWidth = logoWidth + 20; // Padding 20px dari logo ke teks
                        } catch (Exception e) {
                            log.error("Gagal render logo", e);
                        }
                    }

                    // B. Render Teks Header (Kanan - Relative Center)
                    float remainingWidth = width - usedLogoWidth;
                    float textCenterX = margin + usedLogoWidth + (remainingWidth / 2);

                    // Hitung posisi Y agar teks vertikal center terhadap logo
                    // Tinggi blok teks estimasi: Nama(16) + Spasi(15) + Alamat(10) + Spasi(12) + Telp(10) ~= 63
                    // Kita mulai sedikit di atas logo agar seimbang
                    float textY = y + 5;

                    String namaToko = toko.getNamaToko() != null ? toko.getNamaToko() : "";
                    if (!namaToko.isEmpty()) {
                        textY = drawCenterText(content, namaToko, textCenterX, textY, 18, FONT_BOLD); // Font 18
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

                    // Tentukan posisi Y selanjutnya (Pilih yang paling bawah antara Logo atau Teks)
                    y = Math.min(logoBottomY, textY);
                    y -= 15; // Jarak ke garis

                    // Garis Header
                    content.setStrokingColor(Color.BLACK);
                    content.moveTo(margin, y);
                    content.lineTo(margin + width, y);
                    content.stroke();
                    y -= 25;

                    // ==========================================
                    // INFO NOTA & CUSTOMER
                    // ==========================================
                    float col2X = margin + 350; // Kolom Kanan

                    // Baris 1
                    drawText(content, "No. Nota", margin, y, 10, FONT_BOLD);
                    drawText(content, ": " + nota.getKodeNota(), margin + 60, y, 10, FONT_PLAIN);
                    drawText(content, "Kepada Yth.", col2X, y, 10, FONT_BOLD);
                    y -= 14;

                    // Baris 2
                    drawText(content, "Tanggal", margin, y, 10, FONT_BOLD);
                    drawText(content, ": " + nota.getTanggal().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), margin + 60, y, 10, FONT_PLAIN);
                    drawText(content, nota.getCustomerNama(), col2X, y, 10, FONT_PLAIN);
                    y -= 14;

                    // Baris 3
                    drawText(content, "Kasir", margin, y, 10, FONT_BOLD);
                    drawText(content, ": " + nota.getKasirNama(), margin + 60, y, 10, FONT_PLAIN);
                    if (nota.getCustomerAlamat() != null) {
                        drawText(content, nota.getCustomerAlamat(), col2X, y, 10, FONT_PLAIN);
                    }
                    y -= 25;

                    // ==========================================
                    // TABEL ITEM
                    // ==========================================
                    float[] colWidths = {30, 260, 60, 100, 120};
                    String[] headers = {"No", "Nama Barang", "Qty", "Harga", "Subtotal"};

                    // Header Background
                    content.setNonStrokingColor(new Color(230, 230, 230));
                    content.addRect(margin, y - 12, width, 18);
                    content.fill();
                    content.setNonStrokingColor(Color.BLACK);

                    // Header Text
                    float currentX = margin;
                    for (int i = 0; i < headers.length; i++) {
                        if (i >= 3) drawRightText(content, headers[i], currentX + colWidths[i] - 5, y - 10, 10, FONT_BOLD);
                        else drawText(content, headers[i], currentX + 5, y - 10, 10, FONT_BOLD);
                        currentX += colWidths[i];
                    }
                    y -= 20;

                    // Isi Item
                    int no = 1;
                    NumberFormat rp = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

                    for (NotaSnapshotEntity item : nota.getSnapshots()) {
                        currentX = margin;

                        drawText(content, String.valueOf(no++), currentX + 5, y - 10, 10, FONT_PLAIN);
                        currentX += colWidths[0];

                        String namaBarang = item.getNamaBarang();
                        if (namaBarang.length() > 45) namaBarang = namaBarang.substring(0, 45) + "...";
                        drawText(content, namaBarang, currentX + 5, y - 10, 10, FONT_PLAIN);
                        currentX += colWidths[1];

                        drawText(content, String.valueOf(item.getJumlah()), currentX + 5, y - 10, 10, FONT_PLAIN);
                        currentX += colWidths[2];

                        drawRightText(content, rp.format(item.getHargaSatuan()).replace("Rp", ""), currentX + colWidths[3] - 5, y - 10, 10, FONT_PLAIN);
                        currentX += colWidths[3];

                        drawRightText(content, rp.format(item.getTotalHarga()).replace("Rp", ""), currentX + colWidths[4] - 5, y - 10, 10, FONT_PLAIN);

                        y -= 15;
                        // Garis tipis per item
                        content.setStrokingColor(new Color(220, 220, 220));
                        content.moveTo(margin, y);
                        content.lineTo(margin + width, y);
                        content.stroke();
                        content.setStrokingColor(Color.BLACK);
                        y -= 5;
                    }
                    y -= 15;

                    // ==========================================
                    // FOOTER (Total, Bank, Disclaimer, TTD)
                    // ==========================================
                    float footerStartY = y;
                    float startTotalX = margin + width - 180;

                    // 1. TOTAL (Kanan)
                    drawText(content, "Total :", startTotalX, y, 12, FONT_BOLD);
                    drawRightText(content, rp.format(nota.getTotal()), margin + width, y, 12, FONT_BOLD);
                    y -= 18;

                    if (nota.getDp() != null && nota.getDp().compareTo(BigDecimal.ZERO) > 0) {
                        drawText(content, "DP/Bayar :", startTotalX, y, 10, FONT_PLAIN);
                        drawRightText(content, rp.format(nota.getDp()), margin + width, y, 10, FONT_PLAIN);
                        y -= 14;
                        drawText(content, "Sisa/Kembali :", startTotalX, y, 10, FONT_PLAIN);
                        drawRightText(content, rp.format(nota.getSisa()), margin + width, y, 10, FONT_PLAIN);
                        y -= 18;
                    }
                    float rightColumnLastY = y;

                    // 2. BANK (Kiri)
                    y = footerStartY;
                    if (toko.getDaftarRekening() != null && !toko.getDaftarRekening().isEmpty()) {
                        drawText(content, "Transfer Pembayaran:", margin, y, 9, FONT_BOLD);
                        y -= 12;
                        for (TokoBankEntity bank : toko.getDaftarRekening()) {
                            drawText(content, bank.getNamaBank() + " - " + bank.getNoRekening(), margin, y, 9, FONT_PLAIN);
                            y -= 12;
                            drawText(content, "a.n " + bank.getAtasNama(), margin, y, 9, FONT_OBLIQUE);
                            y -= 14;
                        }
                    }

                    // Ambil posisi terbawah
                    y = Math.min(y, rightColumnLastY);
                    y -= 20;

                    // 3. DISCLAIMER (Tengah)
                    drawCenterText(content, "PERHATIAN", y, page, 9, FONT_BOLD);
                    y -= 12;
                    String[] warnings = {
                            "Harap tunjukkan Nota ini saat pengambilan.",
                            "Garansi sesuai kerusakan yang sama (Non-Sparepart).",
                            "Kami tidak bertanggung jawab atas kehilangan data.",
                            "Barang tidak diambil > 1 bulan diluar tanggung jawab kami."
                    };
                    for (String warn : warnings) {
                        drawCenterText(content, warn, y, page, 8, FONT_PLAIN);
                        y -= 10;
                    }
                    y -= 25;

                    // 4. TANDA TANGAN
                    float ttdLabelY = y;
                    drawCenterText(content, "Diterima oleh,", margin + 50, ttdLabelY, 10, FONT_PLAIN);
                    drawCenterText(content, "Hormat Kami,", margin + width - 50, ttdLabelY, 10, FONT_PLAIN);

                    y -= 50; // Space TTD
                    String namaCust = nota.getCustomerNama() != null ? nota.getCustomerNama() : "(.......................)";
                    String namaKasir = nota.getKasirNama() != null ? nota.getKasirNama() : "(.......................)";
                    drawCenterText(content, "( " + namaCust + " )", margin + 50, y, 10, FONT_PLAIN);
                    drawCenterText(content, "( " + namaKasir + " )", margin + width - 50, y, 10, FONT_PLAIN);

                    y -= 25;
                    drawCenterText(content, "Terima kasih atas kunjungan Anda.", y, page, 9, FONT_OBLIQUE);
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                document.save(out);
                return out.toByteArray();
            }
        } catch (Throwable t) {
            log.error("CRITICAL ERROR PDF: ", t);
            // Kembalikan PDF kosong berisi error (agar user tau di frontend)
            // Atau throw exception
            throw new RuntimeException("Gagal render PDF: " + t.getMessage(), t);
        }
    }

    // --- HELPER METHODS (DENGAN SANITASI) ---
    private String sanitize(String text) {
        if (text == null) return "";
        // Hapus newline, tab, dan karakter kontrol lain
        return text.replaceAll("[\\n\\r\\t]", " ").trim();
    }

    private void drawText(PDPageContentStream content, String text, float x, float y, int fontSize, PDType1Font font) throws IOException {
        content.beginText(); content.setFont(font, fontSize); content.newLineAtOffset(x, y); content.showText(sanitize(text)); content.endText();
    }
    private void drawRightText(PDPageContentStream content, String text, float x, float y, int fontSize, PDType1Font font) throws IOException {
        String safeText = sanitize(text);
        float textWidth = font.getStringWidth(safeText) / 1000 * fontSize;
        drawText(content, safeText, x - textWidth, y, fontSize, font);
    }
    private float drawCenterText(PDPageContentStream content, String text, float y, PDPage page, int fontSize, PDType1Font font) throws IOException {
        String safeText = sanitize(text);
        float textWidth = font.getStringWidth(safeText) / 1000 * fontSize;
        float x = (page.getMediaBox().getWidth() - textWidth) / 2;
        drawText(content, safeText, x, y, fontSize, font);
        return y;
    }
    // Helper Center Relative (Untuk Header Toko)
    private float drawCenterText(PDPageContentStream content, String text, float centerX, float y, int fontSize, PDType1Font font) throws IOException {
        String safeText = sanitize(text);
        float textWidth = font.getStringWidth(safeText) / 1000 * fontSize;
        drawText(content, safeText, centerX - (textWidth / 2), y, fontSize, font);
        return y;
    }
}