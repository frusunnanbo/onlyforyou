package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

import java.util.Arrays;
import java.util.Collection;

@Value
public class UserRatings {
    String name;
    Collection<Rating> ratings;

    public static UserRatings user(String name, Rating... ratings) {
        return new UserRatings(name, Arrays.asList(ratings));
    }
}
