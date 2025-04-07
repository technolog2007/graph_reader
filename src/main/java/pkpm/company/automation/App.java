package pkpm.company.automation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import pkpm.company.automation.services.GraphScanner;

@Slf4j
public class App {

  public static void main(String[] args) {

    log.info("Start program time is : " + LocalDateTime.now());

    GraphScanner se = new GraphScanner();
    se.scanning(getGraphName(), getIntervalTime(), getEndTime());

    log.info("End program time is : " + LocalDateTime.now());
  }

  /**
   * Метод встановлює час сканування книги з config файлу, або через інтервал в секундах, що
   * зазначений у методі
   *
   * @return
   */
  private static int getIntervalTime() {
    try {
      return Integer.parseInt(System.getenv("INTERVAL_TIME"));
    } catch (NumberFormatException e) {
      return 60;
    }
  }

  /**
   * Метод встановлює час завершення програми або з config файлу, або до зазначеного часу поточного
   * дня
   *
   * @return
   */
  private static LocalDateTime getEndTime() {
    try {
      LocalDateTime dateTimeEnd = createEndTime(System.getenv("END_TIME"));
      if (dateTimeEnd.isBefore(LocalDateTime.now())) {
        throw new NullPointerException();
      }
      log.warn("Програма працюватиме до {}!", System.getenv("END_TIME"));
      return dateTimeEnd;
    } catch (DateTimeParseException | NullPointerException e) {
      log.warn("Програма працюватиме до 20:00!");
      return setTime(20, 0);
    }
  }

  private static LocalDateTime setTime(int hour, int minutes) {
    return LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(hour, minutes));
  }

  /**
   * Метод створює дату і додає час, до якого працюватиме програма. Час додається із
   * конфігураційного файлу (ключ "END_TIME")
   *
   * @param time
   * @return
   */
  private static LocalDateTime createEndTime(String time) {
    LocalDate currentDate = LocalDateTime.now().toLocalDate();
    LocalTime endTime = LocalTime.parse(time);
    return LocalDateTime.of(currentDate, endTime);
  }

  /**
   * Метод повертає значення ім'я файлу, або помилку, якщо відповідний ключ не знайдено.
   *
   * @return
   */
  private static String getGraphName() {
    String graphName = System.getenv("GRAPH_NAME");
    if (graphName != null) {
      return graphName;
    } else {
      throw new RuntimeException(
          "Please, check the configuration file, it does not contain the key \"GRAPH_NAME\"");
    }
  }
}
