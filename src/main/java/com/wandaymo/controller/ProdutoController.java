package com.wandaymo.controller;

import com.wandaymo.dao.ProdutoDAO;
import com.wandaymo.model.Produto;

import java.sql.SQLException;
import java.util.List;

public class ProdutoController {

    private final ProdutoDAO produtoDAO;

    public ProdutoController() {
        this(new ProdutoDAO());
    }

    public ProdutoController(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public boolean inserirProduto(Produto produto) {
        try {
            if (produtoDAO.codigoExiste(produto.getCodigo())) {
                return false;
            }
            produtoDAO.inserir(produto);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarProduto(Produto produto) {
        try {
            produtoDAO.atualizar(produto);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluirProduto(int codigo) {
        try {
            produtoDAO.excluir(codigo);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Produto buscarProduto(int codigo) {
        try {
            return produtoDAO.buscarPorCodigo(codigo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Produto> buscarProduto(String descricao) {
        try {
            return produtoDAO.buscarPorDescricao(descricao);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Produto> listarProdutos() {
        try {
            return produtoDAO.listarTodos();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
