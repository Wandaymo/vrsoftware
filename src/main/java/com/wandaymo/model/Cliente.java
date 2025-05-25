package com.wandaymo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Cliente {
    private int codigo;
    private String nome;
    private double limiteCompra;
    private int diaFechamentoFatura;
    private boolean ativo;

    public Cliente() {
    }

    @Override
    public String toString() {
        return nome;
    }
}
