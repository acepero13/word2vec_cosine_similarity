package de.semvox.word2vec.reader
import de.semvox.word2vec.linealg.Vector
import de.semvox.word2vec.model.Queryable


case class Vocab(vectors: Map[String, Vector], size: Int) extends  Queryable[Vector] {
  override def get(word: String): Option[Vector] = vectors.get(word)
}

