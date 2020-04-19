package de.semvox.word2vec.reader

import de.semvox.word2vec.linealg.Vector

import scala.io.BufferedSource

case class VecReader(filename: String, limit: Integer, normalize: Boolean, oldFormat: Boolean) extends ModelReader {
  override def load(): Option[Vocab] = {
    val source: BufferedSource = io.Source.fromFile(filename)
    val header = source.getLines().take(1).next().stripLineEnd.split(" ").toList

    val (vecSize: Int, numWords: Int) = getModelStructure(header)

    def process(line: String) = {
      val vector = line.stripLineEnd.split(" ")
      readTxtVector(vector.head, vector.tail.toList, normalize)
    }

    val wPairs = source.getLines.toStream.map(process).toList

    source.close
    Some(Vocab(wPairs.toMap, vecSize))
  }

  def getModelStructure(header: List[String]):(Int, Int) = {
    header match {
      case wordNumber :: size :: Nil => (size.toInt, Math.min(wordNumber.toInt, limit))
      case _ => ( 300, limit.toInt)
    }
  }

  def readTxtVector(word: String, tokens: List[String], normalize: Boolean): (String, Vector) = {
    if(normalize)
      word -> Vector(tokens.map(t => t.toFloat).toArray).normalize()
    else
      word -> Vector(tokens.map(t => t.toFloat).toArray)
  }
}
