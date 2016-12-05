package net.xmeter.emqtt.samplers;

public class DataEntry {
	private long time;
	private long hashCode;
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public long getHashCode() {
		return hashCode;
	}
	public void setHashCode(long hashCode) {
		this.hashCode = hashCode;
	}
}
