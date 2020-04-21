package de.semvox.word2vec.db.query

import de.semvox.word2vec.db.schema.Word2VecTable
import de.semvox.word2vec.linealg.Vector
import de.semvox.word2vec.model.Queryable
import slick.jdbc.SQLiteProfile.api._

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

case class DbQuery(databaseFilename: String) extends Queryable[Vector] {
  private val driver = "org.sqlite.JDBC"
  private val urlPrefix = "jdbc:sqlite:"

  private def buildDatabaseUrl(): String = urlPrefix + databaseFilename

  private val db = Database.forURL(buildDatabaseUrl(), driver = this.driver, keepAliveConnection = true)
  private val embeddings = TableQuery[Word2VecTable]
  private val cachedWords: mutable.ListMap[String, Option[Vector]] = new mutable.ListMap[String, Option[Vector]]

  override def contains(word: String): Boolean = true

  override def get(word: String): Option[Vector] = {
    if(cachedWords.contains(word)) cachedWords(word) else getFromDb(word)
  }

  private def getFromDb(word: String) = {
    val result = Await.result(db.run(embeddings.filter(_.word === word).result), 5 seconds).toList
    val vectorResult = result match {
      case h :: Nil => Some(Vector(h._2.split(" ").map(_.toFloat)))
      case _ => None
    }

    cachedWords.put(word, vectorResult)
    vectorResult
  }
}
