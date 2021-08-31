import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


class ServerTest {

    @org.junit.jupiter.api.Test
    void getCurrentDateTest() {
        Date time;
        String dTime;
        String actual = Server.getCurrentDate();//здесь оптимальное место для получения актуального значения
        time = new Date();

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
        dTime = DATE_FORMAT.format(time);
        String expected = " { " + dTime + " } ";
        assertEquals(expected, actual);
    }
}