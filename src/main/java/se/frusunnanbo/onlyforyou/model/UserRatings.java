package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

import java.util.Collection;
import java.util.List;

@Value
public class UserRatings {
    List<User> users;
    List<Video> items;
    Collection<UserRating> ratings;
}
