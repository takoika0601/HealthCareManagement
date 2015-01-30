package jp.co.akiguchilab.healthcaremanagement.geneticalgorithm;

public class ThresholdData implements Cloneable{
    private static final String TAG = ThresholdData.class.getSimpleName();

    private float ThresholdX_min = 0;
    private float ThresholdX_max = 0;
    private float ThresholdY_min = 0;
    private float ThresholdY_max = 0;
    private float ThresholdZ_min = 0;
    private float ThresholdZ_max = 0;
    private float ThresholdX_width = 0;
    private float ThresholdY_width = 0;
    private float ThresholdZ_width = 0;
    private float Threshold_width = 0;
    private int x_ActivityCount = 0;

    private float fitness = 0;

    public ThresholdData() {
    }

    public float getThresholdX_max() {
        return ThresholdX_max;
    }

    public void setThresholdX_max(float thresholdX_max) {
        ThresholdX_max = thresholdX_max;
    }

    public float getThresholdY_min() {
        return ThresholdY_min;
    }

    public void setThresholdY_min(float thresholdY_min) {
        ThresholdY_min = thresholdY_min;
    }

    public float getThresholdY_max() {
        return ThresholdY_max;
    }

    public void setThresholdY_max(float thresholdY_max) {
        ThresholdY_max = thresholdY_max;
    }

    public float getThresholdZ_min() {
        return ThresholdZ_min;
    }

    public void setThresholdZ_min(float thresholdZ_min) {
        ThresholdZ_min = thresholdZ_min;
    }

    public float getThresholdZ_max() {
        return ThresholdZ_max;
    }

    public void setThresholdZ_max(float thresholdZ_max) {
        ThresholdZ_max = thresholdZ_max;
    }

    public float getThresholdX_min() {
        return ThresholdX_min;
    }

    public float getThresholdX_width() {
        return ThresholdX_width;
    }

    public void setThresholdX_min(float thresholdX_min) {
        ThresholdX_min = thresholdX_min;
    }

    public float getThresholdY_width() {
        return ThresholdY_width;
    }

    public void setThresholdX_width(float thresholdX_width) {
        ThresholdX_width = thresholdX_width;
    }

    public float getThresholdZ_width() {
        return ThresholdZ_width;
    }

    public void setThresholdY_width(float thresholdY_width) {
        ThresholdY_width = thresholdY_width;
    }

    public void setThresholdZ_width(float thresholdZ_width) {
        ThresholdZ_width = thresholdZ_width;
    }

    public float getThreshold_width() {
        return Threshold_width;
    }

    public void setThreshold_width(float threshold_width) {
        Threshold_width = threshold_width;
    }

    public void calcThreshold_width() {
        Threshold_width = ThresholdX_width + ThresholdY_width + ThresholdZ_width;
    }

    public void calcFitness(int ActivityCount) {
        int x = ActivityCount;
        fitness = -1/5 * (float)(Math.pow(x - 5, 2)) + 5 * ThresholdX_width;
    }

    public float getFitness() {
        return fitness;
    }

    public void setX_ActivityCount(int x_activityCount) {
        x_ActivityCount = x_activityCount;
    }

    public int getX_ActivityCount() {
        return x_ActivityCount;
    }

    public ThresholdData clone() {
        ThresholdData data = new ThresholdData();
        data.setThresholdX_min(ThresholdX_min);
        data.setThresholdX_max(ThresholdX_max);
        data.setThresholdX_width(ThresholdX_width);
        data.setX_ActivityCount(x_ActivityCount);

        return data;
    }
}
