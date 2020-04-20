package de.semvox.word2vec.linealg

trait Operable {
  def +(another: Vector): Vector

  def -(another: Vector): Vector

  def *(another: Vector): Float

  def *(number: Float): Vector

  def norm(): Float
}
