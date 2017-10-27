package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

import java.util.Collection;

@Value
public class State {

    double loss;
    Collection<Collection<Double>> ratings;
}
