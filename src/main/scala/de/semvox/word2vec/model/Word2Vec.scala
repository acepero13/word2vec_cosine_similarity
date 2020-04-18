package de.semvox.word2vec.model
import de.semvox.word2vec.linealg.Vector
import de.semvox.word2vec.reader.VecReader

case class Word2Vec(vocab: Queryable[Vector], vecSize: Int)  {

  def get(word: String): Option[Vector] = {
    vocab.get(word)
  }

  def nearestNeighbor(word: String, in: Set[String]):  List[(String, Float)] = {
    val wordVec = get(word)
    in
      .map(w => (w, vocab.get(w).flatMap(v => wordVec.map(wv => wv.cosine(v))).getOrElse(-2.0f)))
      .filter(_._2 != -2.0f)
      .toList
      .sortBy(_._2)
      .reverse
  }

  def rank(word: String, in: Set[String], N: Int = 40): List[(String, Float)] = {
    nearestNeighbor(word, in).take(N)
  }

}

object Word2Vec {
  def apply(filename: String, limit: Integer = Int.MaxValue, normalize: Boolean = true): Option[Word2Vec] = {

    val reader = VecReader(filename, Int.MaxValue, true, false)
    for (v <- reader.load()) yield {
      new Word2Vec(v, v.size)
    }
  }
}

object RunWord2Vec {
  implicit def reportElapsed(elapsed: Double): Unit = println(s"completed in $elapsed s")

  def timed[T](f: => T): T ={
    val start = System.nanoTime
    try f finally reportElapsed((System.nanoTime - start) / 1000000000 )
  }
  def main(args: Array[String]): Unit = {
    timed(execute)
  }

  private def execute = {
    val modelFile = "/home/alvaro/Documents/Projects/Scala/cc.de.10.vec"
    var model = Word2Vec(modelFile)
    model.map(m => m.nearestNeighbor("Apfel", Set("Orange", "Limo", "Kopfsalat", "Kuba")).foreach(p => println(p)))
    println("--------------")
    timed(
      model.map(m => m.nearestNeighbor("Kuba", Set("Deutschland", "Spanien", "Costa Rica")).foreach(p => println(p)))
    )
  }
}
