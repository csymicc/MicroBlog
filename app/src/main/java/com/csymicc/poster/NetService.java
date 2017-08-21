package com.csymicc.poster;


import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Micc on 7/29/2017.
 */

public class NetService extends Thread {
    private Socket requestSocket = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    public static Queue<PosterMessage> sendList = new LinkedList<>();
    private NetOutput netOutput = null;
    private NetInput netInput = null;

    private Timer timer = new Timer();
    private int interval = 10000;

    private String IP = "192.168.1.104";
    private int port = 6457;

    class TimerTask1 extends TimerTask {
        public void run() {
            System.out.println("heart beat");
            if(oos != null) {
                PosterMessage impulse = new PosterMessage(-1);
                try {
                    oos.writeObject(impulse);
                    oos.reset();
                } catch (IOException e) {
                    timer.cancel();
                    e.printStackTrace();
                }
            }
            new Timer().schedule(new TimerTask2(), interval);
        }
    }

    class TimerTask2 extends TimerTask {
        public void run() {
            System.out.println("heart beat");
            if(oos != null) {
                PosterMessage impulse = new PosterMessage(-1);
                try {
                    oos.writeObject(impulse);
                    oos.reset();
                } catch (IOException e) {
                    timer.cancel();
                    e.printStackTrace();
                }
            }
            new Timer().schedule(new TimerTask1(), interval);
        }
    }

    private class NetOutput extends Thread{
        public boolean end = false;
        public void run() {
            while(!end) {
                if(!sendList.isEmpty()) {
                    try {
                        oos.writeObject(sendList.poll());
                        oos.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
    }

    private class NetInput extends Thread{
        public boolean end = false;
        public void run() {
            while(!end) try {
                PosterMessage mess = (PosterMessage) ois.readObject();
                Log.d("NetService: ", "Receive message");
                mess.show("NetService");
                if (mess != null) {
                    Message pMess = new Message();
                    pMess.what = mess.getType();
                    pMess.obj = mess;
                    BaseActivity.handler.sendMessage(pMess);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        try {
            requestSocket = new Socket(IP, port);
            oos = new ObjectOutputStream(requestSocket.getOutputStream());
            ois = new ObjectInputStream(requestSocket.getInputStream());
            timer.schedule(new TimerTask1(), 0);

            netOutput = new NetOutput();
            netOutput.start();

            netInput = new NetInput();
            netInput.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            ois.close();
            oos.close();
            requestSocket.close();
            timer.cancel();
            netOutput.end = true;
            netInput.end = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
