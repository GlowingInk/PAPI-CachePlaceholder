package me.imdanix.papi.cache;

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

public final class CacheExtension extends PlaceholderExpansion implements Listener {
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
        return "1.1";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return null;
        String[] split = params.split("_", 2);
        if (split.length == 1) {
            return null;
        }
        long offset;
        try {
            offset = Long.parseLong(split[0]) * 1000;
        } catch (NumberFormatException ex) {
            return null;
        }
        return findResult(player, split[1], offset);
    }

    private @Nullable String findResult(@NotNull Player player, @NotNull String params, long offset) {
        Map<String, CachedResult> playerCache = cache.computeIfAbsent(player.getUniqueId(), (u) -> new ConcurrentHashMap<>());
        CachedResult result = playerCache.get(params);
        if (result != null && offset <= 0) {
            return result.value();
        } else if (result == null || result.isOutdated(offset)) {
            String bracket = "{" + params + "}";
            String parsed = setBracketPlaceholders(player, bracket);
            result = new CachedResult(
                    parsed.equals(bracket) ? null : parsed,
                    currentTimeMillis()
            );
            playerCache.put(params, result);
        }
        return result.value();
    }
}
