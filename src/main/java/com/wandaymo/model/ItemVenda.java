package com.wandaymo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemVenda {
    private int id;
    private Produto produto;
    private int quantidade;
    private double precoUnitario;


    public ItemVenda(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
    }

    public ItemVenda() {
    }

    public double getSubtotal() {
        return quantidade * precoUnitario;
    }
}

