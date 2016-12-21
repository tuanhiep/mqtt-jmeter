package net.xmeter.emqtt.samplers;

public interface Constants {
	public static final String SERVER = "SERVER";
	public static final String PORT = "PORT";
	public static final String KEEP_ALIVE = "KEEP_ALIVE";
	public static final String CLIENT_ID_PREFIX = "CLIENT_ID_PREFIX";
	public static final String CONN_TIMEOUT = "CONN_TIMEOUT";
	
	public static final String CONN_ELAPSED_TIME = "CONN_ELAPSED_TIME";
	public static final String CONN_CLIENT_AUTH = "CONN_CLIENT_AUTH";
	
	public static final String TOPIC_NAME = "TOPIC_NAME";
	public static final String QOS_LEVEL = "QOS_LEVEL";
	public static final String PAYLOAD_SIZE = "PAYLOAD_SIZE";
	
	public static final String TIME_STAMP = "TIME_STAMP";
	public static final String TIME_STAMP_SEP_FLAG = "xm_ts";
	
	public static final String DEBUG_RESPONSE = "DEBUG_RESPONSE";
	
	public static final int QOS_0 = 0;
	public static final int QOS_1 = 1;
	public static final int QOS_2 = 2;
	
	public static final int MAX_CLIENT_ID_LENGTH = 23;
}
