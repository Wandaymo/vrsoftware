package com.wandaymo.controller;

import com.wandaymo.model.Cliente;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClienteControllerIntegrationTest {

    private ClienteController controller;

    @AfterAll
    static void setupDatabase() throws Exception {
        String sql = new String(Files.readAllBytes(
                Paths.get("src/test/resources/sql/setup_test.sql")));

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/vendamanager",
                "postgres",
                "sysadmin");
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
        }
    }

    @BeforeEach
    public void setUp() {
        controller = new ClienteController();
    }

    @Test
    public void testInserirClienteValido() {
        Cliente cliente = new Cliente(100, "Cliente Teste", 1000.0, 15, true);
        assertTrue(controller.inserirCliente(cliente));
    }

    @Test
    public void testInserirClienteCodigoDuplicado() {
        Cliente cliente1 = new Cliente(101, "Cliente 1", 500.0, 10, true);
        assertTrue(controller.inserirCliente(cliente1));
    }

    @Test
    public void testAtualizarClienteExistente() {
        Cliente cliente = new Cliente(102, "Cliente Atualizar", 1200.0, 5, true);
        controller.inserirCliente(cliente);

        List<Cliente> clienteInserido = controller.buscarCliente("Cliente Atualizar");

        cliente.setCodigo(clienteInserido.get(0).getCodigo());
        cliente.setNome("Cliente Atualizado");
        cliente.setLimiteCompra(1500.0);
        assertTrue(controller.atualizarCliente(cliente));

        List<Cliente> clienteAtualizado = controller.buscarCliente("Cliente Atualizado");
        assertEquals("Cliente Atualizado", clienteAtualizado.get(0).getNome());
        assertEquals(1500.0, clienteAtualizado.get(0).getLimiteCompra());
    }

    @Test
    public void testExcluirClienteExistente() {
        Cliente cliente = new Cliente(103, "Cliente Excluir", 700.0, 25, true);
        controller.inserirCliente(cliente);

        assertTrue(controller.excluirCliente(103));
        assertNull(controller.buscarCliente(103));
    }
}