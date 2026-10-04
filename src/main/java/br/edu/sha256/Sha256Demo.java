package br.edu.sha256;

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

        try (Scanner scanner = new Scanner(System.in)) {
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
