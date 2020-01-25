import java.util.Date

import org.scalatest.FunSuite


class Test extends FunSuite  {



  val timestampFormat = new java.text.SimpleDateFormat("yyyy-MM-dd")

  val t1 = Transaction("1", "1", "5", "2018-01-01", "credit")
  val t2 = Transaction("1", "1", "5", "2018-02-01", "credit")




  val d1 = Map(timestampFormat.parse("2018-01-01") -> DateAmounts(1, 10))

  val d2 = Map(
    timestampFormat.parse("2018-10-15") -> DateAmounts(1, 5),
    timestampFormat.parse("2018-10-16") -> DateAmounts(1, 3),
    timestampFormat.parse("2018-10-18") -> DateAmounts(1, -6),
    timestampFormat.parse("2018-10-19") -> DateAmounts(2, 8),
    timestampFormat.parse("2018-10-20") -> DateAmounts(1, -1)
  )



  test("updateDate") {
    assert(Transaction.updateDate(t1, t1, d1) === Map(timestampFormat.parse("2018-01-01") -> DateAmounts(2, 15)))
    assert(Transaction.updateDate(t2, t2, d1) === Map(timestampFormat.parse("2018-01-01") -> DateAmounts(1, 10), timestampFormat.parse("2018-02-01") -> DateAmounts(1, 5)))
  }

  test("getUserStats") {
    assert(Transaction.getUserStats(d1, "1") === User("1", 1, 10, 0, 10))
    assert(Transaction.getUserStats(d2, "1") === User("1", 6, 9, 0, 10))
  }




}

