package basestation.bot.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPConnection extends Connection {

    private String ip;
    private int port;
    private transient Socket clientSocket;
    private transient DataOutputStream outToServer;
    private transient BufferedReader inFromServer;
    private boolean connectionNotRefused;

    public TCPConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            this.clientSocket = new Socket(ip, port);
            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            connectionNotRefused = true;
            //this.clientSocket.setSoTimeout(500);
        } catch (IOException e) {
            System.err.println("Unable to establish connection! " + e);
            connectionNotRefused = false;
        }
    }

    @Override
    public boolean connectionActive() {
        return connectionNotRefused && clientSocket.isConnected();
    }

    public void destroy() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean sendKV(String messageType, String message) {
        String payload;
        payload = "<<<<" + messageType + "," + message + ">>>>";
        try {
            outToServer.writeBytes(payload);
            outToServer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            connectionNotRefused = false;
            return false;
        }
    }

    public String getIP() {
        return this.ip;
    }

    public String receive() {
        try {
            return inFromServer.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            connectionNotRefused = false;
            return null;
        }
    }
}