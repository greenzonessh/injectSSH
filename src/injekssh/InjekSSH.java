/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package injekssh;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ANGGA
 */
public class InjekSSH {

    public static int LISTEN_PORT = 6909; //Listen untuk port localhost
    public static String ROUTE_HOST = "10.19.19.19"; //Route Operator. ini contoh indosat
    public static int ROUTE_PORT = 8080; //port route operator. contoh indosat
    
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(LISTEN_PORT);
        System.out.println("Nama\t:Angga Dwi Hariadi\nJurusan\t:Sistem Informasi\nAsal\t:Universitas Jember\nUKM\t:Linux and Open Source(LaOS)\n");
        System.out.println("**Matkul SISTER Bypass Kuota Internet Operator**\n\nMenunggu Koneksi Login SSH datang.......");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientThread clientThread = new ClientThread(clientSocket);
            clientThread.start();
        }
    }
    
}
