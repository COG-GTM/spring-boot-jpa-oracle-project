package com.bamossza.project.migration;

import java.util.ArrayList;
import java.util.List;

/** Row-level comparison of two car stores keyed by CAR_ID. */
public class ReconciliationReport {

    public String sourceStore;
    public String targetStore;
    public int sourceCount;
    public int targetCount;
    public List<Integer> missingInTarget = new ArrayList<>();
    public List<Integer> extraInTarget = new ArrayList<>();
    public List<Integer> fieldMismatches = new ArrayList<>();
    public String sourceChecksum;
    public String targetChecksum;

    public boolean isMatched() {
        return sourceCount == targetCount && missingInTarget.isEmpty() && extraInTarget.isEmpty()
                && fieldMismatches.isEmpty() && sourceChecksum.equals(targetChecksum);
    }
}
