package de.semvox.word2vec.db.query

import de.semvox.word2vec.db.schema.Word2VecTable
import de.semvox.word2vec.linealg.Vector
import de.semvox.word2vec.model.Queryable
import slick.jdbc.SQLiteProfile.api._

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

case class DbQuery(databaseFilename: String) extends Queryable[String, Vector] {
  private val driver = "org.sqlite.JDBC"
  private val urlPrefix = "jdbc:sqlite:"

  private def buildDatabaseUrl(): String = urlPrefix + databaseFilename

  private val db = Database.forURL(buildDatabaseUrl(), driver = this.driver, keepAliveConnection = true)
  private val embeddings = TableQuery[Word2VecTable]
  private val cachedWords: mutable.ListMap[String, Option[Vector]] = new mutable.ListMap[String, Option[Vector]]
  private val dbIterator = DBIterator(embeddings)

  override def contains(word: String): Boolean = true

  override def get(word: String): Option[Vector] = {
    if (cachedWords.contains(word)) cachedWords(word) else getFromDb(word)
  }

  private def getFromDb(word: String) = {
    val result = Await.result(db.run(embeddings.filter(_.word === word).result), 5 seconds).toList
    val vectorResult = result match {
      case h :: _ => Some(Vector.from(h._2))
      case _ => None
    }
    cachedWords.put(word, vectorResult)
    vectorResult
  }

  override def iterator: Iterator[(String, Vector)] = dbIterator


  private case class DBIterator(embeddings: TableQuery[Word2VecTable]) extends Iterator[(String, Vector)] {
    private var cachedElements: mutable.Queue[(String, Vector)] = mutable.Queue()
    private val BUFFER_SIZE: Int = 100000
    private var offset = 0

    override def hasNext: Boolean = {
      if (cachedElements.isEmpty ) {
        println("Loading " + BUFFER_SIZE + " elements..." + " offset: " + offset)
        cachedElements = loadBatch()
        println("Loaded " + cachedElements.size)
      }
      cachedElements.size > 0
    }

    override def next(): (String, Vector) = if (hasNext) cachedElements.dequeue() else loadBatchAndDeque()

    private def loadBatchAndDeque(): (String, Vector) = {
      cachedElements = loadBatch()
      cachedElements.dequeue()
    }

    private def loadBatch() = {

      val query = embeddings
        .filter(_.id > offset)
        .filter(_.id < offset + BUFFER_SIZE)
        .result

      offset += BUFFER_SIZE

      mutable.Queue(Await.result(
        db.run(query), 25 seconds).toList
        .map(e => (e._1, Vector.from(e._2)))
        : _*)
    }

  }


}
