package de.semvox.word2vec.model

import de.semvox.word2vec.api.Word2VecModel
import de.semvox.word2vec.db.schema.Word2VecDB
import de.semvox.word2vec.linealg.Vector
import de.semvox.word2vec.reader.VecReader
import de.semvox.word2vec.utils.Neighbors

import scala.language.implicitConversions

case class Word2Vec(vocab: Queryable[String, Vector], vecSize: Int) extends Word2VecModel {
  def distance(sentence: List[String]) = {
    sentence
      .map(s => vocab.get(s))
      .filter(o => o.isDefined)
      .reduceOption((acc, oVec) => acc.flatMap(a => oVec.map(v => a + v)))
      .map(v => nearestNeighbor(v))
      .getOrElse(List())
  }

  def relatedTopicsFor(sentence: Seq[String], possibleTopics: Set[String]): List[(String, Float)] = {
    sentence
      .map(s => vocab.get(s))
      .filter(o => o.isDefined)
      .reduceOption((acc, oVec) => acc.flatMap(a => oVec.map(v => a + v)))
      .map(v => nearestNeighbor(v, possibleTopics))
      .getOrElse(List())
  }

  private def nearestNeighbor(from: Option[Vector], in: Set[String]): List[(String, Float)] = {
    in
      .map(w => (w, vocab.get(w).flatMap(v => from.map(wv => wv.cosine(v))).getOrElse(-2.0f)))
      .filter(_._2 != -2.0f)
      .toList
      .sortBy(_._2)
      .reverse
  }

  private def nearestNeighbor(from: Option[Vector]): List[(String, Float)] = {

    @scala.annotation.tailrec
    def nearestNeighbor(it: Iterator[(String, Vector)], neighbors: Neighbors):  Neighbors = {
      if(!it.hasNext) neighbors
      else  {
        val entry = it.next()
        neighbors+= (entry._1, from.map(f => f.distanceTo(entry._2)).getOrElse(-2.0f))
        nearestNeighbor(it, neighbors)
      }
    }
    nearestNeighbor(vocab.iterator, Neighbors(40)).toList

  }

  def rank(word: String, in: Set[String], limit: Int = 40): List[(String, Float)] = {
    nearestNeighbor(vocab.get(word), in).take(limit)
  }

}

object Word2Vec {
  def apply(filename: String, limit: Integer = Int.MaxValue, normalize: Boolean = true): Option[Word2Vec] = {

    def readFromVecFile = {
      val reader = VecReader(filename, Int.MaxValue, normalize = true, oldFormat = false)
      for (v <- reader.load()) yield {
        new Word2Vec(v, v.size)
      }
    }

    def readFromDB() = Word2VecDB(filename)

    if (filename.endsWith(".db")) Some(readFromDB()) else readFromVecFile

  }
}

object RunWord2Vec {
  implicit def reportElapsed(elapsed: Double): Unit = println(s"completed in $elapsed s")

  def timed[T](f: => T): T = {
    val start = System.nanoTime
    try f finally reportElapsed((System.nanoTime - start) / 1000000000)
  }

  def main(args: Array[String]): Unit = {
    timed(execute())
  }

  private def execute(): Unit = {
    // val modelFile = "/home/alvaro/Documents/Projects/Coursera/Ml/cc.de.300.vec"
    val filename = "/home/alvaro/Documents/Projects/Coursera/Ml/embeddings.db"
    val model = Word2Vec(filename)

    println("--------------")

    val topics2 = Set("Lebensentscheidungen",
      "Lebensbewältigung", "Beziehungskonflikte", "psychologisch",
      "physiologisch", "Herkunftsfamilie", "Sozialaktivitäten", "Freizeitaktivitäten",
      "Haushalt", "Behandlung", "Übung", "Diät", "Suizid",

      "Arbeit", "Freizeit", "Krankenhaus", "Schönheit", "Bücher", "Comics", "Kultur", "Fiktion", "Film",
      "Essen", "Gaming", "Humor", "Macher", "Musik", "Fotografie", "Podcasts", "Lyrik", "Medien", "Computer",
      "Sport", "Mode", "Verbrechen", "TV", "Schreiben", "Biotechnologie", "Business", "Design",
      "Ökonomie", "Freiberuflich", "Führungskraft", "Marketing", "Produktmanagement", "Produktivität",
      "Startups", "Risikokapital", "Zugänglichkeit", "Softwareentwicklung", "AI", "Blockchain", "Kryptowährung",
      "Wissenschaft", "Digitales", "Hilfsmittel", "Mathematik", "Neurowissenschaft", "Programmieren", "Weltall",
      "Technologie", "Sucht", "Cannabis", "Kreativität", "Behinderung", "Familie", "Fitness", "Gesundheit",
      "Lebensstil", "Achtsamkeit", "Geld", "Outdoor", "Elternschaft", "Haustiere", "Psychedelisches",
      "Psychologie", "Beziehungen", "Selbst", "Sexualität", "Spiritualität", "Reisen", "Grundeinkommen",
      "Städte", "Bildung", "Coronavirus", "Umwelt", "Gleichheit", "Zukunft", "Waffenkontrolle", "Geschichte",
      "Einwanderung", "Gerechtigkeit", "Sprache", "LGBTQIA", "Medien", "Philosophie", "Politik", "Privatsphäre",
      "Rasse", "Religion", "Gesellschaft", "Transportwesen", "Frauen", "Welt", "Stress", "Angst"
    )

    val start = System.nanoTime

    val results = model.get.distance(List("Frankreich"))
    results.foreach(r => println((r._1 + " -> " + r._2)))
    println(" Time needed to calculate distance was: " +(System.nanoTime - start) / 1000000000)

    @scala.annotation.tailrec
    def askUser(): Unit = {
      val sentence = io.StdIn.readLine("Enter a sentece to analyze ")
      if (sentence == "exit") return

      model.foreach(m => m.relatedTopicsFor(sentence
        .split(" ")
        .filter(_.length > 3)
        .toSeq, topics2)
        .take(5)
        .foreach(p => println(p)))
      askUser()
    }

    askUser()


  }
}
