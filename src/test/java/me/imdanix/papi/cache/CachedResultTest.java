package me.imdanix.papi.cache;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CachedResultTest {
    @DataProvider
    public Object[][] isOutdatedData() {
        return new Object[][] {
                {0, 0, true},
                {System.currentTimeMillis(), 10000, false}
        };
    }

    @Test(dataProvider = "isOutdatedData")
    public void isOutdatedTest(long start, long offset, boolean expected) {
        assertEquals(
                new CachedResult("", start).isOutdated(offset),
                expected
        );
    }
}
