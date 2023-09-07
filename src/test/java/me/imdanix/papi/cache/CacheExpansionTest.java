package me.imdanix.papi.cache;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class CacheExpansionTest {
    private final CacheExpansion extension = new CacheExpansion() {
        @Override
        public PlaceholderExpansion getExpansion(@NotNull String name) {
            return mockExpansion;
        }
    };

    private Player mockPlayer = mock(Player.class);
    private PlaceholderExpansion mockExpansion = mock(PlaceholderExpansion.class);

    @BeforeClass
    public void setup() {
        when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
    }

    @DataProvider
    public Object[][] onRequestData() {
        return new Object[][] {
                {"Invalid placeholder", null, null, false},
                {"60_valid_but empty", null, null, false},

                {"60_One_Minute Delay", "first cache M", "first cache M", false},
                {"60_One_Minute Delay", "second cache M", "first cache M", false},

                {"1_One_Second Delay", "first cache S", "first cache S", false},
                {"1_One_Second Delay", "second cache S", "second cache S", true},
                {"1_One_Second Delay", "third cache S", "second cache S", false}
        };
    }

    @Test(dataProvider = "onRequestData")
    public void onRequestTest(String params, String phResult, String expected, boolean wait) throws InterruptedException {
        if (wait) Thread.sleep(2000);
        when(mockExpansion.onRequest(any(), anyString())).thenReturn(phResult);
        assertEquals(
                extension.onRequest(mockPlayer, params),
                expected
        );
    }
}
