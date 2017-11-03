package se.frusunnanbo.onlyforyou.input;

import se.frusunnanbo.onlyforyou.current.RatingsMatrix;
import se.frusunnanbo.onlyforyou.model.Rating;
import se.frusunnanbo.onlyforyou.model.User;
import se.frusunnanbo.onlyforyou.model.Video;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
                user("Per Persson", rating("Greta gris", 5), rating("Den goda viljan", 3)),
                user("Sofie Svensson", rating("Skam", 4), rating("Bonusfamiljen", 2)),
                user("Anders Andersson", rating("Bonusfamiljen", 2), rating("Året med kungafamiljen", 5)),
                user("Maja Karlsson", rating("Året med kungafamiljen", 5), rating("Den goda viljan", 3)),
                user("Pia Fåk Sunnanbo", rating("Bon", 5), rating("Bonusfamiljen", 4)),
                user("Barn Barnsson", rating("Bon", 5), rating("Greta gris", 5))
        );
    }

    public static RatingsMatrix ratingsMatrix(Collection<User> users) {
        List<Video> videos = users.stream()
                .flatMap(user -> user.getRatings().stream().map(Rating::getVideo))
                .distinct()
                .collect(toList());

        final List<User> userList = new ArrayList<>(users);
        final List<RatingsMatrix.Element> elements = IntStream.range(0, users.size())
                .mapToObj(i -> userList.get(i).getRatings().stream()
                        .map(rating -> element(i, getColumnNumber(rating, videos), rating.getScore().getScore())))
                .flatMap(s -> s)
                .collect(toList());

        return RatingsMatrix.of(users.size(), videos.size(), elements);
    }

    private static int getColumnNumber(Rating rating, List<Video> videos) {
        return videos.indexOf(rating.getVideo());
    }


    public static Collection<Collection<Optional<Double>>> ratings(Collection<User> users) {
        List<Video> videos = users.stream()
                .flatMap(user -> user.getRatings().stream().map(Rating::getVideo))
                .distinct()
                .collect(toList());

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
