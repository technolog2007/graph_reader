package pkpm.company.automation.models;

public class Report {

  private final String sheetName;
  private final int numOfPositions, numOfTasks, numOfCompleted, numOfUncompleted;

  public Report(String sheetName, int numOfTasks, int numOfPositions, int numOfCompleted,
      int numOfUncompleted
  ) {
    this.sheetName = sheetName;
    this.numOfTasks = numOfTasks;
    this.numOfPositions = numOfPositions;
    this.numOfCompleted = numOfCompleted;
    this.numOfUncompleted = numOfUncompleted;
  }

  @Override
  public String toString() {
    return "| " + sheetName + " | " + numOfTasks + " | " + numOfPositions + " | " + numOfCompleted
        + " | " + numOfUncompleted + " |";
  }
}
