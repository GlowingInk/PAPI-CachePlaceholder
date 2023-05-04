package me.imdanix.papi.cache;

import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;
import static me.clip.placeholderapi.PlaceholderAPI.setBracketPlaceholders;

public final class CacheExtension extends PlaceholderExpansion implements Configurable, Listener {
    private final Map<UUID, Map<String, CachedResult>> cache = new ConcurrentHashMap<>();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cache.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public @NotNull String getAuthor() {
        return "imDaniX";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cache";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return null;
        var playerCache = cache.computeIfAbsent(player.getUniqueId(), (u) -> new ConcurrentHashMap<>());
        CachedResult result = playerCache.get(params);
        if (result == null || result.isOutdated()) {
            String bracket = "{" + params + "}";
            String parsed = setBracketPlaceholders(player, bracket);
            result = new CachedResult(
                    parsed.equals(bracket) ? null : parsed,
                    currentTimeMillis() + getLong("cache-time", 10_000L)
            );
            playerCache.put(params, result);
        }
        return result.value();
    }

    @Override
    public Map<String, Object> getDefaults() {
        return Map.of("cache-time", 10_000L);
    }
}
