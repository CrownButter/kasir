package com.dwincomputer.kasir.item.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nama;

    @Column(unique = true)
    private String kode;

    @PositiveOrZero(message = "Harga jual tidak boleh negatif")
    private BigDecimal harga;

    @PositiveOrZero(message = "Harga beli tidak boleh negatif")
    private BigDecimal hargaBeli;

    @PositiveOrZero(message = "Stok tidak boleh negatif")
    private Integer stok;

    // Field Baru untuk URL Gambar
    private String imageUrl;
}