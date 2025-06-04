const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const path = require('path');
require('dotenv').config();

// Carrega o arquivo proto
const PROTO_PATH = path.join(__dirname, './proto/remedio.proto');

const packageDefinition = protoLoader.loadSync(
    PROTO_PATH,
    {
        keepCase: true,
        longs: String,
        enums: String,
        defaults: true,
        oneofs: true
    }
);

const remedioProto = grpc.loadPackageDefinition(packageDefinition).remedio;

// Cria o cliente gRPC
const client = new remedioProto.RemedioService(
    process.env.GRPC_SERVER_ADDRESS || 'localhost:50051',
    grpc.credentials.createInsecure()
);

// Métodos do cliente
const remedioClient = {
    cadastrar: (dados) => {
        return new Promise((resolve, reject) => {
            client.Cadastrar(dados, (err, response) => {
                if (err) reject(err);
                else resolve(response);
            });
        });
    },

    listar: () => {
        return new Promise((resolve, reject) => {
            client.Listar({}, (err, response) => {
                if (err) reject(err);
                else resolve(response.remedios);
            });
        });
    },

    atualizar: (dados) => {
        return new Promise((resolve, reject) => {
            client.Atualizar(dados, (err, response) => {
                if (err) reject(err);
                else resolve(response);
            });
        });
    },

    deletar: (id) => {
        return new Promise((resolve, reject) => {
            client.Deletar({ id }, (err, response) => {
                if (err) reject(err);
                else resolve(response);
            });
        });
    },

    desativar: (id) => {
        return new Promise((resolve, reject) => {
            client.Desativar({ id }, (err, response) => {
                if (err) reject(err);
                else resolve(response);
            });
        });
    },

    ativar: (id) => {
        return new Promise((resolve, reject) => {
            client.Ativar({ id }, (err, response) => {
                if (err) reject(err);
                else resolve(response);
            });
        });
    },

    detalhar: (id) => {
        return new Promise((resolve, reject) => {
            client.Detalhar({ id }, (err, response) => {
                if (err) reject(err);
                else resolve(response);
            });
        });
    }
};

module.exports = remedioClient;