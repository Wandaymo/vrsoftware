package com.wandaymo.controller;

import com.wandaymo.dao.ClienteDAO;
import com.wandaymo.model.Cliente;

import java.sql.SQLException;
import java.util.List;

public class ClienteController {

    private final ClienteDAO clienteDAO;

    public ClienteController() {
        this(new ClienteDAO());
    }

    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public boolean inserirCliente(Cliente cliente) {
        try {
            if (clienteDAO.codigoExiste(cliente.getCodigo())) {
                return false;
            }
            clienteDAO.inserir(cliente);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarCliente(Cliente cliente) {
        try {
            clienteDAO.atualizar(cliente);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluirCliente(int codigo) {
        try {
            clienteDAO.excluir(codigo);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Cliente buscarCliente(int codigo) {
        try {
            return clienteDAO.buscarPorCodigo(codigo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Cliente> buscarCliente(String nome) {
        try {
            return clienteDAO.buscarPorNome(nome);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Cliente> listarClientes() {
        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
