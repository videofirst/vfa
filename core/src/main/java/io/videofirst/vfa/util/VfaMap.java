package io.videofirst.vfa.util;

import io.videofirst.vfa.enums.VfaStatus;
import java.util.HashMap;
import java.util.Map;

public class VfaMap {

    /**
     * Return a Map<String, String> from a list of parameter String pairs;
     */
    public static Map<String, String> of(String... paramPairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < paramPairs.length; i += 2) {
            map.put(paramPairs[i], paramPairs[i + 1]);
        }
        return map;
    }

    /**
     * Return a Map<VfaStatus, String> from a list of parameter pairs;
     */
    public static Map<VfaStatus, String> ofVfaStatusString(Object... paramPairs) {
        Map<VfaStatus, String> map = new HashMap<>();
        for (int i = 0; i < paramPairs.length; i += 2) {
            map.put((VfaStatus) paramPairs[i], (String) paramPairs[i + 1]);
        }
        return map;
    }

}
