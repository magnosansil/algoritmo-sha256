package br.edu.sha256;

import java.util.List;

/**
 * Dados imutáveis para explicar uma execução do SHA-256 na interface web.
 * Os valores das palavras são representados em hexadecimal big-endian.
 */
public final class Sha256Trace {
    public final String message;
    public final String messageUtf8Hex;
    public final int messageByteLength;
    public final long messageBitLength;
    public final String paddedMessageHex;
    public final int paddedByteLength;
    public final List<BlockTrace> blocks;
    public final String hash;

    Sha256Trace(String message, String messageUtf8Hex, int messageByteLength,
            long messageBitLength, String paddedMessageHex, int paddedByteLength,
            List<BlockTrace> blocks, String hash) {
        this.message = message;
        this.messageUtf8Hex = messageUtf8Hex;
        this.messageByteLength = messageByteLength;
        this.messageBitLength = messageBitLength;
        this.paddedMessageHex = paddedMessageHex;
        this.paddedByteLength = paddedByteLength;
        this.blocks = List.copyOf(blocks);
        this.hash = hash;
    }

    /** Dados de um bloco de 512 bits. */
    public static final class BlockTrace {
        public final int index;
        public final String bytesHex;
        public final String initialHash;
        public final String finalHash;
        public final List<String> words;
        public final List<RoundTrace> rounds;

        BlockTrace(int index, String bytesHex, String initialHash, String finalHash,
                List<String> words, List<RoundTrace> rounds) {
            this.index = index;
            this.bytesHex = bytesHex;
            this.initialHash = initialHash;
            this.finalHash = finalHash;
            this.words = List.copyOf(words);
            this.rounds = List.copyOf(rounds);
        }
    }

    /** Valores observáveis de uma rodada das 64 rodadas. */
    public static final class RoundTrace {
        public final int round;
        public final String constant;
        public final String word;
        public final String a;
        public final String b;
        public final String c;
        public final String d;
        public final String e;
        public final String f;
        public final String g;
        public final String h;
        public final String t1;
        public final String t2;
        public final String nextA;
        public final String nextB;
        public final String nextC;
        public final String nextD;
        public final String nextE;
        public final String nextF;
        public final String nextG;
        public final String nextH;

        RoundTrace(int round, String constant, String word,
                int a, int b, int c, int d, int e, int f, int g, int h,
                int t1, int t2, int nextA, int nextB, int nextC, int nextD,
                int nextE, int nextF, int nextG, int nextH) {
            this.round = round;
            this.constant = constant;
            this.word = word;
            this.a = hex(a); this.b = hex(b); this.c = hex(c); this.d = hex(d);
            this.e = hex(e); this.f = hex(f); this.g = hex(g); this.h = hex(h);
            this.t1 = hex(t1); this.t2 = hex(t2);
            this.nextA = hex(nextA); this.nextB = hex(nextB); this.nextC = hex(nextC);
            this.nextD = hex(nextD); this.nextE = hex(nextE); this.nextF = hex(nextF);
            this.nextG = hex(nextG); this.nextH = hex(nextH);
        }

        private static String hex(int value) {
            return String.format("%08x", value);
        }
    }
}
