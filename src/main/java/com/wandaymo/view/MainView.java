package com.wandaymo.view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class MainView extends JFrame {

    public MainView() {
        super("Sistema de gerenciamento de vendas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        ClienteView clienteView = new ClienteView();
        ProdutoView produtoView = new ProdutoView();
        VendaView vendaView = new VendaView();
        ConsultaVendaView consultaVendaView = new ConsultaVendaView();

        tabbedPane.addTab("Clientes", clienteView);
        tabbedPane.addTab("Produtos", produtoView);
        tabbedPane.addTab("Vendas", vendaView);
        tabbedPane.addTab("Consulta Vendas", consultaVendaView);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
