/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package injekssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author ANGGA
 */
public class ClientThread extends Thread {

    private Socket mClientSocket;
    private Socket mServerSocket;
    private boolean mForwardingActive = false;

    public ClientThread(Socket aClientSocket) {
        mClientSocket = aClientSocket;
    }

    public void run() {
        InputStream clientIn;
        OutputStream clientOut;
        InputStream serverIn;
        OutputStream serverOut;
        try {
            // Melakukan Koneksi ke ROUTE 
            mServerSocket = new Socket(InjekSSH.ROUTE_HOST, InjekSSH.ROUTE_PORT);

            // Lakukan Keep-Alive menghindari terputusanya socket saat komunikasi
            mServerSocket.setKeepAlive(true);
            mClientSocket.setKeepAlive(true);

            // Membaca data Stream (Ouput dan Input)
            clientIn = mClientSocket.getInputStream();
            clientOut = mClientSocket.getOutputStream();
            serverIn = mServerSocket.getInputStream();
            serverOut = mServerSocket.getOutputStream();
        } catch (IOException ioe) {
            System.err.println("Can not connect to " + InjekSSH.ROUTE_HOST + ":" + InjekSSH.ROUTE_PORT);
            connectionBroken();
            return;
        }
        // Starting untuk pembelokkan data 
        mForwardingActive = true;
        belokTCP clientForward = new belokTCP(this, clientIn, serverOut);
        clientForward.start();
        belokTCP serverForward = new belokTCP(this, serverIn, clientOut);
        serverForward.start();

        System.out.println("Belokkan TCP " + mClientSocket.getInetAddress().getHostAddress()
                + ":" + mClientSocket.getPort() + " interkoneksi  " + mServerSocket.getInetAddress().getHostAddress()
                + ":" + mServerSocket.getPort() + " STARTED.");
    }

    public synchronized void connectionBroken() {
        try {
            mServerSocket.close();
        } catch (Exception e) {
        }
        try {
            mClientSocket.close();
        } catch (Exception e) {
        }

        if (mForwardingActive) {
            System.out.println("Belokkan TCP "
                    + mClientSocket.getInetAddress().getHostAddress()
                    + ":" + mClientSocket.getPort() + " interkoneksi "
                    + mServerSocket.getInetAddress().getHostAddress()
                    + ":" + mServerSocket.getPort() + " TERPUTUS.");
            mForwardingActive = false;
        }
    }
}
