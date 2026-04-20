package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class SocketClient implements Runnable {

    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageListener listener;

    public SocketClient(MessageListener listener) throws IOException {
        this.listener = listener;
        this.socket = new Socket(HOST, PORT);
        this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (listener != null) listener.onMessage(line);
            }
        } catch (IOException e) {
            if (listener != null) listener.onMessage("ERROR|Connexion au serveur perdue.");
        } finally {
            close();
        }
    }

    public void send(String command) {
        if (out != null) out.println(command);
    }

    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

    public interface MessageListener {
        void onMessage(String message);
    }
}
