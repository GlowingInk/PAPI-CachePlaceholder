package ink.glowing.papi.cache;

import org.jetbrains.annotations.Nullable;

public final class CachedResult {
    private final @Nullable String value;
    private final long start;

    public CachedResult(@Nullable String value, long start) {
        this.value = value;
        this.start = start;
    }

    public boolean isOutdated(long offset) {
        return System.currentTimeMillis() >= start + offset;
    }

    public @Nullable String value() {
        return value;
    }
}
