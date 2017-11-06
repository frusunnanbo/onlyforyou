package se.frusunnanbo.onlyforyou.model;

import lombok.Value;

@Value
public class UserRating {
    private final int userIndex;
    private final int itemIndex;
    private final int value;

    public static UserRating userRating(int row, int column, int value) {
        return new UserRating(row, column, value);
    }
}
