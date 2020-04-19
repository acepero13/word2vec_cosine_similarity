package de.semvox.word2vec.model

trait Queryable[A] {
  def contains(word: String): Boolean

  def get(word: String): Option[A]
}
