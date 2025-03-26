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

@Slf4j
public class App {

  private static final String FILE_NAME = System.getenv("file_name");

  public static void main(String[] args) {
    ExelReader.read(FILE_NAME);
    List<String> sheetsNames = ExelReader.getSheets().stream()
        .map(Sheet::getSheetName)
        .collect(Collectors.toList());
    sheetsNames.forEach(System.out::println);

    Map<String, List<List<Cell>>> sheetData = ExelReader.getSheets().stream()
        .collect(Collectors.toMap(
            sheet -> sheet.getSheetName(), // Ключ: ім'я аркуша
            sheet -> { // Значення: список списків комірок
              List<List<Cell>> columns = new ArrayList<>();

              sheet.forEach(row -> { // Перебір рядків
                List<Cell> cells = new ArrayList<>();
                row.forEach(cells::add); // Додаємо всі комірки рядка в список
                columns.add(cells);
              });
              return columns;
            }
        ));

//    Collection<Map<String,List<List<Cell>>>> collect = ExelReader.getSheets().stream()
//        .map((sheet) -> {
//          int i = 0;
//          Map<String,List<List<Cell>>> cellsOfSheet = new HashMap<>();
//          List<List<Cell>> columns = new ArrayList<>();
//          while (sheet.rowIterator().hasNext()) {
//            int j = 0;
//            List<Cell> cells = new ArrayList<>();
//            while (sheet.getRow(i).cellIterator().hasNext()){
//              cells.add(sheet.getRow(i).getCell(j));
//              j++;
//            }
//            columns.add(cells);
//            i++;
//          }
//          cellsOfSheet.put(sheet.getSheetName(), columns);
//          return cellsOfSheet;
//        })
//        .collect(Collectors.toMap());

//    ExelReader.getSheets().stream()
//        .map(sheet -> {
//          int i = 4;
//          List<String> cells = new ArrayList<>();
//          while (sheet.rowIterator().hasNext()) {
//            if (sheet.getRow(i).getCell(1) == null || sheet.getRow(i).getCell(1).toString()
//                .equals("")) {
//              break;
//            } else {
//              cells.add(sheet.getRow(i).getCell(1).toString());
//            }
//            i++;
//          }
//          return cells;
//        })
//        .filter((c) -> !c.isEmpty())
//        .forEach(System.out::println);

  }
}
