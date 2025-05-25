package com.wandaymo.controller;

import com.wandaymo.dao.ProdutoDAO;
import com.wandaymo.model.Produto;
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

class ProdutoControllerTest {

    private ProdutoDAO produtoDAO;
    private ProdutoController controller;

    @BeforeEach
    void setUp() {
        produtoDAO = mock(ProdutoDAO.class);
        controller = new ProdutoController(produtoDAO);
    }

    @Test
    void testInserirProduto_ComCodigoExistente() throws SQLException {
        Produto produto = new Produto(1, "Produto A", 10.0, true);
        when(produtoDAO.codigoExiste(1)).thenReturn(true);

        boolean resultado = controller.inserirProduto(produto);

        assertFalse(resultado);
        verify(produtoDAO, never()).inserir(any());
    }

    @Test
    void testInserirProduto_ComSucesso() throws SQLException {
        Produto produto = new Produto(1, "Produto A", 10.0, true);
        when(produtoDAO.codigoExiste(1)).thenReturn(false);

        boolean resultado = controller.inserirProduto(produto);

        assertTrue(resultado);
        verify(produtoDAO).inserir(produto);
    }

    @Test
    void testAtualizarProduto_ComSucesso() throws SQLException {
        Produto produto = new Produto(1, "Produto Atualizado", 15.0, true);

        boolean resultado = controller.atualizarProduto(produto);

        assertTrue(resultado);
        verify(produtoDAO).atualizar(produto);
    }

    @Test
    void testExcluirProduto_ComSucesso() throws SQLException {
        boolean resultado = controller.excluirProduto(1);

        assertTrue(resultado);
        verify(produtoDAO).excluir(1);
    }

    @Test
    void testBuscarProduto_PorCodigo() throws SQLException {
        Produto produto = new Produto(1, "Produto X", 20.0, true);
        when(produtoDAO.buscarPorCodigo(1)).thenReturn(produto);

        Produto resultado = controller.buscarProduto(1);

        assertNotNull(resultado);
        assertEquals("Produto X", resultado.getDescricao());
    }

    @Test
    void testBuscarProduto_PorDescricao() throws SQLException {
        Produto p1 = new Produto(1, "Café", 8.0, true);
        Produto p2 = new Produto(2, "Café Solúvel", 9.0, true);
        List<Produto> mockLista = Arrays.asList(p1, p2);

        when(produtoDAO.buscarPorDescricao("Café")).thenReturn(mockLista);

        List<Produto> resultado = controller.buscarProduto("Café");

        assertEquals(2, resultado.size());
    }

    @Test
    void testListarProdutos() throws SQLException {
        List<Produto> produtos = List.of(
                new Produto(1, "Pão", 5.0, true),
                new Produto(2, "Leite", 4.5, true)
        );

        when(produtoDAO.listarTodos()).thenReturn(produtos);

        List<Produto> resultado = controller.listarProdutos();

        assertEquals(2, resultado.size());
        verify(produtoDAO).listarTodos();
    }
}
