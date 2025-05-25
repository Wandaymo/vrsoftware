package com.wandaymo.dao;

import com.wandaymo.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, limite_compra, dia_fechamento) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setDouble(2, cliente.getLimiteCompra());
            stmt.setInt(3, cliente.getDiaFechamentoFatura());
            stmt.executeUpdate();
        }
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, limite_compra = ?, dia_fechamento = ? WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setDouble(2, cliente.getLimiteCompra());
            stmt.setInt(3, cliente.getDiaFechamentoFatura());
            stmt.setInt(4, cliente.getCodigo());
            stmt.executeUpdate();
        }
    }

    public void excluir(int codigo) throws SQLException {
        String sql = "UPDATE cliente SET ativo = false WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
            stmt.executeUpdate();
        }
    }

    public Cliente buscarPorCodigo(int codigo) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE ativo = true AND codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Cliente(
                        rs.getInt("codigo"),
                        rs.getString("nome"),
                        rs.getDouble("limite_compra"),
                        rs.getInt("dia_fechamento"),
                        rs.getBoolean("ativo")
                );
            }
            return null;
        }
    }

    public List<Cliente> buscarPorNome(String nome) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE ativo = true AND nome LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getInt("codigo"),
                        rs.getString("nome"),
                        rs.getDouble("limite_compra"),
                        rs.getInt("dia_fechamento"),
                        rs.getBoolean("ativo")
                ));
            }
        }
        return clientes;
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE ativo = true ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getInt("codigo"),
                        rs.getString("nome"),
                        rs.getDouble("limite_compra"),
                        rs.getInt("dia_fechamento"),
                        rs.getBoolean("ativo")
                ));
            }
        }
        return clientes;
    }

    public boolean codigoExiste(int codigo) throws SQLException {
        String sql = "SELECT 1 FROM cliente WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
}
