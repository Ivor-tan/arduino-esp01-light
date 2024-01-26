package com.ivor.arduino_esp01_light.tools;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpTool {
    private String TAG = "UdpTool";
    DatagramSocket mSocket;
    private String IP = "255.255.255.255";
    private int PORT = 8888;
    Context context;

    public UdpTool(Context context) {
        this.context = context;
    }

    public void sendMessage(String msg) {
        sendMessage(msg, IP);
    }

    /**
     * 发送信息
     *
     * @param msg 信息内容
     * @param ip  对方ip地址
     */
    public void sendMessage(String msg, String ip) {
        // 需要开线程来发数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket == null) {
                    try {
                        // 这个是本机的端口号
                        mSocket = new DatagramSocket(PORT);

                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }
                // 将字符串转换成字节流，因为底层的传输都是字节传输
                byte[] data = msg.getBytes();
                try {
                    // 对方ip和端口
                    DatagramPacket pack = new DatagramPacket(data, data.length, InetAddress.getByName(ip), PORT);
                    mSocket.send(pack);
                    Log.d(TAG, "发送成功！");
                } catch (IOException e) {
                    Log.d(TAG, "发送失败！");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 接收消息的函数
     */
    public void setUpdListener(UPDListener listener) {
        // 创建线程，同理，接收信息也要放在线程里面接收
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (mSocket == null) {
                        mSocket = new DatagramSocket(PORT);
                    }
                    String str;
                    while (true) {
                        // 创建一个空的字节数组
                        byte[] data = new byte[1024];
                        // 将字节数组和数组的长度传进DatagramPacket 创建的对象里面
                        DatagramPacket pack2 = new DatagramPacket(data, data.length);
                        Log.v("s", "pack2");
                        Log.v("s", "开始 接收");
                        try {
                            // socket对象接收pack包，程序启动的时候，socket会一直处于阻塞状态，直到有信息传输进来
                            mSocket.receive(pack2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 获取发送数据的IP地址
                        String ip = pack2.getAddress().getHostAddress();
                        // 获取发送数据端的端口号
                        int port = pack2.getPort();
                        str = new String(pack2.getData(), 0, pack2.getLength()); // 将字节数组转化成字符串表现出来
                        // 开启接收端口后会持续接收数据，只有页面可见的时候才将收到的数据写入
                        if (listener != null) {
                            listener.OnReceive(str);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 关闭通信
     */
    public void closeSocket() {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }
    }

    public interface UPDListener {
        void OnReceive(String msg);
    }
}