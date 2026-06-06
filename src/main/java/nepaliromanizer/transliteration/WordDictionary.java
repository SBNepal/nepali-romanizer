package nepaliromanizer.transliteration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class WordDictionary {

    private static final Logger log = LoggerFactory.getLogger(WordDictionary.class);
    private static final String DEFAULT_RESOURCE = "words_maps.txt";

    /** Roman Nepali → Devanagari */
    private final Map<String, String> romanToUnicode;
    /** Devanagari → Roman Nepali (best-effort; last entry wins for duplicates) */
    private final Map<String, String> unicodeToRoman;

    public WordDictionary() {
        this(DEFAULT_RESOURCE);
    }

    public WordDictionary(String resourcePath) {
        Map<String, String> fwd = new LinkedHashMap<>();
        Map<String, String> rev = new LinkedHashMap<>();
        URL url = WordDictionary.class.getClassLoader().getResource(resourcePath);
        if (url == null) {
            log.warn("Word-map resource '{}' not found on classpath. Dictionary will be empty.", resourcePath);
        } else {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    line = line.strip();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\s+", 2);
                    if (parts.length != 2) {
                        log.warn("Skipping malformed line {} in '{}': '{}'", lineNum, resourcePath, line);
                        continue;
                    }
                    fwd.put(parts[0], parts[1]);
                    rev.putIfAbsent(parts[1], parts[0]); // first roman wins
                }
                log.debug("Loaded {} word-map entries from '{}'", fwd.size(), resourcePath);
            } catch (IOException e) {
                log.error("Failed to load word-map resource '{}'", resourcePath, e);
            }
        }
        this.romanToUnicode = Collections.unmodifiableMap(fwd);
        this.unicodeToRoman = Collections.unmodifiableMap(rev);
    }

    /** Look up a whole-word roman → unicode mapping, or {@code null} if absent. */
    public String romanToUnicode(String roman) {
        return romanToUnicode.get(roman);
    }

    /** Look up a whole-word unicode → roman mapping, or {@code null} if absent. */
    public String unicodeToRoman(String unicode) {
        return unicodeToRoman.get(unicode);
    }

    /** All roman → unicode entries (unmodifiable). */
    public Map<String, String> getAllRomanToUnicode() { return romanToUnicode; }

    /** All unicode → roman entries (unmodifiable). */
    public Map<String, String> getAllUnicodeToRoman() { return unicodeToRoman; }

    public int size() { return romanToUnicode.size(); }
}
