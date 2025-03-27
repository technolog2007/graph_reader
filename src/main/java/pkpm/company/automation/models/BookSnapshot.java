package pkpm.company.automation.models;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;

@Setter
@Getter
@Slf4j
public class BookSnapshot {

  private Date date;
  private int numOfSheets;
  private Set<String> sheetsNames = new HashSet<>();
  private Map<String, List<List<Cell>>> columnsOfBook = new HashMap<>();

  public BookSnapshot() {
  }

  public BookSnapshot(int numOfSheets, Set<String> sheetsNames, Date date) {
    this.date = date;
    this.numOfSheets = numOfSheets;
    this.sheetsNames = sheetsNames;
  }

  @Override
  public String toString() {
    return "BookSnapshot{" +
        "date=" + date +
        ", numOfSheets=" + numOfSheets +
        ", sheetsNames=" + sheetsNames;
  }
}
