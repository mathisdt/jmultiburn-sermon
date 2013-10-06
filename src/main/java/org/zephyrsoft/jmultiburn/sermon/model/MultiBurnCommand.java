package org.zephyrsoft.jmultiburn.sermon.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * holds a system call related multiburn including its parameters
 */
public class MultiBurnCommand {
	
	private List<String> parts = new LinkedList<>();
	
	private MultiBurnCommand() {
		// only created via static methods
	}
	
	public boolean add(String e) {
		return parts.add(e);
	}
	
	public String[] toArray() {
		return parts.toArray(new String[parts.size()]);
	}
	
	private static String getMultiburnPath(String baseDir) {
		return baseDir + File.separator + "shell" + File.separator + "multiburn-sermon";
	}
	
	public static MultiBurnCommand forBurnSingleFile(String fileToBurn, String part, List<String> burnDevices,
		String baseDir) {
		MultiBurnCommand ret = new MultiBurnCommand();
		ret.add(getMultiburnPath(baseDir));
		ret.add("-s");
		ret.add(fileToBurn);
		if (part != null) {
			ret.add(part);
		} else {
			ret.add("0");
		}
		for (String burnDevice : burnDevices) {
			ret.add(burnDevice);
		}
		return ret;
	}
	
	public static MultiBurnCommand forBurnDirectory(String directoryToBurn, List<String> burnDevices, String baseDir) {
		MultiBurnCommand ret = new MultiBurnCommand();
		ret.add(getMultiburnPath(baseDir));
		ret.add("-a");
		ret.add(directoryToBurn);
		ret.add("0");
		for (String burnDevice : burnDevices) {
			ret.add(burnDevice);
		}
		return ret;
	}
	
	public static MultiBurnCommand forKillMultiBurn(String baseDir) {
		MultiBurnCommand ret = new MultiBurnCommand();
		ret.add("killall");
		ret.add("-9");
		ret.add(getMultiburnPath(baseDir));
		return ret;
	}
}
