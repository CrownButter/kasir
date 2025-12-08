package com.dwincomputer.kasir.nota.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateNotaItem {
    private Long itemId;
    private String namaBarang;
    private String catatan;
    private BigDecimal hargaSatuan;
    private Integer jumlah;
}
