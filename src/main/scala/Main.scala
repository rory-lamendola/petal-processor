import java.io._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

object Main {
  def main(args: Array[String]): Unit = {

    val input = Seq("transactions1.csv", "transactions2.csv", "transactions3.csv")

    val results: Future[Seq[Seq[User]]] = Future.sequence(input.map { file =>
      Future {
        val reader = new CSVReader(file)
        val transactions: Iterator[Transaction] = reader.readFiles()
        Transaction.traverseRows(transactions)
      }
    })

    while (!results.isCompleted) {
      Thread.sleep(100)
    }

    results.onComplete {
      case Success(results) => {
        val users = results.flatten
        val file = "output.txt"
        val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))
        for (user <- users) {
          writer.write(user.userID + "|" + user.numTransactions + "|" + user.balance + "|" + user.currentMin + "|" + user.currentMax + "\n")
        }
        writer.close()
      }
    }

    Thread.sleep(2000)


  }
}











