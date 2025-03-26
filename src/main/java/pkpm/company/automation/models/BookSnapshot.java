package pkpm.company.automation.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookSnapshot {

  @Getter
  @Setter
  private int numOfSheets;

  @Getter
  @Setter
  private List<String> sheetNames = new ArrayList<>();
  @Getter
  @Setter
  private Map<String,List<String>> cellsOfSheet = new HashMap<>();

  public BookSnapshot() {
  }

  public BookSnapshot(int numOfSheets, List<String> sheetNames) {
    this.numOfSheets = numOfSheets;
    this.sheetNames = sheetNames;
  }

  @Override
  public String toString() {
    return "BookSnapshot{" +
        "numOfSheets=" + numOfSheets +
        ", sheetNames=" + sheetNames +
        '}';
  }
}
