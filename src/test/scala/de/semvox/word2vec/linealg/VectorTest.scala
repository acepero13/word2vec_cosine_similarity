package de.semvox.word2vec.linealg

import org.scalatest._

class VectorTest extends FlatSpec with Matchers {
  "Calculate cosine of two Vectors" should "calculate cosine" in {
    val v1 = Vector(Array(2.0f, 2.0f, 2.0f))
    val v2 = Vector(Array(1.0f, 1.0f, 1.0f))
    v2.cosine(v1) should be(1f)
  }

  "Sum of two Vectors" should "give vector with their sum" in {
    val v1 = Vector(Array(2.0f, 2.0f))
    val v2 = Vector(Array(1.0f, 1.0f))
    v1 + v2 should be(Vector(Array(3.0f, 3.0f)))
  }

  "Multiply two Vectors " should "return float" in {
    val v1 = Vector(Array(1.0f, 1.0f))
    val v2 = Vector(Array(2.0f, 2.0f))
    v1 * v2 should be(4)
  }

  "Calculate norm of a Vector" should "return norm" in {
    val v2 = Vector(Array(3.0f, 4.0f))
    v2.norm should be(5)
  }

  "Substract two vectors" should "give substracted" in {
    val v1 = Vector(Array(1.0f, 1.0f))
    val v2 = Vector(Array(2.0f, 2.0f))
    v2 - v1 should be(v1)
  }

  "Multiply vector by a number" should "give new vector" in {
    val v1 = Vector(Array(1.0f, 1.0f))
    val expected = Vector(Array(2.0f, 2.0f))
    v1 * 2.0f should be(expected)
  }

  "Calculates norm of a vector" should "give the norm" in {
    val v1 = Vector(Array(3, 4))
    val expected = Vector(Array(0.6f, 0.8f))
    v1.normalize() should be (expected)
  }

  "Tries to compare vector with number" should "give false" in {
    val v1 = Vector(Array(3, 4))
    v1.equals(1) should be (false)
  }

  "Operation with uneven vectors" should "substract another vector" in {
    val v1 = Vector(Array(1.0f, 1.0f, 1.0f))
    val v2 = Vector(Array(2.0f, 2.0f))
    intercept[AssertionError] {
      v1.cosine(v2)
    }
  }
}

