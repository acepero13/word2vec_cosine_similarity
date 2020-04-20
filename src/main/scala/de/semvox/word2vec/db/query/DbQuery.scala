package de.semvox.word2vec.db.query

import de.semvox.word2vec.db.schema.Word2VecTable
import de.semvox.word2vec.linealg.Vector
import de.semvox.word2vec.model.Queryable
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
case class DbQuery() extends Queryable[Vector]{
  val db = Database.forConfig("embeddings")
  val embeddings = TableQuery[Word2VecTable]

  override def contains(word: String): Boolean = true

  override def get(word: String): Option[Vector] = {
    val start = System.nanoTime
    val res = db.run(embeddings.filter(_.word === word).result)


    val result = Await.result(db.run(embeddings.filter(_.word === word).result), 5 seconds).toList
    result match {
      case h::Nil => Some(Vector(h._2.split(" ").map(_.toFloat)))
      case _ => None
    }

  }
}
