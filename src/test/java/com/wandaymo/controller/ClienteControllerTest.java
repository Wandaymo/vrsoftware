package com.wandaymo.controller;

import com.wandaymo.dao.ClienteDAO;
import com.wandaymo.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClienteControllerTest {

    private ClienteDAO clienteDAO;
    private ClienteController controller;

    @BeforeEach
    void setUp() {
        clienteDAO = mock(ClienteDAO.class);
        controller = new ClienteController(clienteDAO);
    }

    @Test
    void testInserirCliente_ComCodigoExistente() throws SQLException {
        Cliente cliente = new Cliente(1, "João", 1000.0, 15, true);
        when(clienteDAO.codigoExiste(cliente.getCodigo())).thenReturn(true);

        boolean resultado = controller.inserirCliente(cliente);

        assertFalse(resultado);
        verify(clienteDAO, never()).inserir(any());
    }

    @Test
    void testInserirCliente_ComSucesso() throws SQLException {
        Cliente cliente = new Cliente(1, "Maria", 2000.0, 10, true);
        when(clienteDAO.codigoExiste(cliente.getCodigo())).thenReturn(false);

        boolean resultado = controller.inserirCliente(cliente);

        assertTrue(resultado);
        verify(clienteDAO).inserir(cliente);
    }

    @Test
    void testAtualizarCliente() throws SQLException {
        Cliente cliente = new Cliente(2, "Carlos", 1500.0, 5, true);

        boolean resultado = controller.atualizarCliente(cliente);

        assertTrue(resultado);
        verify(clienteDAO).atualizar(cliente);
    }

    @Test
    void testExcluirCliente() throws SQLException {
        boolean resultado = controller.excluirCliente(1);

        assertTrue(resultado);
        verify(clienteDAO).excluir(1);
    }

    @Test
    void testBuscarClientePorCodigo() throws SQLException {
        Cliente cliente = new Cliente(3, "Fernanda", 3000.0, 20, true);
        when(clienteDAO.buscarPorCodigo(3)).thenReturn(cliente);

        Cliente resultado = controller.buscarCliente(3);

        assertNotNull(resultado);
        assertEquals("Fernanda", resultado.getNome());
    }

    @Test
    void testBuscarClientePorNome() throws SQLException {
        Cliente c1 = new Cliente(1, "Ana", 1000.0, 10, true);
        Cliente c2 = new Cliente(2, "Ana Paula", 1200.0, 10, false);
        List<Cliente> clientes = Arrays.asList(c1, c2);

        when(clienteDAO.buscarPorNome("Ana")).thenReturn(clientes);

        List<Cliente> resultado = controller.buscarCliente("Ana");

        assertEquals(2, resultado.size());
    }

    @Test
    void testListarClientes() throws SQLException {
        List<Cliente> clientes = List.of(
                new Cliente(1, "Luiz", 800.0, 5, true),
                new Cliente(2, "Rafaela", 1600.0, 15, false)
        );

        when(clienteDAO.listarTodos()).thenReturn(clientes);

        List<Cliente> resultado = controller.listarClientes();

        assertEquals(2, resultado.size());
        verify(clienteDAO).listarTodos();
    }
}
