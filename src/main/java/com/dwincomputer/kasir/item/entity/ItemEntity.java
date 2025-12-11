package com.dwincomputer.kasir.item.entity;

import jakarta.persistence.*;
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
    private String kode; // Pastikan kode unik

    private BigDecimal harga; // Harga Jual

    // [BARU] Field Modal/HPP
    @Column(precision = 19, scale = 2)
    private BigDecimal hargaBeli;

    private Integer stok;
}