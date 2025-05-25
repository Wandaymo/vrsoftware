package com.wandaymo.dao;

import com.wandaymo.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    public void inserir(Produto produto) throws SQLException {
        String sql = "INSERT INTO produto (descricao, preco) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getDescricao());
            stmt.setDouble(2, produto.getPreco());
            stmt.executeUpdate();
        }
    }

    public void atualizar(Produto produto) throws SQLException {
        String sql = "UPDATE produto SET descricao = ?, preco = ? WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getDescricao());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getCodigo());
            stmt.executeUpdate();
        }
    }

    public void excluir(int codigo) throws SQLException {
        String sql = "UPDATE produto SET ativo = false WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
            stmt.executeUpdate();
        }
    }

    public Produto buscarPorCodigo(int codigo) throws SQLException {
        String sql = "SELECT * FROM produto WHERE ativo = true AND codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Produto(
                        rs.getInt("codigo"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getBoolean("ativo")
                );
            }
            return null;
        }
    }

    public List<Produto> buscarPorDescricao(String descricao) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto WHERE ativo = true AND descricao LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + descricao + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                produtos.add(new Produto(
                        rs.getInt("codigo"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getBoolean("ativo")
                ));
            }
        }
        return produtos;
    }

    public List<Produto> listarTodos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto WHERE ativo = true ORDER BY descricao";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produtos.add(new Produto(
                        rs.getInt("codigo"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getBoolean("ativo")
                ));
            }
        }
        return produtos;
    }

    public boolean codigoExiste(int codigo) throws SQLException {
        String sql = "SELECT 1 FROM produto WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
}
