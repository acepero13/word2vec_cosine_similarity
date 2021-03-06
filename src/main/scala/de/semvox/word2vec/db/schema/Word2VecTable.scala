package de.semvox.word2vec.db.schema

import de.semvox.word2vec.db.query.DbQuery
import de.semvox.word2vec.model.Word2Vec
import slick.jdbc.SQLiteProfile.api._

class Word2VecTable(tag: Tag) extends Table[(String, String, Int)](tag, "embeddings") {
  override def * = (word, embedding, id)

  def word = column[String]("word", O.PrimaryKey)

  def embedding = column[String]("embeddings")
  def id = column[Int]("id")
}

object Word2VecDB {
  def apply(filename: String): Word2Vec = {
    new Word2Vec(DbQuery(filename), 300)
  }
}



