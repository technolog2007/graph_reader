package pkpm.company.automation.services;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import pkpm.company.automation.models.BookSnapshot;
import pkpm.company.automation.models.Employees;
import pkpm.company.automation.models.ReportEmployee;
import pkpm.company.automation.models.ReportGeneral;
import pkpm.company.automation.utils.MessageWriter;

@Slf4j
public class GraphExecutionReport {

  /**
   * Формує дані для звіту по кожній сторінці книги через Report
   *
   * @param bs - останній знімок книги
   * @return - список Report з останнього знімку книги
   */
  public List<ReportGeneral> getDateForGeneralReport(BookSnapshot bs) {
    Map<String, List<List<Cell>>> columnsOfBook = bs.getColumnsOfBook();
    return columnsOfBook.keySet().stream()
        .filter(key -> columnsOfBook.get(key).size() > 10 && columnsOfBook.get(key).get(9) != null
            && !columnsOfBook.get(key).get(9).isEmpty()
            && columnsOfBook.get(key).get(9).get(2) != null
            && columnsOfBook.get(key).get(9).get(3) != null)
        .map(key -> new ReportGeneral(key,
            getNumOfNames(columnsOfBook.get(key)),
            getNumOfUncompleted(columnsOfBook.get(key)),
            getNumOfCompleted(columnsOfBook.get(key)),
            getNumOfPosition(getNumOfCompleted(columnsOfBook.get(key)),
                getNumOfUncompleted(columnsOfBook.get(key)))))
        .collect(Collectors.toList());
  }

  /**
   * Створює список звітів для кожного співробітника у відповідності до ENUM
   * @param bs
   * @return
   */
  public Map<Employees, List<ReportEmployee>> getListOfEmployeesReports(BookSnapshot bs) {
    Map<Employees, List<ReportEmployee>> listOfEmployeesReports = new HashMap<>();
    for (int i = 0; i < Employees.values().length; i++) {
      listOfEmployeesReports.put(Employees.values()[i],
          getDateForEmployeeReport(bs, Employees.values()[i]));
    }
    return listOfEmployeesReports;
  }

  public List<ReportEmployee> getDateForEmployeeReport(BookSnapshot bs, Employees employee) {
    Map<String, List<List<Cell>>> columnsOfBook = bs.getColumnsOfBook();
    Predicate<String> predicate = key -> columnsOfBook.get(key).size() > 10
        && columnsOfBook.get(key).get(9) != null
        && !columnsOfBook.get(key).get(9).isEmpty()
        && columnsOfBook.get(key).get(9).get(2) != null
        && columnsOfBook.get(key).get(9).get(3) != null;
    return columnsOfBook.keySet().stream()
        .filter(predicate)
        .map(key ->
            new ReportEmployee(
                employee,
                key,
                getNumOfNames(columnsOfBook.get(key)),
                getAllTasksForEmployee(columnsOfBook.get(key), employee,
                    getColumnForEmployee(employee)),
                getNumOfCompletedForEmployee(columnsOfBook.get(key), employee,
                    getColumnForEmployee(employee)),
                getNumOfUncompletedForEmployee(columnsOfBook.get(key), employee,
                    getColumnForEmployee(employee))
            ))
        .collect(Collectors.toList());
  }

  private int getColumnForEmployee(Employees employee) {
    switch (employee) {
      case EMPLOYEE_1:
        return 2;
      case EMPLOYEE_2:
        return 3;
      case EMPLOYEE_3:
        return 4;
      case EMPLOYEE_4:
        return 7;
      case EMPLOYEE_5:
        return 8;
      default:
        log.error("Не знайдений відповідний співробітник в переліку!");
    }
    return 0;
  }

  private int getAllTasksForEmployee(List<List<Cell>> sheet, Employees employee, int numOfColumn) {
    if (numOfColumn != 4) {
      return getNumOfCompletedForEmployee(sheet, employee, numOfColumn)
          + getNumOfUncompletedForEmployee(sheet, employee, numOfColumn);
    } else {
      return getNumOfCompletedForEmployee(sheet, employee, numOfColumn)
          + getNumOfUncompletedForEmployee(sheet, employee, numOfColumn)
          + getNumOfCompletedForEmployee(sheet, employee, 5)
          + getNumOfUncompletedForEmployee(sheet, employee, 5)
          + getNumOfCompletedForEmployee(sheet, employee, 6)
          + getNumOfUncompletedForEmployee(sheet, employee, 6);
    }
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

  private int getNumOfCompletedForEmployee(List<List<Cell>> sheet, Employees employee,
      int numOfColumn) {
    if (numOfColumn != 4) {
      return (int) (sheet.get(numOfColumn).get(3).getNumericCellValue());
    } else {
      return (int) (sheet.get(numOfColumn).get(3).getNumericCellValue())
          + (int) (sheet.get(5).get(3).getNumericCellValue())
          + (int) (sheet.get(6).get(3).getNumericCellValue());
    }
  }

  private int getNumOfUncompletedForEmployee(List<List<Cell>> sheet, Employees employee,
      int numOfColumn) {
    if (numOfColumn != 4) {
      return (int) (sheet.get(numOfColumn).get(2).getNumericCellValue());
    } else {
      return (int) (sheet.get(numOfColumn).get(2).getNumericCellValue())
          + (int) (sheet.get(5).get(2).getNumericCellValue())
          + (int) (sheet.get(6).get(2).getNumericCellValue());
    }
  }

  private int getNumOfPosition(int numOfCompleted, int numOfUnCompleted) {
    return numOfCompleted + numOfUnCompleted;
  }

  public void printResult(List<ReportGeneral> results) {
    log.info("|   Name  | Pos | Task | Uncompl | Compl |");
    results.stream()
        .sorted(Comparator.comparing(ReportGeneral::getNumOfUncompleted))
        .forEach((res) -> log.info(res + "\n"));
  }

  public void writeResultsToFile(String fileName, List<ReportGeneral> results) {
    List<String> resultsLines = results.stream()
        .sorted(Comparator.comparing(ReportGeneral::getNumOfUncompleted))
        .map(ReportGeneral::toString)
        .collect(Collectors.toList());
    MessageWriter.writeList(fileName, resultsLines);
  }

  public String writeResultToString(List<ReportGeneral> results) {
    String title = "|   Name  | Pos | Task | Uncompl | Compl |\n";
    return title += results.stream()
        .sorted(Comparator.comparing(ReportGeneral::getNumOfUncompleted))
        .map(ReportGeneral::toString)
        .collect(Collectors.joining("\n"));
  }


}
