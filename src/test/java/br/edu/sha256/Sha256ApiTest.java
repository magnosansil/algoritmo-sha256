package br.edu.sha256;

import com.sun.net.httpserver.HttpServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/** Teste de integração do endpoint REST, sem MessageDigest. */
public final class Sha256ApiTest {
    private Sha256ApiTest() {
    }

    /** Inicia a API em uma porta livre e valida sucesso e erro HTTP. */
    public static void main(String[] args) throws Exception {
        HttpServer server = Sha256ApiServer.createServer(0);
        server.start();
        try {
            int port = server.getAddress().getPort();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest valid = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/hash"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"message\":\"abc\"}", StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(valid, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            assertEquals("status", 200, response.statusCode());
            assertContains("hash", response.body(), "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
            assertContains("rounds", response.body(), "\"rounds\"");

            HttpRequest invalid = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/hash"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{}", StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> invalidResponse = client.send(invalid, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            assertEquals("invalid status", 400, invalidResponse.statusCode());
        } finally {
            server.stop(0);
        }
        System.out.println("Todos os testes da API passaram.");
    }

    private static void assertEquals(String name, int expected, int actual) {
        if (expected != actual) throw new AssertionError(name + ": esperado " + expected + ", obtido " + actual);
    }

    private static void assertContains(String name, String actual, String expected) {
        if (!actual.contains(expected)) throw new AssertionError(name + " não encontrado na resposta");
    }
}
