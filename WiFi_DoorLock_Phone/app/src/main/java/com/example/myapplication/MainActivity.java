package com.example.myapplication;

import static android.os.Build.getSerial;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Build;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private static final String ESP8266_IP = "192.168.31.181";  // ESP8266的IP地址
    private static final int ESP8266_PORT = 8888;               // ESP8266的端口号
    private static final String CORRECT_PASSWORD = "566456789";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String PREF_IS_PASSWORD_VERIFIED = "IsPasswordVerified";
    private boolean isPasswordVerified = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        isPasswordVerified = prefs.getBoolean(PREF_IS_PASSWORD_VERIFIED, false);

        TextView Hello = findViewById(R.id.myTextView);

        Log.d(TAG,"hello");
        System.out.println("hello");
//        System.out.println(phoneNumber);
//        Hello.setText(phoneNumber);

        Button myButton = findViewById(R.id.button);
        EditText passwordEditText = findViewById(R.id.PassWDeditText);
        if (isPasswordVerified) {
            // 密码已验证，启用按钮
            myButton.setEnabled(true);
            passwordEditText.setVisibility(View.INVISIBLE);
        } else {
            // 密码未验证，禁用按钮
            myButton.setEnabled(false);
            passwordEditText.setVisibility(View.VISIBLE);
        }



        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUnlockCommandToESP8266();

            }

        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 不需要实现
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 不需要实现
            }

            @Override
            public void afterTextChanged(Editable s) {
                String enteredPassword = s.toString();
                if (enteredPassword.equals(CORRECT_PASSWORD)) {
                    myButton.setEnabled(true); // 密码正确，启用按钮

                    isPasswordVerified = true;
                    // 将密码验证状态保存到 SharedPreferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(PREF_IS_PASSWORD_VERIFIED, true);
                    editor.apply();
                    passwordEditText.setVisibility(View.INVISIBLE);
                } else {
                    myButton.setEnabled(false); // 密码不正确，禁用按钮
//                    Hello.setEnabled(true);
                }
            }
        });

        }


    private void sendUnlockCommandToESP8266() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    // 创建UDP套接字
                    socket = new DatagramSocket();

                    // 设置目标ESP8266的地址和端口
                    InetAddress destAddress = InetAddress.getByName(ESP8266_IP);
                    int destPort = ESP8266_PORT;

                    // 构造要发送的数据
                    String message = "1"; // 发送"1"来开锁
                    byte[] sendData = message.getBytes();
                    int sendSize = sendData.length;

                    // 创建数据报文
                    DatagramPacket packet = new DatagramPacket(sendData, sendSize, destAddress, destPort);

                    // 发送数据包给ESP8266
                    socket.send(packet);

                    System.out.println("Unlock command sent to ESP8266");
                } catch (IOException e) {
                    System.err.println("Failed to send data: " + e.getMessage());
                } finally {
                    if (socket != null) {
                        socket.close();
                    }
                }
            }
        }).start();
    }

}