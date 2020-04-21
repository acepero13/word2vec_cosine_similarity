package de.semvox.word2vec.db.schema

import de.semvox.word2vec.db.query.DbQuery
import de.semvox.word2vec.model.Word2Vec
import slick.jdbc.SQLiteProfile.api._

class Word2VecTable(tag: Tag) extends Table[(String, String)](tag, "embeddings") {
  override def * = (word, embedding)

  def word = column[String]("word", O.PrimaryKey)

  def embedding = column[String]("embeddings")
}

object Word2VecDB {
  def apply(filename: String): Word2Vec = {
    new Word2Vec(DbQuery(filename), 300)
  }
}



