package nepaliromanizer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractTranslator implements Translator {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public final TranslationResult translate(String text) throws TranslationException {
        if (text == null || text.isEmpty()) {
            return TranslationResult.builder().input(text == null ? "" : text).output("").sourceLanguage(getSupportedPair().getSource()).targetLanguage(getSupportedPair().getTarget()).provider(getProviderName()).build();
        }

        long start = System.currentTimeMillis();
        try {
            String result = doTranslate(text);
            long elapsed = System.currentTimeMillis() - start;
            log.debug("[{}] '{}' → '{}' ({}ms)", getProviderName(), text, result, elapsed);
            return TranslationResult.builder().input(text).output(result).sourceLanguage(getSupportedPair().getSource()).targetLanguage(getSupportedPair().getTarget()).provider(getProviderName()).processingTimeMs(elapsed).build();
        } catch (TranslationException te) {
            throw te;
        } catch (Exception e) {
            throw new TranslationException("Translation failed in " + getProviderName() + ": " + e.getMessage(), text, getSupportedPair(), e);
        }
    }

    protected abstract String doTranslate(String text) throws Exception;
}
