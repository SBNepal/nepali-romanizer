package nepaliromanizer.core;

public interface Translator {

    TranslationResult translate(String text) throws TranslationException;

    LanguagePair getSupportedPair();

    String getProviderName();

    default boolean supports(LanguagePair pair) {
        return getSupportedPair().equals(pair);
    }
}
