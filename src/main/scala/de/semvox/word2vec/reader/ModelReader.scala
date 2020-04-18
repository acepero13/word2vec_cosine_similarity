package de.semvox.word2vec.reader

trait ModelReader {
  def load(): Option[Vocab]
}
