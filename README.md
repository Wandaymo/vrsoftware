# Sistema de gerenciamento de vendas

## 📝 Descrição

Este projeto é um sistema de gerenciamento de vendas desenvolvido em Java com interface Swing e banco de dados PostgreSQL. Ele permite o cadastro e consulta de clientes, produtos e vendas, com validações de limite de crédito e outras regras de negócio.

## 🚀 Funcionalidades Principais

- **Cadastro de Clientes**
    - Código, nome, limite de compra e dia de fechamento da fatura
    - Validação de limite de crédito

- **Cadastro de Produtos**
    - Código, descrição e preço unitário

- **Gestão de Vendas**
    - Registro de vendas com múltiplos produtos
    - Validação de produtos duplicados
    - Cálculo automático do valor total

- **Consultas**
    - Vendas por cliente
    - Vendas por produto
    - Filtros por período
    - Visualização agrupada de dados

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java 18
- **Interface Gráfica**: Java Swing
- **Banco de Dados**: PostgreSQL
- **Gerenciamento de Dependências**: Maven
- **Testes**: JUnit 5, Mockito

## 📋 Pré-requisitos

Antes de executar o projeto, você precisará ter instalado:

- Java JDK 18 ou superior
- PostgreSQL 12 ou superior
- Maven 3.6 ou superior

## 🚀 Configuração do Ambiente

1. **Banco de Dados**:
   ```sql
   CREATE DATABASE vendamanager;
   ```

2. **Configuração da conexão**:
   Edite o arquivo `DatabaseConnection.java` com suas credenciais:
   ```java
   private static final String URL = "jdbc:postgresql://localhost:5432/vendamanager";
   private static final String USER = "seu_usuario";
   private static final String PASSWORD = "sua_senha";
   ```

3. **Estrutura do banco**:
   O sistema criará automaticamente as tabelas na primeira execução.

## ▶️ Como Executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/Wandaymo/vrsoftware.git
   ```

2. Navegue até o diretório do projeto:
   ```bash
   cd vrsoftware
   ```

3. Compile e execute:
   ```bash
   mvn clean install
   mvn exec:java -Dexec.mainClass="com.wandaymo.Main"
   ```

## 🧪 Testes

Para executar os testes unitários:
```bash
mvn test
```

## 🗂️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   ├── com.wandaymo/
│   │   │   ├── controller/    # Lógica de negócio
│   │   │   ├── dao/           # Acesso a dados
│   │   │   ├── model/         # Entidades
│   │   │   ├── view/          # Interface gráfica
│   │   │   └── Main.java      # Classe principal
│   ├── resources/
│   │   └── sql/               # Scripts SQL
├── test/                      # Testes unitários
pom.xml                        # Configuração Maven
```

## 📊 Regras de Negócio Implementadas

1. **Limite de Crédito**:
    - Verificação automática do limite disponível
    - Cálculo considerando o ciclo de fechamento

2. **Validações**:
    - Produtos duplicados na mesma venda
    - Datas válidas para filtros
    - Campos obrigatórios

3. **Relatórios**:
    - Agrupamento por cliente e produto
    - Cálculo de totais e subtotais