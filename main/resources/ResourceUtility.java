package main.resources;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
public class ResourceUtility {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<Integer, List<LiftRequestJSONUtil>> loadLiftRequests(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return new HashMap<>();

        // Read JSON with string keys first
        Map<String, List<LiftRequestJSONUtil>> stringKeyMap = mapper.readValue(
                file,
                new TypeReference<Map<String, List<LiftRequestJSONUtil>>>() {}
        );

        // Convert string keys to integer batch IDs
        Map<Integer, List<LiftRequestJSONUtil>> intKeyMap = new HashMap<>();
        for (Map.Entry<String, List<LiftRequestJSONUtil>> entry : stringKeyMap.entrySet()) {
            intKeyMap.put(Integer.parseInt(entry.getKey()), entry.getValue());
        }

        return intKeyMap;
    }

    public static void saveLiftRequests(String filePath, Map<Integer, List<LiftRequestJSONUtil>> liftRequestsMap) throws IOException {
        // Convert integer keys back to strings for JSON
        Map<String, List<LiftRequestJSONUtil>> stringKeyMap = new HashMap<>();
        liftRequestsMap.forEach((k, v) -> stringKeyMap.put(String.valueOf(k), v));
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), stringKeyMap);
    }

    public static void addLiftRequest(Map<Integer, List<LiftRequestJSONUtil>> liftRequestsMap, int batchId, LiftRequestJSONUtil request) {
        List<LiftRequestJSONUtil> requests = liftRequestsMap.getOrDefault(batchId, new ArrayList<>());
        requests.add(request);
        liftRequestsMap.put(batchId, requests);
    }

    public static List<LiftRequestJSONUtil> getRequestsByBatch(Map<Integer, List<LiftRequestJSONUtil>> liftRequestsMap, int batchId) {
        return liftRequestsMap.getOrDefault(batchId, new ArrayList<>());
    }

    public static boolean verifyBatch(Map<Integer, List<LiftRequestJSONUtil>> liftRequestsMap, int batchId) {
        return liftRequestsMap.containsKey(batchId) && !liftRequestsMap.get(batchId).isEmpty();
    }

}
