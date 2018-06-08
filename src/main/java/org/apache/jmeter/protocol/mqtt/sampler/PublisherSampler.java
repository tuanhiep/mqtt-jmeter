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

package org.apache.jmeter.protocol.mqtt.sampler;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.protocol.mqtt.client.MqttPublisher;
import org.apache.jmeter.protocol.mqtt.control.gui.MQTTPublisherGui;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class PublisherSampler extends BaseMQTTSampler implements ThreadListener,TestStateListener  {

	private static final long serialVersionUID = 233L;
	private static final Logger log = LoggingManager.getLoggerForClass();
	private static final String TEXT_MSG = "mqtt.text_message"; //$NON-NLS-1$
	private static final String CONFIG_CHOICE = "mqtt.config_choice"; //$NON-NLS-1$
	private static final String MESSAGE_CHOICE = "mqtt.config_msg_type"; //$NON-NLS-1$
	private static final String QUALITY = "mqtt.quality"; //$NON-NLS-1$
	private static final String TYPE_FIXED_VALUE = "mqtt.type_fixed_value"; //$NON-NLS-1$
	private static String CLIENT_ID = "mqtt.clientid"; //$NON-NLS-1$
	private static String RETAIN = "mqtt.retain"; //$NON-NLS-1$
	private static String USE_TIMESTAMP = "mqtt.use_timestamp"; //$NON-NLS-1$
	private static String USE_NUMBER_SEQUENCE = "mqtt.use_number_sequence"; //$NON-NLS-1$
	private static String FIXED_VALUE = "mqtt.fixed_value"; //$NON-NLS-1$
	private static String TYPE_RANDOM_VALUE = "mqtt.type_random_value"; //$NON-NLS-1$
	private static String MIN_RANDOM_VALUE = "mqtt.min_random_value"; //$NON-NLS-1$
	private static String MAX_RANDOM_VALUE = "mqtt.max_random_value"; //$NON-NLS-1$
	private static String TYPE_GENERATED_VALUE = "mqtt.type_generated_value"; //$NON-NLS-1$
	private static String SEED = "mqtt.seed"; //$NON-NLS-1$
	private static String FORMAT = "mqtt.format"; //$NON-NLS-1$
	private static String CHARSET = "mqtt.charset"; //$NON-NLS-1$
	private static String SIZE_ARRAY = "mqtt.size_array"; //$NON-NLS-1$
	private static String STRATEGY = "mqtt.strategy"; //$NON-NLS-1$
	private static String OneConnectionPerTopic = "mqtt.one_connection_per_topic"; //$NON-NLS-1$
	private static String RandomSuffix="mqtt.random_suffix_client_id";//$NON-NLS-1$
	private static String Length="mqtt.suffix.length";//$NON-NLS-1$
	public transient MqttPublisher producer = null;
	public static AtomicInteger numberOfConnection= new AtomicInteger(0);
	private JavaSamplerContext context = null;

	/**
	 * Constructor
	 */

	public PublisherSampler() {
	}

	// ---------------------Get/Set Property--------------------------//
		
	public void setTYPE_FIXED_VALUE(String type) {
		setProperty(TYPE_FIXED_VALUE, type);
	}
	public  String getLength() {
		return getPropertyAsString(Length);
	}
	public  void setLength(String length) {
		setProperty(Length,length);
	}

	public boolean useRandomSuffix() {
		
		String randomSuffix = getPropertyAsString(RandomSuffix);
		if("TRUE".equalsIgnoreCase(randomSuffix)){
			return true;
		}
		else {
			return false;
		}
		
	}

	public void setRandomSuffix(boolean randomSuffix) {
		setProperty(RandomSuffix,randomSuffix);
		
	}

	public boolean isOneConnectionPerTopic() {

		String perTopic = getPropertyAsString(OneConnectionPerTopic);
		if("TRUE".equalsIgnoreCase(perTopic)){
			return true;
		}
		else {
			return false;
		}
       
	}
	public void setOneConnectionPerTopic(boolean oneConnectionPerTopic) {

		setProperty(OneConnectionPerTopic, oneConnectionPerTopic);
	}

	public String getSTRATEGY() {
		return getPropertyAsString(STRATEGY);
	
	}

	public void setSTRATEGY(String sTRATEGY) {
		setProperty(STRATEGY,sTRATEGY);
	
	}

	public String getSIZE_ARRAY() {
		return getPropertyAsString(SIZE_ARRAY);
	}

	public void setSIZE_ARRAY(String sIZE_ARRAY) {
		setProperty(SIZE_ARRAY,sIZE_ARRAY);
	}

	public String getCHARSET() {
		return getPropertyAsString(CHARSET);
	}

	public void setCHARSET(String cHARSET) {
		setProperty(CHARSET,cHARSET);
	}

	public String getFORMAT() {
		return getPropertyAsString(FORMAT);
	}

	public void setFORMAT(String fORMAT) {	
		setProperty(FORMAT,fORMAT);
	}

	public String getCLIENT_ID() {
		return getPropertyAsString(CLIENT_ID);
	}

	public void setCLIENT_ID(String cLIENT_ID) {
		setProperty(CLIENT_ID,cLIENT_ID);
	}

	public boolean isUSE_TIMESTAMP() {
		String isUseTimeStamp = getPropertyAsString(USE_TIMESTAMP);
		if("TRUE".equalsIgnoreCase(isUseTimeStamp)){
			return true;                   
			}
		else {
			return false;
		     }
	}

	public void setUSE_TIMESTAMP(boolean uSE_TIMESTAMP) {
		setProperty(USE_TIMESTAMP,uSE_TIMESTAMP);
			}

	public boolean isUSE_NUMBER_SEQUENCE() {
		String isUseNumberSequence = getPropertyAsString(USE_NUMBER_SEQUENCE);
		if("TRUE".equalsIgnoreCase(isUseNumberSequence)){
			return true;                   
			}
		else {
			return false;
		     }
	}

	public void setUSE_NUMBER_SEQUENCE(boolean uSE_NUMBER_SEQUENCE) {
			setProperty(USE_NUMBER_SEQUENCE,uSE_NUMBER_SEQUENCE);
	}
	public String getSEED() {
		return getPropertyAsString(SEED);
	}
	public void setSEED(String sEED) {
		setProperty(SEED,sEED);
	}
	public String getTYPE_GENERATED_VALUE() {
		return getPropertyAsString(TYPE_GENERATED_VALUE);
	}
	public void setTYPE_GENERATED_VALUE(String tYPE_GENERATED_VALUE) {
		setProperty(TYPE_GENERATED_VALUE,tYPE_GENERATED_VALUE);
	}

	public String getTYPE_RANDOM_VALUE() {
		return getPropertyAsString(TYPE_RANDOM_VALUE);
	}

	public void setTYPE_RANDOM_VALUE(String tYPE_RANDOM_VALUE) {
		setProperty(TYPE_RANDOM_VALUE,tYPE_RANDOM_VALUE);
	}

	public String getMIN_RANDOM_VALUE() {
		return getPropertyAsString(MIN_RANDOM_VALUE);
		
	}

	public void setMIN_RANDOM_VALUE(String mIN_RANDOM_VALUE) {
	    setProperty(MIN_RANDOM_VALUE,mIN_RANDOM_VALUE); 
	}

	public String getMAX_RANDOM_VALUE() {
		return getPropertyAsString(MAX_RANDOM_VALUE);
	}

	public void setMAX_RANDOM_VALUE(String mAX_RANDOM_VALUE) {
	    setProperty(MAX_RANDOM_VALUE,mAX_RANDOM_VALUE); 
	
	}

	public String getFIXED_VALUE() {
		return getPropertyAsString( FIXED_VALUE);
		
	}

	public void setFIXED_VALUE(String fIXED_VALUE) {
		setProperty(FIXED_VALUE,fIXED_VALUE);
	
	}

	public String getTYPE_FIXED_VALUE() {
		return getPropertyAsString(TYPE_FIXED_VALUE);

	}
	public void setTextMessage(String message) {
		setProperty(TEXT_MSG, message);
	}
	public void setConfigChoice(String choice) {
		setProperty(CONFIG_CHOICE, choice);

	}

	public void setMessageChoice(String choice) {
		setProperty(MESSAGE_CHOICE, choice);

	}

	public String getTextMessage() {
		return getPropertyAsString(TEXT_MSG);
	}

	/**
	 * To get the message choice
	 * 
	 * @return
	 */
	public String getMessageChoice() {
		return getPropertyAsString(MESSAGE_CHOICE);
	}

	public String getQuality() {
		return getPropertyAsString(QUALITY);
	}

	public void setQuality(String quality) {
		setProperty(QUALITY, quality);
	}

	public void setRetained(boolean isRetained) {
		
		setProperty(RETAIN,isRetained);
	}

	public boolean isRetained() {
		String isRetain = getPropertyAsString(RETAIN);
		if("TRUE".equalsIgnoreCase(isRetain)){
			return true;                   
			}
		else {
			return false;
		     }

	}

	
	private void logThreadStart() {
		if (log.isDebugEnabled()) {
			log.debug("Thread started " + new Date());
			log.debug("MQTT PublishSampler: ["
					+ Thread.currentThread().getName() + "], hashCode=["
					+ hashCode() + "]");

		}

	}

	@Override
	public void threadStarted() {
		logThreadStart();

		if (producer == null) {

			try {
				producer = new MqttPublisher();
			} catch (Exception e) {
				log.warn(e.getLocalizedMessage(), e);
			}
		}

		String host = getProviderUrl();
		String list_topic = getDestination();
		String aggregate = "" + getIterationCount();
		Arguments parameters = new Arguments();
		parameters.addArgument("HOST", host);
		// ------------------------ClientId-----------------------------------//
		parameters.addArgument("CLIENT_ID", getCLIENT_ID());
		parameters.addArgument("TOPIC", list_topic);

		// ------------------------Strategy-----------------------------------//
		if (MQTTPublisherGui.ROUND_ROBIN.equals(this.getSTRATEGY())) {
			parameters.addArgument("STRATEGY", "ROUND_ROBIN");
		} else {
			parameters.addArgument("STRATEGY", "RANDOM");
		}

		parameters.addArgument("AGGREGATE", aggregate);

		String quality = getQuality();
		parameters.addArgument("QOS", quality);
		if (this.isRetained()) {
			parameters.addArgument("RETAINED", "TRUE");
		} else {
			parameters.addArgument("RETAINED", "FALSE");
		}
		// -------------------------TimeStamp-----------------------------//

		if (this.isUSE_TIMESTAMP()) {
			parameters.addArgument("TIME_STAMP", "TRUE");
		} else
			parameters.addArgument("TIME_STAMP", "FALSE");

		// -------------------------Number Sequence-----------------------//

		if (this.isUSE_NUMBER_SEQUENCE()) {
			parameters.addArgument("NUMBER_SEQUENCE", "TRUE");
		} else
			parameters.addArgument("NUMBER_SEQUENCE", "FALSE");

		// ---------------------Message Choice----------------------------//

		if (this.getMessageChoice().equals(MQTTPublisherGui.TEXT_MSG_RSC)) {

			parameters.addArgument("MESSAGE", getTextMessage());
			parameters.addArgument("TYPE_MESSAGE", "TEXT");
			parameters.addArgument("TYPE_VALUE", "TEXT");
		} else if (this.getMessageChoice().equals(MQTTPublisherGui.FIXED_VALUE)) {

			parameters.addArgument("MESSAGE", getFIXED_VALUE());
			parameters.addArgument("TYPE_MESSAGE", "FIXED");
			parameters.addArgument("TYPE_VALUE", getTYPE_FIXED_VALUE());
		} else if (this.getMessageChoice().equals(
				MQTTPublisherGui.GENERATED_VALUE)) {
			parameters.addArgument("TYPE_MESSAGE", "RANDOM");
			parameters.addArgument("TYPE_VALUE", getTYPE_GENERATED_VALUE());
			parameters.addArgument("SEED", getSEED());
			parameters.addArgument("MIN_RANDOM_VALUE", getMIN_RANDOM_VALUE());
			parameters.addArgument("MAX_RANDOM_VALUE", getMAX_RANDOM_VALUE());
			parameters.addArgument("TYPE_RANDOM_VALUE", getTYPE_RANDOM_VALUE());
		} else if (this.getMessageChoice().equals(MQTTPublisherGui.BIG_VOLUME)) {
			parameters.addArgument("TYPE_MESSAGE", "BYTE_ARRAY");
			parameters.addArgument("SIZE_ARRAY", this.getSIZE_ARRAY());
		}

		// -----------------------User/Password-------------------------------//

		if (this.isUseAuth()) {
			parameters.addArgument("AUTH", "TRUE");
			parameters.addArgument("USER", getUsername());
			parameters.addArgument("PASSWORD", getPassword());
		} else
			parameters.addArgument("AUTH", "FALSE");
		// -----------------------Format--------------------------------------//
		parameters.addArgument("FORMAT", getFORMAT());
		if (this.getFORMAT().equals(MQTTPublisherGui.PLAIN_TEXT)) {
			parameters.addArgument("CHARSET", getCHARSET());

		} else
			parameters.addArgument("CHARSET", "NULL");

		// -------------------------List Topic Or Not-------------------------//

		String[] topics = list_topic.split("\\s*,\\s*");
		if (topics.length <= 1) {
			parameters.addArgument("LIST_TOPIC", "FALSE");
		} else {
			parameters.addArgument("LIST_TOPIC", "TRUE");
		}
		// ------------------------Connection per topic--------------------//

		if (this.isOneConnectionPerTopic()) {
			parameters.addArgument("PER_TOPIC", "TRUE");
	    } else {
			parameters.addArgument("PER_TOPIC", "FALSE");
				}
		if(this.useRandomSuffix()){
			parameters.addArgument("RANDOM_SUFFIX","TRUE");
			parameters.addArgument("SUFFIX_LENGTH",this.getLength());
		}else {
			parameters.addArgument("RANDOM_SUFFIX","FALSE");
		}
		
		parameters.addArgument("KEEP_ALIVE", String.valueOf(this.getKeepAlive()));
		
		parameters.addArgument("LABEL", this.getName());
		
		this.context = new JavaSamplerContext(parameters);
		this.producer.setupTest(this.context);
	}
	
 
	@Override
	public void threadFinished() {
		log.debug("Thread ended " + new Date());
				
		if (producer != null) {

			try {
				producer.close();
				MqttPublisher.numSeq = 0;
				
			} catch (IOException e) {
				e.printStackTrace();
				log.warn(e.getLocalizedMessage(), e);
			}

		}

	}

	// -------------------------Sample------------------------------------//

	@Override
	public SampleResult sample() {

		return this.producer.runTest(context);
	}

	@Override
	public void testEnded() {
		log.debug("Thread ended " + new Date());
		
		
		if (producer != null) {

			try {
				producer.close();
				System.out.println("close at:"+ new Date());
				MqttPublisher.numSeq = 0;
				
			} catch (IOException e) {
				e.printStackTrace();
				log.warn(e.getLocalizedMessage(), e);
			}

		}

		
	}

	@Override
	public void testEnded(String arg0) {
		testEnded();
		
	}

	@Override
	public void testStarted() {
				
	}

	@Override
	public void testStarted(String arg0) {
		testStarted();
		
	}

}
