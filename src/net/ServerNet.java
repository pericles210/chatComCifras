package net;

import java.io.*;
import java.net.*;

public class ServerNet {
    private ServerSocket ss;
    private Socket s;
    private BufferedReader bf;
    private InputStreamReader in;
    private int porta;
    private String str;

    public ServerNet(int porta){
        this.porta = porta;
    }
    public void acceptConnection(){
        try{
            ss = new ServerSocket(this.porta);
        
            // connect it to client socket
            s = ss.accept();
            System.out.println("Connection established");
        
            // to send data to the client
            in = new InputStreamReader(s.getInputStream());
            bf = new BufferedReader(in);
            
        }
        catch(Exception e){
            System.err.println(e);
        }
    }
            
    public String receiveText() {
        StringBuilder sb = new StringBuilder();
        // receive from the server
        try {
            str = bf.readLine();
            sb.append(str);
            while (bf.ready()) {
                sb.append('\n');
                str = bf.readLine();
                sb.append(str);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return sb.toString();
    }

    public void closeConnection() {
        // close connection.
        try {
            s.close();
        } catch (Exception e) {
            System.err.println(e);
        }

    }
}
