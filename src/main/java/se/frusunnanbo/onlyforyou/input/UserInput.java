package se.frusunnanbo.onlyforyou.input;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import se.frusunnanbo.onlyforyou.current.RatingsMatrix;
import se.frusunnanbo.onlyforyou.model.Item;
import se.frusunnanbo.onlyforyou.model.Rating;
import se.frusunnanbo.onlyforyou.model.User;
import se.frusunnanbo.onlyforyou.model.UserRating;
import se.frusunnanbo.onlyforyou.model.UserRatings;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static se.frusunnanbo.onlyforyou.model.Rating.rating;
import static se.frusunnanbo.onlyforyou.model.UserRating.userRating;
import static se.frusunnanbo.onlyforyou.model.UserRatings.user;

public class UserInput {

    private final Map<User, List<Rating>> userInput;

    private UserInput(List<UserRatings> userInput) {
        this.userInput = userInput.stream()
                .collect(Collectors.groupingBy(UserRatings::getName))
                .entrySet().stream()
                .collect(Collectors.toMap(entry -> new User(entry.getKey()), this::toRatingsCollection));
    }

    private UserInput(Map<User, List<Rating>> userInput) {
        this.userInput = userInput;
    }

    private List<Rating> toRatingsCollection(Map.Entry<String, List<UserRatings>> entry) {
        return entry.getValue().stream().flatMap(userRatings -> userRatings.getRatings().stream()).collect(toList());
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
                user("Nina", rating("Väder", 4), rating("Hela Sverige bakar", 5), rating("Sweeney Todd", 2)),
                user("Olga", rating("Gift vid första ögonkastet", 1), rating("Hela Sverige bakar", 3), rating("Mitt i naturen", 2)),
                user("Pernilla", rating("Greta Gris", 4), rating("Äntligen hemma", 3), rating("Fotbolls-VM", 3)),
                user("Quinoa", rating("Skilda världar", 2), rating("Gift vid första ögonkastet", 2), rating("Väder", 5)),
                user("Rita", rating("Skilda världar", 1), rating("Fotbolls-VM", 4), rating("Väder", 2)),
                user("Sandra", rating("Väder", 4), rating("Fotbolls-VM", 5), rating("Äntligen hemma", 2)),
                user("Tina", rating("Gift vid första ögonkastet", 1), rating("Hela Sverige bakar", 3), rating("Mitt i naturen", 2))
        ));
    }

    public static UserInput validationInput() {
        return new UserInput(Arrays.asList(
                user("Anna", rating("Skilda världar", 5)),
                user("Britta", rating("Greta Gris", 4)),
                user("Carin", rating("Äntligen hemma", 1)),
                user("Dilba", rating("Fotbolls-VM", 5)),
                user("Eva", rating("Väder", 3)),
                user("Frida", rating("Hela Sverige bakar", 2)),
                user("Gun", rating("Sweeney Todd", 1))));
    }

    public static UserInput of(List<UserRatings> input) {
        return new UserInput(input);
    }

    public UserInput join(UserInput other) {
        Set<User> keys = Sets.union(userInput.keySet(), other.userInput.keySet());
        final Map<User, List<Rating>> union = keys.stream()
                .collect(Collectors.toMap(user -> user, user -> unionOfUserRatings(other, user)));
        return new UserInput(union);
    }

    private List<Rating> unionOfUserRatings(UserInput other, User user) {
        return ImmutableList.<Rating>builder()
                .addAll(Optional.ofNullable(this.userInput.get(user)).orElse(emptyList()))
                .addAll(Optional.ofNullable(other.userInput.get(user)).orElse(emptyList())).build();
    }

    public List<User> users() {
        return userInput.keySet().stream().sorted(Comparator.comparing(User::getName)).collect(toList());
    }

    public List<Item> items() {
        return userInput.values().stream()
                .flatMap(ratings -> ratings.stream().map(Rating::getItem))
                .distinct()
                .sorted(Comparator.comparing(Item::getName))
                .collect(toList());
    }

    public List<UserRating> userRatings() {
        final List<Item> items = items();
        final List<User> users = users();

        return IntStream.range(0, users.size())
                .mapToObj(i -> userInput.get(users.get(i)).stream()
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
