package com.example.huski;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class Peripherique extends Thread {
    private String nom;
    private String adresse;
    private Handler handler = null;
    private BluetoothDevice device = null;
    private BluetoothSocket socket = null;
    private BluetoothSocket fallbackSocket;
    private InputStream receiveStream = null;
    private OutputStream sendStream = null;
    private TReception tReception;
    public final static int CODE_CONNEXION = 0;
    public final static int CODE_RECEPTION = 1;
    public final static int CODE_DECONNEXION = 2;
    public final static int SIZE_BUFFER = 75; //72 max, margin 3

    /**
     * Object that is used to open a socket between the application and the gateway to communicate
     *
     * @param device the device to connect to
     * @param handler the handler that handles the messages received by the Peripherique
     */
    public Peripherique(BluetoothDevice device, Handler handler) {
        if (device != null) {
            this.device = device;
            this.nom = device.getName();
            this.adresse = device.getAddress();
            this.handler = handler;
        } else {
            this.device = device;
            this.nom = "Aucun";
            this.adresse = "";
            this.handler = handler;
        }

        try {
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            receiveStream = socket.getInputStream();
            sendStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            socket = null;
        }


    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String toString() {
        return "\nNom : " + nom + "\nAdresse : " + adresse;
    }

    /**
     * Opens the socket and sends a CODE_CONNEXION message to the handler
     */
    public void connecter() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Class<?> clazz = socket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};

                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[] {Integer.valueOf(1)};

                    fallbackSocket = (BluetoothSocket) m.invoke(socket.getRemoteDevice(), params);
                    receiveStream = fallbackSocket.getInputStream();
                    sendStream = fallbackSocket.getOutputStream();
                    fallbackSocket.connect();
                    Message msg = Message.obtain();
                    Log.d("CodeConnexion", msg.getData().toString());
                    msg.arg1 = CODE_CONNEXION;
                    handler.sendMessage(msg);

                    if (fallbackSocket != null)
                        tReception = new TReception(handler);
                    tReception.start();

                } catch (IOException e) {
                    System.out.println("<Socket> error connect");
                    Log.d("CONNECTERROR", "socket connect failed");
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * closes the socket between the app and the gateway
     * @return true if the deconnection was successfull
     */
    public boolean deconnecter() {
        try {
            tReception.arreter();
            Message deconnexion = new Message();
            deconnexion.arg1 = CODE_DECONNEXION;
            handler.sendMessage(deconnexion);
            socket.close();
            return true;
        } catch (IOException e) {
            System.out.println("<Socket> error close");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends the data through the socket
     * @param data the data to send
     */
    public void envoyer(String data) {
        if (socket == null)
            return;

        try {
            sendStream.write(data.getBytes());
            sendStream.flush();
        } catch (IOException e) {
            System.out.println("<Socket> error send");
            e.printStackTrace();
        }
    }

    /**
     * The thread that's going to listen to the gateway and send the received messages to the handler
     */
    public class TReception extends Thread {
        Handler handlerUI;
        private boolean fini;

        TReception(Handler h) {
            handlerUI = h;
            fini = false;
        }

        /**
         * Listens to the socket and sends the messages received
         */
        @Override
        public void run() {
            while (!fini) {
                try {
                    if (receiveStream.available() > 0 && receiveStream.available() < (SIZE_BUFFER + 10)) {
                        byte buffer[] = new byte[SIZE_BUFFER];
                        int k = receiveStream.read(buffer, 0, SIZE_BUFFER);
                        Log.d("In TRECEPTION", "waiting for data to receive");
                        if (k > 0) {
                            byte rawdata[] = new byte[k];
                            for (int i = 0; i < k; i++)
                                rawdata[i] = buffer[i];

                            String data = new String(rawdata);
                            Log.d("In TRECEPTION", "data received: " + data);

                            Message msg = Message.obtain();
                            msg.arg1 = Peripherique.CODE_RECEPTION;
                            msg.obj = data;
                            handlerUI.sendMessage(msg);
                        }
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    //System.out.println("<Socket> error read");
                    e.printStackTrace();
                }
            }
        }

        /**
         * Stops the listening
         */
        public void arreter() {
            if (fini == false) {
                fini = true;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
