package pkpm.company.automation.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import pkpm.company.automation.models.BookSnapshot;

@Slf4j
public class BookChangesSearcher {

  BookSnapshot bs1;
  BookSnapshot bs2;
  private static final String IGNORE_SHEET_NAME1 = "Лист";
  private static final String IGNORE_SHEET_NAME2 = "Аркуш";

  public BookChangesSearcher(BookSnapshot bs1, BookSnapshot bs2) {
    this.bs1 = bs1;
    this.bs2 = bs2;
  }

  /**
   * Determines whether changes (added or deleted) have been made to the book's tabs
   *
   * @return - returns a list of the names of the letters that have been added
   */
  public Map<String, List<String>> getBookChanges() {
    return Map.of("delSheets", getDelSheets(bs1.getSheetsNames(), bs2.getSheetsNames()),
        "addSheets", getAddSheets(bs1.getSheetsNames(), bs2.getSheetsNames()));
  }

  /**
   * Defines and returns a list of deleted workbook sheet names
   *
   * @param bookSheetsNames1 - ist of letter names in the 1-st snapshot of the book
   * @param bookSheetsNames2 - list of letter names in the 2-nd snapshot of the book
   * @return - list of book sheet names that have been added
   */
  private List<String> getDelSheets(Set<String> bookSheetsNames1, Set<String> bookSheetsNames2) {
    return bookSheetsNames1.stream()
        .filter(e -> !bookSheetsNames2.contains(e))
        .collect(Collectors.toList());
  }

  /**
   * Defines and returns a list of added book sheet names, excluding those that are ignored.
   *
   * @param bookSheetsNames1 - list of letter names in the 1-st snapshot of the book
   * @param bookSheetsNames2 - list of letter names in the 2-nd snapshot of the book
   * @return - list of book sheet names that have been added
   */
  private List<String> getAddSheets(Set<String> bookSheetsNames1, Set<String> bookSheetsNames2) {
    return bookSheetsNames2.stream()
        .filter(e -> !bookSheetsNames1.contains(e))
        .filter(e -> !e.contains(IGNORE_SHEET_NAME1))
        .filter(e -> !e.contains(IGNORE_SHEET_NAME2))
        .collect(Collectors.toList());
  }

  /**
   * The method should work provided that both book snapshots have the same tabs.
   *
   * @param bs1 - snapshot of book #1
   * @param bs2 - snapshot of book #2
   */
  public Map<String, List<Cell>> getSheetsChanges(BookSnapshot bs1, BookSnapshot bs2) {
    Map<String, List<Cell>> filteredColumns1 = extractSecondColumn(bs1.getColumnsOfBook(),
        bs1.getSheetsNames());
    Map<String, List<Cell>> filteredColumns2 = extractSecondColumn(bs2.getColumnsOfBook(),
        bs2.getSheetsNames());
    return findDifferentCells(filteredColumns1, filteredColumns2,
        bs1.getSheetsNames());
  }

  /**
   * Sorts the second column by each sheet of the book and returns a list
   *
   * @param columnsOfBook - list of book letter columns
   * @param sheetsName    - list of book letter names
   * @return - book list with 2 columns for each sheet
   */
  private Map<String, List<Cell>> extractSecondColumn(Map<String, List<List<Cell>>> columnsOfBook,
      Set<String> sheetsName) {
    Map<String, List<Cell>> result = new HashMap<>();
    for (String sheet : sheetsName) {
      List<List<Cell>> columns = columnsOfBook.get(sheet);
      if (columns != null && columns.size() > 1) { // Check that there are at least 2 columns
        List<Cell> filtCol = deleteNullValue(columns.get(1));
        result.put(sheet, filtCol);
      }
    }
    return result;
  }

  /**
   * Removes zero values from a book column
   *
   * @param column - book column
   * @return - filtered book column, no null values
   */
  private List<Cell> deleteNullValue(List<Cell> column) {
    column.removeIf(cell -> cell == null || cell.toString().equals(" "));
    return column;
  }

  /**
   * Finds differences and returns a list of differences in the second column between pages with the
   * same book snapshot names
   *
   * @param book1      - list of letters of the snapshot of book #1, where there is only the 2nd
   *                   column
   * @param book2      - list of letters of the snapshot of book #2, where there is only the 2nd
   *                   column
   * @param sheetsName - list of letter names book snapshot
   * @return - list of letters with differences in the 2nd column of the images
   */
  private Map<String, List<Cell>> findDifferentCells(Map<String, List<Cell>> book1,
      Map<String, List<Cell>> book2, Set<String> sheetsName) {

    Map<String, List<Cell>> differences = new HashMap<>();

    for (String sheet : sheetsName) {
      List<Cell> col1 = book1.get(sheet);
      List<Cell> col2 = book2.get(sheet);

      if (col1 == null || col2 == null) {
        continue;
      }

      List<Cell> diffCells = new ArrayList<>();
      int minSize = col1.size();
      int maxSize = col2.size();
      int difSize = maxSize - minSize;

      for (int i = maxSize - difSize; i < maxSize; i++) {
        diffCells.add(col2.get(i));
      }
      if (!diffCells.isEmpty()) {
        differences.put(sheet, diffCells);
      }
    }
    return differences;
  }
}

