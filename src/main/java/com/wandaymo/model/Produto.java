package com.wandaymo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Produto {
    private int codigo;
    private String descricao;
    private double preco;
    private boolean ativo;

    public Produto(int codigo, String descricao, double preco, boolean ativo) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.preco = preco;
        this.ativo = ativo;
    }

    public Produto() {

    }

    @Override
    public String toString() {
        return descricao;
    }
}
