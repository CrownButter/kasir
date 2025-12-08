package com.dwincomputer.kasir.toko.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "toko_config")
@Data
public class TokoEntity {
    @Id
    private Long id; // Kita kunci ID-nya selalu 1

    private String namaToko;
    private String alamatToko;
    private String noTelp;

    @Lob // Agar bisa simpan string base64 logo yang sangat panjang
    @Column(columnDefinition = "LONGTEXT")
    private String logoBase64;
}