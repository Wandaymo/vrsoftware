package com.wandaymo.model;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Venda {
    private int id;
    private Cliente cliente;
    private LocalDate data;
    private double valorTotal;
    private List<ItemVenda> itens;

    public Venda(Cliente cliente, LocalDate data, double valorTotal) {
        this.cliente = cliente;
        this.data = data;
        this.valorTotal = valorTotal;
    }

    public Venda() {
    }
}