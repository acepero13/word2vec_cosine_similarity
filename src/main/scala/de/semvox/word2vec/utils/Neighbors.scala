package de.semvox.word2vec.utils

import scala.collection.mutable

case class Neighbors(capacity: Int) {
  type Order = (String, Float)
  private val priorityList: mutable.ListBuffer[Order] = mutable.ListBuffer()


  def toList(): List[Order] = priorityList.sortBy(e => e._2).toList

  def +=(item: Order) = {
    priorityList+=item
    if(priorityList.size>= capacity) priorityList-=priorityList.sortBy(e => e._2).reverse.head
    priorityList
  }

  override def toString: String = priorityList.toList.toString()
}


