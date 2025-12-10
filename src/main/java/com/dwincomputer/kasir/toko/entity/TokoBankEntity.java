package com.dwincomputer.kasir.toko.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "toko_bank")
@Data
public class TokoBankEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String namaBank;    // Contoh: BCA, MANDIRI, BRI
    private String noRekening;  // Contoh: 1234567890
    private String atasNama;    // Contoh: Dwin Computer
}