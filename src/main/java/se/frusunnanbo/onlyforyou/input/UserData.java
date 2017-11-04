package se.frusunnanbo.onlyforyou.input;

import se.frusunnanbo.onlyforyou.current.RatingsMatrix;
import se.frusunnanbo.onlyforyou.model.Rating;
import se.frusunnanbo.onlyforyou.model.User;
import se.frusunnanbo.onlyforyou.model.Video;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static se.frusunnanbo.onlyforyou.current.RatingsMatrix.Element.element;
import static se.frusunnanbo.onlyforyou.model.Rating.rating;
import static se.frusunnanbo.onlyforyou.model.User.user;

public class UserData {

    public static Collection<User> users() {
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

    public static RatingsMatrix ratingsMatrix(Collection<User> users) {
        List<Video> videos = items(users);

        final List<User> userList = new ArrayList<>(users);
        final List<RatingsMatrix.Element> elements = IntStream.range(0, users.size())
                .mapToObj(i -> userList.get(i).getRatings().stream()
                        .map(rating -> element(i, getColumnNumber(rating, videos), rating.getScore().getScore())))
                .flatMap(s -> s)
                .collect(toList());

        return RatingsMatrix.of(users.size(), videos.size(), elements);
    }

    public static List<Video> items(Collection<User> users) {
        return users.stream()
                .flatMap(user -> user.getRatings().stream().map(Rating::getVideo))
                .distinct()
                .sorted(Comparator.comparing(Video::getName))
                .collect(toList());
    }

    private static int getColumnNumber(Rating rating, List<Video> videos) {
        return videos.indexOf(rating.getVideo());
    }


    public static Collection<Collection<Optional<Double>>> ratings(Collection<User> users) {
        List<Video> videos = items(users);

        return users.stream()
                .filter(user -> !user.getRatings().isEmpty())
                .map(user -> mapRatingsToVideos(user.getRatings(), videos))
                .collect(toList());
    }

    private static Collection<Optional<Double>> mapRatingsToVideos(Collection<Rating> ratings, List<Video> videos) {
        return videos.stream()
                .map(video -> userRatingForVideo(ratings, video))
                .collect(toList());
    }

    private static Optional<Double> userRatingForVideo(Collection<Rating> ratings, Video video) {
        return ratings.stream()
                .filter(rating -> video.equals(rating.getVideo()))
                .findAny()
                .map(rating -> (double) rating.getScore().getScore());
    }
}
