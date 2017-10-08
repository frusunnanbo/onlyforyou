package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

@Value
public class Rating {
    Video video;
    Score score;

    public static Rating rating(String videoName, int score) {
        return new Rating(new Video(videoName), new Score(score));
    }
}
