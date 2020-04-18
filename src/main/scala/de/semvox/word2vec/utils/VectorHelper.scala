package de.semvox.word2vec.utils

object VectorHelper {
  /** Compute the magnitude of the vector.
   *
   * @param vec The vector.
   * @return The magnitude of the vector.
   */
  def magnitude(vec: Array[Float]): Double = {
    math.sqrt(vec.toStream.map(a => a * a).sum)
  }

  def normVector(vec: Array[Float]): Array[Float] = {
    val norm = magnitude(vec)
    vec.map(a => (a / norm).toFloat)
  }
}