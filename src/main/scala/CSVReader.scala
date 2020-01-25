import java.util.regex.Pattern

import scala.io.Source

class CSVReader(val fileName: String) {
  def readFiles(): Iterator[Transaction] = {
    for {
      line <- Source.fromFile(fileName).getLines().drop(1)
      replaced = line.replace("\\\\", "")
      values = replaced.split("(?<!\\\\)" + Pattern.quote("|")).map(_.trim)
    } yield {
      Transaction(values(0), values(1), values(2), values(4), values(5))}
  }
}