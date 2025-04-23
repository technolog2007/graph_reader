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

  private final static String GRAPH_NAME = getGraphName();

  public static void main(String[] args) {
    log.info("Program started!");
    GraphScanner gs = createFirstSnapshot(GRAPH_NAME);
    scanningWithMacros(gs, getEndTime(), GRAPH_NAME);
  }


  private static GraphScanner createFirstSnapshot(String graphName) {
    try {
      GraphScanner gs = new GraphScanner();
      gs.setOldSnapshot(new MakeSnapshot(graphName).getBs());
      return gs;
    } catch (Exception e) {
      log.error("Initial snapshot creation failed: {}", e.getMessage());
      throw new RuntimeException("Cannot start program without initial snapshot");
    }
  }

  private static void scanningWithMacros(GraphScanner gs, LocalDateTime endTime, String graphName) {
    while (LocalDateTime.now().isBefore(endTime)) {
      log.info("scanning");
      gs.scanButtonPress(graphName);
      pause(getIntervalTime());
    }
  }

  private static void pause(long seconds) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
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
      log.warn("The program will run until {}!", System.getenv("END_TIME"));
      return dateTimeEnd;
    } catch (DateTimeParseException | NullPointerException e) {
      log.warn("The default program will run until 20:00!");
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
      String defaultName = "Y:\\Графіки роботи ВТВС\\График выдачи докуметации.xlsx";
      log.warn("GRAPH_NAME not found in env. Using default: {}", defaultName);
      return defaultName;
    }
  }
}