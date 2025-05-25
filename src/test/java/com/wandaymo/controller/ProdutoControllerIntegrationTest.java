package com.wandaymo.controller;

import com.wandaymo.dao.ProdutoDAO;
import com.wandaymo.model.Produto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProdutoControllerIntegrationTest {

    private ProdutoController produtoController;
    private ProdutoDAO produtoDAO;

    private Produto produtoTeste;

    @BeforeEach
    void setUp() {
        produtoController = new ProdutoController();
        produtoDAO = new ProdutoDAO();

        produtoTeste = new Produto();
        produtoTeste.setCodigo(9999);
        produtoTeste.setDescricao("Produto Teste");
        produtoTeste.setPreco(100.0);
    }

    @AfterEach
    void tearDown() throws SQLException {
        Produto existente = produtoDAO.buscarPorCodigo(produtoTeste.getCodigo());
        if (existente != null) {
            produtoDAO.excluir(produtoTeste.getCodigo());
        }
    }

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

    @Test
    void deveInserirProdutoComSucesso() {
        boolean inserido = produtoController.inserirProduto(produtoTeste);
        assertTrue(inserido);

        List<Produto> buscado = produtoController.buscarProduto(produtoTeste.getDescricao());
        assertNotNull(buscado);
        assertEquals(produtoTeste.getDescricao(), buscado.get(0).getDescricao());
    }

    @Test
    void deveAtualizarProduto() throws SQLException {
        produtoDAO.inserir(produtoTeste);

        List<Produto> produtos = produtoController.buscarProduto(produtoTeste.getDescricao());
        produtoTeste.setCodigo(produtos.get(0).getCodigo());

        produtoTeste.setDescricao("Produto Atualizado");
        produtoTeste.setPreco(150.0);
        boolean atualizado = produtoController.atualizarProduto(produtoTeste);
        assertTrue(atualizado);

        List<Produto> buscado = produtoController.buscarProduto(produtoTeste.getDescricao());
        assertEquals("Produto Atualizado", buscado.get(0).getDescricao());
        assertEquals(150.0, buscado.get(0).getPreco());
    }

    @Test
    void deveExcluirProduto() throws SQLException {
        produtoDAO.inserir(produtoTeste);

        boolean excluido = produtoController.excluirProduto(produtoTeste.getCodigo());
        assertTrue(excluido);

        Produto buscado = produtoController.buscarProduto(produtoTeste.getCodigo());
        assertNull(buscado);
    }

    @Test
    void deveBuscarPorDescricao() throws SQLException {
        produtoDAO.inserir(produtoTeste);

        List<Produto> resultados = produtoController.buscarProduto("Produto Teste");
        assertNotNull(resultados);
        assertTrue(resultados.stream().anyMatch(p -> p.getDescricao().equals(produtoTeste.getDescricao())));
    }

    @Test
    void deveListarTodosProdutos() throws SQLException {
        produtoDAO.inserir(produtoTeste);

        List<Produto> produtos = produtoController.listarProdutos();
        assertNotNull(produtos);
        assertFalse(produtos.isEmpty());
    }
}
