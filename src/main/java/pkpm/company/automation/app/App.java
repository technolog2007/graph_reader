package pkpm.company.automation.app;

import pkpm.company.automation.services.ExelReader;

public class App {
    private static final String FILE_NAME = System.getenv("file_name");
    public static void main( String[] args ) {

        ExelReader.read(FILE_NAME);
    }
}
