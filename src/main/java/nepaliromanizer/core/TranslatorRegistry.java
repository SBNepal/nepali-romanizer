package nepaliromanizer.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public final class TranslatorRegistry {

    private final Map<LanguagePair, Translator> registry = new ConcurrentHashMap<>();

    public TranslatorRegistry register(Translator translator) {
        Objects.requireNonNull(translator, "translator");
        registry.put(translator.getSupportedPair(), translator);
        return this;
    }

    public TranslatorRegistry register(LanguagePair pair, Translator translator, boolean overwrite) {
        Objects.requireNonNull(pair);
        Objects.requireNonNull(translator);
        if (overwrite) {
            registry.put(pair, translator);
        } else {
            registry.putIfAbsent(pair, translator);
        }
        return this;
    }


    public Translator get(LanguagePair pair) {
        Translator t = registry.get(pair);
        if (t == null) {
            throw new NoSuchElementException(
                    "No translator registered for pair: " + pair +
                    ". Registered pairs: " + registry.keySet());
        }
        return t;
    }

    public Set<LanguagePair> supportedPairs() {
        return Collections.unmodifiableSet(registry.keySet());
    }

    public TranslationResult translate(String text, LanguagePair pair) {
        return get(pair).translate(text);
    }
}
