package net;

import java.io.*;
import java.net.*;

public class ClientNet {
    private Socket s;

    private PrintStream ps;

    private String addr;

    private int porta;

    public ClientNet(String addr, int porta){
        this.addr = addr;
        this.porta = porta;
    }
    
    public void connect(){
        while(true){
            try{
                s = new Socket(this.addr, this.porta);
                ps = new PrintStream(s.getOutputStream());
                break;
            }
            catch(Exception e){
                System.err.println("Falha na conexão.");
            }
        }
    }

    public void sendText(String str) {
        // send to client
        ps.println(str);
    }

    public void closeConnection(){
        // close connection.
        try{
            s.close();
        }
        catch(Exception e){
            System.err.println(e);
        }
    }
}
