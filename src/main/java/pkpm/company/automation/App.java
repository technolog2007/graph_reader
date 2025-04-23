package pkpm.company.automation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import pkpm.company.automation.services.GraphScanner;
import pkpm.company.automation.services.MakeSnapshot;

@Slf4j
public class App {

  private static LocalDateTime currentTime = LocalDateTime.now();
  private final static String GRAPH_NAME = getGraphName();

  public static void main(String[] args) {

    int startNum = optionChoice();
    GraphScanner gs = createGraphScanner(GRAPH_NAME);
    if (startNum == 1) {
      log.info("start 1 scenario");
      log.info("Start program time is : " + LocalDateTime.now());

      scanningAllTime(gs, getEndTime(), GRAPH_NAME);

      log.info("End program time is : " + LocalDateTime.now());
    }
    if (startNum == 2) {
      log.info("start 2 scenario");
      scanningWithMacros(gs, getEndTime(), GRAPH_NAME);
    }
  }

  private static int optionChoice() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Scanner scanner = new Scanner(System.in);
    Callable<String> inputTask = () -> {
      log.info("Оберіть варіант роботи програми:");
      log.info("- для постійного сканування введіть \"1\"");
      log.info("- для сканування із використанням макроса введіть \"2\"");
      return scanner.nextLine();
    };
    Future<String> future = executor.submit(inputTask);
    String selectedScenario = "2"; // за замовчуванням
    try {
      // Чекаємо максимум 60 секунд
      String userInput = future.get(20, TimeUnit.SECONDS);
      if (userInput.equals("1") || userInput.equals("2")) {
        selectedScenario = userInput;
      } else {
        log.info("Некоректний ввід. Виконується сценарій за замовчуванням.");
      }
    } catch (TimeoutException e) {
      log.info("Час вичерпано. Виконується сценарій за замовчуванням.");
    } catch (Exception e) {
      log.info("Сталася помилка: " + e.getMessage());
    } finally {
      executor.shutdownNow();
    }
    return Integer.parseInt(selectedScenario);
  }

  private static GraphScanner createGraphScanner(String graphName) {
    GraphScanner gs = new GraphScanner();
    gs.setOldSnapshot(new MakeSnapshot(graphName).getBs());
    return gs;
  }

  private static void scanningAllTime(GraphScanner gs, LocalDateTime endTime, String graphName) {
    while (currentTime.isBefore(endTime)) {
      gs.scanAllTime(GRAPH_NAME);
      update(getIntervalTime());
    }
  }

  private static void scanningWithMacros(GraphScanner gs, LocalDateTime endTime, String graphName) {
    while (currentTime.isBefore(endTime)) {
      log.info("heart beat ...");
      gs.scanButtonPress(graphName);
      update(getIntervalTime());
    }
  }

  private static void update(long pauseTime) {
    pause(pauseTime);
    currentTime = LocalDateTime.now();
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
      return "Y:\\Графіки роботи ВТВС\\График выдачи докуметации.xlsx";
    }
  }
}