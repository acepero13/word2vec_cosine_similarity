package de.semvox.word2vec.reader

import de.semvox.word2vec.linealg.Vector
import de.semvox.word2vec.model.Queryable


case class Vocab(vectors: Map[String, Vector], override val size: Int) extends Queryable[String, Vector] {

  override def get(word: String): Option[Vector] = vectors.get(word)

  override def contains(word: String): Boolean = vectors.contains(word)

  override def iterator: Iterator[(String, Vector)] = vectors.iterator
}

