package com.wandaymo.controller;

import com.wandaymo.dao.ClienteDAO;
import com.wandaymo.dao.ProdutoDAO;
import com.wandaymo.dao.VendaDAO;
import com.wandaymo.model.Cliente;
import com.wandaymo.model.ItemVenda;
import com.wandaymo.model.Produto;
import com.wandaymo.model.Venda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VendaControllerTest {

    @Mock
    VendaDAO vendaDAO;

    @Mock
    ClienteDAO clienteDAO;

    @Mock
    ProdutoDAO produtoDAO;

    @InjectMocks
    VendaController vendaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vendaController = new VendaController(vendaDAO, clienteDAO, produtoDAO);
    }

    @Test
    void testRealizarVenda_sucesso() throws SQLException {
        Venda venda = new Venda();
        ItemVenda item = new ItemVenda();
        venda.setItens(List.of(item));

        when(vendaDAO.inserir(venda)).thenReturn(1);

        boolean resultado = vendaController.realizarVenda(venda);

        assertTrue(resultado);
        verify(vendaDAO).inserirItens(1, venda.getItens());
    }

    @Test
    void testRealizarVenda_falhaSQLException() throws SQLException {
        Venda venda = new Venda();
        ItemVenda item = new ItemVenda();
        venda.setItens(List.of(item));

        when(vendaDAO.inserir(venda)).thenThrow(new SQLException());

        boolean resultado = vendaController.realizarVenda(venda);

        assertFalse(resultado);
    }

    @Test
    void testExcluirVenda_sucesso() throws SQLException {
        assertTrue(vendaController.excluirVenda(1));
        verify(vendaDAO).excluir(1);
    }

    @Test
    void testExcluirVenda_falhaSQLException() throws SQLException {
        doThrow(SQLException.class).when(vendaDAO).excluir(1);

        assertFalse(vendaController.excluirVenda(1));
    }

    @Test
    void testListarVendasPorCliente_sucesso() throws SQLException {
        List<Venda> vendas = List.of(new Venda());
        when(vendaDAO.listarVendasPorCliente(1)).thenReturn(vendas);

        assertEquals(vendas, vendaController.listarVendasPorCliente(1));
    }

    @Test
    void testListarVendasPorCliente_falhaSQLException() throws SQLException {
        when(vendaDAO.listarVendasPorCliente(1)).thenThrow(new SQLException());

        assertTrue(vendaController.listarVendasPorCliente(1).isEmpty());
    }

    @Test
    void testListarVendasPorProduto_sucesso() throws SQLException {
        List<Venda> vendas = List.of(new Venda());
        when(vendaDAO.listarVendasPorProduto(1)).thenReturn(vendas);

        assertEquals(vendas, vendaController.listarVendasPorProduto(1));
    }

    @Test
    void testListarVendasPorProduto_falhaSQLException() throws SQLException {
        when(vendaDAO.listarVendasPorProduto(1)).thenThrow(new SQLException());

        assertTrue(vendaController.listarVendasPorProduto(1).isEmpty());
    }

    @Test
    void testFiltrarVendas_sucesso() throws SQLException {
        List<Venda> vendas = List.of(new Venda());
        when(vendaDAO.filtrarVendas(1, 1, LocalDate.now(), LocalDate.now())).thenReturn(vendas);

        assertEquals(vendas, vendaController.filtrarVendas(1, 1, LocalDate.now(), LocalDate.now()));
    }

    @Test
    void testFiltrarVendas_falhaSQLException() throws SQLException {
        when(vendaDAO.filtrarVendas(any(), any(), any(), any())).thenThrow(new SQLException());

        assertTrue(vendaController.filtrarVendas(null, null, null, null).isEmpty());
    }

    @Test
    void testListarClientes_sucesso() throws SQLException {
        List<Cliente> clientes = List.of(new Cliente());
        when(clienteDAO.listarTodos()).thenReturn(clientes);

        assertEquals(clientes, vendaController.listarClientes());
    }

    @Test
    void testListarClientes_falhaSQLException() throws SQLException {
        when(clienteDAO.listarTodos()).thenThrow(new SQLException());

        assertTrue(vendaController.listarClientes().isEmpty());
    }

    @Test
    void testListarProdutos_sucesso() throws SQLException {
        List<Produto> produtos = List.of(new Produto());
        when(produtoDAO.listarTodos()).thenReturn(produtos);

        assertEquals(produtos, vendaController.listarProdutos());
    }

    @Test
    void testListarProdutos_falhaSQLException() throws SQLException {
        when(produtoDAO.listarTodos()).thenThrow(new SQLException());

        assertTrue(vendaController.listarProdutos().isEmpty());
    }

    @Test
    void testValidarLimiteCredito_valido() throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setCodigo(1);
        cliente.setDiaFechamentoFatura(LocalDate.now().getDayOfMonth());
        cliente.setLimiteCompra(1000.0);

        when(vendaDAO.getTotalComprasAposData(eq(1), any())).thenReturn(200.0);

        boolean valido = vendaController.validarLimiteCredito(cliente, 500.0);

        assertTrue(valido);
    }

    @Test
    void testValidarLimiteCredito_excedido() throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setCodigo(1);
        cliente.setDiaFechamentoFatura(LocalDate.now().getDayOfMonth());
        cliente.setLimiteCompra(300.0);

        when(vendaDAO.getTotalComprasAposData(eq(1), any())).thenReturn(200.0);

        JOptionPane.setRootFrame(null);

        boolean valido = vendaController.validarLimiteCredito(cliente, 200.1);

        assertFalse(valido);
    }

    @Test
    void testValidarLimiteCredito_lancaRuntimeException() throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setCodigo(1);
        cliente.setDiaFechamentoFatura(LocalDate.now().getDayOfMonth());

        when(vendaDAO.getTotalComprasAposData(anyInt(), any())).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () ->
                vendaController.validarLimiteCredito(cliente, 100.0));
    }

    @Test
    void testListarVendasPorPeriodo_sucesso() throws SQLException {
        List<Venda> vendas = List.of(new Venda());
        when(vendaDAO.listarPorPeriodo(any(), any())).thenReturn(vendas);

        assertEquals(vendas, vendaController.listarVendasPorPeriodo(LocalDate.now(), LocalDate.now()));
    }

    @Test
    void testListarVendasPorPeriodo_falhaSQLException() throws SQLException {
        when(vendaDAO.listarPorPeriodo(any(), any())).thenThrow(new SQLException());

        assertNull(vendaController.listarVendasPorPeriodo(LocalDate.now(), LocalDate.now()));
    }
}
