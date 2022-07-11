package front;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cifras.DH_RC4;
import cifras.Padrao;
import cifras.RC4;
import cifras.SDES;
import interfaces.CryptoSystemInterface;
import net.ClientNet;
import net.ServerNet;

public class Window {
    private static JTextArea chat = new JTextArea("Chat:");
    private JFrame frame= new JFrame();
    private JButton botaoEnviar = new JButton("Enviar");
    private JButton botaoDefIP = new JButton("Setar IP:");
    private JButton botaoCryptoPadrao = new JButton("Padrão");
    private JButton botaoCryptoRC4 = new JButton("RC4");
    private JButton botaoCryptoSDES = new JButton("SDES");
    private JButton botaoCryptoDHRC4 = new JButton("DH-RC4");
    private JTextArea in = new JTextArea();
    private JTextArea ipIn = new JTextArea();
    private JTextArea rc4Key = new JTextArea();
    private String ipServer;
    private JScrollPane scrollPane = new JScrollPane(chat,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    private String inStr;

    private static ClientNet client;
    private Thread receiveThread;

    private static ServerNet server;

    private static CryptoSystemInterface cs = new Padrao();

    public Window () {
        chat.setBounds(30, 0, 400, 400);
        frame.add(scrollPane);
        frame.setSize(460, 900);
        frame.setLayout(null);
        frame.setVisible(true);
        scrollPane.setBounds(30, 50, 400, 400);
        chat.setEditable(false);

        botaoDefIP.setBounds(30, 20, 100, 15);
        botaoDefIP.addActionListener(IPListener);
        frame.add(botaoDefIP);
        ipIn.setBounds(150, 20, 200, 15);
        frame.add(ipIn);
        in.setBounds(30, 470, 400, 50);
        frame.add(in);

        botaoEnviar.setBounds(30, 550, 150, 50);
        frame.add(botaoEnviar);
        botaoEnviar.addActionListener(sendListener);

        botaoCryptoPadrao.setBounds(30, 620, 100, 30);
        botaoCryptoPadrao.addActionListener(setPadrao);
        frame.add(botaoCryptoPadrao);

        botaoCryptoRC4.setBounds(30, 670, 100, 30);
        rc4Key.setBounds(150, 670, 150, 30);
        botaoCryptoRC4.addActionListener(setRC4);
        frame.add(botaoCryptoRC4);
        frame.add(rc4Key);

        botaoCryptoSDES.setBounds(30, 720, 100, 30);
        botaoCryptoSDES.addActionListener(setSDES);
        frame.add(botaoCryptoSDES);

        botaoCryptoDHRC4.setBounds(30, 770, 100, 30);
        botaoCryptoDHRC4.addActionListener(setDHRC4);
        frame.add(botaoCryptoDHRC4);
    }

    //conectar o cliente ao servidor
    private static Runnable clienteConectar = new Runnable() {
        @Override
        public void run(){
            client.connect();
        }
    };

    //sempre estar disponivel para receber mensagens
    private static Runnable servidorRecebeMensagem = new Runnable() {
        @Override
        public void run(){
            while(true){
                String str = server.receiveText();
                str = cs.decrypt(str);
                chat.append("\n" + str);
            }
        }
    };

    //servidor deve aguardar conexão
    private static Runnable servidorConectar = new Runnable() {
        @Override
        public void run() {
            server.acceptConnection();
        }
    };

    //ActionListener -> botão de enviar mensagem
    private ActionListener sendListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e){
            inStr = in.getText();
            chat.append("\n" + inStr);
            in.setText("");
            inStr = cs.encrypt(inStr);
            client.sendText(inStr);
        }
    };

    // ActionListener -> definir sem criptografia
    private ActionListener setPadrao = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            cs = new Padrao();
        }
    };
    // ActionListener -> definir criptografia RC4
    private ActionListener setRC4 = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String key = rc4Key.getText();
            cs = new RC4(key);
        }
    };

    // ActionListener -> definir criptografia SDES
    private ActionListener setSDES = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            cs = (CryptoSystemInterface) new SDES();
        }
    };

    // ActionListener -> ação do botão de obter o ip do campo de entrada
    private ActionListener IPListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            ipServer = ipIn.getText();
            estabelecerConexao();
            receiveThread = new Thread(servidorRecebeMensagem);
            receiveThread.start();
        }
    };

    // ActionListener -> definir criptografia DH-RC4
    private ActionListener setDHRC4 = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            cs = new DH_RC4(ipServer);
        }
    };

    //procedimento de estabelecer conexão
    private void estabelecerConexao() {
        server = new ServerNet(3000);
        client = new ClientNet(ipServer, 3000);

        Thread th1 = new Thread(clienteConectar);
        Thread th2 = new Thread(servidorConectar);

        try{
            th1.start();
            th2.start();

            th1.join();
            th2.join();
        }
        catch (InterruptedException e){
            System.err.println(e);
        }
    }
}