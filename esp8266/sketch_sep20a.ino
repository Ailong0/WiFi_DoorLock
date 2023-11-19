//wifi远程门锁esp8066设计，基于udp
#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <Servo.h>
#ifndef STASSID
#define STASSID "Ailong_Link"//wifi名称
#define STAPSK  "ws4rfzvv"//wifi密码
#endif

Servo myServo;  // 定义Servo对象来控制

unsigned int localPort = 8888;//开放端口8888   测试ip地址 192.168.31.181
const int ledPin = D4;

char packetBuffer[UDP_TX_PACKET_MAX_SIZE+1];
char ReplyBuffer[]="收到\r\n";

WiFiUDP Udp;
void setup() {
  // put your setup code here, to run once:
  pinMode(ledPin,OUTPUT);
  myServo.attach(14); //D5  舵机引脚
  Serial.begin(115200);
  WiFi.mode(WIFI_STA);//wifi STA模式
  WiFi.begin(STASSID,STAPSK);
  while(WiFi.status()!=WL_CONNECTED)//检测WiFi连接
  {
    Serial.print('.');
    delay(500);
  }
  Serial.println();
  Serial.print("Connected! IP address:");
  Serial.println(WiFi.localIP());//打印ip地址
  Serial.printf("UDP server on port %d \n",localPort);
  Udp.begin(localPort);
}

void loop() {
  // put your main code here, to run repeatedly:
  int packetSize = Udp.parsePacket();
  if(packetSize)
  {
    Serial.printf("Received packet of size %d form %s : %d\n (to %s:%d,free heap = %d B)\n",
          packetSize,
          Udp.remoteIP().toString().c_str(),Udp.remoteIP(),
          Udp.destinationIP().toString().c_str(),Udp.remoteIP(),
          ESP.getFreeHeap());
    int n = Udp.read(packetBuffer,UDP_TX_PACKET_MAX_SIZE);
    Serial.println(n);
    packetBuffer[n]=0;
    Serial.println("Contents:");
    Serial.println(packetBuffer);

    Udp.beginPacket(Udp.remoteIP(),Udp.remotePort());
    Udp.write(ReplyBuffer);
    Udp.endPacket();

    Serial.println(packetBuffer);
    if(packetBuffer[0]=='1')//收到“1”则开锁，三秒后复位
    {
     digitalWrite(ledPin,LOW);
      myServo.write(180);
      delay(3000);
       digitalWrite(ledPin,HIGH);
      myServo.write(0);
    }
  }
}
