package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

import java.util.List;

@Value
public class State {

    List<Video> items;
    double loss;
    double[][] ratings;
}
