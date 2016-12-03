package net.xmeter.emqtt.samplers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log.Priority;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

@SuppressWarnings("deprecation")
public class SubscriptionSampler extends AbstractJavaSamplerClient implements Constants {
	private MQTT mqtt = new MQTT();
	private CallbackConnection connection = null;
	
	private boolean connectFailed = false;
	private boolean subFailed = false;
	private boolean receivedMsgFailed = false;
	
	private int receivedMessageSize = 0;
	private int receivedCount = 0;

	private boolean debugResponse = false;
	private List<String> contents = new ArrayList<String>();
	private Object lock = new Object();
	
	private String connAuth = "false";
	private int qos = QOS_0;
	@Override
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
		defaultParameters.addArgument(SERVER, "tcp://10.91.41.18");
		defaultParameters.addArgument(PORT, "1883");
		defaultParameters.addArgument(KEEP_ALIVE, "300");
		defaultParameters.addArgument(CLIENT_ID_PREFIX, "sub_");
		defaultParameters.addArgument(CONN_TIMEOUT, "10");
		defaultParameters.addArgument(CONN_ELAPSED_TIME, "60");
		defaultParameters.addArgument(CONN_CLIENT_AUTH, "false");
		defaultParameters.addArgument(QOS_LEVEL, String.valueOf(QOS_0));
		defaultParameters.addArgument(DEBUG_RESPONSE, "false");
		defaultParameters.addArgument(TOPIC_NAME, "test");
		return defaultParameters;
	}

	private void createCallbackConn(String serverAddr, int port, int keepAlive, String clientId, final String topicName) {
		try {
			mqtt.setHost(serverAddr + ":" + port);
			mqtt.setKeepAlive((short) keepAlive);
			mqtt.setClientId(clientId);
			
			if(serverAddr != null && (serverAddr.trim().toLowerCase().startsWith("ssl://"))) {
				boolean flag = "true".equals(this.connAuth);
				mqtt.setSslContext(Util.getContext(flag));
			}
			//To avoid reconnect
			mqtt.setConnectAttemptsMax(0);
			mqtt.setReconnectAttemptsMax(0);

			connection = mqtt.callbackConnection();
			connection.listener(new Listener() {
				@Override
				public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
					try {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						body.writeTo(baos);
						String msg = baos.toString();
						synchronized (lock) {
							if(debugResponse) {
								contents.add(msg);
							}
							receivedMessageSize += msg.length();
							receivedCount++;
						}
						ack.run();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				@Override
				public void onFailure(Throwable value) {
					connectFailed = true;
					connection.kill(null);
				}
				@Override
				public void onDisconnected() {
				}
				@Override
				public void onConnected() {
				}
			});
			
			connection.connect(new Callback<Void>() {
				@Override
				public void onSuccess(Void value) {
					Topic[] topics = new Topic[1];
					if(qos == QOS_0) {
						topics[0] = new Topic(topicName, QoS.AT_MOST_ONCE);	
					} else if(qos == QOS_1) {
						topics[0] = new Topic(topicName, QoS.AT_LEAST_ONCE);
					} else {
						topics[0] = new Topic(topicName, QoS.EXACTLY_ONCE);
					}
					
					connection.subscribe(topics, new Callback<byte[]>() {
						@Override
						public void onSuccess(byte[] value) {
							getLogger().info("sub successful: " + new String(value));
						}
						@Override
						public void onFailure(Throwable value) {
							subFailed = true;
							connection.kill(null);
						}
					});
				}
				@Override
				public void onFailure(Throwable value) {
					connectFailed = true;
				}
			});
		} catch (Exception e) {
			getLogger().log(Priority.ERROR, e.getMessage(), e);
		}
	}

	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		String serverAddr = context.getParameter(SERVER);
		int port = context.getIntParameter(PORT);
		int keepAlive = context.getIntParameter(KEEP_ALIVE);
		String clientId = Util.generateClientId(context.getParameter(CLIENT_ID_PREFIX));
		String topicName = context.getParameter(TOPIC_NAME);
		this.connAuth = context.getParameter(CONN_CLIENT_AUTH, "false");
		String qos = context.getParameter(QOS_LEVEL, String.valueOf(QOS_0));
		this.qos = Integer.parseInt(qos);
		
		if(connection == null) {
			createCallbackConn(serverAddr, port, keepAlive, clientId, topicName);			
		}
		
		if("true".equalsIgnoreCase(context.getParameter(DEBUG_RESPONSE, "false"))) {
			debugResponse = true;
		}
		
		SampleResult result = new SampleResult();
		result.sampleStart();
		if(connectFailed) {
			return fillFailedResult(result, MessageFormat.format("Connection {0} connected failed.", connection));
		} else if(subFailed) {
			return fillFailedResult(result, "Failed to subscribe to topic.");
		} else if(receivedMsgFailed) {
			return fillFailedResult(result, "Failed to receive message.");
		}
		synchronized (lock) {
			String message = MessageFormat.format("Received {0} of message\n.", receivedCount);
			StringBuffer content = new StringBuffer("");
			if(debugResponse) {
				for(int i = 0; i < contents.size(); i++) {
					content.append(contents.get(i) + " \n");
				}	
			}
			//System.out.println(MessageFormat.format("receivedMessageSize {0} with receivedCount {1}.", receivedMessageSize, receivedCount));
			int avgSize = 0;
			if(receivedCount != 0) {
				avgSize = receivedMessageSize / receivedCount;
			}
			result = fillOKResult(result, avgSize, message, content.toString());
			
			receivedMessageSize = 0;
			receivedCount = 0;
			contents.clear();
			return result;
		}
	}
	
	private SampleResult fillFailedResult(SampleResult result, String message) {
		result.setResponseCode("500");
		result.setSuccessful(false);
		result.setResponseMessage(message);
		result.setResponseData("Failed.".getBytes());
		result.sampleEnd();
		return result;
	}
	
	private SampleResult fillOKResult(SampleResult result, int size, String message, String contents) {
		result.setResponseCode("200");
		result.setSuccessful(true);
		result.setResponseMessage(message);
		result.setBodySize(size);
		result.setBytes(size);
		result.setResponseData(contents.getBytes());
		result.sampleEnd();
		return result;
	}

	@Override
	public void teardownTest(JavaSamplerContext context) {
		this.connection.disconnect(new Callback<Void>() {
			@Override
			public void onSuccess(Void value) {
				getLogger().info(MessageFormat.format("Connection {0} disconnect successfully.", connection));
			}
			@Override
			public void onFailure(Throwable value) {
				getLogger().info(MessageFormat.format("Connection {0} failed to disconnect.", connection));
			}
		});
	}
	
	
}
