package com.wandaymo.controller;

import com.wandaymo.dao.ClienteDAO;
import com.wandaymo.dao.ProdutoDAO;
import com.wandaymo.dao.VendaDAO;
import com.wandaymo.model.Cliente;
import com.wandaymo.model.ItemVenda;
import com.wandaymo.model.Produto;
import com.wandaymo.model.Venda;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VendaControllerIntegrationTest {

    private VendaController vendaController;
    private ClienteController clienteController;
    private ProdutoController produtoController;
    private ClienteDAO clienteDAO;
    private ProdutoDAO produtoDAO;
    private VendaDAO vendaDAO;

    private Cliente cliente;
    private Produto produto;

    @BeforeEach
    void setUp() throws SQLException {
        clienteDAO = new ClienteDAO();
        produtoDAO = new ProdutoDAO();
        vendaDAO = new VendaDAO();

        vendaController = new VendaController(vendaDAO, clienteDAO, produtoDAO);

        clienteController = new ClienteController();

        produtoController = new ProdutoController();

        cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setLimiteCompra(1000.0);
        cliente.setDiaFechamentoFatura(15);
        clienteDAO.inserir(cliente);

        produto = new Produto();
        produto.setDescricao("Produto Teste");
        produto.setPreco(50.0);
        produtoDAO.inserir(produto);
    }

    @AfterEach
    void tearDown() throws SQLException {
        List<Venda> vendas = vendaController.listarVendasPorCliente(cliente.getCodigo());
        for (Venda v : vendas) {
            vendaDAO.excluir(v.getId());
        }

        produtoDAO.excluir(produto.getCodigo());
        clienteDAO.excluir(cliente.getCodigo());
    }

    @Test
    void deveRealizarVendaComSucesso() {
        List<Cliente> clientes = clienteController.buscarCliente(cliente.getNome());
        cliente.setCodigo(clientes.get(0).getCodigo());
        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setData(LocalDate.now());

        List<Produto> produtos = produtoController.buscarProduto(produto.getDescricao());
        produto.setCodigo(produtos.get(0).getCodigo());
        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(2);
        venda.setItens(List.of(item));

        boolean sucesso = vendaController.realizarVenda(venda);

        assertTrue(sucesso);

        List<Venda> vendas = vendaController.listarVendasPorCliente(cliente.getCodigo());
        assertFalse(vendas.isEmpty());
        assertEquals(1, vendas.size());
        assertEquals(cliente.getCodigo(), vendas.get(0).getCliente().getCodigo());
    }

    @Test
    void deveValidarLimiteCreditoCorretamente() {
        double valorVenda = 200.0;
        boolean valido = vendaController.validarLimiteCredito(cliente, valorVenda);
        assertTrue(valido);
    }

    @Test
    void deveListarProdutos() {
        List<Produto> produtos = vendaController.listarProdutos();
        assertTrue(produtos.stream().anyMatch(p -> p.getDescricao().equals(produto.getDescricao())));
    }

    @Test
    void deveListarClientes() {
        List<Cliente> clientes = vendaController.listarClientes();
        assertTrue(clientes.stream().anyMatch(c -> c.getNome().equals(cliente.getNome())));
    }
}
