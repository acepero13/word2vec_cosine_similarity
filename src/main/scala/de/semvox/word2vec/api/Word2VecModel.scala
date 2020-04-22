package de.semvox.word2vec.api

trait Word2VecModel {
  def relatedTopicsFor(sentence: Seq[String], possibleTopics: Set[String]): List[(String, Float)]

  def rank(word: String, in: Set[String], limit: Int = 40): List[(String, Float)]
}
