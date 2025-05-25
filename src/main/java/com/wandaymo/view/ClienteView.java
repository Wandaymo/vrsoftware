package com.wandaymo.view;

import com.wandaymo.controller.ClienteController;
import com.wandaymo.model.Cliente;

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

public class ClienteView extends JPanel {
    private JTextField txtNome, txtLimite, txtDiaFechamento;
    private JButton btnSalvar, btnExcluir, btnBuscar;
    private JTable tabelaClientes;
    private DefaultTableModel tableModel;
    private final ClienteController controller;
    private int codigo;


    public ClienteView() {
        controller = new ClienteController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtNome = new JTextField();
        txtLimite = new JTextField();
        txtDiaFechamento = new JTextField();

        formPanel.add(new JLabel("Nome:"));
        formPanel.add(txtNome);
        formPanel.add(new JLabel("Limite de compra (R$):"));
        formPanel.add(txtLimite);
        formPanel.add(new JLabel("Dia de fechamento da fatura:"));
        formPanel.add(txtDiaFechamento);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");
        btnBuscar = new JButton("Buscar");

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnBuscar);
        buttonPanel.add(btnExcluir);

        tableModel = new DefaultTableModel(
                new Object[]{"Código", "Nome", "Limite de compra", "Dia do fechamento"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaClientes = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelaClientes);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        configurarListeners();
    }

    private void configurarListeners() {
        btnSalvar.addActionListener(e -> salvarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnBuscar.addActionListener(e ->buscarCliente());

        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarClienteNaTabela();
            }
        });
    }

    private void salvarCliente() {
        if (txtNome.getText().isEmpty() || txtLimite.getText().isEmpty() || txtDiaFechamento.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            String nome = txtNome.getText();
            double limite = Double.parseDouble(txtLimite.getText().replace(",", "."));
            int diaFechamento = Integer.parseInt(txtDiaFechamento.getText());
            Cliente cliente = new Cliente(0, nome, limite, diaFechamento, true);
            if (diaFechamento > 31 || diaFechamento < 0) {
                JOptionPane.showMessageDialog(this, "O dia de fechamento deve estar entre 1 e 31!");
            } else if (codigo == 0) {
                controller.inserirCliente(cliente);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
                limparFormulario();
            } else {
                try {
                    cliente.setCodigo(codigo);
                    if (controller.buscarCliente(codigo) != null) {
                        if (controller.atualizarCliente(cliente)) {
                            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
                            limparFormulario();
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Dados inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void excluirCliente() {
        int selectedRow = tabelaClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir este cliente?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION
        );


        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.excluirCliente(codigo)) {
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarCliente() {
        if (txtNome.getText() == null || txtNome.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Insira um nome!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Cliente> clientes = controller.buscarCliente(txtNome.getText());
        tableModel.setRowCount(0);

        if (clientes != null) {
            for (Cliente c : clientes) {
                tableModel.addRow(new Object[]{
                        c.getCodigo(),
                        c.getNome(),
                        String.format("R$ %.2f", c.getLimiteCompra()),
                        c.getDiaFechamentoFatura()
                });
            }
        }
    }

    private void limparFormulario() {
        tableModel.setRowCount(0);
        codigo = 0;
        txtNome.setText("");
        txtLimite.setText("");
        txtDiaFechamento.setText("");
        tabelaClientes.clearSelection();
    }

    private void selecionarClienteNaTabela() {
        int selectedRow = tabelaClientes.getSelectedRow();
        if (selectedRow != -1) {
            codigo = (int) tableModel.getValueAt(selectedRow, 0);
            txtNome.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtLimite.setText(tableModel.getValueAt(selectedRow, 2).toString().replace("R$ ", ""));
            txtDiaFechamento.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
    }
}
