package jp.co.akiguchilab.healthcaremanagement.util;

import java.util.*;

import jp.co.akiguchilab.healthcaremanagement.geneticalgorithm.ThresholdData;

public class TheComparator implements Comparator<ThresholdData> {

    public int compare(ThresholdData a, ThresholdData b) {
        double fitnessA = a.getFitness();
        double fitnessB = b.getFitness();

        if (fitnessA > fitnessB) {
            return -1;
        } else if (fitnessA == fitnessB) {
            return 0;
        } else {
            return 1;
        }
    }
}