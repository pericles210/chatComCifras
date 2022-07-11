package cifras;

import interfaces.CryptoSystemInterface;
import net.ClientNet;
import net.ServerNet;

import java.util.Random;

public class DH_RC4 implements CryptoSystemInterface {
    private static ClientNet client;
    private static ServerNet server;
    private Random rand = new Random();
    private RC4 rc4;
    private String addr;
    private static long P = 23, G = 9, mySecretKey, myPublicKey;
    private static String friendsPublicKey;
    public DH_RC4(String addr){
        this.addr = addr;
        this.client = new ClientNet(this.addr, 3001);
        this.server = new ServerNet(3001);
        this.mySecretKey = rand.nextInt(100);
        this.myPublicKey = power(G, this.mySecretKey, P);
        trocaDeChaves();
        this.rc4 = new RC4(friendsPublicKey);
    }
    @Override
    public String encrypt(String msg) {
        return this.rc4.encrypt(msg);
    }

    @Override
    public String decrypt(String msg) {
        return this.rc4.decrypt(msg);
    }

    private void trocaDeChaves(){
        StringBuilder sb = new StringBuilder();
        sb.append(":::");
        sb.append(mySecretKey);

        Thread th1 = new Thread(clienteConectar);
        Thread th2 = new Thread(servidorConectar);
        Thread th3 = new Thread(servidorRecebeMensagem);

        try{
            th1.start();
            th2.start();

            th1.join();
            th2.join();

            client.sendText(sb.toString());
            th3.start();
            th3.join();

            client.closeConnection();
            server.closeConnection();
        }
        catch (InterruptedException e){
            System.err.println(e);
        }
    }

    // Power function to return value of a ^ b mod P
    private static long power(long a, long b, long p) {
        if (b == 1)
            return a;
        else
            return (((long)Math.pow(a, b)) % p);
    }

    private static Runnable servidorConectar = new Runnable() {
        @Override
        public void run() {
            server.acceptConnection();
        }
    };

    private static Runnable clienteConectar = new Runnable() {
        @Override
        public void run(){
            client.connect();
        }
    };

    private static Runnable servidorRecebeMensagem = new Runnable() {
        @Override
        public void run(){
            String mensagem;
            while(true){
                mensagem = server.receiveText();
                if(mensagem.charAt(0) == ':' && mensagem.charAt(1) == ':' && mensagem.charAt(2) == ':') {
                    break;
                }
            }
            friendsPublicKey = mensagem.substring(3);
        }
    };
}
