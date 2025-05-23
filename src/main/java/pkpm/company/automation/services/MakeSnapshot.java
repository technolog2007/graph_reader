package pkpm.company.automation.services;

import java.io.IOException;
import java.nio.file.Path;
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
    Path path = copyGraph(fileName);
    try {
      ExelReader.read(path.toString());
      this.bs.setDate(ExelReader.getFileDate());
      this.bs.setNumOfSheets(ExelReader.getSheets().size());
      this.bs.setSheetsNames(getSheetsNames());
      this.bs.setColumnsOfBook(getColumnsOfBook());
    } finally {
      ExelReader.closeWorkbook();
      ExelReader.deleteCopy(path);
    }
  }

  /**
   * Copies the graph file to a temporary directory
   *
   * @param fileName - full name of the graph file
   * @return - path of the copy of the graphics file
   */
  private Path copyGraph(String fileName) {
    Path path = null;
    try {
      path = ExelReader.createTempCopy(Path.of(fileName));
      log.info("File copied successfully to directory: {}", path);
      return path;
    } catch (IOException e) {
      throw new RuntimeException("Cannot read the file: " + fileName);
    }
  }

  /**
   * Returns a list with unique page names of a book
   *
   * @return - list of unique book page names
   */
  private Set<String> getSheetsNames() {
    return ExelReader.getSheets().stream()
        .map(Sheet::getSheetName)
        .collect(Collectors.toSet());
  }

  /**
   * Creates a list of columns, including cells, for each sheet of the workbook.
   *
   * @return - a list of columns, including cells, for each sheet of the workbook
   */
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
