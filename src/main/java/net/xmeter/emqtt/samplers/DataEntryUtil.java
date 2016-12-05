package net.xmeter.emqtt.samplers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataEntryUtil {
	private List<DataEntry> allEntries = new ArrayList<DataEntry>();
	private static DataEntryUtil util = new DataEntryUtil();
	private static String filePath = "/home/xmeter/DCLogs/";
	private static String fileName = "data_entries.log";
	private static String fullPath = filePath + fileName;
	private Object lock = new Object();

	private DataEntryUtil() {
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
									contents.append(entry.getTime() + ", " + entry.getDockerNum());
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
			fileOutputStream = new FileOutputStream(fullPath, true);
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
	
	public static DataEntryUtil getInstance(String theFullPath) {
		if(theFullPath == null || "".equals(theFullPath.trim())) {
			System.out.println("Specified empty data log file name, will use default " + filePath + fileName);
		} else {
			fullPath = theFullPath;
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
