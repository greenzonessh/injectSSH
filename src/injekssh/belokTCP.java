/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package injekssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

/**
 *
 * @author ANGGA
 */
public class belokTCP extends Thread {

    InputStream mInputStream;
    OutputStream mOutputStream;
    ClientThread mParent;

    public belokTCP(ClientThread aParent, InputStream aInputStream, OutputStream aOutputStream) {
        mInputStream = aInputStream;
        mOutputStream = aOutputStream;
        mParent = aParent;
    }

    public void run() {
        byte[] buffer = new byte[8192];
        int pos;
        String ResponseOK = "HTTP/1.0 200 Connection established\r\n\r\n";
        try {
            while (true) {
                int bytesRead = mInputStream.read(buffer);
                if (bytesRead == -1) {
                    break;
                }

                String data = new String(buffer, 0, bytesRead);
                if (data.indexOf("CONNECT") >= 0) {
                    String[] split = data.split("\r\n");
                    String method = split[0];
                    String[] CmdArray = method.split(" ");

                    String RawUrl = CmdArray[1];
                    String[] Args = RawUrl.split(":");
                    String host = Args[0];
                    int port = Integer.parseInt(Args[1]);

                    InetAddress address = InetAddress.getByName(host);
                    String ip = address.getHostAddress();
                    String urlHost = ip + ":" + port;
                    String Protocol = CmdArray[2];

                    String HEADER = "HEAD http://mmsc.indosat.com.server4.operamini.com/ HTTP/1.1\r\n"
                            + "Host: mmsc.indosat.com.server4.operamini.com\r\n\r\n\r\n"
                            + "CONNECT " + urlHost + " " + Protocol + "\r\n\r\n";
                    mOutputStream.write(HEADER.getBytes());
                    mOutputStream.flush();

                } else {
                    String[] split = data.split("\r\n");
                    if (split[0].startsWith("HTTP")) {
                        data = split[0].substring(8).trim();
                        pos = data.indexOf("200");
                        if (pos < 0) {
                            mOutputStream.write(ResponseOK.getBytes());
                            mOutputStream.flush();
                        } else {
                            mOutputStream.write(buffer, 0, bytesRead);
                            mOutputStream.flush();
                        }
                    } else {
                        mOutputStream.write(buffer, 0, bytesRead);
                        mOutputStream.flush();
                    }
                }
            }
        } catch (IOException e) {

        }
        mParent.connectionBroken();
    }

}
