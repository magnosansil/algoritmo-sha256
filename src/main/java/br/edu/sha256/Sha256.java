package br.edu.sha256;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação didática do algoritmo SHA-256 conforme a especificação
 * FIPS 180-4.
 *
 * <p>A implementação não utiliza {@code MessageDigest} para calcular o
 * resumo. Todos os passos principais — padding, expansão da mensagem,
 * funções lógicas e as 64 rodadas — estão presentes nesta classe.</p>
 */
public final class Sha256 {

    /** Quantidade de palavras de 32 bits no resumo final. */
    private static final int HASH_WORDS = 8;
    /** Tamanho de cada bloco processado, em bytes: 512 bits. */
    private static final int BLOCK_SIZE_BYTES = 64;

    /** Valores iniciais definidos pelo padrão SHA-256. */
    private static final int[] INITIAL_HASH = {
        0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
        0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };

    /** 64 constantes derivadas das partes fracionárias das raízes cúbicas. */
    private static final int[] ROUND_CONSTANTS = {
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
        0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
        0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
        0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
        0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
        0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
        0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    private Sha256() {
        // Classe utilitária: não deve ser instanciada.
    }

    /**
     * Calcula o SHA-256 do texto usando UTF-8.
     *
     * @param message mensagem de entrada
     * @return resumo com 64 caracteres hexadecimais minúsculos
     * @throws NullPointerException se a mensagem for nula
     */
    public static String hash(String message) {
        return hash(message.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Calcula o SHA-256 de bytes arbitrários.
     *
     * @param message bytes da mensagem
     * @return resumo com 64 caracteres hexadecimais minúsculos
     * @throws NullPointerException se o vetor for nulo
     */
    public static String hash(byte[] message) {
        byte[] padded = pad(message);
        int[] hash = INITIAL_HASH.clone();

        for (int offset = 0; offset < padded.length; offset += BLOCK_SIZE_BYTES) {
            processBlock(padded, offset, hash);
        }
        return toHex(hash);
    }

    /**
     * Executa o mesmo algoritmo de {@link #hash(String)}, preservando dados
     * intermediários para fins didáticos e de visualização.
     *
     * @param message mensagem em texto, convertida para UTF-8
     * @return rastreamento completo da execução
     */
    public static Sha256Trace explain(String message) {
        byte[] original = message.getBytes(StandardCharsets.UTF_8);
        byte[] padded = pad(original);
        int[] hash = INITIAL_HASH.clone();
        List<Sha256Trace.BlockTrace> blocks = new ArrayList<>();

        for (int offset = 0; offset < padded.length; offset += BLOCK_SIZE_BYTES) {
            blocks.add(processBlockWithTrace(padded, offset, hash, blocks.size()));
        }

        return new Sha256Trace(message, bytesToHex(original), original.length,
                Math.multiplyExact((long) original.length, 8L), bytesToHex(padded),
                padded.length, blocks, toHex(hash));
    }

    /**
     * Adiciona o bit 1, zeros e o tamanho original em 64 bits big-endian.
     * O resultado sempre possui tamanho múltiplo de 512 bits.
     */
    private static byte[] pad(byte[] message) {
        long bitLength = Math.multiplyExact((long) message.length, 8L);
        int remainder = (message.length + 1) % BLOCK_SIZE_BYTES;
        int zeroBytes = (remainder <= 56) ? 56 - remainder : 120 - remainder;
        int totalLength = Math.addExact(message.length, 1 + zeroBytes + 8);

        byte[] padded = new byte[totalLength];
        System.arraycopy(message, 0, padded, 0, message.length);
        padded[message.length] = (byte) 0x80;

        for (int i = 0; i < Long.BYTES; i++) {
            padded[totalLength - 1 - i] = (byte) (bitLength >>> (8 * i));
        }
        return padded;
    }

    /** Processa um bloco de 64 bytes e atualiza o estado hash. */
    private static void processBlock(byte[] block, int offset, int[] hash) {
        int[] words = expandMessage(block, offset);
        int a = hash[0], b = hash[1], c = hash[2], d = hash[3];
        int e = hash[4], f = hash[5], g = hash[6], h = hash[7];

        for (int round = 0; round < 64; round++) {
            int t1 = h + bigSigma1(e) + choose(e, f, g) + ROUND_CONSTANTS[round] + words[round];
            int t2 = bigSigma0(a) + majority(a, b, c);
            h = g;
            g = f;
            f = e;
            e = d + t1;
            d = c;
            c = b;
            b = a;
            a = t1 + t2;
        }

        hash[0] += a;
        hash[1] += b;
        hash[2] += c;
        hash[3] += d;
        hash[4] += e;
        hash[5] += f;
        hash[6] += g;
        hash[7] += h;
    }

    /** Processa um bloco e registra o estado antes e depois de cada rodada. */
    private static Sha256Trace.BlockTrace processBlockWithTrace(byte[] block, int offset,
            int[] hash, int blockIndex) {
        int[] words = expandMessage(block, offset);
        List<String> wordHex = new ArrayList<>(64);
        for (int word : words) {
            wordHex.add(String.format("%08x", word));
        }
        String initialHash = toHex(hash);
        List<Sha256Trace.RoundTrace> rounds = new ArrayList<>(64);
        int a = hash[0], b = hash[1], c = hash[2], d = hash[3];
        int e = hash[4], f = hash[5], g = hash[6], h = hash[7];

        for (int round = 0; round < 64; round++) {
            int t1 = h + bigSigma1(e) + choose(e, f, g) + ROUND_CONSTANTS[round] + words[round];
            int t2 = bigSigma0(a) + majority(a, b, c);
            int nextA = t1 + t2;
            int nextB = a;
            int nextC = b;
            int nextD = c;
            int nextE = d + t1;
            int nextF = e;
            int nextG = f;
            int nextH = g;

            rounds.add(new Sha256Trace.RoundTrace(round,
                    String.format("%08x", ROUND_CONSTANTS[round]), wordHex.get(round),
                    a, b, c, d, e, f, g, h, t1, t2,
                    nextA, nextB, nextC, nextD, nextE, nextF, nextG, nextH));
            a = nextA; b = nextB; c = nextC; d = nextD;
            e = nextE; f = nextF; g = nextG; h = nextH;
        }

        hash[0] += a; hash[1] += b; hash[2] += c; hash[3] += d;
        hash[4] += e; hash[5] += f; hash[6] += g; hash[7] += h;
        return new Sha256Trace.BlockTrace(blockIndex, bytesToHex(block, offset, BLOCK_SIZE_BYTES),
                initialHash, toHex(hash), wordHex, rounds);
    }

    /** Converte as 16 palavras iniciais do bloco nas 64 palavras da rodada. */
    private static int[] expandMessage(byte[] block, int offset) {
        int[] words = new int[64];
        for (int i = 0; i < 16; i++) {
            int index = offset + i * 4;
            words[i] = ((block[index] & 0xff) << 24)
                    | ((block[index + 1] & 0xff) << 16)
                    | ((block[index + 2] & 0xff) << 8)
                    | (block[index + 3] & 0xff);
        }
        for (int i = 16; i < 64; i++) {
            words[i] = smallSigma1(words[i - 2]) + words[i - 7]
                    + smallSigma0(words[i - 15]) + words[i - 16];
        }
        return words;
    }

    /** Ch(x,y,z) = (x AND y) XOR (NOT x AND z). */
    private static int choose(int x, int y, int z) {
        return (x & y) ^ (~x & z);
    }

    /** Maj(x,y,z) escolhe o valor majoritário bit a bit. */
    private static int majority(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    /** Sigma maiúsculo 0, usado na variável a. */
    private static int bigSigma0(int x) {
        return Integer.rotateRight(x, 2) ^ Integer.rotateRight(x, 13) ^ Integer.rotateRight(x, 22);
    }

    /** Sigma maiúsculo 1, usado na variável e. */
    private static int bigSigma1(int x) {
        return Integer.rotateRight(x, 6) ^ Integer.rotateRight(x, 11) ^ Integer.rotateRight(x, 25);
    }

    /** Sigma minúsculo 0, usado na expansão da mensagem. */
    private static int smallSigma0(int x) {
        return Integer.rotateRight(x, 7) ^ Integer.rotateRight(x, 18) ^ (x >>> 3);
    }

    /** Sigma minúsculo 1, usado na expansão da mensagem. */
    private static int smallSigma1(int x) {
        return Integer.rotateRight(x, 17) ^ Integer.rotateRight(x, 19) ^ (x >>> 10);
    }

    /** Formata oito palavras de 32 bits como 32 bytes hexadecimais. */
    private static String toHex(int[] hash) {
        StringBuilder result = new StringBuilder(HASH_WORDS * 8);
        for (int word : hash) {
            result.append(String.format("%08x", word));
        }
        return result.toString();
    }

    /** Converte todos os bytes para hexadecimal, sem separadores. */
    private static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, 0, bytes.length);
    }

    /** Converte uma faixa de bytes para hexadecimal, sem alterar os bytes. */
    private static String bytesToHex(byte[] bytes, int offset, int length) {
        StringBuilder result = new StringBuilder(length * 2);
        for (int i = offset; i < offset + length; i++) {
            result.append(String.format("%02x", bytes[i] & 0xff));
        }
        return result.toString();
    }
}
