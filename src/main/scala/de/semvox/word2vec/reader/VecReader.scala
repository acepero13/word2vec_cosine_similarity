package de.semvox.word2vec.reader

import de.semvox.word2vec.utils.VectorHelper

import scala.collection.mutable.ListBuffer
import scala.io.BufferedSource

case class VecReader(filename: String, limit: Integer, normalize: Boolean, oldFormat: Boolean) extends ModelReader {
  override def load(): Option[Vocab] = {
    val source: BufferedSource = io.Source.fromFile(filename)
    val header = source.getLines().take(1).next().stripLineEnd.split(" ").toList
    val wordPairs = new ListBuffer[(String, Array[Float])]

    val (vecSize: Int, numWords: Int) = getModelStructure(header)

    val normalizer = if (normalize) (vector: Array[Float]) => VectorHelper.normVector(vector) else (vector: Array[Float]) => vector

    def process(line: String) = {
      val vector = line.stripLineEnd.split(" ")
      wordPairs += readTxtVector(vector.head, vector.tail.toList, normalizer)
    }
    val howMany = (numWords * 0.75).toInt

    source.getLines.take(howMany).foreach(process)

    source.close
    Some(Vocab(wordPairs.toMap, vecSize))
  }

  def getModelStructure(header: List[String]):(Int, Int) = {
    header match {
      case wordNumber :: size :: Nil => (size.toInt, Math.min(wordNumber.toInt, limit))
      case _ => ( 300, limit.toInt)
    }
  }

  def readTxtVector(word: String, tokens: List[String], normalizer: Array[Float] => Array[Float]): (String, Array[Float]) = {
    val vector = tokens.map(t => t.toFloat).toArray
    word -> normalizer(vector)
  }
}
