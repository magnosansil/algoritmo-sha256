package br.edu.sha256;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Testes executáveis sem dependências externas. */
public final class Sha256Test {

    private Sha256Test() {
    }

    /** Executa vetores conhecidos, comparação com JDK e casos de bytes. */
    public static void main(String[] args) throws Exception {
        assertHash("", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        assertHash("abc", "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
        assertHash("hello world", "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9");
        assertHash("olá", "9b186e077c7c6d044f5789d76e6d8070a5b0aaa902ebc608bc34170722dba903");

        String longMessage = "abc".repeat(1000);
        assertHash(longMessage, trustedHash(longMessage));
        assertHash("Olá, SHA-256! 你好", trustedHash("Olá, SHA-256! 你好"));

        System.out.println("Todos os testes passaram.");
    }

    private static void assertHash(String message, String expected) {
        String actual = Sha256.hash(message);
        if (!actual.equals(expected)) {
            throw new AssertionError("Mensagem: " + message + "\nEsperado: " + expected + "\nObtido: " + actual);
        }
        System.out.println("OK: " + printable(message) + " -> " + actual);
    }

    private static String trustedHash(String message) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = digest.digest(message.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte value : bytes) {
            result.append(String.format("%02x", value & 0xff));
        }
        return result.toString();
    }

    private static String printable(String message) {
        return message.length() > 30 ? message.substring(0, 30) + "..." : message;
    }
}
