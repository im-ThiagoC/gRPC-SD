# 💊 gRPC-SD: Sistema Distribuído para Gerenciamento de Medicamentos

Este projeto é uma implementação de um sistema distribuído cliente-servidor desenvolvido para a disciplina de **Sistemas Distribuídos** (UFRRJ – 2025/1). Ele demonstra o uso de **gRPC (Google Remote Procedure Call)** como tecnologia de comunicação eficiente entre serviços heterogêneos, permitindo a gestão remota de medicamentos em uma farmácia digital.

---

## 📁 Estrutura do Projeto

```plaintext
gRPC-SD/
├── gRPC-Client/
│   └── client/           # Aplicação Node.js + Express que atua como cliente gRPC
│       ├── proto/        # Arquivos .proto usados para comunicação gRPC
│       └── public/       # Interface Web (HTML, CSS, JS)
└── gRPC-Server/
    └── servidor/         # Aplicação Java (Spring Boot) que implementa o servidor gRPC
        ├── src/
        │   ├── main/
        │   │   ├── java/com/example/servidor/remedio  # Código-fonte principal
        │   │   ├── proto/                             # Protobufs usados no servidor
        │   │   └── resources/                         # application.properties etc.
        │   └── test/                                  # Testes (JUnit)
        └── target/                                    # Arquivos gerados pela build
```

---

## 🚀 Tecnologias Utilizadas

- **gRPC** com **Protocol Buffers**
- **Java + Spring Boot** no servidor
- **Node.js + Express** no cliente
- **MySQL** para persistência de dados
- **HTML/CSS/JavaScript** para a interface web

---

## 🔄 Fluxo de Requisição

1. O usuário interage com a interface web (cliente).
2. O Express converte requisições REST para chamadas gRPC.
3. O servidor Java processa a lógica e acessa o banco MySQL.
4. A resposta é devolvida ao cliente via gRPC → REST → Web.

---

## 🧩 Funcionalidades

- Cadastrar, listar e atualizar medicamentos.
- Dar baixa no estoque com histórico de alterações.
- Consultar histórico completo de cada remédio.
- Interface Web interativa.
- Comunicação eficiente e serializada com gRPC.

---

## 🧠 Conceitos Aplicados da Disciplina

- Arquiteturas distribuídas (cliente-servidor)
- Comunicação eficiente (gRPC com Protocol Buffers)
- Processos concorrentes com múltiplos clientes
- Serialização e interoperabilidade entre linguagens
- Tolerância à falha com tratamento de exceções

---

## ⚙️ Como Executar

### Pré-requisitos

- Java 17+
- Node.js 18+
- MySQL 8+
- Docker (opcional, para rodar o banco de dados localmente)

### 1. Subir o banco de dados com Docker

```bash
docker run --name farmacia-mysql -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=farmacia_api -p 3306:3306 -d mysql
```

### 2. Compilar e rodar o servidor gRPC

```bash
cd gRPC-Server/servidor
./mvnw spring-boot:run
```

### 3. Instalar dependências do cliente e iniciar

```bash
cd gRPC-Client/client
npm install
node server.js
```

### 4. Acessar a interface web

Abra [http://localhost:3000](http://localhost:3000) no navegador.

---

## 📌 Autores

- Thiago Medeiros Carvalho – [@ThiagoCarvalho](https://github.com/ThiagoCarvalho)
- Carlos Eduardo Alves Ferreira – [@CarlosAlves3031](https://github.com/CarlosAlves3031)

---

## 📄 Licença

Este projeto é apenas para fins acadêmicos e educacionais.