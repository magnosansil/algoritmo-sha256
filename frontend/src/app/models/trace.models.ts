export interface Sha256Step {
  id: string;
  title: string;
  description: string;
}

export interface Sha256Round {
  round: number;
  constant: string;
  word: string;
  a: string;
  b: string;
  c: string;
  d: string;
  e: string;
  f: string;
  g: string;
  h: string;
  t1: string;
  t2: string;
  nextA: string;
  nextB: string;
  nextC: string;
  nextD: string;
  nextE: string;
  nextF: string;
  nextG: string;
  nextH: string;
}

export interface Sha256Block {
  index: number;
  bytesHex: string;
  initialHash: string;
  finalHash: string;
  words: string[];
  rounds: Sha256Round[];
}

export interface Sha256Trace {
  message: string;
  messageUtf8Hex: string;
  messageByteLength: number;
  messageBitLength: number;
  paddedMessageHex: string;
  paddedByteLength: number;
  blocks: Sha256Block[];
  steps: Sha256Step[];
  hash: string;
}
