package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

@Value
public class State {
    double loss;
    double[][] userFeatures;
    double[][] itemFeatures;
    double[][] ratings;
}
