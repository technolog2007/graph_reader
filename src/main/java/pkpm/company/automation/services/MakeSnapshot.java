package pkpm.company.automation.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import pkpm.company.automation.models.BookSnapshot;

@Slf4j
public class MakeSnapshot {

  @Getter
  private BookSnapshot bs = new BookSnapshot();

  public MakeSnapshot(String fileName) {
    ExelReader.read(fileName);
    this.bs.setNumOfSheets(ExelReader.getSheets().size());
    this.bs.setSheetsNames(getSheetsNames());
    this.bs.setColumnsOfBook(getColumnsOfBook());
  }

  private List<String> getSheetsNames() {
    return ExelReader.getSheets().stream()
        .map(Sheet::getSheetName)
        .collect(Collectors.toList());
  }

  private Map<String, List<List<Cell>>> getColumnsOfBook() {
    Map<String, List<List<Cell>>> sheetData = ExelReader.getSheets().stream()
        .collect(Collectors.toMap(
            sheet -> sheet.getSheetName(), // Ключ: назва листа
            sheet -> {
              List<List<Cell>> columns = new ArrayList<>();
              int maxCols = 0;

              // Знаходимо максимальну кількість колонок у листі
              for (Row row : sheet) {
                maxCols = Math.max(maxCols, row.getLastCellNum());
              }

              // Ініціалізуємо список колонок
              for (int col = 0; col < maxCols; col++) {
                columns.add(new ArrayList<>()); // Створюємо список для кожної колонки
              }

              // Заповнюємо дані по колонках
              for (Row row : sheet) {
                for (int col = 0; col < maxCols; col++) {
                  Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                  columns.get(col).add(cell); // Додаємо комірку в відповідну колонку
                }
              }
              return columns;
            }
        ));
    return sheetData;
  }

  public List<Cell> getColumnOfSheet(String sheetName, int columnNum) {
    return this.bs.getColumnsOfBook().get(sheetName).get(columnNum);
  }
}
