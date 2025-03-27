package pkpm.company.automation.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;

@Slf4j
public class BookSnapshot {

  @Getter
  @Setter
  private int numOfSheets;
  @Getter
  @Setter
  private List<String> sheetsNames = new ArrayList<>();
  @Getter
  @Setter
  private Map<String, List<List<Cell>>> columnsOfBook = new HashMap<>();

  public BookSnapshot() {
  }

  public BookSnapshot(int numOfSheets, List<String> sheetsNames) {
    this.numOfSheets = numOfSheets;
    this.sheetsNames = sheetsNames;
  }

  @Override
  public String toString() {
    return "BookSnapshot{" +
        "numOfSheets=" + numOfSheets +
        ", sheetsNames=" + sheetsNames +
        '}';
  }
}
