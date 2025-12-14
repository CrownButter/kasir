package com.dwincomputer.kasir.nota.service;

import com.dwincomputer.kasir.nota.entity.NotaEntity;
import com.dwincomputer.kasir.nota.snapshot.NotaSnapshotEntity;
import com.dwincomputer.kasir.toko.entity.TokoBankEntity;
import com.dwincomputer.kasir.toko.entity.TokoEntity;
import com.dwincomputer.kasir.toko.repository.TokoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final TokoRepository tokoRepo;

    // Font Standar
    private final PDType1Font FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final PDType1Font FONT_PLAIN = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDType1Font FONT_OBLIQUE = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

    public byte[] generateNotaPdf(NotaEntity nota) throws IOException {
        TokoEntity toko = tokoRepo.findAll().stream().findFirst().orElse(new TokoEntity());

        try (PDDocument document = new PDDocument()) {
            // Ukuran Kertas 9.5 x 11 Inch (Continuous Form)
            PDRectangle pageSize = new PDRectangle(9.5f * 72, 11f * 72);
            PDPage page = new PDPage(pageSize);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                // [FIX] Naikkan Margin Atas (Start Y lebih tinggi agar muat 1 halaman)
                float y = page.getMediaBox().getHeight() - 30;
                float margin = 40; // Margin kiri/kanan sedikit diperkecil
                float width = page.getMediaBox().getWidth() - 2 * margin;

                // === 1. LOGO TOKO ===
                // === 1. LOGO TOKO ===
                float logoBottomY = y;
                float headerLeftX = margin;
                float headerRightX = page.getMediaBox().getWidth() - margin;

                if (toko.getLogoBase64() != null && !toko.getLogoBase64().isEmpty()) {
                    try {
                        String base64Image = toko.getLogoBase64();
                        if (base64Image.contains(",")) {
                            base64Image = base64Image.split(",")[1];
                        }

                        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                        PDImageXObject image = PDImageXObject.createFromByteArray(document, imageBytes, "logo");

                        // Ukuran logo aman
                        float maxLogoHeight = 40;
                        float scale = maxLogoHeight / image.getHeight();
                        float logoWidth = image.getWidth() * scale;
                        float logoHeight = image.getHeight() * scale;

                        // Posisi logo (kiri atas)
                        float logoX = margin;
                        float logoY = y - logoHeight;

                        content.drawImage(image, logoX, logoY, logoWidth, logoHeight);

                        // Simpan posisi BAWAH logo yang AKURAT
                        logoBottomY = logoY + 20;
                        headerLeftX = logoX + logoWidth + 10;   // <<< FIX


                    } catch (Exception e) {
                        System.err.println("Gagal load logo: " + e.getMessage());
                    }
                }

                // === 2. HEADER TEXT (CENTERED) ===
                float textY = y;


                String namaToko = toko.getNamaToko() != null ? toko.getNamaToko() : "";
                if (!namaToko.isEmpty()) {
                    textY = drawCenterText(content, namaToko, textY, page, 16, FONT_BOLD);
                    textY -= 15; // Jarak lebih rapat
                }

                String alamatToko = toko.getAlamatToko() != null ? toko.getAlamatToko() : "";
                if (!alamatToko.isEmpty()) {
                    if (alamatToko != null && !alamatToko.isEmpty()) {
                        textY = drawWrappedText(
                                content,
                                alamatToko,
                                headerLeftX,              // mulai setelah logo
                                headerRightX - headerLeftX, // lebar aman
                                textY,
                                10,
                                FONT_PLAIN,
                                12                         // line height
                        );
                        textY -= 4;
                    }
                }

                if (toko.getNoTelp() != null && !toko.getNoTelp().isEmpty()) {
                    textY = drawCenterText(content, "Telp: " + toko.getNoTelp(), textY, page, 10, FONT_PLAIN);
                }

                // Ambil posisi terbawah antara Logo dan Teks
                y = Math.min(logoBottomY, textY);
                y -= 10; // Jarak sebelum garis (DIPERKETAT)

                // Garis Pemisah Header
                content.setStrokingColor(Color.BLACK);
                content.moveTo(margin, y);
                content.lineTo(margin + width, y);
                content.stroke();
                y -= 20;

                // === 3. INFO NOTA & CUSTOMER ===
                float col2X = margin + 350;

                // Baris 1
                drawText(content, "No. Nota", margin, y, 10, FONT_BOLD);
                drawText(content, ": " + nota.getKodeNota(), margin + 60, y, 10, FONT_PLAIN);
                drawText(content, "Kepada Yth.", col2X, y, 10, FONT_BOLD);
                y -= 12; // Spasi baris diperketat

                // Baris 2
                drawText(content, "Tanggal", margin, y, 10, FONT_BOLD);
                drawText(content, ": " + nota.getTanggal().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), margin + 60, y, 10, FONT_PLAIN);
                drawText(content, nota.getCustomerNama(), col2X, y, 10, FONT_PLAIN);
                y -= 12;

                // Baris 3
                drawText(content, "Kasir", margin, y, 10, FONT_BOLD);
                drawText(content, ": " + nota.getKasirNama(), margin + 60, y, 10, FONT_PLAIN);
                if (nota.getCustomerAlamat() != null) {
                    drawText(content, nota.getCustomerAlamat(), col2X, y, 10, FONT_PLAIN);
                }

                y -= 20; // Jarak ke Tabel

                // === 4. TABEL ITEM ===
                float[] colWidths = {30, 260, 60, 100, 120};
                String[] headers = {"No", "Nama Barang", "Qty", "Harga", "Subtotal"};

                // Header Background
                content.setNonStrokingColor(new Color(230, 230, 230));
                content.addRect(margin, y - 12, width, 16); // Tinggi header diperkecil sedikit
                content.fill();
                content.setNonStrokingColor(Color.BLACK);

                // Header Text
                float currentX = margin;
                for (int i = 0; i < headers.length; i++) {
                    if (i >= 3) {
                        drawRightText(content, headers[i], currentX + colWidths[i] - 5, y - 10, 10, FONT_BOLD);
                    } else {
                        drawText(content, headers[i], currentX + 5, y - 10, 10, FONT_BOLD);
                    }
                    currentX += colWidths[i];
                }
                y -= 16; // Turun setelah header

                // Isi Tabel
                int no = 1;
                NumberFormat rp = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

                for (NotaSnapshotEntity item : nota.getSnapshots()) {
                    currentX = margin;

                    // Cek jika halaman hampir habis (misal sisa < 100 point), buat halaman baru (opsional)
                    // Tapi untuk continuous form, biasanya kita biarkan flow.

                    drawText(content, String.valueOf(no++), currentX + 5, y - 10, 10, FONT_PLAIN);
                    currentX += colWidths[0];

                    String namaBarang = item.getNamaBarang();
                    if (namaBarang.length() > 50) namaBarang = namaBarang.substring(0, 50) + "...";
                    drawText(content, namaBarang, currentX + 5, y - 10, 10, FONT_PLAIN);
                    currentX += colWidths[1];

                    drawText(content, String.valueOf(item.getJumlah()), currentX + 5, y - 10, 10, FONT_PLAIN);
                    currentX += colWidths[2];

                    String hargaStr = rp.format(item.getHargaSatuan()).replace("Rp", "");
                    drawRightText(content, hargaStr, currentX + colWidths[3] - 5, y - 10, 10, FONT_PLAIN);
                    currentX += colWidths[3];

                    String totalStr = rp.format(item.getTotalHarga()).replace("Rp", "");
                    drawRightText(content, totalStr, currentX + colWidths[4] - 5, y - 10, 10, FONT_PLAIN);

                    y -= 14; // Tinggi baris item diperketat (sebelumnya 15)

                    content.setStrokingColor(new Color(220, 220, 220));
                    content.moveTo(margin, y);
                    content.lineTo(margin + width, y);
                    content.stroke();
                    content.setStrokingColor(Color.BLACK);

                    y -= 4; // Spasi antar item diperketat
                }
                y -= 10;

                // === 5. FOOTER ===
                float footerStartY = y;
                float startTotalX = margin + width - 180;

                // --- Total Section (Kanan) ---
                drawText(content, "Total :", startTotalX, y, 12, FONT_BOLD);
                drawRightText(content, rp.format(nota.getTotal()), margin + width, y, 12, FONT_BOLD);
                y -= 15; // Jarak diperketat (sebelumnya 20)

                if (nota.getDp() != null && nota.getDp().compareTo(BigDecimal.ZERO) > 0) {
                    drawText(content, "DP/Bayar :", startTotalX, y, 10, FONT_PLAIN);
                    drawRightText(content, rp.format(nota.getDp()), margin + width, y, 10, FONT_PLAIN);
                    y -= 12;

                    drawText(content, "Sisa/Kembali :", startTotalX, y, 10, FONT_PLAIN);
                    drawRightText(content, rp.format(nota.getSisa()), margin + width, y, 10, FONT_PLAIN);
                    y -= 15;
                }
                float rightColumnLastY = y;

                // --- Bank Section (Kiri) ---
                y = footerStartY;
                List<TokoBankEntity> banks = toko.getDaftarRekening();
                if (banks != null && !banks.isEmpty()) {
                    drawText(content, "Transfer Pembayaran:", margin, y, 9, FONT_BOLD);
                    y -= 10;
                    for (TokoBankEntity bank : banks) {
                        String bankInfo = bank.getNamaBank() + " - " + bank.getNoRekening();
                        drawText(content, bankInfo, margin, y, 9, FONT_PLAIN);
                        y -= 10;
                        drawText(content, "a.n " + bank.getAtasNama(), margin, y, 9, FONT_OBLIQUE);
                        y -= 12; // Spasi antar bank diperketat
                    }
                }

                // Reset Y ke posisi paling bawah (antara Kiri atau Kanan)
                y = Math.min(y, rightColumnLastY);
                y -= 15; // Jarak sebelum disclaimer diperketat

                // --- Disclaimer (Tengah) ---
                // [FIX] Mengurangi spacing agar tidak berantakan/masuk ke TTD
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
                    y -= 9; // Jarak antar baris disclaimer diperketat (sebelumnya 10)
                }
                y -= 20; // Jarak ke Tanda Tangan diperketat (sebelumnya 25)

                // === 6. TANDA TANGAN ===
                // Pastikan masih ada sisa kertas
                if (y < 100) {
                    // Jika terlalu mepet bawah, geser sedikit atau biarkan flow
                    // Untuk continuous form 11 inch, y=100 masih aman.
                }

                float ttdLabelY = y;
                drawCenterText(content, "Diterima oleh,", margin + 50, ttdLabelY, 10, FONT_PLAIN);
                drawCenterText(content, "Hormat Kami,", margin + width - 50, ttdLabelY, 10, FONT_PLAIN);

                y -= 40; // Ruang TTD diperkecil sedikit (sebelumnya 60)

                float ttdNameY = y;
                String namaCustomer = nota.getCustomerNama() != null ? nota.getCustomerNama() : "(.......................)";
                drawCenterText(content, "( " + namaCustomer + " )", margin + 50, ttdNameY, 10, FONT_PLAIN);

                String namaKasir = nota.getKasirNama() != null ? nota.getKasirNama() : "(.......................)";
                drawCenterText(content, "( " + namaKasir + " )", margin + width - 50, ttdNameY, 10, FONT_PLAIN);

                y -= 20;
                drawCenterText(content, "Terima kasih atas kunjungan Anda.", y, page, 9, FONT_OBLIQUE);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    // --- HELPER METHODS ---
    private float drawWrappedText(
            PDPageContentStream content,
            String text,
            float startX,
            float maxWidth,
            float startY,
            int fontSize,
            PDType1Font font,
            float lineHeight
    ) throws IOException {

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float y = startY;

        for (String word : words) {
            String testLine = line + word + " ";
            float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;

            if (textWidth > maxWidth) {
                // gambar baris sebelumnya
                drawText(content, line.toString(), startX, y, fontSize, font);
                line = new StringBuilder(word + " ");
                y -= lineHeight;
            } else {
                line.append(word).append(" ");
            }
        }

        // sisa teks
        if (!line.isEmpty()) {
            drawText(content, line.toString(), startX, y, fontSize, font);
            y -= lineHeight;
        }

        return y;
    }
    private void drawText(PDPageContentStream content, String text, float x, float y, int fontSize, PDType1Font font) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private void drawRightText(PDPageContentStream content, String text, float x, float y, int fontSize, PDType1Font font) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        drawText(content, text, x - textWidth, y, fontSize, font);
    }

    private float drawCenterText(PDPageContentStream content, String text, float y, PDPage page, int fontSize, PDType1Font font) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = (page.getMediaBox().getWidth() - textWidth) / 2;
        drawText(content, text, x, y, fontSize, font);
        return y;
    }

    private void drawCenterText(PDPageContentStream content, String text, float centerX, float y, int fontSize, PDType1Font font) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        drawText(content, text, centerX - (textWidth / 2), y, fontSize, font);

    }
    private void drawCenterTextInArea(PDPageContentStream content,
                                      String text,
                                      float leftX,
                                      float rightX,
                                      float y,
                                      int size,
                                      PDType1Font font) throws IOException {

        float textWidth = font.getStringWidth(text) / 1000 * size;
        float centerX = leftX + ((rightX - leftX - textWidth) / 2);
        drawText(content, text, centerX, y, size, font);
    }
}