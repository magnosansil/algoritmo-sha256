package br.edu.sha256;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** Programa de demonstração interativa do SHA-256. */
public final class Sha256Demo {

    private Sha256Demo() {
    }

    /** Executa exemplos fixos e permite digitar mensagens. */
    public static void main(String[] args) {
        System.out.println("=== Demonstração didática do SHA-256 ===");
        System.out.println("Exemplo: abc -> " + Sha256.hash("abc"));
        System.out.println();

        // A codificação precisa ser explícita para que acentos e símbolos
        // sejam transformados nos mesmos bytes UTF-8 em qualquer computador.
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (true) {
                System.out.print("Digite uma mensagem (ou 'sair'): ");
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("sair")) {
                    break;
                }
                System.out.println("SHA-256: " + Sha256.hash(message));
                System.out.println("Tamanho do resumo: 256 bits (64 caracteres hexadecimais)\n");
            }
        }
    }
}
