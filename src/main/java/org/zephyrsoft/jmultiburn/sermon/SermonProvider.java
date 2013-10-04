package org.zephyrsoft.jmultiburn.sermon;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.common.annotations.VisibleForTesting;
import org.zephyrsoft.jmultiburn.sermon.model.Sermon;
import org.zephyrsoft.jmultiburn.sermon.model.SermonPart;
import org.zephyrsoft.jmultiburn.sermon.model.SourceType;

/**
 * Provides the list of sermons which are available for burning.
 */
public class SermonProvider {
	
	/** expected pattern for sermons stored as MP3 files */
	@VisibleForTesting
	protected static final Pattern FILE_PATTERN =
		Pattern
			.compile("^(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})-(?<name>[^-]+)(?:-(?<speaker>[^-]+))?-(?<bitrate>\\d{1,3})kbps\\.mp3$");
	
	/** expected pattern for sermons stored in directories */
	@VisibleForTesting
	protected static final Pattern DIRECTORY_PATTERN =
		Pattern
			.compile("^(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})-(?<name>[^-]+)(?:-(?<speaker>[^-]+))?-(?<part>\\d{1,2})$");
	
	public static List<Sermon> readSermons() {
		List<Sermon> result = new LinkedList<>();
		
		File dir = new File(DB.getSermonsDir());
		File[] files = dir.listFiles();
		
		if (files != null) {
			Arrays.sort(files);
			Sermon previousSermon = null;
			for (File file : files) {
				String date = null;
				String name = null;
				String speaker = null;
				Integer bitrate = null;
				Integer part = null;
				
				// decide what to do with this entry
				if (file.isFile()) {
					Matcher fileMatcher = FILE_PATTERN.matcher(file.getName());
					if (fileMatcher.matches()) {
						date =
							fileMatcher.group("day") + "." + fileMatcher.group("month") + "."
								+ fileMatcher.group("year");
						name = fileMatcher.group("name");
						speaker = fileMatcher.group("speaker");
						bitrate = Integer.valueOf(fileMatcher.group("bitrate"));
						
						String composedName = name;
						if (speaker != null && !speaker.isEmpty()) {
							composedName = name + " (" + speaker + ")";
						}
						
						// TODO use a map for replacements
						composedName =
							composedName.replaceAll("_", " ").replaceAll("fruehstueck", "frühstück")
								.replaceAll("Maenner", "Männer").replaceAll("Joerg", "Jörg");
						
						Sermon sermon = new Sermon();
						sermon.setDate(date);
						sermon.setName(composedName);
						sermon.setSourceType(SourceType.SINGLE_FILE);
						
						// calculate length
						double filesize = file.length();
						double lengthInSeconds = Math.floor(filesize / bitrate * 0.008);
						
						int partCount = 1;
						// up to 5 seconds at the end would be thrown away, but tracks below 5 seconds length are
						// illegal anyway
						while (lengthInSeconds > 4685) {
							partCount++;
							lengthInSeconds -= 4680;
						}
						
						// append all parts
						for (int i = 1; i <= partCount; i++) {
							SermonPart sermonPart = new SermonPart();
							sermonPart.setIndex(i);
							sermonPart.setSource(file.getAbsolutePath());
							sermon.addPart(sermonPart);
						}
						
						result.add(sermon);
					}
				} else if (file.isDirectory()) {
					Matcher directoryMatcher = DIRECTORY_PATTERN.matcher(file.getName());
					if (directoryMatcher.matches()) {
						date =
							directoryMatcher.group("day") + "." + directoryMatcher.group("month") + "."
								+ directoryMatcher.group("year");
						name = directoryMatcher.group("name");
						speaker = directoryMatcher.group("speaker");
						part = Integer.valueOf(directoryMatcher.group("part"));
						
						String composedName = name;
						if (speaker != null && !speaker.isEmpty()) {
							composedName = name + " (" + speaker + ")";
						}
						
						// TODO use a map for replacements
						composedName =
							composedName.replaceAll("_", " ").replaceAll("fruehstueck", "frühstück")
								.replaceAll("Maenner", "Männer").replaceAll("Joerg", "Jörg");
						
						Sermon sermon = new Sermon();
						sermon.setDate(date);
						sermon.setName(composedName);
						sermon.setSourceType(SourceType.DIRECTORY);
						
						if (previousSermon != null && previousSermon.equals(sermon)) {
							sermon = previousSermon;
						} else {
							// the sermon is new to the list
							result.add(sermon);
							previousSermon = sermon;
						}
						
						// append this part
						SermonPart sermonPart = new SermonPart();
						sermonPart.setIndex(part);
						sermonPart.setSource(file.getAbsolutePath() + File.separator + "audio");
						sermon.addPart(sermonPart);
						
					}
				}
			}
		}
		
		return result;
	}
	
}
