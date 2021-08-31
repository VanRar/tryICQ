import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;

//класс для создания подключений в многопоточном режиме
public class ServerForClientInMultiThread extends Thread implements Comparable<ServerForClientInMultiThread> {

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
        //start(); перенесен в класс Сервер
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
//                for (ServerForClientInMultiThread allClient : Server.clientList) {
//                    if (allClient == this) continue; //кроме отправителя
//                    allClient.send(message);
//                }
                //меняем на потокобезопасную коллекцию
//                    for (ServerForClientInMultiThread allClient : Server.clientListCSLM) {
//                        allClient.send(message);
//
//                    }


                    for(ServerForClientInMultiThread allClient : Server.clientListCSLM){
                        if(allClient == this) continue; //кроме отправителя
                        allClient.send(message);
                }
            }

        } catch (IOException e) {
            this.shutDownService();
        }
    }

    private void shutDownService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
//                for (ServerForClientInMultiThread client : Server.clientList) {
//                    if (client.equals(this)) client.interrupt();
//                    Server.clientList.remove(this);
//                }
                //меняем на потокобезопасную коллекцию
                Server.clientListCSLM.remove(this);

            }
        } catch (IOException ignored) {
        }
    }

    private void send(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
    }

//    @Override
//    public int compare(ServerForClientInMultiThread o1, ServerForClientInMultiThread o2) {
//        return o1.hashCode() - o2.hashCode();
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerForClientInMultiThread that = (ServerForClientInMultiThread) o;
        return Objects.equals(socket, that.socket) && Objects.equals(in, that.in) && Objects.equals(out, that.out);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket, in, out);
    }

    @Override
    public int compareTo(ServerForClientInMultiThread o) {
        return o.hashCode() -  this.hashCode();
    }
}