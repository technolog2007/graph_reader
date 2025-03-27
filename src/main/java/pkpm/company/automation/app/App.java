package pkpm.company.automation.app;

import lombok.extern.slf4j.Slf4j;
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
      log.info(ms.getColumnOfSheet(ms.getBs().getSheetsNames().get(i), columnNum).toString());
    }
  }


}
