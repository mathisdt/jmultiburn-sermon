package org.zephyrsoft.jmultiburn.sermon;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.zephyrsoft.jmultiburn.sermon.model.Sermon;
import org.zephyrsoft.jmultiburn.sermon.model.SourceType;

/**
 * Provides the list of sermons which are available for burning.
 */
public class SermonProvider {
	
	public static List<Sermon> readSermons() {
		List<Sermon> result = new LinkedList<>();
		
		File dir = new File(DB.getSermonsDir());
		File[] files = dir.listFiles();
		int fileListLength = files.length;
		
		if (files != null && fileListLength > 0) {
			Arrays.sort(files);
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				int separation = file.getName().lastIndexOf("-");
				if (separation < 0) {
					fileListLength--;
					continue;
				}
				String vorname = file.getName().substring(0, separation);
				String rate = file.getName().substring(separation + 1, file.getName().lastIndexOf("."));
				String bettername;
				String rest = vorname.substring(11);
				if (rest.indexOf("-") > 0) {
					bettername =
						rest.substring(0, rest.indexOf("-")) + " (" + rest.substring(rest.indexOf("-") + 1) + ")";
				} else {
					bettername = rest;
				}
				bettername = replace(bettername, "_", " ");
				bettername = replace(bettername, "fruehstueck", "frühstück");
				bettername = replace(bettername, "Maenner", "Männer");
				bettername = replace(bettername, "Joerg", "Jörg");
				
				// calculate length
				double bitrate = Double.valueOf(rate.substring(0, 2));
				double filesize = file.length();
				double lengthInSeconds = Math.floor(filesize / bitrate * 0.008);
				
				int partCount = 1;
				// up to 5 seconds at the end would be thrown away, but tracks below 5 seconds length are illegal anyway
				while (lengthInSeconds > 4685) {
					partCount++;
					lengthInSeconds -= 4680;
				}
				
				Sermon sermon = new Sermon();
				sermon
					.setDate(vorname.substring(8, 10) + "." + vorname.substring(5, 7) + "." + vorname.substring(0, 4));
				sermon.setName(bettername);
				sermon.setParts(partCount);
				sermon.setSource(file.getAbsolutePath());
				sermon.setSourceType(SourceType.SINGLE_FILE);
				result.add(sermon);
			}
		}
		
		return result;
	}
	
	private static String replace(String in, String toreplace, String replacewith) {
		String ret = new String(in.toString());
		while (ret.indexOf(toreplace) >= 0) {
			ret =
				ret.substring(0, ret.indexOf(toreplace)) + replacewith
					+ ret.substring(ret.indexOf(toreplace) + toreplace.length());
		}
		return ret;
	}
	
}
