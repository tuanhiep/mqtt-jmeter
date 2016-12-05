package net.xmeter.emqtt.samplers;

public class DataEntry {
	private int dockerNum;
	private int threadNum;
	private int loopCount;
	private long time;
	private long elapsedTime;
	
	public int getDockerNum() {
		return dockerNum;
	}
	public void setDockerNum(int dockerNum) {
		this.dockerNum = dockerNum;
	}
	public int getThreadNum() {
		return threadNum;
	}
	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}
	public int getLoopCount() {
		return loopCount;
	}
	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public long getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	
}
