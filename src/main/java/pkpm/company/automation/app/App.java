package pkpm.company.automation.app;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import pkpm.company.automation.services.ExelReader;

@Slf4j
public class App {

  private static final String FILE_NAME = System.getenv("file_name");

  public static void main(String[] args) {
    ExelReader.read(FILE_NAME);
    List<String> sheetsNames = ExelReader.getSheets().stream()
        .map(Sheet::getSheetName)
        .collect(Collectors.toList());
    sheetsNames.forEach(System.out::println);

    ExelReader.getSheets().stream()
        .map(sheet -> {
          int i = 4;
          List<String> cells = new ArrayList<>();
          while (sheet.rowIterator().hasNext()) {
//            log.info("sheetName is {}, cell : {}", sheet.getSheetName(),
//                sheet.getRow(i).getCell(1).toString());
            if (sheet.getRow(i).getCell(1) == null || sheet.getRow(i).getCell(1).toString()
                .equals("")) {
              break;
            } else {
              cells.add(sheet.getRow(i).getCell(1).toString());
            }
            i++;
          }
          return cells;
        })
        .filter((c) -> !c.isEmpty())
        .forEach(System.out::println);

  }
}
