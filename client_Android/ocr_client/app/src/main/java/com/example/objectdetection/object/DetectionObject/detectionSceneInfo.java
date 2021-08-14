package com.example.objectdetection.object.DetectionObject;

import java.util.ArrayList;

public class detectionSceneInfo {
    private long log_id;
    private int result_num;
    private ArrayList<detectionObject> result;

    @Override
    public String toString() {
        return "detectionSceneInfo{" +
                "log_id=" + log_id +
                ", result_num=" + result_num +
                ", result=" + result +
                '}';
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public int getResult_num() {
        return result_num;
    }

    public void setResult_num(int result_num) {
        this.result_num = result_num;
    }

    public ArrayList<detectionObject> getResult() {
        return result;
    }

    public void setResult(ArrayList<detectionObject> result) {
        this.result = result;
    }

    public detectionSceneInfo(long log_id, int result_num, ArrayList<detectionObject> result) {
        this.log_id = log_id;
        this.result_num = result_num;
        this.result = result;
    }
}
