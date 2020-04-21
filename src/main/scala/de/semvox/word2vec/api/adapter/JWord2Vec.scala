package de.semvox.word2vec.api.adapter

import java.{lang, util}

import de.semvox.word2vec.api.Word2VecModel

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable

case class JWord2Vec(adapter: Word2VecModel) extends JWord2VecModel {

  override def relatedTopicsFor(sentence: util.List[String], possibleTopics: util.Set[String]): util.Map[String, lang.Float] = {
    val relatedTopics = adapter.relatedTopicsFor(sentence, possibleTopics.toSet).map(r => r._1 -> float2Float(r._2))
    mutable.LinkedHashMap(relatedTopics:_*)
  }

  override def rank(word: String, in: util.Set[String], N: Integer): util.Map[String, lang.Float] = {
    val ranked = adapter.rank(word, in.toSet, N).map(r => r._1 -> float2Float(r._2))
    mutable.LinkedHashMap(ranked:_*)
  }
}
