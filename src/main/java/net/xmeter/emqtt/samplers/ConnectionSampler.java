package net.xmeter.emqtt.samplers;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log.Priority;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

public class ConnectionSampler extends AbstractJavaSamplerClient implements Constants{
	private MQTT mqtt = new MQTT();
	private FutureConnection connection = null;
	private int elpasedTime;
	private static AtomicBoolean sleepFlag = new AtomicBoolean(false);
	public ConnectionSampler() {
	}
	
	@Override
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
		defaultParameters.addArgument(SERVER, "tcp://localhost");
		defaultParameters.addArgument(PORT, "1883");
		defaultParameters.addArgument(KEEP_ALIVE, "5");
		defaultParameters.addArgument(CLIENT_ID_PREFIX, "conn_");
		defaultParameters.addArgument(CONN_TIMEOUT, "10");
		defaultParameters.addArgument(CONN_ELAPSED_TIME, "60");
		defaultParameters.addArgument(CONN_CLIENT_AUTH, "false");
		return defaultParameters;
	}
	
	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		String serverAddr = context.getParameter(SERVER);
		int port = context.getIntParameter(PORT);
		
		int keepAlive = context.getIntParameter(KEEP_ALIVE);
		SampleResult result = new SampleResult();
		this.elpasedTime = context.getIntParameter(CONN_ELAPSED_TIME);
        result.sampleStart(); 
		try {
			
			if(serverAddr != null && (serverAddr.trim().toLowerCase().startsWith("ssl://"))) {
				mqtt.setSslContext(Util.getContext("true".equals(context.getParameter(CONN_CLIENT_AUTH, "false"))));
			}
			mqtt.setHost(serverAddr + ":" + port);
			mqtt.setKeepAlive((short) keepAlive);
			String clientId = Util.generateClientId(context.getParameter(CLIENT_ID_PREFIX));
			mqtt.setClientId(clientId);
			mqtt.setConnectAttemptsMax(0);
			mqtt.setReconnectAttemptsMax(0);
			
			connection = mqtt.futureConnection();
			
			Future<Void> f1 = connection.connect();
			f1.await(context.getIntParameter(CONN_TIMEOUT), TimeUnit.SECONDS);
			
			Topic[] topics = {new Topic("topic_"+ clientId, QoS.AT_LEAST_ONCE)};
			connection.subscribe(topics);
			
			result.sampleEnd(); 
            result.setSuccessful(true);
            result.setResponseData("Successful.".getBytes());
            result.setResponseMessage(MessageFormat.format("Connection {0} connected successfully.", connection));
            result.setResponseCodeOK(); 
		} catch (Exception e) {
			getLogger().log(Priority.ERROR, e.getMessage(), e);
			result.sampleEnd(); 
            result.setSuccessful(false);
            result.setResponseMessage(MessageFormat.format("Connection {0} connected failed.", connection));
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
