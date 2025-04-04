package pkpm.company.automation.services;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import pkpm.company.automation.models.BookSnapshot;

@Slf4j
@Getter
public class GraphScanner {

  private final List<BookSnapshot> snapshotList = new ArrayList<>(2);
  private final List<Date> dateList = new ArrayList<>();
  private LocalDateTime currentTime = LocalDateTime.now();

  public void scanning(String fileName, long pauseTime, LocalDateTime endTime) {
    save(snapshotList, new MakeSnapshot(fileName));
    while (currentTime.isBefore(endTime)) {
      if (checkBookDate(setBookDate(fileName))) { // перевірка, чи була змінена книга
        save(snapshotList, new MakeSnapshot(fileName));
        definingBookChange(snapshotList);
        update(pauseTime);
      } else {
        log.info("There are no changes in book!");
        update(pauseTime);
      }
    }
  }

  private void update(long pauseTime) {
    pause(pauseTime);
    this.currentTime = LocalDateTime.now();
  }

  private void definingBookChange(List<BookSnapshot> bsl) {
    if (bsl.size() == 2) {
      DefiningBookChanges dbc = new DefiningBookChanges(bsl.get(0), bsl.get(1));
      if (dbc.getBookChanges() != null) {
        if (dbc.getBookChanges().size() == 1) {
          log.warn("Додано нову вкладку : " + dbc.getBookChanges().toString());
        } else if (dbc.getBookChanges().size() > 1) {
          log.warn("Додано нові вкладки : " + dbc.getBookChanges().toString());
        } else if (dbc.getBookChanges().isEmpty()) {
          Map<String, List<Cell>> changes = dbc.getSheetsChanges(bsl.get(0), bsl.get(1));
          printChanges(changes);
        }
      }
    }
  }

  private void printChanges(Map<String, List<Cell>> changes) {
    if (changes.isEmpty()) {
      log.info("Відсутні зміни в графіку!");
    } else {
      log.info("Додано нові позиції : {}", changes);
    }
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

