package com.lift.simulator.utility;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lift.simulator.dto.LiftRequestDTO;

import java.io.File;
import java.io.IOException;
import java.util.*;
public class ResourceUtility {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<Integer, List<LiftRequestDTO>> loadLiftRequests(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return new HashMap<>();

        // Read JSON with string keys first
        Map<String, List<LiftRequestDTO>> stringKeyMap = mapper.readValue(
                file,
                new TypeReference<Map<String, List<LiftRequestDTO>>>() {}
        );

        // Convert string keys to integer batch IDs
        Map<Integer, List<LiftRequestDTO>> intKeyMap = new HashMap<>();
        for (Map.Entry<String, List<LiftRequestDTO>> entry : stringKeyMap.entrySet()) {
            intKeyMap.put(Integer.parseInt(entry.getKey()), entry.getValue());
        }

        return intKeyMap;
    }

    public static void saveLiftRequests(String filePath, Map<Integer, List<LiftRequestDTO>> liftRequestsMap) throws IOException {
        // Convert integer keys back to strings for JSON
        Map<String, List<LiftRequestDTO>> stringKeyMap = new HashMap<>();
        liftRequestsMap.forEach((k, v) -> stringKeyMap.put(String.valueOf(k), v));
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), stringKeyMap);
    }

    public static void addLiftRequest(Map<Integer, List<LiftRequestDTO>> liftRequestsMap, int batchId, LiftRequestDTO request) {
        List<LiftRequestDTO> requests = liftRequestsMap.getOrDefault(batchId, new ArrayList<>());
        requests.add(request);
        liftRequestsMap.put(batchId, requests);
    }

    public static List<LiftRequestDTO> getRequestsByBatch(Map<Integer, List<LiftRequestDTO>> liftRequestsMap, int batchId) {
        return liftRequestsMap.getOrDefault(batchId, new ArrayList<>());
    }

    public static boolean verifyBatch(Map<Integer, List<LiftRequestDTO>> liftRequestsMap, int batchId) {
        return liftRequestsMap.containsKey(batchId) && !liftRequestsMap.get(batchId).isEmpty();
    }

}
