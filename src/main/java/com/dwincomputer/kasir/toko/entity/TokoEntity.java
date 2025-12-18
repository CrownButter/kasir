package com.dwincomputer.kasir.toko.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "toko_config")
@Data
public class TokoEntity {
    @Id
    private Long id;

    private String namaToko;
    private String alamatToko;
    private String noTelp;

    // Ubah dari Base64 ke URL
    private String logoUrl;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "toko_id")
    private List<TokoBankEntity> daftarRekening = new ArrayList<>();
}