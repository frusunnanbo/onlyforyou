package se.frusunnanbo.onlyforyou.input;

import se.frusunnanbo.onlyforyou.current.RatingsMatrix;
import se.frusunnanbo.onlyforyou.model.Item;
import se.frusunnanbo.onlyforyou.model.Rating;
import se.frusunnanbo.onlyforyou.model.User;
import se.frusunnanbo.onlyforyou.model.UserRating;
import se.frusunnanbo.onlyforyou.model.UserRatings;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static se.frusunnanbo.onlyforyou.model.Rating.rating;
import static se.frusunnanbo.onlyforyou.model.UserRating.userRating;
import static se.frusunnanbo.onlyforyou.model.UserRatings.user;

public class UserInput {

    private final List<UserRatings> userInput;

    private UserInput(List<UserRatings> userInput) {
        this.userInput = userInput;
    }

    public static UserInput userInput() {
        return new UserInput(Arrays.asList(
                user("Anna", rating("Hela Sverige bakar", 3), rating("Greta Gris", 5)),
                user("Britta", rating("Gift vid första ögonkastet", 2), rating("Fotbolls-VM", 1)),
                user("Carin", rating("Mitt i naturen", 4), rating("Cityakuten", 5)),
                user("Dilba", rating("Skilda världar", 3)),
                user("Eva", rating("Greta Gris", 5), rating("Sweeney Todd", 5), rating("Cityakuten", 5)),
                user("Frida", rating("Scream", 2), rating("Väder", 1)),
                user("Gun", rating("Mitt i naturen", 3), rating("Fotbolls-VM", 3), rating("Äntligen hemma", 5)),
                user("Hilda", rating("Mitt i naturen", 5), rating("Äntligen hemma", 4), rating("Scream", 4)),
                user("Ida", rating("Gift vid första ögonkastet", 3), rating("Greta Gris", 1), rating("Cityakuten", 2)),
                user("Johanna", rating("Cityakuten", 1), rating("Väder", 2), rating("Sweeney Todd", 1)),
                user("Katrin", rating("Greta Gris", 4), rating("Sweeney Todd", 3), rating("Cityakuten", 3)),
                user("Lisa", rating("Scream", 2), rating("Gift vid första ögonkastet", 2), rating("Väder", 5)),
                user("Maria", rating("Skilda världar", 1), rating("Cityakuten", 4), rating("Scream", 2)),
                user("Nina", rating("Väder", 4), rating("Cityakuten", 5), rating("Sweeney Todd", 2)),
                user("Olga", rating("Gift vid första ögonkastet", 1), rating("Hela Sverige bakar", 3), rating("Mitt i naturen", 2))
        ));
    }

    public static UserInput validationInput() {
        return new UserInput(Arrays.asList(
                user("Anna", rating("Hela Sverige bakar", 3)),
                user("Britta", rating("Gift vid första ögonkastet", 2)),
                user("Carin", rating("Mitt i naturen", 4)),
                user("Dilba", rating("Skilda världar", 3)),
                user("Eva", rating("Greta Gris", 5)),
                user("Frida", rating("Scream", 2)),
                user("Gun", rating("Mitt i naturen", 3))));
    }

    public static UserInput of(List<UserRatings> input) {
        return new UserInput(input);
    }

    public List<User> users() {
        return userInput.stream()
                .map(ratings -> new User(ratings.getName()))
                .collect(Collectors.toList());
    }

    public List<Item> items() {
        return userInput.stream()
                .flatMap(user -> user.getRatings().stream().map(Rating::getItem))
                .distinct()
                .sorted(Comparator.comparing(Item::getName))
                .collect(toList());
    }

    public List<UserRating> userRatings() {
        final List<Item> items = items();
        return IntStream.range(0, userInput.size())
                .mapToObj(i -> userInput.get(i).getRatings().stream()
                        .map(rating -> userRating(i, getColumnNumber(rating, items), rating.getScore().getScore())))
                .flatMap(s -> s)
                .collect(toList());
    }

    public RatingsMatrix toRatingsMatrix() {
        final List<Item> items = items();

        return RatingsMatrix.of(users().size(), items.size(), userRatings());
    }

    private static int getColumnNumber(Rating rating, List<Item> items) {
        return items.indexOf(rating.getItem());
    }
}
