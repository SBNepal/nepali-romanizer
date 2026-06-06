package nepaliromanizer.transliteration;

import nepaliromanizer.core.AbstractTranslator;
import nepaliromanizer.core.Language;
import nepaliromanizer.core.LanguagePair;

import java.util.Map;


public final class RomanToNepaliTransliterator extends AbstractTranslator {

    private static final LanguagePair PAIR =
            LanguagePair.of(Language.ROMAN_NEPALI, Language.NEPALI);

    private final WordDictionary dictionary;

    public RomanToNepaliTransliterator() {
        this(new WordDictionary());
    }

    public RomanToNepaliTransliterator(WordDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public LanguagePair getSupportedPair() { return PAIR; }

    @Override
    public String getProviderName() { return "RuleBasedRomanToNepali"; }

    // ── Core conversion ───────────────────────────────────────────────────────

    @Override
    protected String doTranslate(String text) {
        // Process word by word so whole-word dictionary entries can match
        String[] words = text.split("(?<=\\s)|(?=\\s)"); // preserve spaces as tokens
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                sb.append(word);
            } else {
                String dictMatch = dictionary.romanToUnicode(word);
                sb.append(dictMatch != null ? dictMatch : convertSegment(word));
            }
        }
        return sb.toString();
    }

    /**
     * Converts a single non-space segment using character-level rules.
     */
    private String convertSegment(String segment) {
        StringBuilder output  = new StringBuilder();
        int pos = 0;
        boolean verbatim = false;

        while (pos < segment.length()) {
            // Escape: {{ → literal {
            if (!verbatim && segment.startsWith("{{", pos)) {
                output.append('{');
                pos += 2;
                continue;
            }
            // Start verbatim block
            if (!verbatim && segment.charAt(pos) == '{') {
                verbatim = true;
                pos++;
                continue;
            }
            // End verbatim block
            if (verbatim && segment.charAt(pos) == '}') {
                verbatim = false;
                pos++;
                continue;
            }
            // Inside verbatim block — pass through unchanged
            if (verbatim) {
                output.append(segment.charAt(pos));
                pos++;
                continue;
            }

            // Special: anusvara
            if (segment.startsWith("M", pos)) {
                output.append(NepaliMappings.AMKAAR);
                pos++;
                continue;
            }
            // Special: chandrabindu
            if (segment.startsWith("NN", pos)) {
                output.append(NepaliMappings.ANNKAAR);
                pos += 2;
                continue;
            }
            // Special: vocalic ṛ matra (only after a consonant)
            if (segment.startsWith("RI", pos)) {
                // If previous output ends with a consonant cluster, replace last char
                // with the matra form. Otherwise treat as the independent vowel.
                if (output.length() > 0) {
                    output.append(NepaliMappings.RI_MATRA);
                } else {
                    output.append("ऋ");
                }
                pos += 2;
                continue;
            }

            // Greedy longest-match scan
            boolean matched = false;
            for (Map.Entry<String, String> entry : NepaliMappings.ALL_MAPPINGS.entrySet()) {
                String rom = entry.getKey();
                if (segment.startsWith(rom, pos)) {
                    output.append(entry.getValue());
                    pos += rom.length();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                // Unknown character — pass through
                output.append(segment.charAt(pos));
                pos++;
            }
        }
        return output.toString();
    }
}
