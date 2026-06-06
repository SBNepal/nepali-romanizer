package nepaliromanizer.core;

public class TranslationException extends RuntimeException {

    private final String inputText;
    private final LanguagePair pair;

    public TranslationException(String message, String inputText, LanguagePair pair) {
        super(message);
        this.inputText = inputText;
        this.pair = pair;
    }

    public TranslationException(String message, String inputText, LanguagePair pair, Throwable cause) {
        super(message, cause);
        this.inputText = inputText;
        this.pair = pair;
    }

    public String getInputText() { return inputText; }
    public LanguagePair getPair() { return pair; }
}
