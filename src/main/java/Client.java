import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Client {
    public static String host;
    public static int port;

    public static void main(String[] args) {
        //Прочитать настройки приложения из файла настроек - например, номер порта сервера;
        clientSettings();
        //и запускаем циклы прослушивания и отправки в многопоточном режиме
        new ClientMultiThread(host, port);

        //Для выхода из чата нужно набрать команду выхода - “/exit”;
        //Каждое сообщение участников должно записываться в текстовый файл - файл логирования. При каждом запуске приложения файл должен дополняться.
    }

    public static void clientSettings() {
        Properties settings = new Properties();
        try {
            //выгрузим файл настроек
            settings.load(new FileInputStream("src/main/resources/settings.properties"));
            System.out.println(settings);
            //прочитаем требуемые значения
            host = settings.getProperty("host");
            System.out.println("Хост сервера: " + host);
            port = Integer.parseInt(settings.getProperty("port"));
            System.out.println("Порт сервера: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getCurrentDate() {
        Date time;
        String dTime;
        SimpleDateFormat dateFormat;

        time = new Date();//выставляем текущую дату
        dateFormat = new SimpleDateFormat("HH:mm:ss"); //устанавливаем формат даты
        dTime = dateFormat.format(time);
        return " { " + dTime + " } ";
    }
}
