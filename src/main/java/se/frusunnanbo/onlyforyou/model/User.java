package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

import java.util.Arrays;
import java.util.Collection;

@Value
public class User {
    String name;
    Collection<Rating> ratings;

    public static User user(String name, Rating... ratings) {
        return new User(name, Arrays.asList(ratings));
    }
}
