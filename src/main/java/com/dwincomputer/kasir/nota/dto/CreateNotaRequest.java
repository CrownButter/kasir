package com.dwincomputer.kasir.nota.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateNotaRequest {

    private Long kasirId;
    private String kasirNama;

    private String customerNama;
    private String customerAlamat;
    private String customerTelp;

    private String tipe;
    private String status;

    private BigDecimal dp;

    private List<CreateNotaItem> items;

    @Data
    public static class CreateNotaItem {
        private Long itemId;
        private String namaBarang;
        private String catatan;
        private BigDecimal hargaSatuan;
        private Integer jumlah;
    }
}
