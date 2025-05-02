package pkpm.company.automation.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import pkpm.company.automation.models.BookSnapshot;
import pkpm.company.automation.models.Report;
@Slf4j
public class GraphExecutionReport {

  public List<Report> getDate(BookSnapshot bs) {
    Map<String, List<List<Cell>>> columnsOfBook = bs.getColumnsOfBook();
    return columnsOfBook.keySet().stream()
        .filter(key -> columnsOfBook.get(key).size() > 10 && columnsOfBook.get(key).get(9) != null && !columnsOfBook.get(key).get(9).isEmpty()
            && columnsOfBook.get(key).get(9).get(2) != null && columnsOfBook.get(key).get(9).get(3) != null)
        .map(key -> new Report(key,
            getNumOfTasks(columnsOfBook.get(key)),
            getNumOfCompleted(columnsOfBook.get(key)),
            getNumOfUncompleted(columnsOfBook.get(key)),
            getNumOfPosition(getNumOfCompleted(columnsOfBook.get(key)),
                getNumOfUncompleted(columnsOfBook.get(key)))))
        .collect(Collectors.toList());
  }

  private int getNumOfTasks(List<List<Cell>> sheet){
    return (int) sheet.get(1).stream()
        .skip(4)
        .filter(e -> e != null && !e.toString().contains("2025") && !e.toString().trim().isEmpty())
        .count();
  }

  private int getNumOfCompleted(List<List<Cell>> sheet) {
    return (int)(sheet.get(9).get(2).getNumericCellValue());
  }

  private int getNumOfUncompleted(List<List<Cell>> sheet) {
    return (int)(sheet.get(9).get(3).getNumericCellValue());
  }

  private int getNumOfPosition(int numOfCompleted, int numOfUnCompleted) {
    return numOfCompleted + numOfUnCompleted;
  }

  public void printResult(List<Report> results) {
    results.forEach((res) -> log.info(res.toString() + "\n"));
  }
}
