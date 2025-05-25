package com.wandaymo.dao;

import com.wandaymo.model.Cliente;
import com.wandaymo.model.ItemVenda;
import com.wandaymo.model.Produto;
import com.wandaymo.model.Venda;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    public int inserir(Venda venda) throws SQLException {
        String sql = "INSERT INTO venda (cliente_id, data, valor_total) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, venda.getCliente().getCodigo());
            stmt.setDate(2, Date.valueOf(venda.getData()));
            stmt.setDouble(3, venda.getValorTotal());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Falha ao inserir venda - nenhum ID retornado.");
        }
    }

    public void inserirItens(int vendaId, List<ItemVenda> itens) throws SQLException {
        String sql = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (ItemVenda item : itens) {
                stmt.setInt(1, vendaId);
                stmt.setInt(2, item.getProduto().getCodigo());
                stmt.setInt(3, item.getQuantidade());
                stmt.setDouble(4, item.getPrecoUnitario());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM venda WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Venda> listarPorPeriodo(LocalDate inicio, LocalDate fim) throws SQLException {
        List<Venda> vendas = new ArrayList<>();
        String sql = "SELECT v.*, c.codigo as cliente_codigo, c.nome as cliente_nome, " +
                "c.limite_compra, c.dia_fechamento, c.ativo " +
                "FROM venda v JOIN cliente c ON v.cliente_id = c.codigo " +
                "WHERE v.data BETWEEN ? AND ? " +
                "ORDER BY v.data DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fim));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("cliente_codigo"),
                        rs.getString("cliente_nome"),
                        rs.getDouble("limite_compra"),
                        rs.getInt("dia_fechamento"),
                        rs.getBoolean("ativo")
                );

                Venda venda = new Venda(
                        cliente,
                        rs.getDate("data").toLocalDate(),
                        rs.getDouble("valor_total")
                );
                venda.setId(rs.getInt("id"));
                vendas.add(venda);
            }
        }
        return vendas;
    }

    public double getTotalComprasAposData(int clienteId, LocalDate data) throws SQLException {
        String sql = "SELECT COALESCE(SUM(valor_total), 0) as total " +
                "FROM venda " +
                "WHERE cliente_id = ? AND data > ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            stmt.setDate(2, Date.valueOf(data));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
            return 0;
        }
    }

    public List<Venda> listarVendasPorCliente(int clienteId) throws SQLException {
        List<Venda> vendas = new ArrayList<>();
        String sql = "SELECT v.*, c.nome as cliente_nome FROM venda v " +
                "JOIN cliente c ON v.cliente_id = c.codigo " +
                "WHERE v.cliente_id = ? ORDER BY v.data DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setCodigo(clienteId);
                cliente.setNome(rs.getString("cliente_nome"));

                Venda venda = new Venda();
                venda.setId(rs.getInt("id"));
                venda.setCliente(cliente);
                venda.setData(rs.getDate("data").toLocalDate());
                venda.setValorTotal(rs.getDouble("valor_total"));
                venda.setItens(listarItensVenda(venda.getId()));

                vendas.add(venda);
            }
        }
        return vendas;
    }

    public List<Venda> listarVendasPorProduto(int produtoId) throws SQLException {
        List<Venda> vendas = new ArrayList<>();
        String sql = "SELECT v.*, c.nome as cliente_nome, p.descricao as produto_descricao " +
                "FROM venda v " +
                "JOIN cliente c ON v.cliente_id = c.codigo " +
                "JOIN item_venda iv ON v.id = iv.venda_id " +
                "JOIN produto p ON iv.produto_id = p.codigo " +
                "WHERE iv.produto_id = ? " +
                "GROUP BY v.id, c.nome, p.descricao " +
                "ORDER BY v.data DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setNome(rs.getString("cliente_nome"));

                Venda venda = new Venda();
                venda.setId(rs.getInt("id"));
                venda.setCliente(cliente);
                venda.setData(rs.getDate("data").toLocalDate());
                venda.setValorTotal(rs.getDouble("valor_total"));
                venda.setItens(listarItensVenda(venda.getId()));

                vendas.add(venda);
            }
        }
        return vendas;
    }

    public List<Venda> filtrarVendas(Integer clienteId, Integer produtoId, LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        List<Venda> vendas = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT v.*, c.nome as cliente_nome FROM venda v " +
                        "JOIN cliente c ON v.cliente_id = c.codigo " +
                        "JOIN item_venda iv ON v.id = iv.venda_id " +
                        "WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (clienteId != null) {
            sql.append(" AND v.cliente_id = ?");
            params.add(clienteId);
        }

        if (produtoId != null) {
            sql.append(" AND iv.produto_id = ?");
            params.add(produtoId);
        }

        if (dataInicio != null) {
            sql.append(" AND v.data >= ?");
            params.add(Date.valueOf(dataInicio));
        }

        if (dataFim != null) {
            sql.append(" AND v.data <= ?");
            params.add(Date.valueOf(dataFim));
        }

        sql.append(" ORDER BY v.data DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setNome(rs.getString("cliente_nome"));

                Venda venda = new Venda();
                venda.setId(rs.getInt("id"));
                venda.setCliente(cliente);
                venda.setData(rs.getDate("data").toLocalDate());
                venda.setValorTotal(rs.getDouble("valor_total"));
                venda.setItens(listarItensVenda(venda.getId()));

                vendas.add(venda);
            }
        }
        return vendas;
    }

    private List<ItemVenda> listarItensVenda(int vendaId) throws SQLException {
        List<ItemVenda> itens = new ArrayList<>();
        String sql = "SELECT iv.*, p.descricao, p.preco FROM item_venda iv " +
                "JOIN produto p ON iv.produto_id = p.codigo " +
                "WHERE iv.venda_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vendaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setCodigo(rs.getInt("produto_id"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getDouble("preco"));

                ItemVenda item = new ItemVenda();
                item.setId(rs.getInt("id"));
                item.setProduto(produto);
                item.setQuantidade(rs.getInt("quantidade"));
                item.setPrecoUnitario(rs.getDouble("preco_unitario"));

                itens.add(item);
            }
        }
        return itens;
    }
}
