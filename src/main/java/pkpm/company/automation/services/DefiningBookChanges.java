package pkpm.company.automation.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import pkpm.company.automation.models.BookSnapshot;

@Slf4j
public class DefiningBookChanges { // Визначення змін в книзі

  BookSnapshot bs1;
  BookSnapshot bs2;

  public DefiningBookChanges(BookSnapshot bs1, BookSnapshot bs2) {
    this.bs1 = bs1;
    this.bs2 = bs2;
  }

  /**
   * Метод визначає, чи були додані нові вкладки!
   *
   * @return
   */
  public List<String> getBookChanges() {
    Set<String> bookSheetsNames1 = bs1.getSheetsNames();
    Set<String> bookSheetsNames2 = bs2.getSheetsNames();
    if (bookSheetsNames1.size() <= bookSheetsNames2.size()) {
      return getListOfDifferentSheets(bookSheetsNames1, bookSheetsNames2);
    }
    log.warn("Відбулося очищення графіка, потрібне ручне перезавантаження!");
    return null;
  }


  /**
   * Метод порівнює два переліка сторінок графіка і повертає список імен що відрізняються
   *
   * @param bookSheetsNames1
   * @param bookSheetsNames2
   * @return
   */
  private List<String> getListOfDifferentSheets(Set<String> bookSheetsNames1,
      Set<String> bookSheetsNames2) {
    return Stream.concat(
        bookSheetsNames1.stream().filter(e -> !bookSheetsNames2.contains(e)),
        bookSheetsNames2.stream().filter(e -> !bookSheetsNames1.contains(e))
    ).collect(Collectors.toList());
  }

  /**
   * Даний метод повинен працювати при умові що обидва знімки з книги мають однакові вкладки
   *
   * @param bs1
   * @param bs2
   */
  public Map<String, List<Cell>> getSheetsChanges(BookSnapshot bs1, BookSnapshot bs2) {
    Map<String, List<Cell>> filteredColumns1 = extractSecondColumn(bs1.getColumnsOfBook(),
        bs1.getSheetsNames());
    Map<String, List<Cell>> filteredColumns2 = extractSecondColumn(bs2.getColumnsOfBook(),
        bs2.getSheetsNames());
    Map<String, List<Cell>> result = findDifferentCells(filteredColumns1, filteredColumns2,
        bs1.getSheetsNames());
    return result;
  }

  public Map<String, List<Cell>> extractSecondColumn(Map<String, List<List<Cell>>> columnsOfBook,
      Set<String> sheetsName) {
    Map<String, List<Cell>> result = new HashMap<>();

    for (String sheet : sheetsName) {
      List<List<Cell>> columns = columnsOfBook.get(sheet);
      if (columns != null && columns.size() > 1) { // Перевіряємо, що є хоча б 2 колонки
        List<Cell> filtCol = deleteNullValue(columns.get(1));
        result.put(sheet, filtCol);
      }
    }
    log.info("Extract second column size is : {}", result.size());
    return result;
  }

  private List<Cell> deleteNullValue(List<Cell> column) {
    column.removeIf(cell -> cell == null || cell.toString().equals(" "));
    return column;
  }

  /**
   * Метод приймає книгу з відфільтрованою 2-ю колонкою, і повертає книгу лише зі змінами.
   *
   * @param book1
   * @param book2
   * @param sheetsName
   * @return
   */
  public Map<String, List<Cell>> findDifferentCells(Map<String, List<Cell>> book1,
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

      for (int i = maxSize-difSize; i < maxSize; i++) {
        diffCells.add(col2.get(i));
      }
      if (!diffCells.isEmpty()) {
        differences.put(sheet, diffCells);
      }
    }
    return differences;
  }
}

