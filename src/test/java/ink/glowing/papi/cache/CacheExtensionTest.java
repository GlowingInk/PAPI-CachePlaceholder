package ink.glowing.papi.cache;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class CacheExtensionTest {
    private final CacheExtension extension = new CacheExtension();
    private final Player mockPlayer = Mockito.mock(Player.class);

    @BeforeClass
    public void setup() {
        when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
    }

    @DataProvider
    public Object[][] onRequestData() {
        return new Object[][] {
                {"Invalid placeholder", "", null, false},
                {"0Invalid placeholder", "", null, false},
                {"Invalid_placeholder", "", null, false},

                {"60_One Minute Delay", "first cache M", "first cache M", false},
                {"60_One Minute Delay", "second cache M", "first cache M", false},

                {"1_One Second Delay", "first cache S", "first cache S", false},
                {"1_One Second Delay", "second cache S", "second cache S", true},
                {"1_One Second Delay", "third cache S", "second cache S", false}
        };
    }

    @Test(dataProvider = "onRequestData")
    public void onRequestTest(String params, String phResult, String expected, boolean wait) throws InterruptedException {
        if (wait) Thread.sleep(2000);
        try (MockedStatic<PlaceholderAPI> mockPapi = Mockito.mockStatic(PlaceholderAPI.class)) {
            mockPapi.when(() -> PlaceholderAPI.setBracketPlaceholders(any(Player.class), anyString()))
                    .thenReturn(phResult);
            assertEquals(
                    extension.onPlaceholderRequest(mockPlayer, params),
                    expected
            );
        }
    }
}
