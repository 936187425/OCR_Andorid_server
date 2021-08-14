package com.example.objectdetection.object.DetectionObject;

public class detectionObject {
    private float score;
    private String root;
    private String keyword;

    @Override
    public String toString() {
        return "detectionObject{" +
                "score=" + score +
                ", root='" + root + '\'' +
                ", keyword='" + keyword + '\'' +
                '}';
    }

    public detectionObject(float score, String root, String keyword) {
        this.score = score;
        this.root = root;
        this.keyword = keyword;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
