package pkpm.company.automation.models;

import lombok.Getter;

@Getter
public class Report {

  private final String sheetName;
  private final int numOfTasks, numOfNames, numOfCompleted, numOfUncompleted;

  public Report(String sheetName, int numOfNames, int numOfTasks, int numOfUncompleted,
      int numOfCompleted
  ) {
    this.sheetName = sheetName;
    this.numOfNames = numOfNames;
    this.numOfTasks = numOfTasks;
    this.numOfUncompleted = numOfUncompleted;
    this.numOfCompleted = numOfCompleted;
  }

  @Override
  public String toString() {
    return "|" + sheetName + "| " + numOfNames + " | " + numOfTasks + " | " + numOfUncompleted
        + " | " + numOfCompleted + " |";
  }
}
