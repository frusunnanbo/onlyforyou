package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

import java.util.Collection;

@Value
public class State {

    Collection<UserRatings> users;
    Collection<Item> items;
    double loss;
    double[][] ratings;
}
