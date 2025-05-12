package pkpm.company.automation.services;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import pkpm.company.automation.models.BookSnapshot;
import pkpm.company.automation.models.Report;
import pkpm.company.automation.utils.MessageWriter;

@Slf4j
public class GraphExecutionReport {

  /**
   * Формує дані для звіту по кожній сторінці книги через Report
   *
   * @param bs - останній знімок книги
   * @return - список Report з останнього знімку книги
   */
  public List<Report> getDateForGeneralReport(BookSnapshot bs) {
    Map<String, List<List<Cell>>> columnsOfBook = bs.getColumnsOfBook();
    return columnsOfBook.keySet().stream()
        .filter(key -> columnsOfBook.get(key).size() > 10 && columnsOfBook.get(key).get(9) != null
            && !columnsOfBook.get(key).get(9).isEmpty()
            && columnsOfBook.get(key).get(9).get(2) != null
            && columnsOfBook.get(key).get(9).get(3) != null)
        .map(key -> new Report(key,
            getNumOfNames(columnsOfBook.get(key)),
            getNumOfUncompleted(columnsOfBook.get(key)),
            getNumOfCompleted(columnsOfBook.get(key)),
            getNumOfPosition(getNumOfCompleted(columnsOfBook.get(key)),
                getNumOfUncompleted(columnsOfBook.get(key)))))
        .collect(Collectors.toList());
  }

  public void getDateForEmployeeReport(){
    // тут повинен бути код, який буде формувати дані для звіту по співробітниках
  }

  private int getNumOfNames(List<List<Cell>> sheet) {
    return (int) sheet.get(1).stream()
        .skip(4)
        .filter(e -> e != null && !e.toString().contains("2025") && !e.toString().trim().isEmpty())
        .count();
  }

  private int getNumOfUncompleted(List<List<Cell>> sheet) {
    return (int) (sheet.get(9).get(2).getNumericCellValue());
  }

  private int getNumOfCompleted(List<List<Cell>> sheet) {
    return (int) (sheet.get(9).get(3).getNumericCellValue());
  }

  private int getNumOfPosition(int numOfCompleted, int numOfUnCompleted) {
    return numOfCompleted + numOfUnCompleted;
  }

  public void printResult(List<Report> results) {
    log.info("|   Name  | Pos | Task | Uncompl | Compl |");
    results.stream()
        .sorted(Comparator.comparing(Report::getNumOfUncompleted))
        .forEach((res) -> log.info(res + "\n"));
  }

  public void writeResultsToFile(String fileName, List<Report> results) {
    List<String> resultsLines = results.stream()
        .sorted(Comparator.comparing(Report::getNumOfUncompleted))
        .map(Report::toString)
        .collect(Collectors.toList());
    MessageWriter.writeList(fileName, resultsLines);
  }

  public String writeResultToString(List<Report> results) {
    return results.stream()
        .sorted(Comparator.comparing(Report::getNumOfUncompleted))
        .map(Report::toString)
        .collect(Collectors.joining("\n"));
  }
}
