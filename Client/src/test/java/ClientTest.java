import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @org.junit.jupiter.api.Test
    void getCurrentDateTest() {
        Date time;
        String dTime;
        String actual = Client.getCurrentDate();//здесь оптимальное место для получения актуального значения
        time = new Date();

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
        dTime = DATE_FORMAT.format(time);
        String expected = " { " + dTime + " } ";
        assertEquals(expected, actual);

    }

    //такой себе тест
    @org.junit.jupiter.api.Test
    void clientSettingsTest(){
        //частично правильно, правда метод смотрит родительскую папку относительно себя, то есть папку расположенную в Client
        File file = new File("src/main/resources/settings.properties");
        boolean expected = file.exists();
        assertEquals(expected, Client.clientSettings());
    }
}