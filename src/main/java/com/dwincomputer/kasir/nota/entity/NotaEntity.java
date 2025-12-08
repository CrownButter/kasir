package com.dwincomputer.kasir.nota.entity;

import com.dwincomputer.kasir.nota.snapshot.NotaSnapshotEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference; // <--- WAJIB IMPORT
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nota")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String nomorSeri;

    private String kodeNota;
    private LocalDateTime tanggal;

    // Kasir
    private Long kasirId;
    private String kasirNama;

    // Customer
    private String customerNama;
    private String customerAlamat;
    private String customerTelp;

    // === KHUSUS SERVICE ===
    @Column(length = 100)
    private String barangCustomer;

    @Column(columnDefinition = "TEXT")
    private String keluhan;
    // ======================

    // Nota
    private String tipe;   // SERVICE / JUAL
    private String status; // PROSES / PENDING / SELESAI / LUNAS

    private BigDecimal total;
    private BigDecimal dp;
    private BigDecimal sisa;

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<NotaSnapshotEntity> snapshots = new ArrayList<>();
}