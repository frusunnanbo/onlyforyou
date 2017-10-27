package se.frusunnanbo.onlyforyou.input;

import org.junit.Test;

import java.util.Collection;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static se.frusunnanbo.onlyforyou.input.UserData.ratings;
import static se.frusunnanbo.onlyforyou.model.Rating.rating;
import static se.frusunnanbo.onlyforyou.model.User.user;

public class UserDataTest {

    @Test
    public void empty_userdata_gives_empty_ratings() {
        assertThat(ratings(emptyList()))
                .isEmpty();
    }

    @Test
    public void user_with_no_ratings_gives_empty_ratings() {
        assertThat(ratings(singletonList(user("Glenn"))))
                .isEmpty();
    }

    @Test
    public void user_with_one_rating_gives_one_by_one_matrix() {
        final Collection<Collection<Optional<Double>>> ratings = ratings(singletonList(user("Ulla", rating("Stranger things", 8))));
        assertThat(ratings).hasSize(1);
        assertThat(ratings).containsExactlyInAnyOrder(singletonList(Optional.of(8.0)));
    }

    @Test
    public void user_with_two_ratings_gives_one_by_two_matrix() {
        final Collection<Collection<Optional<Double>>> ratings =
                ratings(singletonList(user("Blenda",
                        rating("Höstsonaten", 9),
                        rating("Sista tangon i Paris", 4))));
        assertThat(ratings).hasSize(1);
        assertThat(ratings).containsExactlyInAnyOrder(asList(Optional.of(9.0), Optional.of(4.0)));
    }

    @Test
    public void two_users_with_one_rating_gives_two_by_one_matrix() {
        final Collection<Collection<Optional<Double>>> ratings =
                ratings(asList(
                        user("Maj-Britt",
                                rating("Smultronstället", 6)),
                        user("Lennart",
                                rating("Smultronstället", 5))));
        assertThat(ratings).hasSize(2);
        assertThat(ratings).containsExactlyInAnyOrder(singletonList(Optional.of(6.0)), singletonList(Optional.of(5.0)));
    }

    @Test
    public void two_users_with_different_ratings_gives_sparse_matrix() {
        final Collection<Collection<Optional<Double>>> ratings =
                ratings(asList(
                        user("Ann-Christin",
                                rating("Gilbert Grape", 7)),
                        user("Bengt",
                                rating("Ett päron till farsa", 3))));
        assertThat(ratings).hasSize(2);
        assertThat(ratings).containsExactlyInAnyOrder(asList(Optional.of(7.0), Optional.empty()), asList(Optional.empty(), Optional.of(3.0)));
    }
}