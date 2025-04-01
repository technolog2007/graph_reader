package pkpm.company.automation.app;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import pkpm.company.automation.services.ScheduleExecution;

@Slf4j
public class App {

  private static final String FILE_NAME = System.getenv("file_name");

  public static void main(String[] args) {

    log.info("Start program time is : " + LocalDateTime.now());

    ScheduleExecution se = new ScheduleExecution();
    se.execute(FILE_NAME, getIntervalTime(), getEndTime()); // час інтервалу в сек

    log.info("End program time is : " + LocalDateTime.now());
  }

  /**
   * Метод встановлює час сканування книги з config файлу, або через кожні 60 сек
   *
   * @return
   */
  private static int getIntervalTime() {
    try {
      return Integer.parseInt(System.getenv("interval_time"));
    } catch (NumberFormatException e) {
      return 60;
    }
  }

  /**
   * Метод встановлює час завершення програми або з config файлу, або до 17:00 поточної дати
   *
   * @return
   */
  private static LocalDateTime getEndTime() {
    try {
      return LocalDateTime.parse(System.getenv("end_time"));
    } catch (DateTimeParseException | NullPointerException e) {
      return LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(17, 0));
    }
  }
}
