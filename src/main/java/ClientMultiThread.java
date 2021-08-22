import java.io.*;
import java.net.Socket;

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
        }
        try {
            //создаем потоки чтения и отправки
            inputClientConsole = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            this.pressNickname();
            new ReadMessage().start();
            new WriteMessage().start();
        } catch (IOException e) {
            //e.printStackTrace();
            //закрываем сокет при любой ошибке, кроме ошибки конструктора
            ClientMultiThread.this.shutDownService();
        }
    }


    private void pressNickname() {
        System.out.print("Введите свой ник >>");
        try {
            nickname = inputClientConsole.readLine();
            System.out.println("Привет, " + nickname + ". Вы вошли в чат " + Client.getCurrentDate());

            out.write("В чат зашёл " + nickname + " " + Client.getCurrentDate() + "\n");
            out.flush();
        } catch (IOException ignored) {
            // e.printStackTrace();
        }
    }

    private void shutDownService() {
        try {
            if (!client.isClosed()) {
                client.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {
            //  e.printStackTrace();
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
                        break;
                    }
                    System.out.println(message);//выводим полученное сообщение на консоль
                }
            } catch (IOException e) {
                //e.printStackTrace();
                ClientMultiThread.this.shutDownService();
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
                        break;
                    } else {
                        out.write(Client.getCurrentDate() + "(" + nickname + "): " + userMessage + "\n");
                        out.flush();
                    }

                } catch (IOException e) {
                    //e.printStackTrace();
                    ClientMultiThread.this.shutDownService();
                }
            }
        }
    }
}