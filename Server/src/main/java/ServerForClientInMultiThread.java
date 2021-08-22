import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

//класс для создания подключений в многопоточном режиме
public class ServerForClientInMultiThread extends Thread {

    //сокет клиента, для общения с ним
    private final Socket socket;
    //потоки чтения и записи
    private final BufferedReader in;
    private final BufferedWriter out;

    //в конструктор будем передавать как раз таки сокет который получен при подключении клиента
    public ServerForClientInMultiThread(Socket socket) throws IOException {
        this.socket = socket;
        //инициализируем потоки ввода вывода каждого клиента, исключения пробросим наверх
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();//run();
    }

    //ждём бесконечно от клиента сообщение, в случае получения отправляем всем клиентам
    @Override
    public void run() {
        //первое сообщение - информируем всех
        String message;
        try {
                while (true) {
                    message = in.readLine();
                    if (message == null) {
                        this.shutDownService();
                        break;
                    }
                    Server.LOGGER.log(Level.INFO, message);
                    for (ServerForClientInMultiThread allClient : Server.clientList) {
                        if(allClient == this) continue; //кроме отправителя
                        allClient.send(message);
                    }
                }

        } catch (IOException e) {
            this.shutDownService();
        }
    }

        private void shutDownService () {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                    in.close();
                    out.close();
                    for (ServerForClientInMultiThread client : Server.clientList) {
                        if (client.equals(this)) client.interrupt();
                        Server.clientList.remove(this);
                    }
                }
            } catch (IOException ignored) {
            }
        }

        private void send (String message){
            try {
                out.write(message + "\n");
                out.flush();
            } catch (IOException ignored) {
            }
        }
    }