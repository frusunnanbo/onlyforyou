package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

import java.util.Collection;

@Value
public class State {

    Collection<User> users;
    Collection<Video> items;
    double loss;
    double[][] ratings;
}
