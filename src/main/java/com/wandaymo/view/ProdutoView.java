package com.wandaymo.view;

import com.wandaymo.controller.ProdutoController;
import com.wandaymo.model.Produto;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

public class ProdutoView extends JPanel {
    private JTextField txtDescricao, txtPreco;
    private JButton btnSalvar, btnExcluir, btnBuscar;
    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;
    private final ProdutoController controller;
    private int codigo;

    public ProdutoView() {
        controller = new ProdutoController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtDescricao = new JTextField();
        txtPreco = new JTextField();

        formPanel.add(new JLabel("Descrição:"));
        formPanel.add(txtDescricao);
        formPanel.add(new JLabel("Preço (R$):"));
        formPanel.add(txtPreco);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");
        btnBuscar = new JButton("Buscar");

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnBuscar);
        buttonPanel.add(btnExcluir);

        tableModel = new DefaultTableModel(
                new Object[]{"Código", "Descrição", "Preço"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        configurarListeners();
    }

    private void configurarListeners() {
        btnSalvar.addActionListener(e -> salvarProduto());
        btnExcluir.addActionListener(e -> excluirProduto());
        btnBuscar.addActionListener(e -> buscarProduto());

        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarProdutoNaTabela();
            }
        });
    }

    private void salvarProduto() {
        if (txtDescricao.getText().isEmpty() || txtPreco.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            String descricao = txtDescricao.getText();
            double preco = Double.parseDouble(txtPreco.getText());
            Produto produto = new Produto(0, descricao, preco, true);
            if (codigo == 0) {
                controller.inserirProduto(produto);
                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
                limparFormulario();
            } else {
                try {
                    produto.setCodigo(codigo);
                    if (controller.buscarProduto(codigo) != null) {
                        if (controller.atualizarProduto(produto)) {
                            JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
                            limparFormulario();
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Dados inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void excluirProduto() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir este produto?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.excluirProduto(codigo)) {
                JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir produto!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarProduto() {
        if (txtDescricao.getText() == null || txtDescricao.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Insira uma descricao!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Produto> produtos = controller.buscarProduto(txtDescricao.getText());
        tableModel.setRowCount(0);

        if (produtos != null) {
            for (Produto p : produtos) {
                tableModel.addRow(new Object[]{
                        p.getCodigo(),
                        p.getDescricao(),
                        p.getPreco()
                });
            }
        }
    }

    private void limparFormulario() {
        tableModel.setRowCount(0);
        codigo = 0;
        txtDescricao.setText("");
        txtPreco.setText("");
        tabelaProdutos.clearSelection();
    }

    private void selecionarProdutoNaTabela() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow != -1) {
            codigo = (int) tableModel.getValueAt(selectedRow, 0);
            txtDescricao.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtPreco.setText(tableModel.getValueAt(selectedRow, 2).toString().replace("R$ ", ""));
        }
    }
}
