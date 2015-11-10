package jp.co.akiguchilab.healthcaremanagement.geneticalgorithm;

public class ThresholdData {
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

    private int x_ActivityCount = 0;
    private int y_ActivityCount = 0;
    private int z_ActivityCount = 0;

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

    public void setThresholdX_min(float thresholdX_min) {
        ThresholdX_min = thresholdX_min;
    }

    public void setThresholdX_width(float thresholdX_width) {
        ThresholdX_width = thresholdX_width;
    }

    public void setThresholdY_width(float thresholdY_width) {
        ThresholdY_width = thresholdY_width;
    }

    public void setThresholdZ_width(float thresholdZ_width) {
        ThresholdZ_width = thresholdZ_width;
    }

    public void calcFitness() {
        float x_fit = -15 * (float) (Math.pow(x_ActivityCount - 5, 2)) + 20 + ThresholdX_width;
        float y_fit = -15 * (float) (Math.pow(y_ActivityCount - 5, 2)) + 20 + ThresholdY_width;
        float z_fit = -15 * (float) (Math.pow(z_ActivityCount - 5, 2)) + 20 + ThresholdX_width;

        fitness = x_fit + y_fit + z_fit;
    }

    public float getFitness() {
        return fitness;
    }

    public int getX_ActivityCount() {
        return x_ActivityCount;
    }

    public void setX_ActivityCount(int x_activityCount) {
        x_ActivityCount = x_activityCount;
    }

    public int getY_ActivityCount() {
        return y_ActivityCount;
    }

    public void setY_ActivityCount(int y_ActivityCount) {
        this.y_ActivityCount = y_ActivityCount;
    }

    public int getZ_ActivityCount() {
        return z_ActivityCount;
    }

    public void setZ_ActivityCount(int z_ActivityCount) {
        this.z_ActivityCount = z_ActivityCount;
    }

    public ThresholdData clone() {
        ThresholdData data = new ThresholdData();
        data.setThresholdX_min(ThresholdX_min);
        data.setThresholdX_max(ThresholdX_max);
        data.setThresholdX_width(ThresholdX_width);

        data.setThresholdY_min(ThresholdY_min);
        data.setThresholdY_max(ThresholdY_max);
        data.setThresholdY_width(ThresholdY_width);

        data.setThresholdZ_min(ThresholdZ_min);
        data.setThresholdZ_max(ThresholdZ_max);
        data.setThresholdZ_width(ThresholdZ_width);

        data.setX_ActivityCount(x_ActivityCount);
        data.setY_ActivityCount(y_ActivityCount);
        data.setZ_ActivityCount(z_ActivityCount);

        data.calcFitness();
        return data;
    }
}
