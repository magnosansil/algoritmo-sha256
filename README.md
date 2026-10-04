# SHA-256 didático em Java

Projeto acadêmico para a disciplina de Segurança de Redes. A classe `Sha256` implementa manualmente o SHA-256 conforme o padrão FIPS 180-4. `MessageDigest` aparece somente em `Sha256Test`, como referência independente para validar os resultados.

## 1. Visão geral

Uma função hash recebe uma entrada de tamanho arbitrário e produz uma saída de tamanho fixo. No SHA-256, a saída possui 256 bits, normalmente exibidos como 64 caracteres hexadecimais. A mesma entrada sempre gera o mesmo resumo; uma alteração mínima na entrada normalmente muda muitos bits do resultado.

Hash não é criptografia reversível: não existe chave de descriptografia nem operação prática para recuperar a mensagem original. O SHA-256 é unidirecional e deve ser usado com cuidado em senhas, que precisam de funções específicas e lentas, como Argon2, scrypt ou bcrypt.

## 2. Estrutura de pastas

```text
projeto-sha256/
├── README.md
└── src/
    ├── main/java/br/edu/sha256/
    │   ├── Sha256.java
    │   └── Sha256Demo.java
    └── test/java/br/edu/sha256/
        └── Sha256Test.java
```

## 3. Como compilar e executar

Requer JDK 11 ou superior, pois o teste usa `String.repeat`.

```bash
javac -encoding UTF-8 -d out src/main/java/br/edu/sha256/*.java src/test/java/br/edu/sha256/Sha256Test.java
java -cp out br.edu.sha256.Sha256Test
java -cp out br.edu.sha256.Sha256Demo
```

## 4. Funcionamento do algoritmo

### Conversão, padding e comprimento

`Sha256.hash(String)` converte o texto para bytes UTF-8. O método `pad` acrescenta um byte `0x80`, que representa o bit 1 seguido de sete zeros, depois acrescenta zeros até restarem 8 bytes no final do bloco. Esses últimos 8 bytes guardam o tamanho original da mensagem em bits, como inteiro de 64 bits big-endian. Assim, o tamanho final é múltiplo de 512 bits.

### Blocos e expansão

Cada bloco tem 64 bytes e é dividido em 16 palavras de 32 bits (`W[0]` até `W[15]`). A expansão cria `W[16]` até `W[63]` com:

```text
W[t] = σ1(W[t-2]) + W[t-7] + σ0(W[t-15]) + W[t-16]
```

As somas usam o comportamento natural de inteiros Java de 32 bits: qualquer transporte que ultrapasse 32 bits é descartado, exatamente como no SHA-256.

### Estado inicial e constantes

O estado possui oito palavras `a` a `h`, inicializadas com os oito valores definidos pelo padrão. Cada rodada usa uma constante `K[t]`; as 64 constantes e os valores iniciais estão declarados como tabelas na classe.

### Funções bit a bit

`Ch(x,y,z)` escolhe cada bit de `y` ou `z` conforme o bit correspondente de `x`. `Maj(x,y,z)` retorna o bit que aparece na maioria das três palavras. `Σ0`, `Σ1`, `σ0` e `σ1` combinam rotações à direita, deslocamentos à direita e XOR.

Rotações e operações bit a bit são importantes porque o SHA-256 trabalha diretamente com palavras binárias de 32 bits. `Integer.rotateRight` preserva os bits que saem de uma extremidade, recolocando-os na outra; `>>>` desloca e preenche com zeros, mesmo quando o inteiro é negativo na representação Java.

### As 64 rodadas

Para cada `t` de 0 a 63:

```text
T1 = h + Σ1(e) + Ch(e,f,g) + K[t] + W[t]
T2 = Σ0(a) + Maj(a,b,c)
h = g; g = f; f = e; e = d + T1
d = c; c = b; b = a; a = T1 + T2
```

Depois das 64 rodadas, `a` até `h` são somados ao estado acumulado. Ao terminar todos os blocos, as oito palavras são concatenadas em hexadecimal, formando 256 bits.

## 5. Classes e métodos

- `Sha256`: classe utilitária pública. `hash(String)` trata texto UTF-8; `hash(byte[])` trata bytes; `pad` faz o preenchimento; `processBlock` executa as rodadas; `expandMessage` monta as 64 palavras; os métodos `choose`, `majority`, `bigSigma*` e `smallSigma*` representam as fórmulas oficiais; `toHex` produz o resultado final.
- `Sha256Demo`: mostra um exemplo e lê mensagens do usuário até receber `sair`.
- `Sha256Test`: verifica vetores conhecidos e compara mensagens longas e Unicode com `MessageDigest`.

## 6. Resultados esperados

```text
""            e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
"abc"         ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad
"hello world" b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9
```

O teste de `abc` é um vetor clássico. O caso longo usa `abc` repetido 1000 vezes e é comparado com a implementação confiável da biblioteca padrão. A validação não participa do cálculo principal.

## 7. Complexidade e limitações

Para uma mensagem de `n` bytes, o tempo é O(n), pois todos os bytes são processados em blocos. O espaço auxiliar é O(1) em relação ao tamanho da mensagem durante o processamento, além de um bloco preenchido e 64 palavras. O SHA-256 é resistente a colisões conhecido atualmente em nível prático, mas nenhum hash elimina matematicamente colisões. Não deve ser usado sozinho para armazenar senhas, e integridade com origem autenticada exige uma MAC, como HMAC-SHA-256.

Aplicações incluem verificação de integridade de arquivos, assinaturas digitais, certificados, HMAC, identificação de conteúdo e componentes de protocolos. Em sistemas reais, deve-se preferir uma biblioteca criptográfica auditada; esta implementação é para aprendizagem.

## 8. Possíveis erros durante a implementação

Os erros mais comuns são esquecer que o comprimento é em bits, gravá-lo em little-endian, omitir `& 0xff` ao montar uma palavra a partir de bytes, usar `>>` em vez de `>>>`, confundir rotação com deslocamento e atualizar as variáveis `a` até `h` na ordem errada. Outro erro é usar caracteres Java diretamente como se cada caractere fosse um byte; por isso o projeto explicita UTF-8.

## 9. Roteiro para apresentação e vídeo

1. Apresentem o objetivo: hash de 256 bits, sem descriptografia e sem `MessageDigest` no cálculo principal.
2. Mostrem a estrutura de arquivos e executem o teste, destacando os vetores vazio, `abc` e `hello world`.
3. Expliquem UTF-8, o `0x80`, os zeros e os 64 bits finais do padding. Desenhem um bloco de 512 bits.
4. Mostrem `expandMessage` e expliquem como 16 palavras se tornam 64.
5. Apresentem `Ch`, `Maj`, as quatro funções sigma e a diferença entre rotação e deslocamento.
6. Percorram `processBlock`, exibindo `T1`, `T2` e a atualização de `a` até `h`. Expliquem o overflow modular de 32 bits.
7. Mostrem `toHex` e o resumo final. Alterem uma letra da mensagem para demonstrar o efeito avalanche.
8. Executem `Sha256Demo`, digitem uma mensagem e depois comparem o resultado com uma ferramenta ou `MessageDigest` apenas para validação.
9. Encerrem com aplicações, complexidade, limitações e a recomendação de usar bibliotecas auditadas em produção.

Uma divisão simples entre a dupla é: uma pessoa explica conceitos, padding e expansão; a outra explica as rodadas, demonstra a execução e apresenta os testes.
