package pkpm.company.automation.services;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import pkpm.company.automation.models.BookSnapshot;
import pkpm.company.automation.models.GraphMessage;
import pkpm.company.automation.utils.MessageWriter;

@Slf4j
@Getter
public class GraphScanner {

  private final List<BookSnapshot> snapshotList = new ArrayList<>(2);
  private final List<Date> dateList = new ArrayList<>();
  private LocalDateTime currentTime = LocalDateTime.now();
  private final String KEY_1 = "delSheets";
  private final String KEY_2 = "attachSheets";


  public void scanning(String graphName, long pauseTime, LocalDateTime endTime) {
    save(snapshotList, new MakeSnapshot(graphName));
    while (currentTime.isBefore(endTime)) {
      if (checkBookDate(setBookDate(graphName))) { // перевірка, чи була змінена книга
        save(snapshotList, new MakeSnapshot(graphName));
        definingBookChange(snapshotList);
        update(pauseTime);
      } else {
        update(pauseTime);
      }
    }
  }

  private void update(long pauseTime) {
    pause(pauseTime);
    this.currentTime = LocalDateTime.now();
  }

  private void definingBookChange(List<BookSnapshot> bsl) { // bookSnapshotList
    if (bsl.size() == 2) {
      DefiningBookChanges dbc = new DefiningBookChanges(bsl.get(0), bsl.get(1));
      Map<String, List<String>> bookChanges = dbc.getBookChanges();
      if (!bookChanges.get(KEY_1).isEmpty()) {
        printDelBookChanges(bookChanges);
      }
      if (!bookChanges.get(KEY_2).isEmpty()) {
        writeAttachBookChanges(bookChanges.get(KEY_2));
      }
//      if (bookChanges.get(KEY_1).isEmpty() && bookChanges.get(KEY_2).isEmpty()) {
//        log.info("Зміни вкладок не виявлені!");
//      }
      writeSheetsChanges(dbc.getSheetsChanges(bsl.get(0), bsl.get(1)));
    }
  }

  private void printDelBookChanges(Map<String, List<String>> bookChanges) {
    if (bookChanges.get(KEY_1).size() == 1) {
      String message =
          GraphMessage.INFORM_DELETE_FOLDER.getMessage() + "\"" + bookChanges.get(KEY_1).get(0)
              + "\"";
      log.warn(message);
    } else {
      String message =
          GraphMessage.INFORM_DELETE_FOLDERS.getMessage() + createMessageBody(
              bookChanges.get(KEY_1));
      log.warn(message);
    }
  }

  private void writeAttachBookChanges(List<String> bookChanges) {
    if (bookChanges.size() == 1) {
      String message =
          GraphMessage.INFORM_ADD_FOLDER.getMessage() + "\"" + bookChanges.get(0) + "\"";
      log.warn(message);
      MessageWriter.writeLine(message);
    }
    if (bookChanges.size() > 1) {
      String message =
          GraphMessage.INFORM_ADD_FOLDERS.getMessage() + createMessageBody(bookChanges);
      log.warn(message);
      MessageWriter.writeLine(message);
    }
  }

  private void writeSheetsChanges(Map<String, List<Cell>> changes) {
    if (changes.isEmpty()) {
      log.info(GraphMessage.INFORM_NO_CHANGE.getMessage());
    } else if (changes.keySet().size() == 1) {
      for (String sheet : changes.keySet()) {
        String message = GraphMessage.INFORM_ADD_POSITION.getMessage() + "\"" + sheet + "\"";
        MessageWriter.writeLine(message);
        log.info(message);
      }
    } else {
      List<String> sheets = new ArrayList<>(changes.keySet());
      String message = GraphMessage.INFORM_ADD_POSITIONS.getMessage() + createMessageBody(sheets);
      MessageWriter.writeLine(message);
      log.info(message);
    }
  }

  /**
   * Створює, у потрібному форматі, тіло повідомлення зі списка змінених сторінок книги
   *
   * @param sheets - список змінених сторінок книги
   * @return - тіло повідомлення
   */
  private String createMessageBody(List<String> sheets) {
    String result = "\"";
    for (int i = 0; i < sheets.size(); i++) {
      if (i < sheets.size() - 1) {
        result += sheets.get(i) + "\", ";
      } else {
        result += "\"" + sheets.get(i) + "\"";
      }
    }
    return result;
  }

  /**
   * Визначає останню дату книги та повертає список з датами книг
   *
   * @param fileName -
   * @return -
   */
  private List<Date> setBookDate(String fileName) {
    if (dateList.isEmpty()) {
      dateList.add(new Date(new File(fileName).lastModified()));
      return dateList;
    } else if (dateList.size() == 1) {
      dateList.add(new Date(new File(fileName).lastModified()));
      return dateList;
    } else {
      dateList.set(0, dateList.get(1));
      dateList.set(1, new Date(new File(fileName).lastModified()));
      return dateList;
    }
  }

  private boolean checkBookDate(List<Date> bookDateList) {
    if (!bookDateList.isEmpty() && bookDateList.size() > 1) {
      return !bookDateList.get(0).equals(bookDateList.get(1)) && bookDateList.get(0)
          .before(bookDateList.get(1));
    }
    return false;
  }

  private void save(List<BookSnapshot> bsl, MakeSnapshot ms) {
    if (bsl.isEmpty()) {
      bsl.add(ms.getBs());
    } else if (bsl.size() == 1) {
      bsl.add(1, ms.getBs());
    } else {
      bsl.set(0, bsl.get(1));
      bsl.set(1, ms.getBs());
    }
  }

  private void pause(long seconds) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}

