package com.wandaymo.view;

import com.toedter.calendar.JDateChooser;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsultaVendaView extends JPanel {
    private DefaultTableModel tableModelTodasAsVendas, tableModelVendasCliente, tableModelVendasProduto;
    private JComboBox<Cliente> cmbClientes;
    private JComboBox<Produto> cmbProdutos;
    private JDateChooser dateInicio, dateFim;
    private final VendaController controller;
    private JTable tabelaVendas;

    public ConsultaVendaView() {
        controller = new VendaController();
        initComponents();
        carregarFiltros();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel filtroPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbClientes = new JComboBox<>();
        cmbClientes.addItem(new Cliente(0, "Todos os Clientes", 0, 1, true));

        cmbProdutos = new JComboBox<>();
        cmbProdutos.addItem(new Produto(0, "Todos os Produtos", 0, true));

        dateInicio = new JDateChooser();
        dateFim = new JDateChooser();

        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnExluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");

        gbc.gridx = 0;
        gbc.gridy = 0;
        filtroPanel.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        filtroPanel.add(cmbClientes, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        filtroPanel.add(new JLabel("Produto:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        filtroPanel.add(cmbProdutos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        filtroPanel.add(new JLabel("Data Início:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        filtroPanel.add(dateInicio, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        filtroPanel.add(new JLabel("Data Fim:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        filtroPanel.add(dateFim, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnFiltrar);
        buttonPanel.add(btnExluir);
        buttonPanel.add(btnLimpar);
        filtroPanel.add(buttonPanel, gbc);

        tableModelTodasAsVendas = new DefaultTableModel(
                new Object[]{"ID", "Cliente", "Data", "Valor total", "Itens"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModelVendasCliente = new DefaultTableModel(
                new Object[]{"Id", "Produto", "Valor total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModelVendasProduto = new DefaultTableModel(
                new Object[]{"Id", "Cliente", "Valor total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaVendas = new JTable(tableModelTodasAsVendas);
        tabelaVendas.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(tabelaVendas);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Todas as Vendas", scrollPane);

        JPanel clientePanel = new JPanel(new BorderLayout());
        JTable tabelaClientes = new JTable(tableModelVendasCliente);
        tabelaClientes.setAutoCreateRowSorter(true);
        clientePanel.add(new JScrollPane(tabelaClientes), BorderLayout.CENTER);
        tabbedPane.addTab("Por Cliente", clientePanel);

        JPanel produtoPanel = new JPanel(new BorderLayout());
        JTable tabelaProdutos = new JTable(tableModelVendasProduto);
        tabelaProdutos.setAutoCreateRowSorter(true);
        produtoPanel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);
        tabbedPane.addTab("Por Produto", produtoPanel);

        add(filtroPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        btnFiltrar.addActionListener(e -> filtrarVendas());
        btnExluir.addActionListener(e -> excluirVenda());
        btnLimpar.addActionListener(e -> limparFiltros());
    }

    private void carregarFiltros() {
        List<Cliente> clientes = controller.listarClientes();
        for (Cliente c : clientes) {
            cmbClientes.addItem(c);
        }

        List<Produto> produtos = controller.listarProdutos();
        for (Produto p : produtos) {
            cmbProdutos.addItem(p);
        }
    }

    private void filtrarVendas() {
        tableModelTodasAsVendas.setRowCount(0);
        tableModelVendasCliente.setRowCount(0);
        tableModelVendasProduto.setRowCount(0);

        Cliente clienteSelecionado = (Cliente) cmbClientes.getSelectedItem();
        Produto produtoSelecionado = (Produto) cmbProdutos.getSelectedItem();

        Integer clienteId = clienteSelecionado.getCodigo() == 0 ? null : clienteSelecionado.getCodigo();
        Integer produtoId = produtoSelecionado.getCodigo() == 0 ? null : produtoSelecionado.getCodigo();

        LocalDate dataInicio = dateInicio.getDate() != null ?
                dateInicio.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate dataFim = dateFim.getDate() != null ?
                dateFim.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        List<Venda> vendas = controller.filtrarVendas(clienteId, produtoId, dataInicio, dataFim);
        preencherTabelaTodasAsVendas(vendas);

        if (clienteId != null) {
            List<Venda> vendasCliente = controller.listarVendasPorCliente(clienteId);
            preencherTabelaVendasCliente(vendasCliente);
        }

        if (produtoId != null) {
            List<Venda> vendasProduto = controller.listarVendasPorProduto(produtoId);
            preencherTabelaVendasProduto(vendasProduto);
        }
    }

    private void excluirVenda() {
        int selectedRow = tabelaVendas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecione uma venda para excluir!", "Aviso", JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir esta venda?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int codigo = (int) tabelaVendas.getValueAt(selectedRow, 0);
            if (controller.excluirVenda(codigo)) {
                JOptionPane.showMessageDialog(this, "Venda excluída com sucesso!");
                filtrarVendas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir venda!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void preencherTabelaTodasAsVendas(List<Venda> vendas) {
        tableModelTodasAsVendas.setRowCount(0);
        for (Venda v : vendas) {
            StringBuilder itens = new StringBuilder();
            for (ItemVenda item : v.getItens()) {
                itens.append(item.getProduto().getDescricao())
                        .append(" (")
                        .append(item.getQuantidade())
                        .append("x R$ ")
                        .append(String.format("%.2f", item.getPrecoUnitario()))
                        .append("), ");
            }

            tableModelTodasAsVendas.addRow(new Object[]{
                    v.getId(),
                    v.getCliente().getNome(),
                    v.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    String.format("R$ %.2f", v.getValorTotal()),
                    !itens.isEmpty() ? itens.substring(0, itens.length() - 2) : ""
            });
        }
    }

    private void preencherTabelaVendasCliente(List<Venda> vendas) {
        tableModelVendasCliente.setRowCount(0);
        for (Venda v : vendas) {
            StringBuilder itens = new StringBuilder();
            for (ItemVenda item : v.getItens()) {
                itens.append(item.getProduto().getDescricao())
                        .append(" (")
                        .append(item.getQuantidade())
                        .append("x R$ ")
                        .append(String.format("%.2f", item.getPrecoUnitario()))
                        .append("), ");
            }

            tableModelVendasCliente.addRow(new Object[]{
                    v.getId(),
                    !itens.isEmpty() ? itens.substring(0, itens.length() - 2) : "",
                    String.format("R$ %.2f", v.getValorTotal())
            });
        }
    }

    private void preencherTabelaVendasProduto(List<Venda> vendas) {
        tableModelVendasProduto.setRowCount(0);
        for (Venda v : vendas) {
            StringBuilder itens = new StringBuilder();
            for (ItemVenda item : v.getItens()) {
                itens.append(item.getProduto().getDescricao())
                        .append(" (")
                        .append(item.getQuantidade())
                        .append("x R$ ")
                        .append(String.format("%.2f", item.getPrecoUnitario()))
                        .append("), ");
            }

            tableModelVendasProduto.addRow(new Object[]{
                    v.getId(),
                    v.getCliente().getNome(),
                    String.format("R$ %.2f", v.getValorTotal())
            });
        }
    }

    private void limparFiltros() {
        cmbClientes.setSelectedIndex(0);
        cmbProdutos.setSelectedIndex(0);
        dateInicio.setDate(null);
        dateFim.setDate(null);
    }
}