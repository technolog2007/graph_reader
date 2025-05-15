package pkpm.company.automation.models;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Employees {

  EMPLOYEE_1("Didenko"),
  EMPLOYEE_2("Evdokimov"),
  EMPLOYEE_3("Ivaskiv"),
  EMPLOYEE_4("Churilov"),
  EMPLOYEE_5("Cherkashin");

  private final String name;

  Employees(String name) {
    this.name = name;
  }

  public static Employees fromName(String name){
    return Arrays.stream(Employees.values())
        .filter(element -> element.getName().equals(name))
        .findFirst()
        .orElse(null);
  }


}
