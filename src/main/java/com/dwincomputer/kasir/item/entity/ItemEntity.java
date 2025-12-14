package com.dwincomputer.kasir.item.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero; // Tambahkan import ini
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

    // Tambahkan validasi agar tidak negatif
    @PositiveOrZero(message = "Harga jual tidak boleh negatif")
    private BigDecimal harga;

    // [BARU] Field Modal/HPP
    // Tambahkan validasi agar tidak negatif
    @PositiveOrZero(message = "Harga beli tidak boleh negatif")
    @Column(precision = 19, scale = 2)
    private BigDecimal hargaBeli;

    // Opsional: Stok juga sebaiknya tidak negatif
    @PositiveOrZero(message = "Stok tidak boleh negatif")
    private Integer stok;
}