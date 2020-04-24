package de.semvox.word2vec.model

import de.semvox.word2vec.linealg.Vector
import de.semvox.word2vec.reader.Vocab
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Word2VecTest extends AnyFlatSpec with Matchers {
  "A word" should "nearestNeighbors" in {
    val v1 = Vector(Array(1.0f, 1.0f, 1.0f))
    val v4 = Vector(Array(12.9f, -2.5f, -2.14f))
    val v2 = Vector(Array(2.0f, 2.0f, 2.0f))
    val v3 = Vector(Array(2.5f, 5.1f, 2.0f))
    val map = Map("v1" -> v1, "v2" -> v2, "v3" -> v3, "v4" -> v4)
    val vec = Word2Vec(Vocab(map, 3), 3)
    val res: List[(String, Float)] = vec.rank("v1", Set("v2", "v4", "v3"))
    res match {
      case h1:: _ ::h3::_ => h1._1 should be("v2"); h3._1 should be("v4")
      case _ => true should be (false)
    }
  }

  "A word" should "return a ranked list of closest related words" in {
    val v1 = Vector(Array(1.0f, 1.0f, 1.0f))
    val v4 = Vector(Array(12.9f, -2.5f, -2.14f))
    val v2 = Vector(Array(2.0f, 2.0f, 2.0f))
    val v3 = Vector(Array(2.5f, 5.1f, 2.0f))
    val map = Map("v1" -> v1, "v2" -> v2, "v3" -> v3, "v4" -> v4)
    val vec = Word2Vec(Vocab(map, 3), 3)
    val res = vec.rank("v1", Set("v2", "v4", "v3"), 1)
    res match {
      case h1:: _ => h1._1 should be("v2")
      case _ => true should be (false)
    }
  }

  "Get Relevant topics from sentence" should "return ranked words" in {
    val v1 = Vector(Array(1.0f, 1.0f, 1.0f))
    val v4 = Vector(Array(12.9f, -2.5f, -2.14f))
    val v2 = Vector(Array(2.0f, 2.0f, 2.0f))
    val v3 = Vector(Array(2.5f, 5.1f, 2.0f))
    val v7 = Vector(Array(-1.8f, 0.57f, -2.0f))
    val sentence = Seq("v1", "v2", "v3")
    val possibleTopics = Set("v5", "v6", "v7")
    val map = Map("v1" -> v1, "v2" -> v2, "v3" -> v3, "v4" -> v4, "v5" -> v1 * 2, "v6" -> v2 * 4, "v7" -> v7)

    val vec = Word2Vec(Vocab(map, 3), 3)
    val res = vec.relatedTopicsFor(sentence, possibleTopics)
    res match {
      case h::_ => h._1 should be("v6")
      case _ => true should be (false)
    }
  }

  "A unexisting set of words" should "return empty" in {
    val v1 = Vector(Array(1.0f, 1.0f, 1.0f))
    val v4 = Vector(Array(12.9f, -2.5f, -2.14f))
    val v2 = Vector(Array(2.0f, 2.0f, 2.0f))
    val v3 = Vector(Array(2.5f, 5.1f, 2.0f))
    val map = Map("v1" -> v1, "v2" -> v2, "v3" -> v3, "v4" -> v4)
    val vec = Word2Vec(Vocab(map, 3), 3)
    val res = vec.relatedTopicsFor(Seq("v10", "v11"), Set("v2", "v4", "v3"))
    res.size should be (0)
  }
}
