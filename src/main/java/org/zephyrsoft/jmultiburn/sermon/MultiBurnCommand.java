package org.zephyrsoft.jmultiburn.sermon;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * holds a system call related multiburn including its parameters
 */
public class MultiBurnCommand {
	
	private static final String MULTIBURN_PATH = DB.getBaseDir() + File.separator + "shell" + File.separator
		+ "multiburn-sermon";
	
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
	
	public static MultiBurnCommand forBurnSingleFile(String fileToBurn, String part, String[] burnDevices) {
		MultiBurnCommand ret = new MultiBurnCommand();
		ret.add(MULTIBURN_PATH);
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
	
	public static MultiBurnCommand forBurnDirectory(String directoryToBurn, String[] burnDevices) {
		MultiBurnCommand ret = new MultiBurnCommand();
		ret.add(MULTIBURN_PATH);
		ret.add("-a");
		ret.add(directoryToBurn);
		ret.add("0");
		for (String burnDevice : burnDevices) {
			ret.add(burnDevice);
		}
		return ret;
	}
	
	public static MultiBurnCommand forKillMultiBurn() {
		MultiBurnCommand ret = new MultiBurnCommand();
		ret.add("killall");
		ret.add("-9");
		ret.add(MULTIBURN_PATH);
		return ret;
	}
}
