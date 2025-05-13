package pkpm.company.automation.models;

import lombok.Getter;

@Getter
public class ReportEmployee {

  private final Employees employee;
  private final String sheetName;
  private final int positions, allTasks, completed, uncompleted;

  public ReportEmployee(Employees employee, String sheetName, int positions, int allTasks,
      int completed, int uncompleted) {
    this.employee= employee;
    this.sheetName = sheetName;
    this.positions = positions;
    this.allTasks = allTasks;
    this.completed = completed;
    this.uncompleted = uncompleted;
  }

  @Override
  public String toString() {
    return "|" + sheetName
        + "|" + positions
        + "|" + allTasks
        + "|" + completed
        + "|" + uncompleted
        + "|";
  }
}
