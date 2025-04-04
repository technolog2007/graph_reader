package pkpm.company.automation.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import pkpm.company.automation.models.BookSnapshot;
import pkpm.company.automation.utils.ExelReader;

@Slf4j
@Getter
public class MakeSnapshot {

  private final BookSnapshot bs = new BookSnapshot();

  public MakeSnapshot(String fileName) {
    ExelReader.read(fileName);
    this.bs.setDate(ExelReader.getBookDate());
    this.bs.setNumOfSheets(ExelReader.getSheets().size());
    this.bs.setSheetsNames(getSheetsNames());
    this.bs.setColumnsOfBook(getColumnsOfBook());
  }

  private Set<String> getSheetsNames() {
    return ExelReader.getSheets().stream()
        .map(Sheet::getSheetName)
        .collect(Collectors.toSet());
  }

  private Map<String, List<List<Cell>>> getColumnsOfBook() {
    return ExelReader.getSheets().stream()
        .collect(Collectors.toMap(
            sheet -> sheet.getSheetName(), // Key is letter name
            sheet -> {
              List<List<Cell>> columns = new ArrayList<>();
              int maxCols = 0;

              // Find the maximum number of columns in a list
              for (Row row : sheet) {
                maxCols = Math.max(maxCols, row.getLastCellNum());
              }

              // Ініціалізуємо список колонок
              for (int col = 0; col < maxCols; col++) {
                columns.add(new ArrayList<>()); // Create a list for each column
              }

              // Fill in the data in the columns
              for (Row row : sheet) {
                for (int col = 0; col < maxCols; col++) {
                  Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                  columns.get(col).add(cell); // Add a cell to the appropriate column
                }
              }
              return columns;
            }
        ));
  }
}
