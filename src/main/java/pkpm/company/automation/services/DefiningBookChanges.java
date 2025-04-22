package pkpm.company.automation.services;

import jakarta.validation.constraints.Pattern;
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
public class DefiningBookChanges {

  BookSnapshot bs1;
  BookSnapshot bs2;

  public DefiningBookChanges(BookSnapshot bs1, BookSnapshot bs2) {
    this.bs1 = bs1;
    this.bs2 = bs2;
  }

  /**
   * Визначає, чи були зміни (додані або видалені) у вкладках книги
   *
   * @return - повертає список назв листів, що були додані
   */
  public Map<String, List<String>> getBookChanges() {
    return Map.of("delSheets", getDelSheets(bs1.getSheetsNames(), bs2.getSheetsNames()),
        "attachSheets", getAttachSheets(bs1.getSheetsNames(), bs2.getSheetsNames()));
  }

  private List<String> getDelSheets(Set<String> bookSheetsNames1, Set<String> bookSheetsNames2) {
    return bookSheetsNames1.stream()
        .filter(e -> !bookSheetsNames2.contains(e))
        .collect(Collectors.toList());
  }

  private List<String> getAttachSheets(Set<String> bookSheetsNames1, Set<String> bookSheetsNames2) {
    return bookSheetsNames2.stream()
        .filter(e -> !bookSheetsNames1.contains(e))
        .collect(Collectors.toList());
  }

  /**
   * Метод повинен працювати при умові що обидва знімки з книги мають однакові вкладки
   *
   * @param bs1 - знімок з книги №1
   * @param bs2 - знімок з книги №2
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
   * Відсортовує другу колонку по кожному листу книги і повертає список
   *
   * @param columnsOfBook - список колонок листів книги
   * @param sheetsName    - список імен листів книги
   * @return - список книги з 2-ми колонками по кожному листу
   */
  private Map<String, List<Cell>> extractSecondColumn(Map<String, List<List<Cell>>> columnsOfBook,
      Set<String> sheetsName) {
    Map<String, List<Cell>> result = new HashMap<>();
    for (String sheet : sheetsName) {
      List<List<Cell>> columns = columnsOfBook.get(sheet);
      if (columns != null && columns.size() > 1) { // Перевіряємо, що є хоча б 2 колонки
        List<Cell> filtCol = deleteNullValue(columns.get(1));
        result.put(sheet, filtCol);
      }
    }
    return result;
  }

  /**
   * Видаляє нульові значення з колонки книги
   *
   * @param column - колонка книги
   * @return - відфільтрована колонка книги, без нульових значень
   */
  private List<Cell> deleteNullValue(List<Cell> column) {
    column.removeIf(cell -> cell == null || cell.toString().equals(" "));
    return column;
  }

  /**
   * Знаходить відмінності і повертає список відмінностей по другій колонці між сторінками з
   * однаковими назвами знімків книги
   *
   * @param book1      - список листів знімка книги №1, де є лише 2-га колонка
   * @param book2      - список листів знімка книги №1, де є лише 2-га колонка
   * @param sheetsName - список назв листів знімка книги
   * @return - список листів із відмінностями по 2-й колонці знімків
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

