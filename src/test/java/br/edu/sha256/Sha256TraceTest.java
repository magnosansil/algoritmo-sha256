package br.edu.sha256;

/** Verifica a instrumentação didática sem alterar o resultado do algoritmo. */
public final class Sha256TraceTest {
    private Sha256TraceTest() {
    }

    /** Executa as verificações do rastreamento para uma mensagem curta e longa. */
    public static void main(String[] args) {
        Sha256Trace abc = Sha256.explain("abc");
        assertEquals("abc hash", "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", abc.hash);
        assertEquals("abc bytes", "616263", abc.messageUtf8Hex);
        assertEquals("abc blocks", 1, abc.blocks.size());
        assertEquals("abc words", 64, abc.blocks.get(0).words.size());
        assertEquals("abc rounds", 64, abc.blocks.get(0).rounds.size());

        Sha256Trace longMessage = Sha256.explain("a".repeat(100));
        assertEquals("long blocks", 2, longMessage.blocks.size());
        if (!Sha256Json.trace(abc).contains("\"hash\":\"" + abc.hash + "\"")) {
            throw new AssertionError("JSON não contém o hash final");
        }
        System.out.println("Todos os testes de rastreamento passaram.");
    }

    private static void assertEquals(String name, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + ": esperado " + expected + ", obtido " + actual);
        }
    }
}
