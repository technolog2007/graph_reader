# graph_reader

## Запуск

1.  Розархівуйте ZIP-архів.
2.  Перейдіть до каталогу, де знаходиться JAR-файл.
3.  Запустіть проєкт за допомогою команди:
4.  Додайте from.env в корінь проекту

    ```bash
    for /f "delims=" %%a in (config.env) do set "%%a"
    ```
    ```bash
    java -cp "lib/*:config/*:." -jar graph_reader-1.0.jar
    ```