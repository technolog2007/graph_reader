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

  private String formatToString(int v) {
    return String.format("%-" + 3 + "s", v);
  }

  @Override
  public String toString() {
    String sheetName = String.format("%-" + 13 + "s", this.sheetName);
    return "|" + sheetName
        + "|" + formatToString(numOfNames)
        + "|" + formatToString(numOfTasks)
        + "|" + formatToString(numOfUncompleted)
        + "|" + formatToString(numOfCompleted)
        + "|";
  }
}
