package jp.co.akiguchilab.healthcaremanagement.util;

import java.util.*;

import jp.co.akiguchilab.healthcaremanagement.geneticalgorithm.ThresholdData;

public class TheComparator implements Comparator<ThresholdData> {

    public int compare(ThresholdData a, ThresholdData b) {
        double fitnessA = a.getThresholdX_width();
        double fitnessB = b.getThresholdX_width();

        if (fitnessA > fitnessB) {
            return -1;
        } else if (fitnessA == fitnessB) {
            return 0;
        } else {
            return 1;
        }
    }
}