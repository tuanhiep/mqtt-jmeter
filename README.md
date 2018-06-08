mqtt-jmeter
===========

This is the plugin for Jmeter to Test MQTT protocol.

**Update:** The live project is moved to https://github.com/emqtt/mqtt-jmeter


# Introduction

The MQTT Plugin in Jmeter is used for the injection testing of MQTT server. It permits the complete
test correspond many scenarios, which depend on type of messages, type of connections. Thanks to it's
interface graphic, the fact of testing mqtt protocol is taken easily.


# How to install MQTT plugin in Jmeter

From the repository: https://github.com/tuanhiep/mqtt-jmeter  
Get the source code, go to mqtt-jemeter folder and and use the command maven in terminal (Ubuntu):

	mvn clean install package

to obtain the file **mqtt-jmeter.jar** in **mqtt-jemeter/target**.  
Put the **mqtt-jemeter.jar** in the folder **lib/ext** of Jmeter
(to be downloaded on http://jmeter.apache.org/download_jmeter.cgi ).

Remind that, it's necessary to update the file **ApacheJMeter_core.jar** in the repository lib/ext of Jmeter.
Update the file messages.properties in the folder :/org/apache/jmeter/resources/
in **ApacheJMeter_core.jar** by new file messages.properties from
https://github.com/tuanhiep/mqtt-jmeter/tree/master/ressource

#  How to use MQTT plugin in Jmeter

##  MQTT Publisher

The interface graphic of Jmeter:

![Alt text](images/Main_Interface_Jmeter.png)

Right-click “Thread” and choose : Add → Sampler → MQTT Publisher

![Alt text](images/MQTT_Publisher.png)

In the principal interface of MQTT Publisher we have the fields:  
*Connection Info*  

**Name:** Name of the MQTT Publisher  
**Comments:** Your comments  
**Provider URL:** the address of MQTT server example: tcp://localhost:1883  
**Client Id:** Your Id in the session with MQTT server example: Noel De Palma  
**List Topic:** The list of topic's names you want to publish to  
The topic's names are separated by a comma ","  
For example: List Topic: GRENOBLE/LIG,GRENOBLE/UJF-LIG  
This means, you'll publish to 2 topics: GRENOBLE/LIG and GRENOBLE/UJF-LIG  
You can choose the option **One connection per topic** : It means that for each topic in the list above, the plugin will create one correspondant
 connection. Note that, if the client Id is "Noel De Palma", for example, and you have 2 topics in the list, so the plugin will create 2 connections with 2 Client 
Id : "Noel De Palma 0" and "Noel De Palma 1"  
The plugin provide two strategies to publish:  
 **1: Round Robin** : You'll publish to the topics in equal portions and in circular order  
 **2: Random** : You'll publish to a random topic in the list above  
If the list of topic has only one topic, so regardless the strategies, you'll publish to the topic for sure.  
 **Use Authorization check box:** Necessary in the case the connection needs the username and
password  
**User:** Your username  
**Password:** Your password  
**Number of samples to aggregate:** In other way, the number of messages you want to publish to
the MQTT sever in this MQTT Publisher thread, with the value like the configuration below.  
**Message Type:** You can choose : Text, Generated Value, Fixed Value, Random Byte Array (more detail below)  

![Alt text](images/Publisher_Text.png)  

*Encoding*  
 
**Message Format** : The type of encoding that you'll encode your data before publish .You can choose Binary Codec, Base64, BinHex or Plain Text  
If you choose Plain Text, you can choose 6 types of charsets : UTF-8, UTF-16, US-ASCII,UTF-16LE, UTF-16BE, ISO-8859-1.  
Of course, you can choose to no encoding too.  
  
*Option*  

**Add TimeStamp check box:** Add the timestamps to the message. The timestamps is 8 bytes  
**Add Number Sequence check box:** Add the number sequence to the message. Example: if you
publish 100 messages in your session, the message is numbered from 0 to 99. The number sequence 
field in the message is 4 bytes.  
**Retained check box:** You publish the messages as retained messages or not. The retain flag for an
MQTT message is set to false by default. This means that a broker will not hold onto the message 
so that any subscribers arriving after the message was sent will not see the message. By setting 
the retain flag, the message is held onto by the broker, so when the late arrivers connect to the 
broker or clients create a new subscription they get all the relevant retained messages”  
**Quality of service:** Three levels:  
0 : At most once  
1 : At least once  
2 : Exactly once  
Each message in MQTT can have its quality of service and retain flag set. The quality of service
advises the code if and how it should ensure the message arrives. There are three options, 0 (At Most Once),
1 (At Least Once) and 2 (Exactly Once). By default, a new message instance is set to "At Least Once",a Quality 
of Service (QoS) of 1, which means the sender will deliver the message at least once and, if there's no acknowledgement
 of it, it will keep sending it with a duplicate flag set until an acknowledgement turns up, at which point the
client removes the message from its persisted set of messages.  
A QoS of 0, "At Most Once", is the fastest mode, where the client doesn't wait for an
acknowledgement. This means, of course, that if there’s a disconnection or server failure, a message
may be lost. At the other end of the scale is a QoS of 2, "Exactly Once", which uses two pairs of
exchanges, first to transfer the message and then to ensure only one copy has been received and is
being processed. This does make Exactly Once the slower but most reliable QoS setting.

With MQTT Publisher in Jmeter, three type of messages can be sent (Message Type):  
**Text:** The text message, without flag header and the server MQTT can deliver it like a normal
text if you choose *No Encoding* or *Plain Text*.  

![Alt text](images/Publisher_Text.png)  
 
![Alt text](images/Receive.png)  

1 byte “flag header” for the messages of type: Generated value, Fixed value  

![Alt text](images/Flag_Header.png)  
In the flag header, if one field is set to 1, it means, we use the header in the message.
For example: With this flag header  

![Alt text](images/Flag_Header_Example.png)  
It means that, in the message, we have :  
![Alt text](images/Message.png)  

**Generated Value:**  
The generated value can be of type: Integer, Long, Float, Double within the range [Min,Max] .
The type of random can be: Pseudo random or Secure random. In the two cases, we can set the Seed
for the generator.  

![Alt text](images/Publisher_generated_value.png)  
  
**Fixed Value:**  
The fixed value can be of type: Integer, Long, Float, Double, String within the range [Min,Max].  
  
![Alt text](images/Publisher_fixed_value.png)  
**Random Byte Array:**  

The data in form of random byte array with the size array as an input.  
For example, if you type 9 in the field size array, so without encoding, time header, number sequence, the message has 9 bytes of content(random data) and 1 byte of flag header  
and so, 10 bytes to publish.
The images below show when you publish with option : One Connection Per Topic and the data is type of Random Byte Array.
In the terminal,you see that there are two connections of 2 client Id : "Didier Donsez 0" and "Didier Donsez 1" 
     
![Alt text](images/One_connection_per_topic.png)  

![Alt text](images/Random_Byte_Array.png)  


For mesuring, thanks to Jmeter, we can add some listeners:  
  
![Alt text](images/Publisher_result.png)  

## MQTT Subscriber  
 
 
![Alt text](images/MQTT_Subscriber.png)  
 
 
 
*Name:* Name of the MQTT Subscriber  
*Comments:* Your comments  
*Provider URL:* The address of MQTT server  
*Client Id:* Your Id in the session  
*Topic:* The topic you want to subscribe.  
*Use Authorization :* Necessary in the case the connection need username and password  
*User:* your username  
*Password:* your password  
*Number of samples to aggregate:* In other way, the number of message you want to receive from
the topic in one session  
*Time out (milliseconds):* Timeout for the connection to receive message from the topic  

![Alt text](images/Subscriber_result.png)  
 
 
![Alt text](images/Publisher_Subscriber.png)  
  
  
  
  Grenoble, France 14/03/2014,
  
  ERODS Team
  
  http://www.liglab.fr/erods?lang=fr&var_mode=calcul 
  
    
