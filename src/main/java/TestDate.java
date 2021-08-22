import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDate {


    public static void main(String[] args) {
         Date time;
         String dTime;
         SimpleDateFormat dateFormat;


        time = new Date();//выставляем текущую дату
        dateFormat = new SimpleDateFormat("HH:mm:ss"); //устанавливаем формат даты
        dTime = dateFormat.format(time);


        System.out.println(dTime);
    }
}
