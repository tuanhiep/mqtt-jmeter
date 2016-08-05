/**
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License. 

  Copyright 2014 University Joseph Fourier, LIG Laboratory, ERODS Team

*/
package org.apache.jmeter.protocol.mqtt.client;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.protocol.mqtt.control.gui.MQTTPublisherGui;
import org.apache.jmeter.protocol.mqtt.sampler.PublisherSampler;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;


public class MqttPublisher extends AbstractJavaSamplerClient implements
		Serializable, Closeable {
	private static final long serialVersionUID = 1L;
	private AtomicInteger total = new AtomicInteger(0);
	private FutureConnection[] connectionArray;
	public static int numSeq=0;
	private Random generator = new Random();	
	private SecureRandom secureGenerator= new SecureRandom();
	private String label = "";
	
	@Override
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
		defaultParameters.addArgument("HOST", "tcp://localhost:1883");
		defaultParameters.addArgument("CLIENT_ID", "Hiep");
		defaultParameters.addArgument("TOPIC", "TEST.MQTT");
		defaultParameters.addArgument("AGGREGATE", "1");
		defaultParameters.addArgument("MESSAGE", "This is my test message");
		return defaultParameters;
	}

	public void setupTest(JavaSamplerContext context) {
		label = context.getParameter("LABEL");
		
		String host = context.getParameter("HOST");
		String clientId = context.getParameter("CLIENT_ID");
		if("TRUE".equalsIgnoreCase(context.getParameter("RANDOM_SUFFIX"))){
		clientId= MqttPublisher.getClientId(clientId,Integer.parseInt(context.getParameter("SUFFIX_LENGTH")));	
		}
		if("FALSE".equals(context.getParameter("PER_TOPIC"))){			
			if("TRUE".equals(context.getParameter("AUTH"))){			
				setupTest(host,clientId,context.getParameter("USER"),context.getParameter("PASSWORD"),1);		
				}
				else{	setupTest(host, clientId,1);}		

		}
		else if("TRUE".equals(context.getParameter("PER_TOPIC"))){
			
			String topics= context.getParameter("TOPIC");
			String[] topicArray = topics.split("\\s*,\\s*");
			int size= topicArray.length;
		
			if("TRUE".equals(context.getParameter("AUTH"))){			
				setupTest(host,clientId,context.getParameter("USER"),context.getParameter("PASSWORD"),size);		
				}
				else {	setupTest(host, clientId,size);}
		    }
			}

	public void setupTest(String host, String clientId, int size) {
		try {
			JMeterContext jmcx = JMeterContextService.getContext();
			this.connectionArray= new FutureConnection[size];
			if(size==1){
				this.connectionArray[0]= createConnection(host,clientId+jmcx.getThreadNum());
				this.connectionArray[0].connect().await();
				this.getLogger().info("NUMBER CONNECTION: "+PublisherSampler.numberOfConnection.getAndIncrement());
			}
			else 
			{				
				for(int i = 0;i< size;i++){
					this.connectionArray[i]= createConnection(host,clientId+jmcx.getThreadNum()+i);
					this.connectionArray[i].connect().await();
					this.getLogger().info("NUMBER CONNECTION: "+PublisherSampler.numberOfConnection.getAndIncrement());
				}
			}
					
		} catch (Exception e) {
			getLogger().error(e.getMessage());
		}
		
	}
	public void setupTest(String host, String clientId, String user, String password, int size) {
		try {
			JMeterContext jmcx = JMeterContextService.getContext();
			this.connectionArray= new FutureConnection[size];
			
			if(size==1){
				this.connectionArray[0]= createConnection(host,clientId+jmcx.getThreadNum(),user,password);
				this.connectionArray[0].connect().await();
				this.getLogger().info("NUMBER CONNECTION: "+PublisherSampler.numberOfConnection.getAndIncrement());
				
			}
			else {
				for(int i = 0;i< size;i++){
					this.connectionArray[i]= createConnection(host,clientId+jmcx.getThreadNum()+i,user,password);
					this.connectionArray[i].connect().await();
					this.getLogger().info("NUMBER CONNECTION: "+PublisherSampler.numberOfConnection.getAndIncrement());
				 }
				 }
			getLogger().info("Connection successful..");
		} catch (Exception e) {
			getLogger().error(e.getMessage());
		}
	}

	private FutureConnection createConnection(String host,String clientId) {

		try {
			MQTT client = new MQTT();
			client.setHost(host);
			client.setClientId(clientId);
			return client.futureConnection();
		} catch (URISyntaxException e) {
			getLogger().error(e.getMessage());
			return null;
		}

	}
	private FutureConnection createConnection(String host,String clientId,String user, String password) {

		try {
			MQTT client = new MQTT();
			client.setHost(host);
			client.setUserName(user);
			client.setPassword(password);
			client.setClientId(clientId);
			return client.futureConnection();
		} catch (URISyntaxException e) {
			getLogger().error(e.getMessage());
			return null;
		}

	}

	private void produce(JavaSamplerContext context) throws Exception {
		
		// ---------------------Type of message -------------------//

		if ("FIXED".equals(context.getParameter("TYPE_MESSAGE"))) {
			produce(context.getParameter("MESSAGE"),
					context.getParameter("TOPIC"),
					Integer.parseInt(context.getParameter("AGGREGATE")),
					context.getParameter("QOS"),
					context.getParameter("RETAINED"),
					context.getParameter("TIME_STAMP"),
					context.getParameter("NUMBER_SEQUENCE"),					
					context.getParameter("TYPE_VALUE"),
					context.getParameter("FORMAT"),
					context.getParameter("CHARSET"),
					context.getParameter("LIST_TOPIC"),
					context.getParameter("STRATEGY"),
					context.getParameter("PER_TOPIC"));

		} else if ("RANDOM".equals(context.getParameter("TYPE_MESSAGE"))) {

			produceRandomly(context.getParameter("SEED"),context.getParameter("MIN_RANDOM_VALUE"),
					context.getParameter("MAX_RANDOM_VALUE"),context.getParameter("TYPE_RANDOM_VALUE"),
					context.getParameter("TOPIC"),Integer.parseInt(context.getParameter("AGGREGATE")),
					context.getParameter("QOS"),context.getParameter("RETAINED"),
					context.getParameter("TIME_STAMP"),context.getParameter("NUMBER_SEQUENCE"),
					context.getParameter("TYPE_VALUE"),
					context.getParameter("FORMAT"),
					context.getParameter("CHARSET"),
					context.getParameter("LIST_TOPIC"),
					context.getParameter("STRATEGY"),
					context.getParameter("PER_TOPIC"));
									
		} else if ("TEXT".equals(context.getParameter("TYPE_MESSAGE"))) {
			produce(context.getParameter("MESSAGE"),
					context.getParameter("TOPIC"),
					Integer.parseInt(context.getParameter("AGGREGATE")),
					context.getParameter("QOS"),
					context.getParameter("RETAINED"),
					context.getParameter("TIME_STAMP"),
					context.getParameter("NUMBER_SEQUENCE"),					
					context.getParameter("TYPE_VALUE"),
					context.getParameter("FORMAT"),
					context.getParameter("CHARSET"),
					context.getParameter("LIST_TOPIC"),
					context.getParameter("STRATEGY"),
					context.getParameter("PER_TOPIC"));
		} else if("BYTE_ARRAY".equals(context.getParameter("TYPE_MESSAGE"))){
			produceBigVolume(
					context.getParameter("TOPIC"),
					Integer.parseInt(context.getParameter("AGGREGATE")),
					context.getParameter("QOS"),
					context.getParameter("RETAINED"),
					context.getParameter("TIME_STAMP"),
					context.getParameter("NUMBER_SEQUENCE"),					
					context.getParameter("FORMAT"),
					context.getParameter("CHARSET"),
					context.getParameter("SIZE_ARRAY"),
					context.getParameter("LIST_TOPIC"),
					context.getParameter("STRATEGY"),
					context.getParameter("PER_TOPIC"));			
		}

	}



	private void produceBigVolume(String topic, int aggregate,
			String qos, String isRetained, String useTimeStamp,
			String useNumberSeq, String format, String charset,String sizeArray,String isListTopic,String strategy,String isPerTopic) {
		try {

			// Quality
			QoS quality = null;
			if (MQTTPublisherGui.EXACTLY_ONCE.equals(qos)) {
				quality = QoS.EXACTLY_ONCE;
			} else if (MQTTPublisherGui.AT_LEAST_ONCE.equals(qos)) {
				quality = QoS.AT_LEAST_ONCE;
			} else if (MQTTPublisherGui.AT_MOST_ONCE.equals(qos)) {
				quality = QoS.AT_MOST_ONCE;

			}
			// Retained
			boolean retained = false;
			if ("TRUE".equals(isRetained))
				retained = true;
			// List topic
			if("FALSE".equals(isListTopic)){		
				for (int i = 0; i < aggregate; ++i) {
					byte[] payload = createBigVolume( useTimeStamp, useNumberSeq,format, charset,sizeArray);	
					this.connectionArray[0].publish(topic,payload,quality,retained).await();
					total.incrementAndGet();
				}
			}
			else if("TRUE".equals(isListTopic)){
				String[] topicArray= topic.split("\\s*,\\s*");
				int length= topicArray.length;
				Random rand = new Random();	
				for (int i = 0; i < aggregate; ++i) {
					byte[] payload = createBigVolume( useTimeStamp, useNumberSeq,format, charset,sizeArray);	
					//-----------------------------Publish after strategy---------------------//					
					if("ROUND_ROBIN".equals(strategy)){
						for(int j=0;j<length;j++){							
							if("TRUE".equals(isPerTopic)){
								this.connectionArray[j].publish(topicArray[j],payload,quality, retained).await();
								total.incrementAndGet();
							} else if("FALSE".equals(isPerTopic)){
								this.connectionArray[0].publish(topicArray[j],payload,quality, retained).await();	
								total.incrementAndGet();
							}							
						}
					}
					else if("RANDOM".equals(strategy)){											
						int  r = rand.nextInt(length);
						if("TRUE".equals(isPerTopic)){
							this.connectionArray[r].publish(topicArray[r],payload,quality,retained).await();
							total.incrementAndGet();
						} else if("FALSE".equals(isPerTopic)){
							this.connectionArray[0].publish(topicArray[r],payload,quality,retained).await();
							total.incrementAndGet();
						}
						
					}
						}
			}
					} catch (Exception e) {
			e.printStackTrace();
			getLogger().warn(e.getLocalizedMessage(), e);
		}
			
	}



	private void produce(String message, String topic, int aggregate,
			String qos, String isRetained, String useTimeStamp, String useNumberSeq,String type_value, String format, String charset,String isListTopic,String strategy,String isPerTopic) throws Exception {
		
		try {
			// Quality
			QoS quality = null;
			if (MQTTPublisherGui.EXACTLY_ONCE.equals(qos)) {
				quality = QoS.EXACTLY_ONCE;
			} else if (MQTTPublisherGui.AT_LEAST_ONCE.equals(qos)) {
				quality = QoS.AT_LEAST_ONCE;
			} else if (MQTTPublisherGui.AT_MOST_ONCE.equals(qos)) {
				quality = QoS.AT_MOST_ONCE;
			}
			// Retained
			boolean retained = false;
			if ("TRUE".equals(isRetained))
				retained = true;
			// List topic
			if("FALSE".equals(isListTopic)){		
				for (int i = 0; i < aggregate; ++i) {
					byte[] payload = createPayload(message, useTimeStamp, useNumberSeq, type_value,format, charset);	
					this.connectionArray[0].publish(topic,payload,quality,retained).await();
					total.incrementAndGet();
				}
			}
			else if("TRUE".equals(isListTopic)){
				String[] topicArray= topic.split("\\s*,\\s*");
				int length= topicArray.length;
				Random rand = new Random();	
				for (int i = 0; i < aggregate; ++i) {
					byte[] payload = createPayload(message, useTimeStamp, useNumberSeq, type_value,format, charset);	
					//---------------------Publish after strategy------------------//					
					if("ROUND_ROBIN".equals(strategy)){
						for(int j=0;j<length;j++){
							if("TRUE".equals(isPerTopic)){
								this.connectionArray[j].publish(topicArray[j],payload,quality, retained).await();
								total.incrementAndGet();
							} else if("FALSE".equals(isPerTopic)){
								this.connectionArray[0].publish(topicArray[j],payload,quality, retained).await();							
								total.incrementAndGet();
							}											
						}
					}
					else if("RANDOM".equals(strategy)){											
						int  r = rand.nextInt(length);
						if("TRUE".equals(isPerTopic)){
							this.connectionArray[r].publish(topicArray[r],payload,quality,retained).await();
							total.incrementAndGet();
						} else if("FALSE".equals(isPerTopic)){
							this.connectionArray[0].publish(topicArray[r],payload,quality,retained).await();
							total.incrementAndGet();
						}
					}
						}
			}					
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().warn(e.getLocalizedMessage(), e);
		}
	}

	public void produceRandomly(String seed, String min, String max, String type_random,String topic, int aggregate,
			String qos, String isRetained, String useTimeStamp, String useNumberSeq,String type_value,String format, String charset,String isListTopic,String strategy,String isPerTopic){
		try {
			// Quality
			QoS quality = null;
			if (MQTTPublisherGui.EXACTLY_ONCE.equals(qos)) {
				quality = QoS.EXACTLY_ONCE;
			} else if (MQTTPublisherGui.AT_LEAST_ONCE.equals(qos)) {
				quality = QoS.AT_LEAST_ONCE;
			} else if (MQTTPublisherGui.AT_MOST_ONCE.equals(qos)) {
				quality = QoS.AT_MOST_ONCE;
			}
			// Retained
			boolean retained = false;
			if ("TRUE".equals(isRetained))
			retained = true;
			// List topic
			if("FALSE".equals(isListTopic)){		
				for (int i = 0; i < aggregate; ++i) {
					byte[] payload = this.createRandomPayload(seed, min, max, type_random, useTimeStamp, useNumberSeq, type_value,format, charset);		
					this.connectionArray[0].publish(topic,payload,quality,retained).await();
					total.incrementAndGet();
				}
			}
			else if("TRUE".equals(isListTopic)){
				String[] topicArray= topic.split("\\s*,\\s*");
				int length= topicArray.length;
				Random rand = new Random();	
				for (int i = 0; i < aggregate; ++i) {
					byte[] payload = this.createRandomPayload(seed, min, max, type_random, useTimeStamp, useNumberSeq, type_value,format, charset);		
					//---------------------Publish after strategy------------------//					
					if("ROUND_ROBIN".equals(strategy)){
						for(int j=0;j<length;j++){
							if("TRUE".equals(isPerTopic)){
								this.connectionArray[j].publish(topicArray[j],payload,quality, retained).await();
								total.incrementAndGet();
							} else if("FALSE".equals(isPerTopic)){
								this.connectionArray[0].publish(topicArray[j],payload,quality, retained).await();							
								total.incrementAndGet();
							}
						}
					}
					else if("RANDOM".equals(strategy)){											
						int  r = rand.nextInt(length);
						
						if("TRUE".equals(isPerTopic)){
							this.connectionArray[r].publish(topicArray[r],payload,quality,retained).await();
							total.incrementAndGet();
						} else if("FALSE".equals(isPerTopic)){
							this.connectionArray[0].publish(topicArray[r],payload,quality,retained).await();
							total.incrementAndGet();
						}
						
					}
						}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().warn(e.getLocalizedMessage(), e);
		}
			
	}

	public SampleResult runTest(JavaSamplerContext context) {
		SampleResult result = new SampleResult();
		result.setSampleLabel(label);
		try {
			
			result.sampleStart(); // start stopwatch
			produce(context);
			result.sampleEnd(); // stop stopwatch
			result.setSuccessful(true);
			result.setResponseMessage("Sent " + total.get() + " messages total");
			result.setResponseCodeOK();
		} catch (Exception e) {
			result.sampleEnd(); // stop stopwatch
			result.setSuccessful(false);
			result.setResponseMessage("Exception: " + e);
			// get stack trace as a String to return as document data
			java.io.StringWriter stringWriter = new java.io.StringWriter();
			e.printStackTrace(new java.io.PrintWriter(stringWriter));
			result.setResponseData(stringWriter.toString(), null);
			result.setDataType(org.apache.jmeter.samplers.SampleResult.TEXT);
			result.setResponseCode("FAILED");
		}

		return result;
	}

	@Override
	public void close() throws IOException {

	if(this.connectionArray!=null){
		for(int p=0;p<this.connectionArray.length;p++){			
			if (this.connectionArray[p] != null)
				this.connectionArray[p].disconnect();
				this.getLogger().info("NUMBER CONNECTION: "+PublisherSampler.numberOfConnection.getAndDecrement());
			   	this.connectionArray[p]=null;
													  }		
	                                                  }			
		this.connectionArray= null;
	}
		
	public byte[] createPayload(String message, String useTimeStamp, String useNumSeq ,String type_value, String format, String charset) throws IOException, NumberFormatException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream d = new DataOutputStream(b);
// flags  	
    	byte flags=0x00;
		if("TRUE".equals(useTimeStamp)) flags|=0x80;
		if("TRUE".equals(useNumSeq)) flags|=0x40;
		if (MQTTPublisherGui.INT.equals(type_value)) flags|=0x20;
		if (MQTTPublisherGui.LONG.equals(type_value)) flags|=0x10;
		if (MQTTPublisherGui.FLOAT.equals(type_value)) flags|=0x08;
		if (MQTTPublisherGui.DOUBLE.equals(type_value)) flags|=0x04;
		if (MQTTPublisherGui.STRING.equals(type_value)) flags|=0x02;
		if(!"TEXT".equals(type_value)){
			d.writeByte(flags); 
		}		
// TimeStamp
		if("TRUE".equals(useTimeStamp)){
   		 Date date= new java.util.Date();
    	 d.writeLong(date.getTime());
    	                               }
// Number Sequence
		if("TRUE".equals(useNumSeq)){
   	     d.writeInt(numSeq++);   	
   	    
  	    }
// Value				
  	    if (MQTTPublisherGui.INT.equals(type_value)) {
  			d.writeInt(Integer.parseInt(message));  			
  		} else if (MQTTPublisherGui.LONG.equals(type_value)) {
  			d.writeLong(Long.parseLong(message));  		
  		} else if (MQTTPublisherGui.DOUBLE.equals(type_value)) {
  			d.writeDouble(Double.parseDouble(message));  		
  		} else if (MQTTPublisherGui.FLOAT.equals(type_value)) {
  			d.writeDouble(Float.parseFloat(message));  			
  		} else if (MQTTPublisherGui.STRING.equals(type_value)) {
  			d.write(message.getBytes());  			
  		} else if ("TEXT".equals(type_value)) {
  			d.write(message.getBytes());
  		}   
  	      
// Format: Encoding  	   
  	   if(MQTTPublisherGui.BINARY.equals(format)){
  		   BinaryCodec encoder= new BinaryCodec();
  		   return encoder.encode(b.toByteArray());
  	   } else if(MQTTPublisherGui.BASE64.equals(format)){
  		   return Base64.encodeBase64(b.toByteArray());
  	   } else if(MQTTPublisherGui.BINHEX.equals(format)){
  		   Hex encoder= new Hex();
  		   return encoder.encode(b.toByteArray());
  	   } else if(MQTTPublisherGui.PLAIN_TEXT.equals(format)){  		  
  		   String s= new String (b.toByteArray(),charset);
  		   return s.getBytes();
  		   
  	   } else return b.toByteArray();
	}
       
    
    public byte[] createRandomPayload(String Seed,String min, String max, String type_random, String useTimeStamp, String useNumSeq ,String type_value, String format, String charset) throws IOException, NumberFormatException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream d = new DataOutputStream(b);
// flags  	
    	byte flags=0x00;
		if("TRUE".equals(useTimeStamp)) flags|=0x80;
		if("TRUE".equals(useNumSeq)) flags|=0x40;
		if (MQTTPublisherGui.INT.equals(type_value)) flags|=0x20;
		if (MQTTPublisherGui.LONG.equals(type_value)) flags|=0x10;
		if (MQTTPublisherGui.FLOAT.equals(type_value)) flags|=0x08;
		if (MQTTPublisherGui.DOUBLE.equals(type_value)) flags|=0x04;
		if (MQTTPublisherGui.STRING.equals(type_value)) flags|=0x02;
		if(!"TEXT".equals(type_value)){
			d.writeByte(flags); 
		}		
// TimeStamp
		if("TRUE".equals(useTimeStamp)){
   		 Date date= new java.util.Date();
    	 d.writeLong(date.getTime());
    	                               }
// Number Sequence
		if("TRUE".equals(useNumSeq)){
   	     d.writeInt(numSeq++);   	
   	    
  	    }
// Value
		
	if(MQTTPublisherGui.PSEUDO.equals(type_random)){
		    generator.setSeed(Long.parseLong(Seed));
		    if (MQTTPublisherGui.INT.equals(type_value)) {	  	    	
	  			d.writeInt(generator.nextInt(Integer.parseInt(max)
	  					-Integer.parseInt(min))+Integer.parseInt(min));  
	   		} else if (MQTTPublisherGui.LONG.equals(type_value)) {	  			
	  			long Max= Long.parseLong(max);
	  			long Min= Long.parseLong(min);
	  			d.writeLong((Math.abs(generator.nextLong() % (Max - Min)) + Min));  		
	  		} else if (MQTTPublisherGui.DOUBLE.equals(type_value)) {
	  			double Max= Double.parseDouble(max);
	  			double Min= Double.parseDouble(min);
	  	      	d.writeDouble((Min+(Max-Min)*generator.nextDouble()));
	  		} else if (MQTTPublisherGui.FLOAT.equals(type_value)) {
	  			float Max= Float.parseFloat(max);
	  			float Min= Float.parseFloat(min);
	  			d.writeFloat((Min+(Max-Min)*generator.nextFloat()));			
	   		} 
			}
	else if(MQTTPublisherGui.SECURE.equals(type_random)){
		
		 secureGenerator.setSeed(Long.parseLong(Seed));
		 if (MQTTPublisherGui.INT.equals(type_value)) {	  	    	
	  			d.writeInt(secureGenerator.nextInt(Integer.parseInt(max)-Integer.parseInt(min))+Integer.parseInt(min));  
	  		} else if (MQTTPublisherGui.LONG.equals(type_value)) {	  			
	  			long Max= Long.parseLong(max);
	  			long Min= Long.parseLong(min);
	  			d.writeLong((Math.abs(secureGenerator.nextLong() % (Max - Min)) + Min));  		
	  		} else if (MQTTPublisherGui.DOUBLE.equals(type_value)) {
	  			double Max= Double.parseDouble(max);
	  			double Min= Double.parseDouble(min);
	  	      	d.writeDouble((Min+(Max-Min)*secureGenerator.nextDouble()));
	  		} else if (MQTTPublisherGui.FLOAT.equals(type_value)) {
	  			float Max= Float.parseFloat(max);
	  			float Min= Float.parseFloat(min);
	  			d.writeFloat((Min+(Max-Min)*secureGenerator.nextFloat()));			
	   		}				
	}
  	  
// Format: Encoding  	   
	   if(MQTTPublisherGui.BINARY.equals(format)){
		   BinaryCodec encoder= new BinaryCodec();
		   return encoder.encode(b.toByteArray());
	   } else if(MQTTPublisherGui.BASE64.equals(format)){
		   return Base64.encodeBase64(b.toByteArray());
	   } else if(MQTTPublisherGui.BINHEX.equals(format)){
		   Hex encoder= new Hex();
		   return encoder.encode(b.toByteArray());
	   } else if(MQTTPublisherGui.PLAIN_TEXT.equals(format)){  		  
		   String s= new String (b.toByteArray(),charset);
		   return s.getBytes();
		   
	   } else return b.toByteArray();   
		
	}
	private byte[] createBigVolume(String useTimeStamp, String useNumberSeq,String format, String charset, String sizeArray) throws IOException, NumberFormatException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream d = new DataOutputStream(b);
// flags  	
    	byte flags=0x00;
		if("TRUE".equals(useTimeStamp)) flags|=0x80;
		if("TRUE".equals(useNumberSeq)) flags|=0x40;
		d.writeByte(flags); 
// TimeStamp
		if("TRUE".equals(useTimeStamp)){
   		 Date date= new java.util.Date();
    	 d.writeLong(date.getTime());
     	                               }
// Number Sequence
		if("TRUE".equals(useNumberSeq)){
   	     d.writeInt(numSeq++);   	
   	    
  	    }
		int size= Integer.parseInt(sizeArray);
		byte[] content= new byte[size];
		
		for(int i=0;i<size;i++){
			content[i]= (byte)(i%10);		
		}		
		d.write(content);
// Format: Encoding  	   
	   if(MQTTPublisherGui.BINARY.equals(format)){
		   BinaryCodec encoder= new BinaryCodec();
		   return encoder.encode(b.toByteArray());
	   } else if(MQTTPublisherGui.BASE64.equals(format)){
		   return Base64.encodeBase64(b.toByteArray());
	   } else if(MQTTPublisherGui.BINHEX.equals(format)){
		   Hex encoder= new Hex();
		   return encoder.encode(b.toByteArray());
	   } else if(MQTTPublisherGui.PLAIN_TEXT.equals(format)){  		  
		   String s= new String (b.toByteArray(),charset);
		   return s.getBytes();
		   
	   } else 
		   
	   return b.toByteArray();
	
				
	}
	
	private static final String mycharset = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String getClientId(String clientPrefix, int suffixLength) {
	    Random rand = new Random(System.nanoTime()*System.currentTimeMillis());
	    StringBuilder sb = new StringBuilder();
	    sb.append(clientPrefix);
	    for (int i = 0; i < suffixLength; i++) {
	        int pos = rand.nextInt(mycharset.length());
	        sb.append(mycharset.charAt(pos));
	    }
	    return sb.toString();
	}


}