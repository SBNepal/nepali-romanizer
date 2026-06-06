package nepaliromanizer.transliteration;

import java.util.*;

public final class NepaliMappings {

    public static final String HALANTA = "्";
    public static final String AMKAAR = "ं";
    public static final String ANNKAAR = "ँ";
    public static final String RI_MATRA = "ृ";

    public static final Map<String, String> NUMBERS = Map.of("0", "०", "1", "१", "2", "२", "3", "३", "4", "४", "5", "५", "6", "६", "7", "७", "8", "८", "9", "९");
    public static final Map<String, String> BASIC_VOWELS = Map.ofEntries(Map.entry("aa", "आ"), Map.entry("ai", "ऐ"), Map.entry("au", "औ"), Map.entry("ee", "ई"), Map.entry("ii", "ई"), Map.entry("oo", "ऊ"), Map.entry("uu", "ऊ"), Map.entry("Ri", "ऋ"), Map.entry("a", "अ"), Map.entry("e", "ए"), Map.entry("i", "इ"), Map.entry("o", "ओ"), Map.entry("u", "उ"));

    public static final Map<String, String> CONSONANT_MAATRAS = Map.ofEntries(Map.entry("aa", "ा"), Map.entry("ai", "ै"), Map.entry("au", "ौ"), Map.entry("ee", "ी"), Map.entry("ii", "ी"), Map.entry("i", "ि"), Map.entry("oo", "ू"), Map.entry("uu", "ू"), Map.entry("o", "ो"), Map.entry("u", "ु"), Map.entry("e", "े"), Map.entry("Ri", "ृ"));

    public static final Map<String, String> AKAARS = new LinkedHashMap<>() {{
        put("ksha", "क्ष");
        put("gya", "ज्ञ");
        put("tra", "त्र");
        put("gYa", "ज्ञ");
        put("NGa", "ङ");

        put("chha", "छ");
        put("cha", "च");

        put("Sha", "ष");
        put("sha", "श");

        put("Tha", "ठ");
        put("tha", "थ");

        put("Dha", "ढ");
        put("dha", "ध");

        put("kha", "ख");
        put("gha", "घ");
        put("jha", "झ");
        put("bha", "भ");
        put("pha", "फ");

        put("yNa", "ञ");

        put("Nga", "ङ");
        put("nga", "ङ");

        put("Ta", "ट");
        put("Da", "ड");
        put("Na", "ण");

        put("ka", "क");
        put("ga", "ग");
        put("ja", "ज");

        put("ta", "त");
        put("da", "द");
        put("na", "न");

        put("pa", "प");
        put("fa", "फ");
        put("ba", "ब");

        put("va", "व");
        put("wa", "व");

        put("ma", "म");
        put("ya", "य");
        put("ra", "र");
        put("la", "ल");

        put("sa", "स");
        put("ha", "ह");
    }};

    public static final Map<String, String> ALL_MAPPINGS;

    static {
        Map<String, String> m = new LinkedHashMap<>();

        m.putAll(BASIC_VOWELS);

        m.putAll(AKAARS);

        for (Map.Entry<String, String> e : AKAARS.entrySet()) {
            String roman = e.getKey();
            String devanagari = e.getValue();
            if (roman.endsWith("a")) {
                String stem = roman.substring(0, roman.length() - 1);
                if (!stem.isEmpty()) {
                    m.putIfAbsent(stem, devanagari + HALANTA);
                }
            }
        }

        // 4. Consonant + vowel matra combinations
        //    e.g. "ki" = क + ि, "kaa" = क + ा, etc.
        for (Map.Entry<String, String> cons : AKAARS.entrySet()) {
            String consRoman = cons.getKey();          // "ka"
            String consDevanagari = cons.getValue();   // "क"
            String stem = consRoman.endsWith("a") ? consRoman.substring(0, consRoman.length() - 1) : consRoman;
            if (stem.isEmpty()) continue;

            for (Map.Entry<String, String> matra : CONSONANT_MAATRAS.entrySet()) {
                String vowelRoman = matra.getKey();   // "i"
                String vowelMatra = matra.getValue(); // "ि"
                String combined = stem + vowelRoman;  // "ki"
                String devanagariOut = consDevanagari + vowelMatra; // "कि"
                m.putIfAbsent(combined, devanagariOut);
            }
        }

        // 5. Punctuation
        m.put(".", "।");
        m.put("M", "ं");
        m.put("N", "ँ");

        // 6. Numbers
        m.putAll(NUMBERS);

        // Sort by descending key length to prefer longer matches
        List<Map.Entry<String, String>> entries = new ArrayList<>(m.entrySet());
        entries.sort((a, b) -> b.getKey().length() - a.getKey().length());
        Map<String, String> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : entries) sorted.put(e.getKey(), e.getValue());

        ALL_MAPPINGS = Collections.unmodifiableMap(sorted);
    }

    // Reverse: Devanagari → Roman (best-effort, consonants only)
    public static final Map<String, String> REVERSE_MAPPINGS;

    static {
        // Build from AKAARS (canonical consonant forms)
        Map<String, String> rev = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : AKAARS.entrySet()) {
            rev.putIfAbsent(e.getValue(), e.getKey()); // first wins
        }
        for (Map.Entry<String, String> e : BASIC_VOWELS.entrySet()) {
            rev.putIfAbsent(e.getValue().trim(), e.getKey());
        }
        for (Map.Entry<String, String> e : CONSONANT_MAATRAS.entrySet()) {
            rev.putIfAbsent(e.getValue(), e.getKey());
        }
        // Digits
        for (Map.Entry<String, String> e : NUMBERS.entrySet()) {
            rev.put(e.getValue(), e.getKey());
        }
        rev.put("।", ".");
        rev.put(HALANTA, "");
        rev.put(AMKAAR, "M");
        rev.put(ANNKAAR, "NN");
        rev.put(RI_MATRA, "Ri");


        // Sort by descending key length
        List<Map.Entry<String, String>> entries = new ArrayList<>(rev.entrySet());
        entries.sort((a, b) -> b.getKey().length() - a.getKey().length());
        Map<String, String> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : entries) sorted.put(e.getKey(), e.getValue());
        REVERSE_MAPPINGS = Collections.unmodifiableMap(sorted);
    }

    private NepaliMappings() {
    }
}
