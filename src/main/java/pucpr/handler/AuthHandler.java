package pucpr.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import pucpr.model.Usuario;
import pucpr.repository.UsuarioRepository;
import pucpr.service.JwtService;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AuthHandler {

    private final UsuarioRepository repository;
    private final JwtService jwtService;

    public AuthHandler(UsuarioRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    public void handleLogin(HttpExchange exchange) throws IOException {

        cors(exchange);
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> body = mapper.readValue(exchange.getRequestBody(), Map.class);

        String email= body.get("email");
        String password = body.get("password");

        Optional<Usuario> optUsuario = repository.findByEmail(email);

        if (optUsuario.isEmpty() || !BCrypt.checkpw(password, optUsuario.get().getSenha())) {
            String resposta = "{\"message\": \"E-mail ou senha inválidos.\"}";
            byte[] bytes = resposta.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(401, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
            return;
        }

        String token = jwtService.generateToken(optUsuario.get());
        String resposta = "{\"token\": \"" + token + "\"}";
        byte[] bytes = resposta.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();

        // System.out.println("Login realizado por: " + email);
    }

    /**
     * Gerencia o processo de Cadastro (Registro).
     * Objetivo: Criar um novo usuário de forma segura.
     */
    public void handleRegister(HttpExchange exchange) throws IOException {

        cors(exchange);
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> body = mapper.readValue(exchange.getRequestBody(), Map.class);

        String nomeCompleto = body.get("name");
        String email = body.get("email");
        String senha = body.get("password");

        if (nomeCompleto == null || email == null || senha == null || nomeCompleto.isBlank() || email.isBlank() || senha.isBlank()) {

            String resposta = "Campos obrigatórios, por favor, complete espaços vazios.";
            byte[] bytes = resposta.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
            return;
        }

        if (repository.findByEmail(email).isPresent()) {
            String resposta = "{\"message\": \"E-mail já cadastrado.\"}";
            byte[] bytes = resposta.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
            return;
        }

        String id = UUID.randomUUID().toString();
        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt(12));
        Usuario novoUsuario = new Usuario(id, nomeCompleto, email, senhaCriptografada, "USER");
        repository.save(novoUsuario);

        String resposta = "{\"message\": \"Usuário cadastrado com sucesso!\"}";
        byte[] bytes = resposta.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(201, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();

        // System.out.println("Cadasro feito: " + email);
    }

    private void cors(HttpExchange exchange) {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}