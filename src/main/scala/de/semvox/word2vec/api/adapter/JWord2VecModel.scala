package de.semvox.word2vec.api.adapter

trait JWord2VecModel {
  def relatedTopicsFor(sentence: java.util.List[java.lang.String],
                       possibleTopics: java.util.Set[java.lang.String]): java.util.Map[java.lang.String, java.lang.Float]

  def rank(word: java.lang.String,
           in: java.util.Set[java.lang.String],
           limit: java.lang.Integer ): java.util.Map[java.lang.String, java.lang.Float]
}
