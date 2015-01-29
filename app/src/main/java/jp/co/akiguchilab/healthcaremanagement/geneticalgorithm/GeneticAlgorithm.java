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
import java.util.Arrays;
import java.util.Random;

/**
 * Created by i09324 on 2014/08/31.
 */
public class GeneticAlgorithm {

    //POPULATION : 個体数、 PHENOTYPE : 軸数、 CROSSOVER : 交叉率、　MUTATION : 突然変異率
    private static int POPULATION = 100;
    private static int PHENOTYPE = 7;
    private static double CROSSOVER = 70;
    private static double MUTATION = 5;

    private int ga_count = 0;
    private int p_target = 5;
    private int p_count = 0;

    private float[] ranking = new float[POPULATION];
    private float[] Newranking = new float[POPULATION];
    private double Chooser;
    private int Selectrank1, Selectrank2;
    private int crossoverpoint;

    private boolean x_flag = false;
    private boolean y_flag = false;
    private boolean z_flag = false;
    private float x_max_value = 0;
    private float x_min_value = 0;
    private float y_max_value = 0;
    private float y_min_value = 0;
    private float z_max_value = 0;
    private float z_min_value = 0;
    private float max_value = 0;
    private float min_value = 0;
    private float x_value_width = 0;
    private float y_value_width = 0;
    private float z_value_width = 0;
    private float ThresholdX_min = 0;
    private float ThresholdX_max = 0;
    private float ThresholdY_min = 0;
    private float ThresholdY_max = 0;
    private float ThresholdZ_min = 0;
    private float ThresholdZ_max = 0;
    private float ThresholdX_width = 0;
    private float ThresholdY_width = 0;
    private float ThresholdZ_width = 0;

    private float[][] Pheno_Type;
    private float[][] New_Pheno_Type;
    private double value[][] = new double[3][2];

    private float x_second = 0;
    private float y_second = 0;
    private float z_second = 0;

    private int SelectZ = 0;
    private int Zflag = 0;
    private int Sflag = 0;

    private float elite_per = 0;
    private int elite_num = 0;
    private int elite_value = 0;

    private float tmp;
    private float width;
    private float par;
    private int Use_axis;

    public void doGA(ArrayList<AccelerometerData> data) {
        setData(data);

        CreateGane();
        if (x_value_width < y_value_width) {
            if (y_value_width < z_value_width) {
                Zflag = 3;
                if (x_value_width < y_value_width && (y_value_width > z_value_width * 0.5f)) {
                    Sflag = 2;
                }
            } else {
                Zflag = 2;
                if (x_value_width < z_value_width && (z_value_width > y_value_width * 0.5f)) {
                    Sflag = 3;
                } else if (z_value_width < x_value_width && (x_value_width > y_value_width * 0.5f)) {
                    Sflag = 1;
                }
            }
        } else {
            if (x_value_width < z_value_width) {
                Zflag = 3;
                if (y_value_width < x_value_width && (x_value_width > z_value_width * 0.5f)) {
                    Sflag = 1;
                }
            } else {
                Zflag = 1;
                if (z_value_width < y_value_width && (y_value_width > x_value_width * 0.5f)) {
                    Sflag = 2;
                } else if (y_value_width < z_value_width && (z_value_width > x_value_width * 0.5f)) {
                    Sflag = 3;
                }
            }
        }
        x_value_width = x_value_width * 0.7f;
        y_value_width = y_value_width * 0.7f;
        z_value_width = z_value_width * 0.7f;

        while (ga_count <= 10000) {
            for (int i = 0; i < POPULATION; i++) {
                ranking[i] = 0;

                for (int j = 1; j < PHENOTYPE - 1; j+=2) {
                    if (Pheno_Type[i][j] < Pheno_Type[i][j + 1]) {
                        tmp = Pheno_Type[i][j];
                        Pheno_Type[i][j] = Pheno_Type[i][j + 1];
                        Pheno_Type[i][j + 1] = tmp;
                    }
                }
            }
            elite_per = 0;
            elite_value = 0;
            elite_num = 1000;
            width = 0;

            // 評価
            for (int i = 0; i < POPULATION; i++) {
                p_count = 0;
                SelectZ = (int) Pheno_Type[i][0];
                x_flag = false;
                y_flag = false;
                z_flag = false;
                ThresholdX_max = Pheno_Type[i][1];
                ThresholdX_min = Pheno_Type[i][2];
                ThresholdY_max = Pheno_Type[i][3];
                ThresholdY_min = Pheno_Type[i][4];
                ThresholdZ_max = Pheno_Type[i][5];
                ThresholdZ_min = Pheno_Type[i][6];

                for (int j = 1; j < data.size(); j++) {
                    switch (SelectZ) {
                        case 0:
                            if (data.get(j).getAccelerometer_x() <= ThresholdX_min && !x_flag) {
                                x_second = j;
                                x_flag = true;
                            } else if (data.get(j).getAccelerometer_x() >= ThresholdX_max && x_flag) {
                                if (j - x_second >= 5 && j != x_second) {
                                    p_count++;
                                    x_flag = false;
                                }
                            }
                            break;
                        case 1:
                            if (data.get(j).getAccelerometer_y() <= ThresholdY_min && !y_flag) {
                                y_second = j;
                                y_flag = true;
                            } else if (data.get(j).getAccelerometer_y() >= ThresholdY_max && y_flag) {
                                if (j - y_second >= 5 && j != y_second) {
                                    p_count++;
                                    y_flag = false;
                                }
                            }
                            break;
                        case 2:
                            if (data.get(j).getAccelerometer_z() <= ThresholdZ_min && !z_flag) {
                                z_second = j;
                                z_flag = true;
                            } else if (data.get(j).getAccelerometer_z() >= ThresholdZ_max && z_flag) {
                                if (j - z_second >= 5 && j != z_second) {
                                    p_count++;
                                    z_flag = false;
                                }
                            }
                            break;
                        case 3:
                            if (data.get(j).getAccelerometer_x() <= ThresholdX_min && !x_flag) {
                                x_second = j;
                                x_flag = true;
                            } else if (data.get(j).getAccelerometer_y() <= ThresholdY_min && !y_flag) {
                                y_second = j;
                                y_flag = true;
                            } else if (data.get(j).getAccelerometer_x() >= ThresholdX_max && data.get(j).getAccelerometer_y() >= ThresholdY_max && x_flag && y_flag) {
                                if (j - x_second >= 5 && j - y_second >= 5 && j != x_second && j != y_second) {
                                    p_count++;
                                    x_flag = false;
                                    y_flag = false;
                                }
                            }
                            break;
                        case 4:
                            if (data.get(j).getAccelerometer_y() <= ThresholdY_min && !y_flag) {
                                y_second = j;
                                y_flag = true;
                            } else if (data.get(j).getAccelerometer_z() <= ThresholdZ_min && !z_flag) {
                                z_second = j;
                                z_flag = true;
                            } else if (data.get(j).getAccelerometer_y() >= ThresholdY_max && data.get(j).getAccelerometer_z() >= ThresholdZ_max && y_flag && z_flag) {
                                if (j - y_second >= 5 && j - z_second >= 5 && j != y_second && j != z_second) {
                                    p_count++;
                                    y_flag = false;
                                    z_flag = false;
                                }
                            }
                            break;
                        case 5:
                            if (data.get(j).getAccelerometer_x() <= ThresholdX_min && !x_flag) {
                                x_second = j;
                                x_flag = true;
                            } else if (data.get(j).getAccelerometer_z() <= ThresholdZ_min && !z_flag) {
                                z_second = j;
                                z_flag = true;
                            } else if (data.get(j).getAccelerometer_x() >= ThresholdX_max && data.get(j).getAccelerometer_z() >= ThresholdZ_max && x_flag && z_flag) {
                                if (j - x_second >= 5 && j - z_second >= 5 && j != x_second && j != z_second) {
                                    p_count++;
                                    x_flag = false;
                                    z_flag = false;
                                }
                            }
                            break;
                        case 6:
                            if (data.get(j).getAccelerometer_x() <= ThresholdX_min && !x_flag) {
                                x_second = j;
                                x_flag = true;
                            } else if (data.get(j).getAccelerometer_y() <= ThresholdY_min && !y_flag) {
                                y_second = j;
                                y_flag = true;
                            } else if (data.get(j).getAccelerometer_z() <= ThresholdZ_min && !z_flag) {
                                z_second = j;
                                z_flag = true;
                            } else if (data.get(j).getAccelerometer_x() >= ThresholdX_max && data.get(j).getAccelerometer_y() >= ThresholdY_max && data.get(j).getAccelerometer_z() >= ThresholdZ_max && x_flag && y_flag && z_flag) {
                                if (j - x_second >= 5 && j - y_second >= 5 && j - z_second >= 5 && j != x_second && j != y_second && j != z_second) {
                                    p_count++;
                                    x_flag = true;
                                    y_flag = true;
                                    z_flag = true;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }

                ThresholdX_width = Math.abs(ThresholdX_max - ThresholdX_min);
                ThresholdY_width = Math.abs(ThresholdY_max - ThresholdY_min);
                ThresholdZ_width = Math.abs(ThresholdZ_max - ThresholdZ_min);

                switch (SelectZ) {
                    case 0:
                        par = ThresholdX_width / x_value_width;
                        break;
                    case 1:
                        par = ThresholdY_width / y_value_width;
                        break;
                    case 2:
                        par = ThresholdZ_width / z_value_width;
                        break;
                    case 3:
                        par = ((ThresholdX_width / x_value_width) + (ThresholdY_width / y_value_width)) / 2;
                        break;
                    case 4:
                        par = ((ThresholdY_width / y_value_width) + (ThresholdZ_width / z_value_width)) / 2;
                        break;
                    case 5:
                        par = ((ThresholdX_width / x_value_width) + (ThresholdZ_width / z_value_width)) / 2;
                        break;
                    case 6:
                        par = ((ThresholdX_width / x_value_width) + (ThresholdY_width / y_value_width) + (ThresholdZ_width / z_value_width)) / 3;
                        break;
                    default:
                        break;
                }

                if (p_count >= 6) {
                    p_count = 0;
                }
                ranking[i] = p_count;

                if (elite_value <= p_count && p_count != 0) {
                    switch (Zflag) {
                        case 1:
                            switch (Sflag) {
                                case 0:
                                    switch (SelectZ) {
                                        case 0:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case 2:
                                    switch (SelectZ) {
                                        case 0:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 3:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 6:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case 3:
                                    switch (SelectZ) {
                                        case 0:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 5:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 6:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 2:
                            switch (Sflag) {
                                case 0:
                                    switch (SelectZ) {
                                        case 1:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case 1:
                                    switch (SelectZ) {
                                        case 1:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 3:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 6:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case 3:
                                    switch (SelectZ) {
                                        case 1:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 4:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 6:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 3:
                            switch (Sflag) {
                                case 0:
                                    switch (SelectZ) {
                                        case 2:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case 1:
                                    switch (SelectZ) {
                                        case 2:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 5:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 6:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case 2:
                                    switch (SelectZ) {
                                        case 2:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 4:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        case 6:
                                            if (elite_per < par) {
                                                elite_value = p_count;
                                                elite_num = i;
                                                elite_per = par;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

            //遺伝子操作
            //ランキング作成
            Arrays.sort(ranking);
            for (int i = 0; i < POPULATION; i++) {
                if (i == 0) {
                    ranking[i] = POPULATION;
                } else {
                    ranking[i] = ranking[i - 1] + POPULATION - i;
                }
            }

            //選択
            for (int i = 0; i < POPULATION; i += 2) {
                for (int j = 0; j < 2; j++) {
                    Chooser = Math.random() % (ranking[POPULATION - 1] + 1);
                    for (int k = 0; k < POPULATION; k++) {
                        if (Chooser < ranking[k]) {
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

                Newranking[i] = ranking[Selectrank1];
                Newranking[i + 1] = ranking[Selectrank2];
            }

            //選択した遺伝子を今後は使用する
            for (int i = 0; i < POPULATION; i++) {
                ranking[i] = Newranking[i];
            }

            //交差
            for (int i = 0; i < POPULATION; i += 2) {
                //交差する
                if (CROSSOVER >= Math.random() % 101) {
                    crossoverpoint = (int) (Math.random() % (PHENOTYPE - 1));

                    for (int j = 0; j < PHENOTYPE; j++) {
                        if (j < crossoverpoint) {
                            New_Pheno_Type[i][j] = Pheno_Type[Selectrank1][j];
                            New_Pheno_Type[i + 1][j] = Pheno_Type[Selectrank2][j];
                        } else {
                            New_Pheno_Type[i][j] = Pheno_Type[Selectrank2][j];
                            New_Pheno_Type[i + 1][j] = Pheno_Type[Selectrank1][j];
                        }
                    }
                } else {
                    //交差しない
                    for (int j = 0; j < PHENOTYPE; j++) {
                        New_Pheno_Type[i][j] = Pheno_Type[Selectrank1][j];
                        New_Pheno_Type[i + 1][j] = Pheno_Type[Selectrank2][j];
                    }
                }
            }
            //突然変異
            for (int i = 0; i < POPULATION; i++) {
                for (int j = 0; j < PHENOTYPE; j++) {
                    if (MUTATION >= Math.random() % 101) {
                        if (j == 0) {
                            New_Pheno_Type[i][j] = (float) Math.random() % 7;
                        } else {
                            New_Pheno_Type[i][j] = ((float) (Math.random() % 300) / 100) - 1.5f;
                        }
                    }
                }
            }

            //エリート個体を保存する処理
            for (int i = 0; i < POPULATION; i++) {
                if (elite_num != 1000) {
                    for (int j = 0; j < PHENOTYPE; j++) {
                        New_Pheno_Type[0][j] = Pheno_Type[elite_num][j];
                    }
                } else {
                    for (int j = 0; j < PHENOTYPE; j++) {
                        New_Pheno_Type[0][j] = Pheno_Type[(int) (Math.random() % (POPULATION - 1))][j];
                    }
                }
            }

            //次世代の遺伝子をコピー
            Pheno_Type = New_Pheno_Type.clone();
            ga_count++;
        }

        //最終的な個体の保存
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                value[i][j] = 0;
            }
        }

        Use_axis = (int) Pheno_Type[0][0];

        value[0][0] = Pheno_Type[0][1];
        value[0][1] = Pheno_Type[0][2];
        value[1][0] = Pheno_Type[0][3];
        value[1][1] = Pheno_Type[0][4];
        value[2][0] = Pheno_Type[0][5];
        value[2][1] = Pheno_Type[0][6];

        //Toast.makeText(context, "Calcuration finished ! Push the Practice button", Toast.LENGTH_LONG).show();
        Register(Use_axis, value);
    }

    private void CreateGane() {
        Random rnd = new Random();
        for (int i = 0; i < POPULATION; i++) {
            for (int j = 0; j < PHENOTYPE; j++) {
                if (j == 0) {
                    Pheno_Type[i][j] = (float) rnd.nextInt(PHENOTYPE);
                } else {
                    Pheno_Type[i][j] = (float) rnd.nextInt((int) (max_value - min_value)) + rnd.nextFloat() + min_value;
                }
            }

            for (int j = 1; j < 6; j+=2) {
                if (Pheno_Type[i][j] < Pheno_Type[i][j + 1]) {
                    tmp = Pheno_Type[i][j];
                    Pheno_Type[i][j] = Pheno_Type[i][j + 1];
                    Pheno_Type[i][j + 1] = tmp;
                }
            }
        }
    }

    private void setData(ArrayList<AccelerometerData> data) {
        for (int i = 0; i < data.size(); i++) {
            if (i == 0) {
                x_max_value = data.get(i).getAccelerometer_x();
                x_min_value = data.get(i).getAccelerometer_x();
                y_max_value = data.get(i).getAccelerometer_y();
                y_min_value = data.get(i).getAccelerometer_y();
                z_max_value = data.get(i).getAccelerometer_z();
                z_min_value = data.get(i).getAccelerometer_z();
            } else {
                if (x_max_value < data.get(i).getAccelerometer_x()) {
                    x_max_value = data.get(i).getAccelerometer_x();
                }
                if (x_min_value > data.get(i).getAccelerometer_x()) {
                    x_min_value = data.get(i).getAccelerometer_x();
                }
                if (y_max_value < data.get(i).getAccelerometer_y()) {
                    y_max_value = data.get(i).getAccelerometer_y();
                }
                if (y_min_value > data.get(i).getAccelerometer_y()) {
                    y_min_value = data.get(i).getAccelerometer_y();
                }
                if (z_max_value < data.get(i).getAccelerometer_z()) {
                    z_max_value = data.get(i).getAccelerometer_z();
                }
                if (z_min_value > data.get(i).getAccelerometer_z()) {
                    z_min_value = data.get(i).getAccelerometer_z();
                }
            }
            x_value_width = x_max_value - x_min_value;
            y_value_width = y_max_value - y_min_value;
            z_value_width = z_max_value - z_min_value;


            if (x_max_value > y_max_value && x_max_value > z_max_value) {
                max_value = x_max_value;
            } else if (y_max_value > x_max_value && y_max_value > z_max_value) {
                max_value = y_max_value;
            } else if (z_max_value > x_max_value && z_max_value > y_max_value) {
                max_value = z_max_value;
            }

            if (x_min_value < y_min_value && x_min_value < z_min_value) {
                min_value = x_min_value;
            } else if (y_min_value < x_min_value && y_min_value < z_min_value) {
                min_value = y_min_value;
            } else if (z_min_value < x_min_value && z_min_value < y_min_value) {
                min_value = z_min_value;
            }
        }
    }

    void Register(int Use_axis, double[][] value) {
        Log.d("Use", Use_axis + "");
        File directory = Environment.getExternalStorageDirectory();
        String folderpath = directory.getAbsolutePath() + "/HealthCare";
        String filepath = null;
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
            bw.newLine();
            bw.write(Use_axis);
            for (int i = 0; i < 3; i++) {
                bw.newLine();
                bw.write(value[i][0] + "," + value[i][1]);
            }
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
