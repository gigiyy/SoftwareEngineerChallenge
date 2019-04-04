package queue

trait Queue[T] {
  def isEmpty: Boolean

  def enQueue(t: T): Queue[T]

  // Removes the element at the beginning of the immutable queue, and returns the new queue.
  def deQueue(): Queue[T]

  def head: Option[T]
}

object Queue {
  def empty[T]: Queue[T] = new QueueImpl[T](Nil, Nil)
}

class QueueImpl[T](val in: List[T], val out: List[T]) extends Queue[T] {
  override def isEmpty: Boolean = in.isEmpty && out.isEmpty

  override def enQueue(t: T): Queue[T] = new QueueImpl[T](t :: in, out)

  override def deQueue(): Queue[T] = out match {
    case Nil if in.nonEmpty =>
      val rev = in.reverse
      new QueueImpl[T](Nil, rev.tail)
    case _ :: tl => new QueueImpl[T](in, tl)
    case _ => throw new NoSuchElementException("empty queue")
  }

  override def head: Option[T] = out match {
    case Nil => in.lastOption
    case hd :: _ => Some(hd)
  }
}
