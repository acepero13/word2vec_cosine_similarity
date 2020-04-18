package de.semvox.word2vec.linealg

case class Vector(components: Array[Float]) extends Operable {
  def normalize(): Vector = {
    val normV = norm()
    Vector(components.map(_ / normV))
  }

  def cosine(another: Vector): Float = {
    assert(this.components.length == another.components.length, "Uneven vectors!")
    (this * another) / (this.norm * another.norm)
  }

  override def +(another: Vector): Vector = vecOp(another, _ + _)

  override def -(another: Vector): Vector = vecOp(another, _ - _)

  override def *(another: Vector): Float = op(another, _ * _).sum

  override def norm(): Float = {
    Math.sqrt(
      this.components
        .map(a => a * a)
        .sum
    ).toFloat
  }

  private def vecOp(another: Vector, operation: (Float, Float) => Float): Vector = {
    Vector(op(another, operation))
  }

  private def op(another: Vector, op: (Float, Float) => Float): Array[Float] = {
    this.components
      .zip(another.components)
      .map(c => op(c._1, c._2))
  }

  override def equals(that: Any): Boolean = {
    that match {
      case that: Vector => that.asInstanceOf[Vector].components
        .zip(this.components)
        .forall(c => c._1 == c._2)
      case _ => false
    }
  }

  override def toString: String = {
    components.toString
  }
}