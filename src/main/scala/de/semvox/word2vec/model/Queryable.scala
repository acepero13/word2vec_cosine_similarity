package de.semvox.word2vec.model

trait Queryable[A, B] extends Iterable[(A, B)]{
  def contains(word: A): Boolean

  def get(word: A): Option[B]
}
