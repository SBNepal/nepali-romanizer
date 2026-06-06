package nepaliromanizer.core;

import java.util.Objects;

public final class LanguagePair {

    private final Language source;
    private final Language target;

    private LanguagePair(Language source, Language target) {
        this.source = Objects.requireNonNull(source, "source");
        this.target = Objects.requireNonNull(target, "target");
    }

    public static LanguagePair of(Language source, Language target) {
        return new LanguagePair(source, target);
    }

    public Language getSource() { return source; }
    public Language getTarget() { return target; }

    /** Returns the reversed pair. */
    public LanguagePair reversed() { return new LanguagePair(target, source); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LanguagePair p)) return false;
        return source == p.source && target == p.target;
    }

    @Override
    public int hashCode() { return Objects.hash(source, target); }

    @Override
    public String toString() { return source.getBcp47Tag() + " → " + target.getBcp47Tag(); }
}
