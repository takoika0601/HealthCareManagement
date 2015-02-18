package jp.co.akiguchilab.healthcaremanagement.geneticalgorithm;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import jp.co.akiguchilab.healthcaremanagement.util.TheComparator;

/**
 * Created by i09324 on 2014/08/31.
 */
public class GeneticAlgorithm {
    private static final String TAG = GeneticAlgorithm.class.getSimpleName();

    // 世代数
    private static int POPULATION = 100;
    // 交叉率
    private static int CROSS = 75;
    // 突然変異率
    private static int MUTATION = 5;
    // 生成する閾値の数（x_min x_max, y_min y_max, z_min z_max）
    private static int VALUE = 6;
    // 遺伝子に格納する変数の数 (xmin, xmax, ymin, ymax, zmin, zmax, xwidth, ywidth, zwidth, allwidth)
    private static int INN = 10;

    private float x_max_value;
    private float x_min_value;
    private float y_max_value;
    private float y_min_value;
    private float z_max_value;
    private float z_min_value;
    private float x_width;
    private float y_width;
    private float z_width;

    private int[] ranking = new int[POPULATION];

    private Random random = new Random();
    private ArrayList<ThresholdData> datas = new ArrayList<ThresholdData>();
    private ArrayList<ThresholdData> new_datas = new ArrayList<ThresholdData>();

    private File directory = Environment.getExternalStorageDirectory();
    private String folderpath = directory.getAbsolutePath() + "/HealthCare";
    private String filepath = null;

    public void start(ArrayList<AccelerometerData> data) {
        ThresholdData elite = null;

        setData(data);

        generate();

        for (int GAcount = 0; GAcount < 500; GAcount++) {
            measure(data);

            if (GAcount != 0) {
                datas.remove(datas.size() - 1);
                datas.add(elite);
                Collections.sort(datas, new TheComparator());
            }
            elite = datas.get(0).clone();

            WriteElite(elite);

            select();

            cross();

            mutate();

            compare();

        }
        measure(data);

        datas.remove(datas.size() - 1);
        datas.add(elite);

        Collections.sort(datas, new TheComparator());

        register();
    }

    private void setData(ArrayList<AccelerometerData> data) {
        int i = 0;

        // 初期値の入力
        x_max_value = data.get(i).getAccelerometer_x();
        x_min_value = data.get(i).getAccelerometer_x();
        /*
        y_max_value = data.get(i).getAccelerometer_x();
        y_min_value = data.get(i).getAccelerometer_x();
        z_max_value = data.get(i).getAccelerometer_x();
        z_min_value = data.get(i).getAccelerometer_x();
        */

        // 次のデータと比較して、一番大きい(小さい)ものを保存する
        for (i = 1; i < data.size(); i++) {
            if (x_max_value < data.get(i).getAccelerometer_x()) {
                x_max_value = data.get(i).getAccelerometer_x();
            } else if (x_min_value > data.get(i).getAccelerometer_x()) {
                x_min_value = data.get(i).getAccelerometer_x();
            }

            /*
            if (y_max_value < data.get(i).getAccelerometer_y()) {
                y_max_value = data.get(i).getAccelerometer_y();
            } else if (y_min_value > data.get(i).getAccelerometer_y()) {
                y_min_value = data.get(i).getAccelerometer_y();
            }

            if (z_max_value < data.get(i).getAccelerometer_z()) {
                z_max_value = data.get(i).getAccelerometer_z();
            } else if (z_min_value > data.get(i).getAccelerometer_z()) {
                z_min_value = data.get(i).getAccelerometer_z();
            }
            */
        }

        // 各軸の加速度の幅を求める
        x_width = x_max_value - x_min_value;

        /*
        if ((y_max_value > 0 && y_min_value > 0) || (y_max_value < 0 && y_min_value < 0)) {
            y_width = Math.abs(Math.abs(y_max_value) - Math.abs(y_min_value));
        } else if (y_max_value > 0 && y_min_value < 0) {
            y_width = Math.abs(y_max_value - y_min_value);
        }

        if ((z_max_value > 0 && z_min_value > 0) || (z_max_value < 0 && z_min_value < 0)) {
            z_width = Math.abs(Math.abs(z_max_value) - Math.abs(z_min_value));
        } else if (z_max_value > 0 && z_min_value < 0) {
            z_width = Math.abs(z_max_value - z_min_value);
        }
        */
    }

    private void generate() {
        for (int i = 0; i < POPULATION; i++) {
            ThresholdData data = new ThresholdData();

            data.setThresholdX_min(random.nextInt((int) x_width) + random.nextFloat() + x_min_value);
            data.setThresholdX_max(random.nextInt((int) x_width) + random.nextFloat() + x_min_value);

            /*
            data.setThresholdY_min(random.nextInt((int) (y_width)) + random.nextFloat() + y_min_value);
            data.setThresholdY_max(random.nextInt((int) (y_width)) + random.nextFloat() + y_min_value);
            data.setThresholdZ_min(random.nextInt((int) (z_width)) + random.nextFloat() + z_min_value);
            data.setThresholdZ_max(random.nextInt((int) (z_width)) + random.nextFloat() + z_min_value);
            */

            datas.add(data);
        }
        compare();
    }

    private void measure(ArrayList<AccelerometerData> data) {
        int x_Activity_count;
        int y_Activity_count;
        int z_Activity_count;

        boolean x_flag;
        boolean y_flag;
        boolean z_flag;

        float Threshold_x_max;
        float Threshold_x_min;
        float Threshold_y_max;
        float Threshold_y_min;
        float Threshold_z_max;
        float Threshold_z_min;

        float Threshold_x_width = 0;
        float Threshold_y_width = 0;
        float Threshold_z_width = 0;

        for (int i = 0; i < POPULATION; i++) {
            x_Activity_count = 0;
            y_Activity_count = 0;
            z_Activity_count = 0;
            x_flag = false;
            y_flag = false;
            z_flag = false;

            Threshold_x_max = datas.get(i).getThresholdX_max();
            Threshold_x_min = datas.get(i).getThresholdX_min();

            /*
            Threshold_y_max = datas.get(i).getThresholdY_max();
            Threshold_y_min = datas.get(i).getThresholdY_min();
            Threshold_z_max = datas.get(i).getThresholdZ_max();
            Threshold_z_min = datas.get(i).getThresholdZ_min();
            */

            for (int j = 0; j < data.size(); j++) {
                if (data.get(j).getAccelerometer_x() <= Threshold_x_min && !x_flag) {
                    x_flag = true;
                }

                /*
                if (data.get(j).getAccelerometer_y() <= Threshold_y_min && !y_flag) {
                    y_flag = true;
                }
                if (data.get(j).getAccelerometer_z() <= Threshold_z_min && !z_flag) {
                    z_flag = true;
                }
                */

                if (data.get(j).getAccelerometer_x() >= Threshold_x_max && x_flag) {
                    x_Activity_count++;
                    x_flag = false;
                }

                /*
                if (data.get(j).getAccelerometer_y() >= Threshold_y_max && y_flag) {
                    y_Activity_count++;
                    y_flag = false;
                }
                if (data.get(j).getAccelerometer_z() >= Threshold_z_max && z_flag) {
                    z_Activity_count++;
                    z_flag = false;
                }
                */
            }

            // 各軸の閾値の幅を求める
            if ((Threshold_x_max > 0 && Threshold_x_min > 0) || (Threshold_x_max < 0 && Threshold_x_min < 0)) {
                Threshold_x_width = Math.abs(Math.abs(Threshold_x_max) - Math.abs(Threshold_x_min));
            } else if (Threshold_x_max > 0 && Threshold_x_min < 0) {
                Threshold_x_width = Math.abs(Threshold_x_max - Threshold_x_min);
            }

            /*
            if ((Threshold_y_max > 0 && Threshold_y_min > 0) || (Threshold_y_max < 0 && Threshold_y_min < 0)) {
                Threshold_y_width = Math.abs(Math.abs(Threshold_y_max) - Math.abs(Threshold_y_min));
            } else if (Threshold_y_max > 0 && Threshold_y_min < 0) {
                Threshold_y_width = Math.abs(Threshold_y_max - Threshold_y_min);
            }

            if ((Threshold_z_max > 0 && Threshold_z_min > 0) || (Threshold_z_max < 0 && Threshold_z_min < 0)) {
                Threshold_z_width = Math.abs(Math.abs(Threshold_z_max) - Math.abs(Threshold_z_min));
            } else if (Threshold_z_max > 0 && Threshold_z_min < 0) {
                Threshold_z_width = Math.abs(Threshold_z_max - Threshold_z_min);
            }
            */

            datas.get(i).setX_ActivityCount(x_Activity_count);
            datas.get(i).setThresholdX_width(Threshold_x_width);
            /*
            datas.get(i).setThresholdY_width(Threshold_y_width);
            datas.get(i).setThresholdZ_width(Threshold_z_width);
            datas.get(i).calcThreshold_width();

            */
            datas.get(i).calcFitness();
        }
        // 降順ソート
        Collections.sort(datas, new TheComparator());

        //Log.d(TAG, "Thres max width is " + datas.get(0).getThresholdX_width());
        //Log.d(TAG, "Thres min width is " + datas.get(datas.size()-1).getThresholdX_width());
    }

    private void select() {
        int Choose;
        int Selectrank1 = 0, Selectrank2 = 0;

        // ランキング配列生成
        ranking[0] = POPULATION;
        for (int i = 1; i < POPULATION; i++) {
            ranking[i] = ranking[i - 1] + POPULATION - i;
        }

        // ランキング選択
        for (int i = 0; i < POPULATION; i += 2) {
            for (int j = 0; j < 2; j++) {
                Choose = random.nextInt(ranking[POPULATION - 1]);

                for (int k = 0; k < POPULATION; k++) {
                    if (Choose < ranking[k]) {
                        if (j == 0) {
                            Selectrank1 = k;
                            break;
                        } else {
                            Selectrank2 = k;
                            break;
                        }
                    }
                }
            }
            new_datas.add(datas.get(Selectrank1));
            new_datas.add(datas.get(Selectrank2));
        }
        datas = new_datas;
    }

    // 改善の余地有り　現在：ランダムでx,y,zのmin,maxを一つだけ入れ替える
    private void cross() {
        int change;
        float tmp;

        for (int i = 0; i < POPULATION; i += 2) {
            if (random.nextInt(100) <= CROSS) {
                change = random.nextInt(1);

                switch (change) {
                    case 0: // x_maxの交換
                        tmp = datas.get(i).getThresholdX_max();
                        datas.get(i).setThresholdX_max(datas.get(i + 1).getThresholdX_max());
                        datas.get(i + 1).setThresholdX_max(tmp);
                        break;

                    case 1: // x_minの交換
                        tmp = datas.get(i).getThresholdX_min();
                        datas.get(i).setThresholdX_min(datas.get(i + 1).getThresholdX_min());
                        datas.get(i + 1).setThresholdX_min(tmp);
                        break;

                    case 2: // y_maxの交換
                        tmp = datas.get(i).getThresholdY_max();
                        datas.get(i).setThresholdY_max(datas.get(i + 1).getThresholdY_max());
                        datas.get(i + 1).setThresholdY_max(tmp);
                        break;

                    case 3: // y_minの交換
                        tmp = datas.get(i).getThresholdY_min();
                        datas.get(i).setThresholdY_min(datas.get(i + 1).getThresholdY_min());
                        datas.get(i + 1).setThresholdY_min(tmp);
                        break;

                    case 4: // z_maxの交換
                        tmp = datas.get(i).getThresholdZ_max();
                        datas.get(i).setThresholdZ_max(datas.get(i + 1).getThresholdZ_max());
                        datas.get(i + 1).setThresholdZ_max(tmp);
                        break;

                    case 5: // z_minの交換
                        tmp = datas.get(i).getThresholdZ_min();
                        datas.get(i).setThresholdZ_min(datas.get(i + 1).getThresholdZ_min());
                        datas.get(i + 1).setThresholdZ_min(tmp);
                        break;
                }
            }
        }
    }

    private void mutate() {
        for (int i = 0; i < POPULATION; i++) {
            if (random.nextInt(100) <= MUTATION) {
                datas.get(i).setThresholdX_max(random.nextInt((int) (x_max_value - x_min_value)) + random.nextFloat() + x_min_value);
                datas.get(i).setThresholdX_min(random.nextInt((int) (x_max_value - x_min_value)) + random.nextFloat() + x_min_value);
                /*
                datas.get(i).setThresholdY_max(random.nextInt((int) (y_max_value - y_min_value)) + random.nextFloat() + y_min_value);
                datas.get(i).setThresholdY_min(random.nextInt((int) (y_max_value - y_min_value)) + random.nextFloat() + y_min_value);
                datas.get(i).setThresholdZ_max(random.nextInt((int) (z_max_value - z_min_value)) + random.nextFloat() + z_min_value);
                datas.get(i).setThresholdZ_min(random.nextInt((int) (z_max_value - z_min_value)) + random.nextFloat() + z_min_value);
                */
            }
        }
    }

    // 最小値と最大値の比較
    private void compare() {
        float xmax, xmin, ymax, ymin, zmax, zmin;

        for (int i = 0; i < POPULATION; i++) {
            xmax = datas.get(i).getThresholdX_max();
            xmin = datas.get(i).getThresholdX_min();
            if (xmax < xmin) {
                datas.get(i).setThresholdX_min(xmax);
                datas.get(i).setThresholdX_max(xmin);
            }
            /*
            ymax = datas.get(i).getThresholdY_max();
            ymin = datas.get(i).getThresholdY_min();
            if (ymax < ymin) {
                datas.get(i).setThresholdY_min(ymax);
                datas.get(i).setThresholdY_max(ymin);
            }

            zmax = datas.get(i).getThresholdZ_max();
            zmin = datas.get(i).getThresholdZ_min();
            if (zmax < zmin) {
                datas.get(i).setThresholdZ_min(zmax);
                datas.get(i).setThresholdZ_max(zmin);
            }
            */
        }
    }

    private void register() {
        Log.d(TAG, "register actived :" + datas.get(0).getThresholdX_width());
        int flag_ga = 3;

        switch (flag_ga) {
            case 0:
                break;
            case 1:
                filepath = folderpath + File.separator + "dum.csv";
                break;
            case 2:
                filepath = folderpath + File.separator + "add1.csv";
                break;
            case 3:
                filepath = folderpath + File.separator + "add2.csv";
                break;
            case 4:
                filepath = folderpath + File.separator + "add3.csv";
                break;
            default:
                break;
        }
        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(filepath, false), "UTF-8"));

            bw.write("x," + datas.get(0).getThresholdX_max() + "," + datas.get(0).getThresholdX_min() + "," + datas.get(0).getX_ActivityCount());
            bw.newLine();
            /*
            bw.write("y," + datas.get(0).getThresholdY_max() + "," + datas.get(0).getThresholdY_min());
            bw.newLine();
            bw.write("z," + datas.get(0).getThresholdZ_max() + "," + datas.get(0).getThresholdZ_min());
            bw.newLine();
            */
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void WriteElite(ThresholdData elite) {
        String elitepath = folderpath + File.separator + "elite.csv";
        float average = 0.0f;

        for (int i = 0; i < datas.size(); i++) {
            average += datas.get(i).getFitness();
        }
        average = average / (datas.size() - 1);

        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(elitepath, true), "UTF-8"));

            bw.write("elite," + elite.getFitness() + ",average," + average);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}