package nepaliromanizer;

import nepaliromanizer.cache.CachingTranslator;
import nepaliromanizer.core.*;
import nepaliromanizer.transliteration.NepaliToRomanTransliterator;
import nepaliromanizer.transliteration.RomanToNepaliTransliterator;
import nepaliromanizer.transliteration.WordDictionary;

import java.time.Duration;

public final class NepaliRomanizer {

    private final TranslatorRegistry registry;

    private NepaliRomanizer(TranslatorRegistry registry) {
        this.registry = registry;
    }

    public String romanToNepali(String text) {
        return registry.translate(text, Language.ROMAN_NEPALI.to(Language.NEPALI)).getOutput();
    }

    public String nepaliToRoman(String text) {
        return registry.translate(text, Language.NEPALI.to(Language.ROMAN_NEPALI)).getOutput();
    }

    public TranslationResult translateFull(String text, LanguagePair pair) {
        return registry.translate(text, pair);
    }

    public TranslatorRegistry getRegistry() {
        return registry;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean cacheEnabled = true;
        private long cacheMaxSize = 10_000;
        private Duration cacheTtl = Duration.ofHours(12);

        private Builder() {}

        public Builder cacheEnabled(boolean enabled) {
            this.cacheEnabled = enabled;
            return this;
        }

        public Builder cacheMaxSize(long size) {
            this.cacheMaxSize = size;
            return this;
        }

        public Builder cacheTtl(Duration ttl) {
            this.cacheTtl = ttl;
            return this;
        }

        public NepaliRomanizer build() {
            WordDictionary dict = new WordDictionary();
            TranslatorRegistry registry = new TranslatorRegistry();

            Translator romanToNepali = new RomanToNepaliTransliterator(dict);
            Translator nepaliToRoman = new NepaliToRomanTransliterator(dict);

            if (cacheEnabled) {
                romanToNepali = CachingTranslator.wrap(romanToNepali)
                        .maxSize(cacheMaxSize).ttl(cacheTtl).build();
                nepaliToRoman = CachingTranslator.wrap(nepaliToRoman)
                        .maxSize(cacheMaxSize).ttl(cacheTtl).build();
            }

            registry.register(romanToNepali);
            registry.register(nepaliToRoman);

            return new NepaliRomanizer(registry);
        }
    }
}
