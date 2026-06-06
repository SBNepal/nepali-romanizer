package nepaliromanizer.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import nepaliromanizer.core.LanguagePair;
import nepaliromanizer.core.TranslationException;
import nepaliromanizer.core.TranslationResult;
import nepaliromanizer.core.Translator;

import java.time.Duration;
import java.util.Objects;

public final class CachingTranslator implements Translator {

    private static final Logger log = LoggerFactory.getLogger(CachingTranslator.class);

    private final Translator delegate;
    private final Cache<String, TranslationResult> cache;

    private CachingTranslator(Translator delegate, Cache<String, TranslationResult> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public LanguagePair getSupportedPair() {
        return delegate.getSupportedPair();
    }

    @Override
    public String getProviderName() {
        return delegate.getProviderName() + "+Cache";
    }

    @Override
    public TranslationResult translate(String text) throws TranslationException {
        if (text == null || text.isEmpty()) return delegate.translate(text);

        TranslationResult cached = cache.getIfPresent(text);
        if (cached != null) {
            log.debug("Cache hit for '{}' [{}]", text, getSupportedPair());
            return TranslationResult.builder().input(cached.getInput()).output(cached.getOutput()).sourceLanguage(cached.getSourceLanguage()).targetLanguage(cached.getTargetLanguage()).provider(cached.getProvider()).cached(true).processingTimeMs(0).build();
        }

        TranslationResult result = delegate.translate(text);
        cache.put(text, result);
        return result;
    }

    /**
     * Invalidate the entire cache (useful after dictionary updates).
     */
    public void invalidateAll() {
        cache.invalidateAll();
    }

    /**
     * Current number of cached entries.
     */
    public long estimatedSize() {
        return cache.estimatedSize();
    }


    public static Builder wrap(Translator translator) {
        return new Builder(translator);
    }

    public static final class Builder {
        private final Translator translator;
        private long maxSize = 5_000;
        private Duration ttl = Duration.ofHours(12);

        private Builder(Translator translator) {
            this.translator = Objects.requireNonNull(translator);
        }

        public Builder maxSize(long size) {
            this.maxSize = size;
            return this;
        }

        public Builder ttl(Duration d) {
            this.ttl = d;
            return this;
        }

        public CachingTranslator build() {
            Cache<String, TranslationResult> c = Caffeine.newBuilder().maximumSize(maxSize).expireAfterWrite(ttl).recordStats().build();
            return new CachingTranslator(translator, c);
        }
    }
}
