package net.xmeter.emqtt.samplers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log.Priority;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

public class PublishSampler extends AbstractJavaSamplerClient implements Constants /*, TestStateListener */{
	
	private MQTT mqtt = new MQTT();
	private FutureConnection connection = null;
	private int elpasedTime;
	private static AtomicBoolean sleepFlag = new AtomicBoolean(false);
	
	private String serverAddr = null;
	private int port = 0;
	private int keepAlive = 0;
	private String clientId = null;
	
	@Override
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
		defaultParameters.addArgument(SERVER, "tcp://192.168.10.6");
		defaultParameters.addArgument(PORT, "1883");
		defaultParameters.addArgument(KEEP_ALIVE, "5");
		defaultParameters.addArgument(CLIENT_ID_PREFIX, "xmeter_emqtt");
		defaultParameters.addArgument(CONN_TIMEOUT, "10");
		defaultParameters.addArgument(CONN_ELAPSED_TIME, "1");
		defaultParameters.addArgument(CONN_CLIENT_AUTH, "false");
		defaultParameters.addArgument(QOS_LEVEL, String.valueOf(QOS_0));
		defaultParameters.addArgument(DEBUG_RESPONSE, "false");
		defaultParameters.addArgument(TOPIC_NAME, "test");
		return defaultParameters;
	}
	
	private void printThreadAndTime(String mark) {
		getLogger().log(Priority.INFO, "*** " + mark + ": " + Thread.currentThread().getName() + ", " +  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
	}
	
	@Override
	public void setupTest(JavaSamplerContext context) {
		if (sleepFlag.get()) {
			printThreadAndTime("reset sleepFlag");
			sleepFlag.set(false);
			
		}
		
		serverAddr = context.getParameter(SERVER);
		port = context.getIntParameter(PORT);
		keepAlive = context.getIntParameter(KEEP_ALIVE);
		elpasedTime = context.getIntParameter(CONN_ELAPSED_TIME);
		clientId = Util.generateClientId(context.getParameter(CLIENT_ID_PREFIX));
		
		try {
			
			mqtt.setHost(serverAddr + ":" + port);
			mqtt.setKeepAlive((short) keepAlive);
			if(serverAddr != null && (serverAddr.trim().toLowerCase().startsWith("ssl://"))) {
				boolean flag = "true".equals(context.getParameter(CONN_CLIENT_AUTH, "false"));
				getLogger().log(Priority.INFO, "****setSslContext: " + flag);
				mqtt.setSslContext(Util.getContext(flag));
			}
			//To avoid reconnect
			mqtt.setConnectAttemptsMax(0);
			mqtt.setReconnectAttemptsMax(0);
			
			mqtt.setClientId(clientId);
			
			connection = mqtt.futureConnection();
			Future<Void> f1 = connection.connect();
			f1.await(context.getIntParameter(CONN_TIMEOUT), TimeUnit.SECONDS);
			
		} catch (Exception e) {
			getLogger().log(Priority.ERROR, e.getMessage(), e);
		}
    }
	
	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		SampleResult result = new SampleResult();
        result.sampleStart(); 
       
		try {
			String topicName = context.getParameter(TOPIC_NAME);
			//Topic[] topics = {new Topic(topicName, QoS.AT_LEAST_ONCE)};
			//Topic topic= new Topic(topicName, QoS.AT_LEAST_ONCE);
			
			Future<Void> f3 = connection.publish(topicName, "Hello from pub".getBytes(), QoS.AT_LEAST_ONCE, false);
			f3.await();
			
			result.sampleEnd(); 
            result.setSuccessful(true);
            result.setResponseData((MessageFormat.format("Publish Successful by {0}.", clientId)).getBytes());
            result.setResponseMessage(MessageFormat.format("publish successfully via Connection {0}.", connection));
            result.setResponseCodeOK(); 
		} catch (Exception e) {
			getLogger().log(Priority.ERROR, e.getMessage(), e);
			result.sampleEnd(); 
            result.setSuccessful(false);
            result.setResponseData((MessageFormat.format("Publish failed by {0}.", clientId)).getBytes());
            result.setResponseMessage(MessageFormat.format("publish failed via Connection {0}", connection));
            result.setResponseData("Failed.".getBytes());
            result.setResponseCode("500"); 
		}
		return result;
	}
	
	
	@Override
	public void teardownTest(JavaSamplerContext context) {
		try {
			if(!sleepFlag.get()) {
				TimeUnit.SECONDS.sleep(elpasedTime);	
				sleepFlag.set(true);
			}
			
			if(this.connection != null) {
				this.connection.disconnect();
				getLogger().log(Priority.INFO, MessageFormat.format("The connection {0} disconneted successfully.", connection));	
			}
		} catch (InterruptedException e) {
			getLogger().log(Priority.ERROR, e.getMessage(), e);
		} 
	}



}
