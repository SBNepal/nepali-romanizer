package nepaliromanizer.core;


public enum Language {

    NEPALI("ne", "Nepali", "नेपाली"),

    ROMAN_NEPALI("ne-Latn", "Roman Nepali", "Roman Nepali"),

    ENGLISH("en", "English", "English"),

    HINDI("hi", "Hindi", "हिंदी"),

    SANSKRIT("sa", "Sanskrit", "संस्कृतम्");

    private final String bcp47Tag;      // IETF language tag
    private final String englishName;
    private final String nativeName;

    Language(String bcp47Tag, String englishName, String nativeName) {
        this.bcp47Tag    = bcp47Tag;
        this.englishName = englishName;
        this.nativeName  = nativeName;
    }

    public String getBcp47Tag()     { return bcp47Tag; }
    public String getEnglishName()  { return englishName; }
    public String getNativeName()   { return nativeName; }

    public LanguagePair to(Language target) {
        return LanguagePair.of(this, target);
    }
}
