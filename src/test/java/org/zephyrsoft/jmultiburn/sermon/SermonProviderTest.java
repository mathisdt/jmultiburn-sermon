package org.zephyrsoft.jmultiburn.sermon;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SermonProviderTest {
	
	@Test
	public void testFilePattern() {
		assertTrue(SermonProvider.FILE_PATTERN.matcher("2013-09-01-SomeName-24kbps.mp3").matches());
		assertTrue(SermonProvider.FILE_PATTERN.matcher("2013-09-01-SomeName-128kbps.mp3").matches());
		assertTrue(SermonProvider.FILE_PATTERN.matcher("2013-09-01-SomeName-Some_Speaker-128kbps.mp3").matches());
	}
	
	@Test
	public void testDirectoryPattern() {
		assertTrue(SermonProvider.DIRECTORY_PATTERN.matcher("2013-09-01-SomeName-0").matches());
		assertTrue(SermonProvider.DIRECTORY_PATTERN.matcher("2013-09-01-SomeName-3").matches());
		assertTrue(SermonProvider.DIRECTORY_PATTERN.matcher("2013-09-01-SomeName-Some_Speaker-2").matches());
	}
	
}
