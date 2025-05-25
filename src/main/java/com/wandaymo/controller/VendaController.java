package com.wandaymo.controller;

import com.wandaymo.dao.ClienteDAO;
import com.wandaymo.dao.ProdutoDAO;
import com.wandaymo.dao.VendaDAO;
import com.wandaymo.model.Cliente;
import com.wandaymo.model.ItemVenda;
import com.wandaymo.model.Produto;
import com.wandaymo.model.Venda;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VendaController {

    private final VendaDAO vendaDAO;
    private final ClienteDAO clienteDAO;
    private final ProdutoDAO produtoDAO;

    public VendaController() {
        this(new VendaDAO(), new ClienteDAO(), new ProdutoDAO());
    }

    public VendaController(VendaDAO vendaDAO, ClienteDAO clienteDAO, ProdutoDAO produtoDAO) {
        this.vendaDAO = vendaDAO;
        this.clienteDAO = clienteDAO;
        this.produtoDAO = produtoDAO;
    }

    public boolean realizarVenda(Venda venda) {
        try {
            double valorTotal = venda.getItens().stream()
                    .mapToDouble(ItemVenda::getSubtotal)
                    .sum();
            venda.setValorTotal(valorTotal);

            int vendaId = vendaDAO.inserir(venda);

            vendaDAO.inserirItens(vendaId, venda.getItens());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluirVenda(int vendaId) {
        try {
            vendaDAO.excluir(vendaId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Venda> listarVendasPorCliente(int clienteId) {
        try {
            return vendaDAO.listarVendasPorCliente(clienteId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Venda> listarVendasPorProduto(int produtoId) {
        try {
            return vendaDAO.listarVendasPorProduto(produtoId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Venda> filtrarVendas(Integer clienteId, Integer produtoId, LocalDate dataInicio, LocalDate dataFim) {
        try {
            return vendaDAO.filtrarVendas(clienteId, produtoId, dataInicio, dataFim);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Cliente> listarClientes() {
        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Produto> listarProdutos() {
        try {
            return produtoDAO.listarTodos();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean validarLimiteCredito(Cliente cliente, double valorVenda) {
        LocalDate hoje = LocalDate.now();
        int diaFechamento = cliente.getDiaFechamentoFatura();

        LocalDate ultimoFechamento;
        if (hoje.getDayOfMonth() >= diaFechamento) {
            ultimoFechamento = hoje.withDayOfMonth(diaFechamento);
        } else {
            ultimoFechamento = hoje.minusMonths(1).withDayOfMonth(diaFechamento);
        }

        double totalCompras;
        try {
            totalCompras = vendaDAO.getTotalComprasAposData(cliente.getCodigo(), ultimoFechamento);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        double limiteDisponivel = cliente.getLimiteCompra() - totalCompras;

        if (valorVenda > limiteDisponivel) {
            LocalDate proximoFechamento = ultimoFechamento.plusMonths(1);
            String mensagem = String.format(
                    "Limite de crédito excedido! Disponível: R$ %.2f\nPróximo fechamento: %s",
                    limiteDisponivel,
                    proximoFechamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
            JOptionPane.showMessageDialog(null, mensagem, "Limite Excedido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public List<Venda> listarVendasPorPeriodo(LocalDate inicio, LocalDate fim) {
        try {
            return vendaDAO.listarPorPeriodo(inicio, fim);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}