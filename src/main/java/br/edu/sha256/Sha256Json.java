package br.edu.sha256;

/** Serializador JSON mínimo para a resposta da API, sem dependências externas. */
final class Sha256Json {
    private Sha256Json() {
    }

    static String trace(Sha256Trace trace) {
        StringBuilder json = new StringBuilder(64_000);
        json.append('{');
        field(json, "message", trace.message).append(',');
        field(json, "messageUtf8Hex", trace.messageUtf8Hex).append(',');
        number(json, "messageByteLength", trace.messageByteLength).append(',');
        number(json, "messageBitLength", trace.messageBitLength).append(',');
        field(json, "paddedMessageHex", trace.paddedMessageHex).append(',');
        number(json, "paddedByteLength", trace.paddedByteLength).append(',');
        field(json, "hash", trace.hash).append(',');
        json.append("\"blocks\":[");
        for (int i = 0; i < trace.blocks.size(); i++) {
            if (i > 0) json.append(',');
            appendBlock(json, trace.blocks.get(i));
        }
        json.append("],\"steps\":[");
        step(json, "message", "Mensagem original", "Texto digitado pelo usuário");
        json.append(',');
        step(json, "bytes", "Conversão para bytes", "UTF-8");
        json.append(',');
        step(json, "padding", "Padding", "Bit 1, zeros e comprimento em bits");
        json.append(',');
        step(json, "blocks", "Blocos de 512 bits", "Mensagem preenchida dividida em blocos");
        json.append(',');
        step(json, "expansion", "Expansão da mensagem", "W[0] até W[63]");
        json.append(',');
        step(json, "rounds", "64 rodadas", "T1, T2 e valores a até h");
        json.append(',');
        step(json, "state", "Atualização do estado", "Soma ao estado hash acumulado");
        json.append(',');
        step(json, "result", "Resultado final", "Resumo de 256 bits em hexadecimal");
        json.append(']');
        return json.append('}').toString();
    }

    private static void appendBlock(StringBuilder json, Sha256Trace.BlockTrace block) {
        json.append('{');
        number(json, "index", block.index).append(',');
        field(json, "bytesHex", block.bytesHex).append(',');
        field(json, "initialHash", block.initialHash).append(',');
        field(json, "finalHash", block.finalHash).append(',');
        json.append("\"words\":[");
        for (int i = 0; i < block.words.size(); i++) {
            if (i > 0) json.append(',');
            quote(json, block.words.get(i));
        }
        json.append("],\"rounds\":[");
        for (int i = 0; i < block.rounds.size(); i++) {
            if (i > 0) json.append(',');
            appendRound(json, block.rounds.get(i));
        }
        json.append("]}");
    }

    private static void appendRound(StringBuilder json, Sha256Trace.RoundTrace round) {
        json.append('{');
        number(json, "round", round.round).append(',');
        field(json, "constant", round.constant).append(',');
        field(json, "word", round.word).append(',');
        field(json, "a", round.a).append(','); field(json, "b", round.b).append(',');
        field(json, "c", round.c).append(','); field(json, "d", round.d).append(',');
        field(json, "e", round.e).append(','); field(json, "f", round.f).append(',');
        field(json, "g", round.g).append(','); field(json, "h", round.h).append(',');
        field(json, "t1", round.t1).append(','); field(json, "t2", round.t2).append(',');
        field(json, "nextA", round.nextA).append(','); field(json, "nextB", round.nextB).append(',');
        field(json, "nextC", round.nextC).append(','); field(json, "nextD", round.nextD).append(',');
        field(json, "nextE", round.nextE).append(','); field(json, "nextF", round.nextF).append(',');
        field(json, "nextG", round.nextG).append(','); field(json, "nextH", round.nextH);
        json.append('}');
    }

    private static void step(StringBuilder json, String id, String title, String description) {
        json.append('{');
        field(json, "id", id).append(',');
        field(json, "title", title).append(',');
        field(json, "description", description);
        json.append('}');
    }

    private static StringBuilder field(StringBuilder json, String name, String value) {
        quote(json, name).append(':');
        return quote(json, value);
    }

    private static StringBuilder number(StringBuilder json, String name, long value) {
        quote(json, name).append(':').append(value);
        return json;
    }

    private static StringBuilder quote(StringBuilder json, String value) {
        json.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"': json.append("\\\""); break;
                case '\\': json.append("\\\\"); break;
                case '\n': json.append("\\n"); break;
                case '\r': json.append("\\r"); break;
                case '\t': json.append("\\t"); break;
                case '\b': json.append("\\b"); break;
                case '\f': json.append("\\f"); break;
                default:
                    if (c < 0x20) {
                        json.append(String.format("\\u%04x", (int) c));
                    } else {
                        json.append(c);
                    }
                    break;
            }
        }
        return json.append('"');
    }
}
