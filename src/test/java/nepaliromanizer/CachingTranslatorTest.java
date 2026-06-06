package nepaliromanizer;

import nepaliromanizer.cache.CachingTranslator;
import org.junit.jupiter.api.*;
import nepaliromanizer.core.TranslationResult;
import nepaliromanizer.core.Translator;
import nepaliromanizer.transliteration.RomanToNepaliTransliterator;
import nepaliromanizer.transliteration.WordDictionary;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CachingTranslator Tests")
class CachingTranslatorTest {

    private CachingTranslator cached;

    @BeforeEach
    void setUp() {
        Translator raw = new RomanToNepaliTransliterator(new WordDictionary());
        cached = CachingTranslator.wrap(raw).maxSize(100).build();
    }

    @Test
    @DisplayName("First call is not cached")
    void firstCallNotCached() {
        TranslationResult r = cached.translate("namaste");
        assertFalse(r.isCached());
        assertEquals("नमस्ते", r.getOutput());
    }

    @Test
    @DisplayName("Second call is served from cache")
    void secondCallCached() {
        cached.translate("nepal");
        TranslationResult r2 = cached.translate("nepal");
        assertTrue(r2.isCached());
        assertEquals("नेपाल", r2.getOutput());
    }

    @Test
    @DisplayName("Cache invalidation clears entries")
    void cacheInvalidation() {
        cached.translate("nepal");
        assertEquals(1, cached.estimatedSize());
        cached.invalidateAll();
        assertEquals(0, cached.estimatedSize());
        // Next call should not be cached
        assertFalse(cached.translate("nepal").isCached());
    }

    @Test
    @DisplayName("Provider name includes +Cache suffix")
    void providerNameSuffix() {
        assertTrue(cached.getProviderName().endsWith("+Cache"));
    }
}
