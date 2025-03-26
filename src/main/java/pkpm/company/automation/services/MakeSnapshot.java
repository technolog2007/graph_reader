package pkpm.company.automation.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Sheet;
import pkpm.company.automation.models.BookSnapshot;

public class MakeSnapshot {

  private BookSnapshot bs = new BookSnapshot();

  public MakeSnapshot(String fileName) {
    ExelReader.read(fileName);
  }

  public List<String> getSheetsNames(){
    return ExelReader.getSheets().stream()
        .map(Sheet::getSheetName)
        .collect(Collectors.toList());
  }
  public Map<String,List<String>> getCellsOfList(){
    return null;
  }

}
