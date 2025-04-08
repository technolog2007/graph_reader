package pkpm.company.automation.models;

import lombok.Getter;

@Getter
public enum GraphMessage {

  INFORM_ADD_POSITION("➕\uD83D\uDCC4 Додано нові позиції на вкладку \uD83D\uDC49 "),
  INFORM_ADD_POSITIONS("➕\uD83D\uDCC4 Додано нові позиції на вкладки \uD83D\uDC49 "),
  INFORM_ADD_FOLDER("➕\uD83D\uDCC2 Додана нова вкладка \uD83D\uDC49 "),
  INFORM_ADD_FOLDERS("➕\uD83D\uDCC2 Додані нові вкладки \uD83D\uDC49 "),
  INFORM_DELETE_FOLDER("\uD83D\uDDD1\uFE0F Видалена наступна вкладка \uD83D\uDC49 "),
  INFORM_DELETE_FOLDERS("\uD83D\uDDD1\uFE0F Видалені наступні вкладки \uD83D\uDC49 "),
  INFORM_NO_CHANGE("\uD83D\uDD04 No graph's changes!");

  private final String message;

  GraphMessage(String message) {
    this.message = message;
  }

}
