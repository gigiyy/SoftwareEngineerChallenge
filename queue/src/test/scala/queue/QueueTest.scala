package queue

import org.scalatest._

class QueueTest extends FunSuite with Matchers with OptionValues with Inside with Inspectors {

  test("new queue is empty") {
    val q = Queue.empty
    assert(q.isEmpty === true)
  }

  test("enqueue and deque all") {
    val values = Array(1, 2, 3, 4, 5)
    var q = Queue.empty[Int]
    for (v <- values) {
      q = q.enQueue(v)
    }
    val result = (for (_ <- 1 to 5) yield {
      val hd = q.head
      q = q.deQueue()
      hd
    }).flatten.toArray
    assert(result === values)
    assert(q.isEmpty)
    assertThrows[NoSuchElementException] {
      q.deQueue()
    }
  }

  test("enqueue returns a new queue, and old one not affected") {
    val q1 = Queue.empty[Int]
    assert(q1.isEmpty)
    val q2 = q1.enQueue(1)
    assert(q1.isEmpty)
    assert(q2.isEmpty === false)
    val q3 = q2.enQueue(2)
    val q4 = q1.enQueue(3)
    assert(q2.head === Some(1))
    assert(q3.head === Some(1))
    assert(q4.head === Some(3))
    assert(q1.head === None)
  }

  test("same for dequeue") {
    var q = Queue.empty[Int]
    for (i <- 1 to 5) {
      q = q.enQueue(i)
    }

    val q1 = q
    assert(q1.head === Some(1))
    val q2 = q1.deQueue()
    assert(q1.head === Some(1))
    assert(q2.head === Some(2))
    val q3 = q2.deQueue()
    assert(q1.head === Some(1))
    assert(q2.head === Some(2))
    assert(q3.head === Some(3))
  }
}
