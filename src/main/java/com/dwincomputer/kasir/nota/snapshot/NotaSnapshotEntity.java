package com.dwincomputer.kasir.nota.snapshot;

import com.dwincomputer.kasir.nota.entity.NotaEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "nota_snapshot")
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class NotaSnapshotEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long itemId;
    private String namaBarang;
    private String catatan;

    private BigDecimal hargaSatuan; // Harga Jual saat itu

    // [BARU] Modal saat transaksi terjadi (dikunci agar laporan akurat)
    private BigDecimal modal;

    private Integer jumlah;
    private BigDecimal totalHarga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_id")
    @JsonBackReference
    private NotaEntity nota;
}