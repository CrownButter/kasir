package com.dwincomputer.kasir.nota.entity;

import com.dwincomputer.kasir.nota.snapshot.NotaSnapshotEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nota")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String nomorSeri;   // contoh: 29112501

    private String kodeNota;    // contoh: NT-29112501
    private LocalDateTime tanggal;

    // Kasir
    private Long kasirId;
    private String kasirNama;

    // Customer
    private String customerNama;
    private String customerAlamat;
    private String customerTelp;

    // Nota
    private String tipe;    // SERVICE / JUAL
    private String status;  // OPEN / DONE

    private BigDecimal total;
    private BigDecimal dp;
    private BigDecimal sisa;

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotaSnapshotEntity> snapshots = new ArrayList<>();
}
