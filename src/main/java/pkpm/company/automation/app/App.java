package pkpm.company.automation.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import pkpm.company.automation.services.ExelReader;
import pkpm.company.automation.services.MakeSnapshot;

@Slf4j
public class App {

  private static final String FILE_NAME = System.getenv("file_name");

  public static void main(String[] args) {
    MakeSnapshot ms1 = new MakeSnapshot(FILE_NAME);
    printColumns(ms1, 1);
  }

  private static void printColumns(MakeSnapshot ms, int columnNum) {
    for (int i = 0; i < ms.getBs().getNumOfSheets(); i++) {
      log.info(ms.getColumn(ms.getBs().getSheetsNames().get(i), columnNum).toString());
    }
  }


}
