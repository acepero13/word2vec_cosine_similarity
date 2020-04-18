package de.semvox.word2vec.model
import de.semvox.word2vec.linealg.Vector

case class Word2Vec(vocab: Map[String, Vector], vecSize: Int, normalized: Boolean) extends Queryable[Vector] {

  override def get(word: String): Option[Vector] = {
    vocab.get(word)
  }

  def nearestNeighbor(word: String, in: Set[String]):  List[(String, Float)] = {
    val wordVec = get(word)
    in
      .map(w => (w, vocab.get(w).flatMap(v => wordVec.map(wv => wv.cosine(v))).getOrElse(-2.0f)))
      .filter(r => r._2 != -2.0f)
      .toList
      .sortBy(s => s._2)
      .reverse
  }

  def rank(word: String, in: Set[String], N: Int): List[(String, Float)] = {
    nearestNeighbor(word, in).take(N)
  }


}
