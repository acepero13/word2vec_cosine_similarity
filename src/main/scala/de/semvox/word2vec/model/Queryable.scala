package de.semvox.word2vec.model

trait Queryable[A] {
  def get(word: String): Option[A]
}
