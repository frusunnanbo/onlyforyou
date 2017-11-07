package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

@Value
public class Rating {
    Item item;
    Score score;

    public static Rating rating(String videoName, int score) {
        return new Rating(new Item(videoName), new Score(score));
    }
}
