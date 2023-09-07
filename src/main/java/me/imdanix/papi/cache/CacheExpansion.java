package me.imdanix.papi.cache;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;

public class CacheExpansion extends PlaceholderExpansion implements Listener {
    private static final UUID ZERO_UUID = new UUID(0,0);
    private static final int OFFSET_INDEX = 0;
    private static final int PLACEHOLDER_INDEX = 1;
    private static final int PARAMETERS_INDEX = 2;

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
        return "1.2";
    }

    public PlaceholderExpansion getExpansion(@NotNull String name) {
        return getPlaceholderAPI().getLocalExpansionManager().getExpansion(name.toLowerCase(Locale.ROOT));
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return onRequest(player, params);
    }

    @Override
    public @Nullable String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        String[] split = params.split("_", 3); // offset_phname_params
        if (split.length < 3) {
            return null;
        }
        try {
            return findResult(
                    player,
                    Long.parseLong(split[OFFSET_INDEX]) * 1000,
                    split[PLACEHOLDER_INDEX],
                    split.length == 3 ? split[PARAMETERS_INDEX] : ""
            );
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private @Nullable String findResult(@Nullable OfflinePlayer player, long offset, @NotNull String phName, @NotNull String params) {
        UUID id = player == null ? ZERO_UUID : player.getUniqueId();
        var playerCache = cache.computeIfAbsent(id, (v) -> new ConcurrentHashMap<>());
        CachedResult result = playerCache.get(params);
        if (result == null || (offset != 0 && result.isOutdated(offset))) {
            PlaceholderExpansion expansion = getExpansion(phName);
            result = new CachedResult(
                    expansion == null ? null : expansion.onRequest(player, params),
                    currentTimeMillis()
            );
            playerCache.put(params, result);
        }
        return result.value();
    }
}
