package com.scalyr.util;

import com.scalyr.api.logs.EventAttributes;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsDeep;

public class UtilTest {
    @BeforeMethod
    public void setUp() throws Exception {
    }

    @AfterMethod
    public void tearDown() throws Exception {
    }

    @Test
    public void testStringToIntMemory() throws Exception {
        assertEquals(new Integer(4194304), Util.stringToIntMemory("4m"));
        assertEquals(new Integer(4194304), Util.stringToIntMemory("4194304"));
        assertEquals(new Integer(4194304), Util.stringToIntMemory("4096k"));
        assertEquals(Util.stringToIntMemory("4m"), Util.stringToIntMemory("4096k"));
        assertEquals(null, Util.stringToIntMemory(""));
    }

    @Test
    public void testMakeEventAttributesFromString() throws Exception {
        EventAttributes originalAttrs = new EventAttributes();
        originalAttrs.put("serverHost", "123");
        originalAttrs.put("logfile", "loggy");
        originalAttrs.put("env", "proddy");
        originalAttrs.addAll(Util.makeEventAttributesFromString("appName=appofdoom,zodiac=rooster"));

        EventAttributes testAttrs = new EventAttributes();
        testAttrs.put("serverHost", "123");
        testAttrs.put("logfile", "loggy");
        testAttrs.put("env", "proddy");
        testAttrs.put("appName", "appofdoom");
        testAttrs.put("zodiac", "rooster");
        assertEquals(testAttrs, originalAttrs);
    }

    @Test
    public void testKvStringToMap() throws Exception {
        assertEqualsDeep(new HashMap<String, String>(), Util.kvStringToMap(""));
        assertEqualsDeep(new HashMap<String, String>(), Util.kvStringToMap(null));

        assertEqualsDeep(Collections.singletonMap("zodiac", "rooster"), Util.kvStringToMap("zodiac=rooster"));
        assertEqualsDeep(Collections.singletonMap("zodiac", "rooster"), Util.kvStringToMap("zodiac =rooster"));
        assertEqualsDeep(Collections.singletonMap("zodiac", "rooster"), Util.kvStringToMap(" zodiac=rooster"));
        assertEqualsDeep(Collections.singletonMap("zodiac", "rooster"), Util.kvStringToMap("zodiac= rooster"));
        assertEqualsDeep(Collections.singletonMap("zodiac", "rooster"), Util.kvStringToMap("zodiac=rooster "));
        assertEqualsDeep(Collections.singletonMap("zodiac", "roo ster"), Util.kvStringToMap("     zodiac=roo ster     "));
        Map<String, String> test = new HashMap<String, String>();
        test.put("zodiac", "rooster");
        test.put("he llo", "wor ld");
        assertEqualsDeep(test, Util.kvStringToMap(" zodiac = rooster , he llo  = wor ld"));
    }
}
