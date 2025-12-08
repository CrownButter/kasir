package com.dwincomputer.kasir.print;

import com.dwincomputer.kasir.nota.entity.NotaEntity;
import com.dwincomputer.kasir.nota.snapshot.NotaSnapshotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ThermalPrintService {

    @Value("${app.printer.name}")
    private String printerName;

    public void printNota(NotaEntity nota) {
        try {
            byte[] data = buildEscPos(nota);
            send(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] buildEscPos(NotaEntity nota) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(new byte[]{0x1B, 0x40}); // reset

        out.write(center("DWINKOMPUTER\n").getBytes(StandardCharsets.UTF_8));
        out.write(center("STRUK PEMBAYARAN\n\n").getBytes(StandardCharsets.UTF_8));

        out.write(("Nota : " + nota.getKodeNota() + "\n").getBytes());
        out.write(("Kasir: " + nota.getKasirNama() + "\n").getBytes());
        out.write(("Tanggal: " + nota.getTanggal() + "\n").getBytes());

        out.write("--------------------------------\n".getBytes());

        for (NotaSnapshotEntity s : nota.getSnapshots()) {
            out.write((s.getNamaBarang() + "\n").getBytes());
            out.write((" " + s.getJumlah() + " x " + s.getHargaSatuan() +
                    " = " + s.getTotalHarga() + "\n").getBytes());
        }

        out.write("--------------------------------\n".getBytes());
        out.write(("TOTAL: " + nota.getTotal() + "\n\n").getBytes());

        out.write(center("TERIMA KASIH\n").getBytes());
        out.write(new byte[]{0x1D, 0x56, 0x00}); // cut

        return out.toByteArray();
    }

    private String center(String s) {
        int width = 32;
        int left = (width - s.length()) / 2;
        return " ".repeat(Math.max(0, left)) + s;
    }

    private void send(byte[] data) throws Exception {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        PrintService printer = null;
        for (PrintService ps : services) {
            if (ps.getName().equalsIgnoreCase(printerName)) {
                printer = ps;
                break;
            }
        }
        if (printer == null) printer = PrintServiceLookup.lookupDefaultPrintService();

        DocPrintJob job = printer.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        job.print(new SimpleDoc(data, flavor, null), null);
    }
}
