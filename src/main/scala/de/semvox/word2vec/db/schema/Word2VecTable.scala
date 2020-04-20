package de.semvox.word2vec.db.schema
import de.semvox.word2vec.db.query.DbQuery
import de.semvox.word2vec.model.Word2Vec
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
class Word2VecTable(tag: Tag) extends Table[(String, String)](tag, "embeddings") {
  def word = column[String]("word", O.PrimaryKey)
  def embedding = column[String]("embeddings")
  override def * = (word, embedding)
}

object Word2VecDB {
  def apply() = {
    new Word2Vec(DbQuery(), 300)
  }
}

object DBGenerator extends App {
  val db = Database.forConfig("embeddings")
  val embeddings = TableQuery[Word2VecTable]
  val toSearch = "Apfel"
  val start = System.nanoTime
  val res = db.run(embeddings.filter(_.word === toSearch).result)
  res.onSuccess {case s => println(s); println("It took " + (System.nanoTime - start) / 1000000000)}
  res.onFailure {case s => print(s)}

  Thread.sleep(50000)

  //res.foreach((w) => println(w._1))
}

