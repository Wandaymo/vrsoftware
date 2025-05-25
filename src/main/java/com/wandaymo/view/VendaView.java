package com.wandaymo.view;

import com.toedter.calendar.JDateChooser;
import com.wandaymo.controller.ClienteController;
import com.wandaymo.controller.ProdutoController;
import com.wandaymo.controller.VendaController;
import com.wandaymo.model.Cliente;
import com.wandaymo.model.ItemVenda;
import com.wandaymo.model.Produto;
import com.wandaymo.model.Venda;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VendaView extends JPanel {
    private JComboBox<Cliente> cmbClientes;
    private JComboBox<Produto> cmbProdutos;
    private JSpinner spnQuantidade;
    private JButton btnAdicionar, btnRemover, btnFinalizar, btnLimpar, btnAtualizar;
    private JTable tabelaItens;
    private DefaultTableModel tableModelItens;
    private DefaultTableModel tableModelVendas;
    private JDateChooser dateChooser;

    private final List<ItemVenda> itensVenda;
    private final ClienteController clienteController;
    private final ProdutoController produtoController;
    private final VendaController vendaController;

    public VendaView() {
        clienteController = new ClienteController();
        produtoController = new ProdutoController();
        vendaController = new VendaController();
        itensVenda = new ArrayList<>();

        initComponents();
        carregarClientes();
        carregarProdutos();
        carregarVendas();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbClientes = new JComboBox<>();
        cmbProdutos = new JComboBox<>();
        spnQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        dateChooser = new JDateChooser();
        dateChooser.setDate(new java.util.Date());

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(cmbClientes, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Produto:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(cmbProdutos, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Quantidade:"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        formPanel.add(spnQuantidade, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Data:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(dateChooser, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("Adicionar");
        btnRemover = new JButton("Remover");
        btnFinalizar = new JButton("Finalizar Venda");
        btnLimpar = new JButton("Limpar");
        btnAtualizar = new JButton("Atualizar lista de produtos");

        buttonPanel.add(btnAdicionar);
        buttonPanel.add(btnRemover);
        buttonPanel.add(btnFinalizar);
        buttonPanel.add(btnLimpar);
        buttonPanel.add(btnAtualizar);

        tableModelItens = new DefaultTableModel(
                new Object[]{"Código", "Descrição", "Preço unitário", "Quantidade", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaItens = new JTable(tableModelItens);
        JScrollPane scrollPaneItens = new JScrollPane(tabelaItens);
        scrollPaneItens.setPreferredSize(new Dimension(600, 150));

        tableModelVendas = new DefaultTableModel(
                new Object[]{"ID", "Cliente", "Data", "Valor Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabelaVendas = new JTable(tableModelVendas);
        JScrollPane scrollPaneVendas = new JScrollPane(tabelaVendas);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Nova Venda", scrollPaneItens);
        tabbedPane.addTab("Histórico", scrollPaneVendas);

        add(northPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        configurarListeners();
    }

    private void configurarListeners() {
        btnAdicionar.addActionListener(e -> adicionarItem());
        btnRemover.addActionListener(e -> removerItem());
        btnFinalizar.addActionListener(e -> finalizarVenda());
        btnLimpar.addActionListener(e -> limparVenda());
        btnAtualizar.addActionListener(e -> carregarProdutos());
    }

    private void adicionarItem() {
        Produto produto = (Produto) cmbProdutos.getSelectedItem();
        int quantidade = (int) spnQuantidade.getValue();

        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (ItemVenda item : itensVenda) {
            if (item.getProduto().getCodigo() == produto.getCodigo()) {
                JOptionPane.showMessageDialog(this, "Produto já adicionado!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        itensVenda.add(new ItemVenda(produto, quantidade));
        atualizarTabelaItens();
    }

    private void removerItem() {
        int selectedRow = tabelaItens.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        itensVenda.remove(selectedRow);
        atualizarTabelaItens();
    }

    private void finalizarVenda() {
        Cliente cliente = (Cliente) cmbClientes.getSelectedItem();

        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (itensVenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um item à venda!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double valorTotal = itensVenda.stream()
                .mapToDouble(ItemVenda::getSubtotal)
                .sum();

        if (vendaController.validarLimiteCredito(cliente, valorTotal)) {

            Venda venda = new Venda(
                    cliente,
                    dateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                    valorTotal
            );
            venda.setItens(itensVenda);

            if (vendaController.realizarVenda(venda)) {
                JOptionPane.showMessageDialog(this, "Venda realizada com sucesso!");
                limparVenda();
                carregarVendas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao realizar venda!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparVenda() {
        itensVenda.clear();
        atualizarTabelaItens();
        cmbClientes.setSelectedIndex(-1);
        cmbProdutos.setSelectedIndex(-1);
        spnQuantidade.setValue(1);
        dateChooser.setDate(new java.util.Date());
    }

    private void atualizarTabelaItens() {
        tableModelItens.setRowCount(0);
        for (ItemVenda item : itensVenda) {
            tableModelItens.addRow(new Object[]{
                    item.getProduto().getCodigo(),
                    item.getProduto().getDescricao(),
                    String.format("R$ %.2f", item.getPrecoUnitario()),
                    item.getQuantidade(),
                    String.format("R$ %.2f", item.getSubtotal())
            });
        }
    }

    private void carregarClientes() {
        cmbClientes.removeAllItems();
        List<Cliente> clientes = clienteController.listarClientes();
        if (clientes != null) {
            for (Cliente c : clientes) {
                cmbClientes.addItem(c);
            }
        }
    }

    private void carregarProdutos() {
        cmbProdutos.removeAllItems();
        List<Produto> produtos = produtoController.listarProdutos();
        if (produtos != null) {
            for (Produto p : produtos) {
                cmbProdutos.addItem(p);
            }
        }
    }

    private void carregarVendas() {
        tableModelVendas.setRowCount(0);
        List<Venda> vendas = vendaController.listarVendasPorPeriodo(
                LocalDate.now().minusMonths(1),
                LocalDate.now()
        );

        if (vendas != null) {
            for (Venda v : vendas) {
                tableModelVendas.addRow(new Object[]{
                        v.getId(),
                        v.getCliente().getNome(),
                        v.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        String.format("R$ %.2f", v.getValorTotal())
                });
            }
        }
    }
}
