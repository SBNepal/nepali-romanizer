package nepaliromanizer;


import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import nepaliromanizer.core.Language;
import nepaliromanizer.core.LanguagePair;
import nepaliromanizer.core.TranslationResult;
import nepaliromanizer.transliteration.NepaliToRomanTransliterator;
import nepaliromanizer.transliteration.RomanToNepaliTransliterator;
import nepaliromanizer.transliteration.WordDictionary;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Transliteration Tests")
class TransliterationTest {

    private RomanToNepaliTransliterator romanToNepali;
    private NepaliToRomanTransliterator nepaliToRoman;

    @BeforeEach
    void setUp() {
        WordDictionary dict = new WordDictionary();
        romanToNepali = new RomanToNepaliTransliterator(dict);
        nepaliToRoman = new NepaliToRomanTransliterator(dict);
    }

    @DisplayName("Roman → Nepali: consonants with implicit-a")
    @ParameterizedTest(name = "''{0}'' → ''{1}''")
    @CsvSource({
            "ka,    क",
            "kha,   ख",
            "ga,    ग",
            "gha,   घ",
            "cha,   च",
            "chha,  छ",
            "ja,    ज",
            "ta,    त",
            "da,    द",
            "na,    न",
            "pa,    प",
            "ba,    ब",
            "ma,    म",
            "ya,    य",
            "ra,    र",
            "la,    ल",
            "sa,    स",
            "ha,    ह",
    })
    void testConsonants(String roman, String expected) {
        assertEquals(expected.strip(), romanToNepali.translate(roman.strip()).getOutput());
    }

    @DisplayName("Roman → Nepali: vowels")
    @ParameterizedTest(name = "''{0}'' → ''{1}''")
    @CsvSource({
            "a,   अ",
            "aa,  आ",
            "i,   इ",
            "ee,  ई",
            "u,   उ",
            "oo,  ऊ",
            "e,   ए",
            "o,   ओ",
            "ai,  ऐ",
    })
    void testVowels(String roman, String expected) {
        assertEquals(expected.strip(), romanToNepali.translate(roman.strip()).getOutput());
    }

    @Test
    @DisplayName("Roman → Nepali: Namaste")
    void testNameaste() {
        String result = romanToNepali.translate("namaste").getOutput();
        assertEquals("नमस्ते", result);
    }

    @Test
    @DisplayName("Roman → Nepali: Nepal")
    void testNepal() {
        String result = romanToNepali.translate("nepal").getOutput();
        assertEquals("नेपाल", result);
    }

    @Test
    @DisplayName("Roman → Nepali: numbers are converted")
    void testNumbers() {
        String result = romanToNepali.translate("123").getOutput();
        assertEquals("१२३", result);
    }

    @Test
    @DisplayName("Roman → Nepali: period becomes danda")
    void testPeriodToDanda() {
        assertEquals("।", romanToNepali.translate(".").getOutput());
    }

    @Test
    @DisplayName("Roman → Nepali: whole-word dictionary override")
    void testDictionaryOverride() {
        String result = romanToNepali.translate("kathmandu").getOutput();
        assertEquals("काठमाडौं", result);
    }

    @Test
    @DisplayName("Roman → Nepali: verbatim escape block")
    void testVerbatimEscape() {
        // {Hello} should pass through as-is
        String result = romanToNepali.translate("{Hello}").getOutput();
        assertEquals("Hello", result);
    }

    @Test
    @DisplayName("Roman → Nepali: empty input returns empty output")
    void testEmptyInput() {
        TranslationResult r = romanToNepali.translate("");
        assertEquals("", r.getOutput());
    }

    @Test
    @DisplayName("Roman → Nepali: null input returns empty output")
    void testNullInput() {
        TranslationResult r = romanToNepali.translate(null);
        assertEquals("", r.getOutput());
    }

    @Test
    @DisplayName("Nepali → Roman: basic round-trip sanity check")
    void testNepaliToRomanBasic() {
        // क → ka
        String result = nepaliToRoman.translate("क").getOutput();
        assertFalse(result.isEmpty(), "Expected non-empty roman output for 'क'");
    }

    @Test
    @DisplayName("Nepali → Roman: digits are restored")
    void testDigitsRoundTrip() {
        // Convert roman → nepali digits, then back
        String nepaliDigits = romanToNepali.translate("2025").getOutput();
        assertEquals("२०२५", nepaliDigits);
        String romanBack = nepaliToRoman.translate(nepaliDigits).getOutput();
        assertEquals("2025", romanBack);
    }

    @Test
    @DisplayName("Facade: transliteration-only build works without API key")
    void testFacadeTransliterationOnly() {
        NepaliRomanizer facade = NepaliRomanizer.builder()
                .cacheEnabled(false)
                .build();
        assertEquals("नमस्ते", facade.romanToNepali("namaste"));
        assertFalse(facade.nepaliToRoman("नमस्ते").isEmpty());
    }

    @Test
    @DisplayName("Facade: unsupported pair throws NoSuchElementException")
    void testFacadeUnsupportedPair() {
        NepaliRomanizer facade = NepaliRomanizer.builder().build();
        assertThrows(java.util.NoSuchElementException.class,
                () -> facade.translateFull("Hello", Language.ENGLISH.to(Language.NEPALI)));
    }

    @Test
    @DisplayName("Translator reports correct provider name")
    void testProviderName() {
        assertEquals("RuleBasedRomanToNepali", romanToNepali.getProviderName());
    }

    @Test
    @DisplayName("Translator reports correct language pair")
    void testLanguagePair() {
        assertEquals(
                LanguagePair.of(Language.ROMAN_NEPALI, Language.NEPALI),
                romanToNepali.getSupportedPair()
        );
    }
}
