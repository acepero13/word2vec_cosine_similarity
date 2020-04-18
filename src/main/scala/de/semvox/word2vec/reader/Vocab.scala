package de.semvox.word2vec.reader
import de.semvox.word2vec.linealg.Vector
case class Vocab(vectors: Map[String, Vector], size: Int)

