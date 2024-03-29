package org.zephyrsoft.jmultiburn.sermon;

import static org.zephyrsoft.jmultiburn.sermon.Setting.SERMON_DIR;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zephyrsoft.jmultiburn.sermon.model.Sermon;
import org.zephyrsoft.jmultiburn.sermon.model.SermonPart;
import org.zephyrsoft.jmultiburn.sermon.model.SourceType;

import com.google.common.annotations.VisibleForTesting;

/**
 * Provides the list of sermons which are available for burning.
 */
public class SermonProvider {

	private static final Logger LOG = LoggerFactory.getLogger(SermonProvider.class);

	@Autowired
	private PropertyHolder propertyHolder;

	/** expected pattern for sermons stored as MP3 files */
	@VisibleForTesting
	static final Pattern FILE_PATTERN =
		Pattern
			.compile("^(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})-(?<name>[^-]+)(?:-(?<speaker>[^-]+))?-(?<bitrate>\\d{1,3})kbps\\.mp3$");

	/** expected pattern for sermons stored in directories */
	@VisibleForTesting
	static final Pattern DIRECTORY_PATTERN =
		Pattern
			.compile("^(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})-(?<name>[^-]+)(?:-(?<speaker>[^-]+))?-(?<part>\\d{1,2})$");

	private static final Map<String, String> replacements = new HashMap<String, String>();

	static {
		replacements.put("_", " ");
		replacements.put("fruehstueck", "frühstück");
		replacements.put("Maenner", "Männer");
		replacements.put("Joerg", "Jörg");
	}

	public List<Sermon> readSermons() {
		List<Sermon> result = new ArrayList<>();

		String pathname = propertyHolder.getProperty(SERMON_DIR);
		LOG.info("reading sermons from {}", pathname);
		File dir = new File(pathname);
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

						for (String toReplace : replacements.keySet()) {
							composedName = composedName.replaceAll(toReplace, replacements.get(toReplace));
						}

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

						for (String toReplace : replacements.keySet()) {
							composedName = composedName.replaceAll(toReplace, replacements.get(toReplace));
						}

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
