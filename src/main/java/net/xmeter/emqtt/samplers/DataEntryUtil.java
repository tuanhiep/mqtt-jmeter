package net.xmeter.emqtt.samplers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataEntryUtil {
	private List<DataEntry> allEntries = new ArrayList<DataEntry>();
	private static DataEntryUtil util = new DataEntryUtil();
	private static String filePath = "/home/xmeter/DClogs/";
	private static String fileName = "data_entries.log";
	private Object lock = new Object();
	private static String hostName;
	private static String foldName = null;
	private DataEntryUtil() {
		try {
			hostName = (InetAddress.getLocalHost()).getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}  
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						synchronized (lock) {
							if(allEntries.size() > 0) {
								Iterator<DataEntry> it = allEntries.iterator();
								StringBuffer contents = new StringBuffer();
								while(it.hasNext()) {
									DataEntry entry = it.next();
									contents.append(entry.getElapsedTime());
									contents.append(",");
									contents.append(entry.getDockerNum());
									contents.append(",");
									contents.append(entry.getThreadNum());
									contents.append(",");
									contents.append(entry.getLoopCount());
									contents.append(",");
									contents.append(entry.getTime());
									contents.append("\n");
								}
								saveToFile(contents.toString());
								allEntries.clear();
							}
						}
						TimeUnit.SECONDS.sleep(3);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		service.shutdown();
	}

	private void saveToFile(String contents) {
		FileOutputStream fileOutputStream = null;
		try {
			if(foldName == null || "".equals(foldName.trim())) {
				String fullPath = filePath + hostName + "_" + fileName;
				fileOutputStream = new FileOutputStream(fullPath, true);	
			} else {
				fileOutputStream = new FileOutputStream(foldName + hostName + "_" + fileName, true);
			}
			
			fileOutputStream.write(contents.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @param theFullPath: The fullpath of packet log. If the para is null or empty, will use the default path.
	 * @return
	 */
	public static DataEntryUtil getInstance(String theFoldName) {
		if(theFoldName == null || "".equals(theFoldName.trim())) {
			System.out.println("Specified empty data log file name, will use default " + filePath + hostName + "_" + fileName);
		} else {
			foldName = theFoldName;
		}
		return util;
	}

	public void addDataEntry(DataEntry dataEntry) {
		synchronized (lock) {
			allEntries.add(dataEntry);
		}
	}

	public void addDataEntries(List<DataEntry> dataEntries) {
		synchronized (lock) {
			allEntries.addAll(dataEntries);
		}
	}
}
