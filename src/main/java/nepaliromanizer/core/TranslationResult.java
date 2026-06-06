package nepaliromanizer.core;

import java.util.Objects;

public final class TranslationResult {

    private final String input;
    private final String output;
    private final Language sourceLanguage;
    private final Language targetLanguage;
    private final String provider;        // which engine/provider produced this
    private final boolean cached;
    private final long processingTimeMs;

    private TranslationResult(Builder builder) {
        this.input = Objects.requireNonNull(builder.input);
        this.output = Objects.requireNonNull(builder.output);
        this.sourceLanguage = Objects.requireNonNull(builder.sourceLanguage);
        this.targetLanguage = Objects.requireNonNull(builder.targetLanguage);
        this.provider = builder.provider != null ? builder.provider : "unknown";
        this.cached = builder.cached;
        this.processingTimeMs = builder.processingTimeMs;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────
    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    public Language getTargetLanguage() {
        return targetLanguage;
    }

    public String getProvider() {
        return provider;
    }

    public boolean isCached() {
        return cached;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    @Override
    public String toString() {
        return String.format("TranslationResult{src=%s, tgt=%s, out='%s', provider='%s', cached=%b, ms=%d}", sourceLanguage, targetLanguage, output, provider, cached, processingTimeMs);
    }

    // ── Builder ───────────────────────────────────────────────────────────────
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String input;
        private String output;
        private Language sourceLanguage;
        private Language targetLanguage;
        private String provider;
        private boolean cached;
        private long processingTimeMs;

        public Builder input(String v) {
            this.input = v;
            return this;
        }

        public Builder output(String v) {
            this.output = v;
            return this;
        }

        public Builder sourceLanguage(Language v) {
            this.sourceLanguage = v;
            return this;
        }

        public Builder targetLanguage(Language v) {
            this.targetLanguage = v;
            return this;
        }

        public Builder provider(String v) {
            this.provider = v;
            return this;
        }

        public Builder cached(boolean v) {
            this.cached = v;
            return this;
        }

        public Builder processingTimeMs(long v) {
            this.processingTimeMs = v;
            return this;
        }

        public TranslationResult build() {
            return new TranslationResult(this);
        }
    }
}
