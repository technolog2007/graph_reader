package pkpm.company.automation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import pkpm.company.automation.services.GraphScanner;
import pkpm.company.automation.services.MakeSnapshot;

@Slf4j
public class App {

  private final static LocalDateTime CURRENT_TIME = LocalDateTime.now();
  private final static String GRAPH_NAME = getGraphName();

  public static void main(String[] args) {

    log.info("Start program time is : " + LocalDateTime.now());

    GraphScanner gs = createGraphScanner(GRAPH_NAME);
    scanning(gs, getEndTime(), GRAPH_NAME);

    log.info("End program time is : " + LocalDateTime.now());
  }

  private static GraphScanner createGraphScanner(String graphName) {
    GraphScanner gs = new GraphScanner();
    gs.set(0, new MakeSnapshot(graphName).getBs());
    return gs;
  }

  private static void scanning(GraphScanner gs, LocalDateTime endTime, String graphName) {
    while (CURRENT_TIME.isBefore(endTime)) {
      gs.scanning(graphName, getIntervalTime());
    }
  }

  /**
   * Встановлює час сканування книги з config файлу, або через інтервал в секундах, що зазначений у
   * методі
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
   * Встановлює час завершення програми або з config файлу, або до зазначеного часу поточного дня
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
      log.warn("Програма по замовчуванню працюватиме до 20:00!");
      return setTime(20, 0);
    }
  }

  /**
   * Встановлює і повертає поточну дату з відповідним часом (години і хвилини)
   *
   * @param hour
   * @param minutes
   * @return
   */
  private static LocalDateTime setTime(int hour, int minutes) {
    return LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(hour, minutes));
  }

  /**
   * Створює дату і додає час, до якого працюватиме програма. Час додається із конфігураційного
   * файлу (ключ "END_TIME")
   *
   * @param time - час завершення програми заданий користувачем в config
   * @return - час закінчення роботи програми
   */
  private static LocalDateTime createEndTime(String time) {
    LocalDate currentDate = LocalDateTime.now().toLocalDate();
    LocalTime endTime = LocalTime.parse(time);
    return LocalDateTime.of(currentDate, endTime);
  }

  /**
   * Повертає значення ім'я файлу, або помилку, якщо відповідний ключ не знайдено.
   *
   * @return - повне ім'я файлу з графіком
   */
  private static String getGraphName() {
    String graphName = System.getenv("GRAPH_NAME");
    log.info("graph_file is : {}", graphName);
    if (graphName != null) {
      return graphName;
    } else {
      return "Y:\\Графіки роботи ВТВС\\График выдачи докуметации.xlsx";
    }
  }
}
