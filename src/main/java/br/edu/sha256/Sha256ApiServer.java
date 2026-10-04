package br.edu.sha256;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * API REST mínima para a interface Angular.
 *
 * <p>Endpoint: {@code POST /api/hash}, com corpo
 * {@code {"message":"texto"}}. O servidor usa somente APIs do JDK.</p>
 */
public final class Sha256ApiServer {
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_REQUEST_BYTES = 10 * 1024 * 1024;

    private Sha256ApiServer() {
    }

    /** Inicializa o servidor na porta informada ou em 8080. */
    public static void main(String[] args) throws IOException {
        int port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
        HttpServer server = createServer(port);
        server.start();
        System.out.println("API SHA-256 disponível em http://localhost:" + port + "/api/hash");
    }

    /** Cria uma instância configurada; útil para testes de integração. */
    static HttpServer createServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext("/api/hash", Sha256ApiServer::handleHash);
        server.setExecutor(Executors.newCachedThreadPool(task -> {
            Thread thread = new Thread(task, "sha256-api-worker");
            thread.setDaemon(true);
            return thread;
        }));
        return server;
    }

    private static void handleHash(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange.getResponseHeaders());
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Use o método POST.\"}");
            return;
        }

        try {
            String body = readBody(exchange.getRequestBody());
            String message = readJsonMessage(body);
            if (message == null) {
                sendJson(exchange, 400, "{\"error\":\"O corpo deve conter uma propriedade message.\"}");
                return;
            }
            sendJson(exchange, 200, Sha256Json.trace(Sha256.explain(message)));
        } catch (IllegalArgumentException exception) {
            sendJson(exchange, 400, "{\"error\":" + quote(exception.getMessage()) + "}");
        } catch (RuntimeException exception) {
            sendJson(exchange, 500, "{\"error\":\"Não foi possível processar a mensagem.\"}");
        } finally {
            exchange.close();
        }
    }

    private static String readBody(InputStream input) throws IOException {
        byte[] buffer = input.readNBytes(MAX_REQUEST_BYTES + 1);
        if (buffer.length > MAX_REQUEST_BYTES) {
            throw new IllegalArgumentException("A mensagem excede o limite de 10 MB.");
        }
        return new String(buffer, StandardCharsets.UTF_8);
    }

    /** Extrai uma string JSON, incluindo escapes básicos e Unicode. */
    private static String readJsonMessage(String body) {
        int key = body.indexOf("\"message\"");
        if (key < 0) return null;
        int colon = body.indexOf(':', key + 9);
        if (colon < 0) return null;
        int start = colon + 1;
        while (start < body.length() && Character.isWhitespace(body.charAt(start))) start++;
        if (start >= body.length() || body.charAt(start) != '"') return null;

        StringBuilder value = new StringBuilder();
        for (int i = start + 1; i < body.length(); i++) {
            char c = body.charAt(i);
            if (c == '"') return value.toString();
            if (c != '\\') {
                value.append(c);
                continue;
            }
            if (++i >= body.length()) throw new IllegalArgumentException("Escape JSON incompleto.");
            char escaped = body.charAt(i);
            switch (escaped) {
                case '"': value.append('"'); break;
                case '\\': value.append('\\'); break;
                case '/': value.append('/'); break;
                case 'b': value.append('\b'); break;
                case 'f': value.append('\f'); break;
                case 'n': value.append('\n'); break;
                case 'r': value.append('\r'); break;
                case 't': value.append('\t'); break;
                case 'u':
                    if (i + 4 >= body.length()) throw new IllegalArgumentException("Escape Unicode inválido.");
                    value.append((char) Integer.parseInt(body.substring(i + 1, i + 5), 16));
                    i += 4;
                    break;
                default: throw new IllegalArgumentException("Escape JSON inválido.");
            }
        }
        throw new IllegalArgumentException("String JSON não terminada.");
    }

    private static void addCorsHeaders(Headers headers) {
        headers.set("Access-Control-Allow-Origin", "http://localhost:4200");
        headers.set("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    private static String quote(String value) {
        StringBuilder result = new StringBuilder("\"");
        for (char c : (value == null ? "" : value).toCharArray()) {
            if (c == '"' || c == '\\') result.append('\\');
            result.append(c);
        }
        return result.append('"').toString();
    }
}
