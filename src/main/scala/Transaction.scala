import java.util.Date

case class Transaction(
  userID: String,
  accountID: String,
  amount: Double,
  date: Date
)

object Transaction {
  def convertTS(time: String): Date = {
    val timestampFormat = new java.text.SimpleDateFormat("yyyy-MM-dd")
    timestampFormat.parse(time)
  }

  def apply(userID: String, accountID: String, amount: String, date: String, transactionType: String): Transaction = {
    val typeConversion = if (transactionType == "debit") -1 else 1
    val convertedAmount = amount.toDouble * typeConversion

    Transaction(userID, accountID, convertedAmount, convertTS(date))
  }

  def getUserStats(dates: Map[Date, DateAmounts], userID: String): User = {
    val sorted = dates.toSeq.sortBy(_._1)

    val balances = sorted.map(date => date._2.balance)
    val runningBalances = balances.map {
      var sum = 0.toDouble; balance => {
        sum += balance; sum
      }
    }
    val counts = sorted.map(date => date._2.numTransactions)

    User(
      userID,
      counts.sum,
      runningBalances.last,
      math.min(0, runningBalances.min),
      math.max(0, runningBalances.max)
    )
  }

  def updateDate(currTransaction: Transaction, prevTransaction: Transaction, dates: Map[Date, DateAmounts]): Map[Date, DateAmounts] = {
    if (prevTransaction.userID == currTransaction.userID) {
      val currAmount = DateAmounts(1, currTransaction.amount)
      val prevAmount = dates.getOrElse(currTransaction.date, DateAmounts(0, 0))
      dates + (currTransaction.date ->
        DateAmounts(
          currAmount.numTransactions + prevAmount.numTransactions,
          currAmount.balance + prevAmount.balance
        ))
    }
    else dates
  }

  def processRow(currTransaction: Transaction, prevTransaction: Transaction, dates: Map[Date, DateAmounts], users: Seq[User]): (Map[Date, DateAmounts], Seq[User]) = {
    if (prevTransaction.userID == currTransaction.userID) {
      val updatedDates = Transaction.updateDate(currTransaction, prevTransaction, dates)
      (updatedDates, users)
    }
    else {
      val updatedUsers = users ++ Seq(Transaction.getUserStats(dates, prevTransaction.userID))
      val updatedDates = Transaction.updateDate(currTransaction, prevTransaction, Map())
      (updatedDates, updatedUsers)
    }
  }

  def traverseRowHelper(transactions: Iterator[Transaction], prevTransaction: Transaction, dates: Map[Date, DateAmounts], users: Seq[User]): Seq[User] = {
    if (transactions.hasNext) {
      val currTransaction = transactions.next()
      val dateUser = processRow(currTransaction, prevTransaction, dates, users)
      traverseRowHelper(transactions, currTransaction, dateUser._1, dateUser._2)
    }
    else {
      users ++ Seq(Transaction.getUserStats(dates, prevTransaction.userID))
    }
  }


  def traverseRows(transactions: Iterator[Transaction]): Seq[User] = {
    if (transactions.hasNext) {
      val prevTransaction = transactions.next
      val dateUser = Transaction.processRow(prevTransaction, prevTransaction, Map(), Seq())

      traverseRowHelper(transactions, prevTransaction, dateUser._1, dateUser._2)
    }
    else Nil
  }

}
