package nepaliromanizer.transliteration;

import nepaliromanizer.core.AbstractTranslator;
import nepaliromanizer.core.Language;
import nepaliromanizer.core.LanguagePair;

import java.util.Map;

public final class NepaliToRomanTransliterator extends AbstractTranslator {

    private static final LanguagePair PAIR =
            LanguagePair.of(Language.NEPALI, Language.ROMAN_NEPALI);

    private final WordDictionary dictionary;

    public NepaliToRomanTransliterator() {
        this(new WordDictionary());
    }

    public NepaliToRomanTransliterator(WordDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public LanguagePair getSupportedPair() { return PAIR; }

    @Override
    public String getProviderName() { return "RuleBasedNepaliToRoman"; }

    @Override
    protected String doTranslate(String text) {
        // Split on whitespace preserving the whitespace tokens
        String[] tokens = text.split("(?<=\\s)|(?=\\s)");
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (token.isBlank()) {
                sb.append(token);
            } else {
                // Check word-level dictionary first
                String dictMatch = dictionary.unicodeToRoman(token);
                sb.append(dictMatch != null ? dictMatch : convertSegment(token));
            }
        }
        return sb.toString();
    }

    private String convertSegment(String segment) {
        StringBuilder output = new StringBuilder();
        int pos = 0;

        while (pos < segment.length()) {
            boolean matched = false;
            for (Map.Entry<String, String> entry : NepaliMappings.REVERSE_MAPPINGS.entrySet()) {
                String devanagari = entry.getKey();
                if (segment.startsWith(devanagari, pos)) {
                    output.append(entry.getValue());
                    pos += devanagari.length();
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                // Pass through (covers ASCII, punctuation, unknown Devanagari)
                output.append(segment.charAt(pos));
                pos++;
            }
        }
        return output.toString();
    }
}
