package pkpm.company.automation.services;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pkpm.company.automation.models.BookSnapshot;

@Slf4j
public class ScheduleExecution {

  @Getter
  private List<BookSnapshot> bsl = new ArrayList<>();
  @Getter
  private List<Date> bookDateList = new ArrayList<>();

  public void execute(String fileName, long pauseTime, LocalDateTime endTime) {
    LocalDateTime currentTime = LocalDateTime.now();
    save(bsl, new MakeSnapshot(fileName));
    while (currentTime.isBefore(endTime)) {
      if (checkBookDate(setDBookDate(fileName))) { // перевірка, чи була змінена книга
        MakeSnapshot ms = new MakeSnapshot(fileName);
        save(bsl, ms);
        definingBookChange(bsl);
        log.info(ms.getBs().toString());
      }
      log.info("There are no changes in book!");
      pause(pauseTime);
      currentTime = LocalDateTime.now();
    }
  }

  private void definingBookChange(List<BookSnapshot> bsl) {
    if (bsl.size() == 2) {
      DefiningBookChanges dbc = new DefiningBookChanges(bsl.get(0), bsl.get(1));
      if (dbc.getBookChanges() != null) {
        if(dbc.getBookChanges().size() == 1) {
          log.warn("Додано нову вкладку : " + dbc.getBookChanges().toString());
        }else if (dbc.getBookChanges().size() > 1){
          log.warn("Додано нові вкладки : " + dbc.getBookChanges().toString());
        } else if (dbc.getBookChanges().isEmpty()){
          dbc.getSheetsChanges(bsl.get(0), bsl.get(1));
          log.warn("Треба перевірити чи були додані нові позиції у вкладки!!!");
        }
      }
    }
  }

  /**
   * Визначає останню дату книги та повертає список з датами книг
   *
   * @param fileName
   * @return
   */
  private List<Date> setDBookDate(String fileName) {
    if (bookDateList.isEmpty()) {
      bookDateList.add(new Date(new File(fileName).lastModified()));
      return bookDateList;
    } else if (bookDateList.size() == 1) {
      bookDateList.add(new Date(new File(fileName).lastModified()));
      return bookDateList;
    } else {
      bookDateList.set(0, bookDateList.get(1));
      bookDateList.set(1, new Date(new File(fileName).lastModified()));
      return bookDateList;
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

  private void pause(long min) {
    try {
      TimeUnit.SECONDS.sleep(min);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}

