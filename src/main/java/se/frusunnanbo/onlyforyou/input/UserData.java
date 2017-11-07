package se.frusunnanbo.onlyforyou.input;

import se.frusunnanbo.onlyforyou.current.RatingsMatrix;
import se.frusunnanbo.onlyforyou.model.Item;
import se.frusunnanbo.onlyforyou.model.Rating;
import se.frusunnanbo.onlyforyou.model.UserRating;
import se.frusunnanbo.onlyforyou.model.UserRatings;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static se.frusunnanbo.onlyforyou.model.Rating.rating;
import static se.frusunnanbo.onlyforyou.model.UserRating.userRating;
import static se.frusunnanbo.onlyforyou.model.UserRatings.user;

public class UserData {

    public static List<UserRatings> users() {
        return Arrays.asList(
                user("Anna", rating("Hela Sverige bakar", 3), rating("Greta Gris", 5)),
                user("Britta", rating("Gift vid första ögonkastet", 2), rating("Fotbolls-VM", 1)),
                user("Carin", rating("Mitt i naturen", 4), rating("Cityakuten", 5)),
                user("Dilba", rating("Skilda världar", 3)),
                user("Eva", rating("Greta Gris", 5), rating("Sweeney Todd", 5), rating("Cityakuten", 5)),
                user("Frida", rating("Scream", 2), rating("Väder", 1)),
                user("Gun", rating("Mitt i naturen", 3), rating("Fotbolls-VM", 3), rating("Äntligen hemma", 5))
        );
    }

    public static RatingsMatrix ratingsMatrix(List<UserRatings> users) {
        List<Item> items = items(users);

        final List<UserRating> userRatings = userRatings(users, items);

        return RatingsMatrix.of(users.size(), items.size(), userRatings);
    }

    public static List<UserRating> userRatings(List<UserRatings> userRatings, List<Item> items) {
        return IntStream.range(0, userRatings.size())
                .mapToObj(i -> userRatings.get(i).getRatings().stream()
                        .map(rating -> userRating(i, getColumnNumber(rating, items), rating.getScore().getScore())))
                .flatMap(s -> s)
                .collect(toList());
    }

    public static List<Item> items(Collection<UserRatings> userRatings) {
        return userRatings.stream()
                .flatMap(user -> user.getRatings().stream().map(Rating::getItem))
                .distinct()
                .sorted(Comparator.comparing(Item::getName))
                .collect(toList());
    }

    private static int getColumnNumber(Rating rating, List<Item> items) {
        return items.indexOf(rating.getItem());
    }
}
