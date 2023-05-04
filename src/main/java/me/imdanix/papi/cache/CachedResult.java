package me.imdanix.papi.cache;

import org.jetbrains.annotations.Nullable;

public record CachedResult(@Nullable String value, long until) {
    public boolean isOutdated() {
        return System.currentTimeMillis() >= until;
    }
}
