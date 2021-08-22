import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

public class ClientMultiThread {
    private Socket client;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader inputClientConsole;
    private String nickname;


    public ClientMultiThread(String host, int port) {
        try {
            this.client = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Подключение не удалось " + Client.getCurrentDate());
            Client.LOGGER.log(Level.WARNING, "Подключение не удалось");
        }
        try {
            //создаем потоки чтения и отправки
            inputClientConsole = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            this.pressNickname();
            new ReadMessage().start();
            new WriteMessage().start();
            Client.LOGGER.log(Level.INFO, "созданы потоки чтения и отправки");
        } catch (IOException e) {
            //e.printStackTrace();
            //закрываем сокет при любой ошибке, кроме ошибки конструктора
            ClientMultiThread.this.shutDownService();
            Client.LOGGER.log(Level.WARNING, "Не удалось создать потоки чтения и отправки");
        }
    }


    private void pressNickname() {
        System.out.print("Введите свой ник >>");
        try {
            nickname = inputClientConsole.readLine();
            System.out.println("Привет, " + nickname + ". Вы вошли в чат " + Client.getCurrentDate());
            Client.LOGGER.log(Level.INFO, "Клиент вошел в чат");
            out.write("В чат зашёл " + nickname + " " + Client.getCurrentDate() + "\n");
            out.flush();
        } catch (IOException ignored) {
            // e.printStackTrace();
            Client.LOGGER.log(Level.WARNING, "Не удалось войти в чат");
        }
    }

    private void shutDownService() {
        try {
            if (!client.isClosed()) {
                client.close();
                in.close();
                out.close();
                Client.LOGGER.log(Level.CONFIG, "приложение выключено");
            }
        } catch (IOException ignored) {
            //  e.printStackTrace();
            Client.LOGGER.log(Level.WARNING, "не удалось выключить приложение");
        }
    }

    private class ReadMessage extends Thread {
        @Override
        public void run() {
            String message;

            try {
                while (true) {
                    message = in.readLine();//читаем сообщение с сервера
                    if ("/exit".equals(message)) {
                        ClientMultiThread.this.shutDownService();
                        Client.LOGGER.log(Level.INFO, "Клиент вышел из чата");
                        break;
                    }
                    System.out.println(message);//выводим полученное сообщение на консоль
                    Client.LOGGER.log(Level.INFO, "получено сообщение: " + message);
                }
            } catch (IOException e) {
                //e.printStackTrace();
                ClientMultiThread.this.shutDownService();
                Client.LOGGER.log(Level.WARNING, "полетел цикл чтения сообщений");
            }
        }
    }


    private class WriteMessage extends Thread {

        @Override
        public void run() {
            while (true) {
                String userMessage;
                try {
                    //по хорошему дату заводить здесь, но тут скорее всего миллисекунды, так что не критично
                    userMessage = inputClientConsole.readLine();
                    if ("/exit".equals(userMessage)) {
                        out.write(nickname + " покинул чат в " + Client.getCurrentDate() + "\n");
                        out.flush();
                        ClientMultiThread.this.shutDownService();
                        Client.LOGGER.log(Level.INFO, nickname + " покинул чат в " + Client.getCurrentDate());//немного другое время будет
                        break;
                    } else {
                        out.write(Client.getCurrentDate() + "(" + nickname + "): " + userMessage + "\n");
                        out.flush();
                        Client.LOGGER.log(Level.INFO, "Отправлено сообщение: " + Client.getCurrentDate() + "(" + nickname + "): " + userMessage);
                    }

                } catch (IOException e) {
                    //e.printStackTrace();
                    ClientMultiThread.this.shutDownService();
                    Client.LOGGER.log(Level.WARNING, "Полетел цикл отправки сообщений");
                }
            }
        }
    }
}