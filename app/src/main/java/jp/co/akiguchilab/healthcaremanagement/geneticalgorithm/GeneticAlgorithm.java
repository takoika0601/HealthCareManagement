package jp.co.akiguchilab.healthcaremanagement.geneticalgorithm;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

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
    private float x_value = 0;
    private float y_value = 0;
    private float z_value = 0;
    private float x_width = 0;
    private float y_width = 0;
    private float z_width = 0;
    private float ThresholdX_min = 0;
    private float ThresholdX_max = 0;
    private float ThresholdY_min = 0;
    private float ThresholdY_max = 0;
    private float ThresholdZ_min = 0;
    private float ThresholdZ_max = 0;
    private int Use_axis = 0;
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
    private float[][] Pheno_Type = new float[POPULATION][PHENOTYPE];
    private float[][] New_Pheno_Type = new float[POPULATION][PHENOTYPE];

    public void doGA(ArrayList<AccelerometerData> data) {
        CreateGane();

        setData(data);

        if (x_value < y_value) {
            if (y_value < z_value) {
                Zflag = 3;
                if (y_value < x_value && (x_value > z_value * 0.5f)) {
                    Sflag = 1;
                } else if (x_value < y_value && (y_value > z_value * 0.5f)) {
                    Sflag = 2;
                }
            } else {
                Zflag = 2;
                if (x_value < z_value && (z_value > y_value * 0.5f)) {
                    Sflag = 3;
                } else if (z_value < x_value && (y_value > y_value * 0.5f)) {
                    Sflag = 1;
                }
            }
        } else {
            if (x_value < z_value) {
                Zflag = 3;
                if (x_value < y_value && (y_value > z_value * 0.5f)) {
                    Sflag = 2;
                } else if (y_value < x_value && (x_value > z_value * 0.5f)) {
                    Sflag = 1;
                }
            } else {
                Zflag = 1;
                if (z_value < y_value && (y_value > x_value * 0.5f)) {
                    Sflag = 2;
                } else if (y_value < z_value && (z_value > x_value * 0.5f)) {
                    Sflag = 3;
                }
            }
        }
        x_value = x_value * 0.7f;
        y_value = y_value * 0.7f;
        z_value = z_value * 0.7f;

        while (ga_count <= 10000) {
            for (int i = 0; i < POPULATION; i++) {
                ranking[i] = 0;

                for (int j = 0; j < PHENOTYPE - 1; j++) {
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

                if (ThresholdX_min < 0 && ThresholdX_max < 0) {
                    if (ThresholdX_min < ThresholdX_max) {
                        x_width = Math.abs(ThresholdX_max) - Math.abs(ThresholdX_min);
                    } else {
                        x_width = Math.abs(ThresholdX_min) - Math.abs(ThresholdX_max);
                    }
                } else if (ThresholdX_min > 0 && ThresholdX_max > 0) {
                    if (ThresholdX_min < ThresholdX_max) {
                        x_width = ThresholdX_max - ThresholdX_min;
                    } else {
                        x_width = ThresholdX_min - ThresholdX_max;
                    }
                } else if (ThresholdX_min < 0 && ThresholdX_max > 0) {
                    x_width = ThresholdX_max - ThresholdX_min;
                } else if (ThresholdX_min > 0 && ThresholdX_max < 0) {
                    x_width = ThresholdX_min - ThresholdX_max;
                }

                if (ThresholdY_min < 0 && ThresholdY_max < 0) {
                    if (ThresholdY_min < ThresholdY_max) {
                        y_width = Math.abs(ThresholdY_max) - Math.abs(ThresholdY_min);
                    } else {
                        y_width = Math.abs(ThresholdY_min) - Math.abs(ThresholdY_max);
                    }
                } else if (ThresholdY_min > 0 && ThresholdY_max > 0) {
                    if (ThresholdY_min < ThresholdY_max) {
                        y_width = ThresholdY_max - ThresholdY_min;
                    } else {
                        y_width = ThresholdY_min - ThresholdY_max;
                    }
                } else if (ThresholdY_min < 0 && ThresholdY_max > 0) {
                    y_width = ThresholdY_max - ThresholdY_min;
                } else if (ThresholdY_min > 0 && ThresholdY_max < 0) {
                    y_width = ThresholdY_min - ThresholdY_max;
                }

                if (ThresholdZ_min < 0 && ThresholdZ_max < 0) {
                    if (ThresholdZ_min < ThresholdZ_max) {
                        z_width = Math.abs(ThresholdZ_max) - Math.abs(ThresholdZ_min);
                    } else {
                        z_width = Math.abs(ThresholdZ_min) - Math.abs(ThresholdZ_max);
                    }
                } else if (ThresholdZ_min > 0 && ThresholdZ_max > 0) {
                    if (ThresholdZ_min < ThresholdZ_max) {
                        z_width = ThresholdZ_max - ThresholdZ_min;
                    } else {
                        z_width = ThresholdZ_min - ThresholdZ_max;
                    }
                } else if (ThresholdZ_min < 0 && ThresholdZ_max > 0) {
                    z_width = ThresholdZ_max - ThresholdZ_min;
                } else if (ThresholdZ_min > 0 && ThresholdZ_max < 0) {
                    z_width = ThresholdZ_min - ThresholdZ_max;
                }

                switch (SelectZ) {
                    case 0:
                        par = x_width / x_value;
                        break;
                    case 1:
                        par = y_width / y_value;
                        break;
                    case 2:
                        par = z_width / z_value;
                        break;
                    case 3:
                        par = ((x_width / x_value) + (y_width / y_value)) / 2;
                        break;
                    case 4:
                        par = ((y_width / y_value) + (z_width / z_value)) / 2;
                        break;
                    case 5:
                        par = ((x_width / x_value) + (z_width / z_value)) / 2;
                        break;
                    case 6:
                        par = ((x_width / x_value) + (y_width / y_value) + (z_width / z_value)) / 3;
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
        for (int i = 0; i < POPULATION; i++) {
            for (int j = 0; j < PHENOTYPE; j++) {
                if (j == 0) {
                    Pheno_Type[i][j] = (float) Math.random() % PHENOTYPE;
                } else {
                    Pheno_Type[i][j] = (((float) Math.random() % 300f) / 100f) - 1.5f;
                }
            }

            for (int j = 1; j < 6; j++) {
                if (Pheno_Type[i][j] < Pheno_Type[i][j + 1]) {
                    tmp = Pheno_Type[i][j];
                    Pheno_Type[i][j] = Pheno_Type[i][j + 1];
                    Pheno_Type[i][j + 1] = Pheno_Type[i][j];
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
            x_value = x_max_value - x_min_value;
            y_value = y_max_value - y_min_value;
            z_value = z_max_value - z_min_value;
        }
    }

    void Register(int Use_axis, double[][] value) {
        File directory = Environment.getExternalStorageDirectory();
        String folderpath = directory.getAbsolutePath() + "/HealthCare";
        String filepath = null;
        int flag_ga = 2;

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
