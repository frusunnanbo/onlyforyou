package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

import java.util.Collection;
import java.util.List;

@Value
public class UserRatingInput {
    List<User> users;
    List<Item> items;
    Collection<UserRating> ratings;
}
