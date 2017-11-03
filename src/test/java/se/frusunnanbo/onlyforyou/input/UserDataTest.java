package se.frusunnanbo.onlyforyou.input;

import org.assertj.core.api.Condition;
import org.junit.Test;
import se.frusunnanbo.onlyforyou.current.RatingsMatrix;

import java.util.Collection;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static se.frusunnanbo.onlyforyou.current.RatingsMatrix.Element.element;
import static se.frusunnanbo.onlyforyou.input.UserData.ratings;
import static se.frusunnanbo.onlyforyou.input.UserData.ratingsMatrix;
import static se.frusunnanbo.onlyforyou.model.Rating.rating;
import static se.frusunnanbo.onlyforyou.model.User.user;

public class UserDataTest {

    @Test
    public void empty_userdata_gives_empty_old_ratings() {
        assertThat(ratings(emptyList()))
                .isEmpty();
    }

    @Test
    public void empty_userdata_gives_empty_ratings() {
        final RatingsMatrix matrix = ratingsMatrix(emptyList());
        assertThat(matrix)
                .has(dimensions(0, 0));
        assertThat(matrix)
                .is(emptyRatingsMatrix());
    }

    @Test
    public void user_with_no_ratings_gives_empty_old_ratings() {
        assertThat(ratings(singletonList(user("Glenn"))))
                .isEmpty();
    }

    @Test
    public void user_with_no_ratings_gives_empty_ratings() {
        final RatingsMatrix matrix = ratingsMatrix(singletonList(user("Glenn")));
        assertThat(matrix).has(dimensions(1, 0));
        assertThat(matrix).is(emptyRatingsMatrix());
    }

    @Test
    public void user_with_one_rating_gives_one_by_one_old_matrix() {
        final Collection<Collection<Optional<Double>>> ratings = ratings(singletonList(user("Ulla", rating("Stranger things", 8))));
        assertThat(ratings).hasSize(1);
        assertThat(ratings).containsExactlyInAnyOrder(singletonList(Optional.of(8.0)));
    }

    @Test
    public void user_with_one_rating_gives_one_by_one_matrix() {
        final RatingsMatrix ratings = ratingsMatrix(singletonList(user("Ulla", rating("Stranger things", 8))));
        assertThat(ratings).has(dimensions(1, 1));
        assertThat(ratings.knownElements()).containsExactlyInAnyOrder(element(0, 0, 8));
    }

    @Test
    public void user_with_two_ratings_gives_one_by_two_old_matrix() {
        final Collection<Collection<Optional<Double>>> ratings =
                ratings(singletonList(user("Blenda",
                        rating("Höstsonaten", 9),
                        rating("Sista tangon i Paris", 4))));
        assertThat(ratings).hasSize(1);
        assertThat(ratings).containsExactlyInAnyOrder(asList(Optional.of(9.0), Optional.of(4.0)));
    }

    @Test
    public void user_with_two_ratings_gives_one_by_two_matrix() {
        final RatingsMatrix ratings = ratingsMatrix(singletonList(user("Blenda",
                rating("Höstsonaten", 9),
                rating("Sista tangon i Paris", 4))));
        assertThat(ratings).has(dimensions(1, 2));
        assertThat(ratings.knownElements()).containsExactlyInAnyOrder(
                element(0, 0, 9.0),
                element(0, 1, 4.0));
    }

    @Test
    public void two_users_with_one_rating_gives_two_by_one_old_matrix() {
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
    public void two_users_with_one_rating_gives_two_by_one_matrix() {
        final RatingsMatrix ratings =
                ratingsMatrix(asList(
                        user("Maj-Britt",
                                rating("Smultronstället", 6)),
                        user("Lennart",
                                rating("Smultronstället", 5))));
        assertThat(ratings).has(dimensions(2, 1));
        assertThat(ratings.knownElements()).containsExactlyInAnyOrder(
                element(0, 0, 6.0),
                element(1, 0, 5.0));
    }

    @Test
    public void two_users_with_different_ratings_gives_sparse_old_matrix() {
        final Collection<Collection<Optional<Double>>> ratings =
                ratings(asList(
                        user("Ann-Christin",
                                rating("Gilbert Grape", 7)),
                        user("Bengt",
                                rating("Ett päron till farsa", 3))));
        assertThat(ratings).hasSize(2);
        assertThat(ratings).containsExactlyInAnyOrder(asList(Optional.of(7.0), Optional.empty()), asList(Optional.empty(), Optional.of(3.0)));
    }

    @Test
    public void two_users_with_different_ratings_gives_sparse_matrix() {
        final RatingsMatrix ratings =
                ratingsMatrix(asList(
                        user("Ann-Christin",
                                rating("Gilbert Grape", 7)),
                        user("Bengt",
                                rating("Ett päron till farsa", 3))));
        assertThat(ratings).has(dimensions(2, 2));
        assertThat(ratings.known()).isEqualTo(new double[][]{
                {1, 0},
                {0, 1}
        });
        assertThat(ratings.knownElements()).containsExactlyInAnyOrder(
                element(0, 0, 7.0),
                element(1, 1, 3.0));
    }

    private Condition<RatingsMatrix> dimensions(int i, int j) {
        return new Condition<>(matrix -> matrix.numberOfRows() == i && matrix.numberOfColumns() == j,
                "has dimensions [%d,%d]", i, j);
    }

    private Condition<RatingsMatrix> emptyRatingsMatrix() {
        return new Condition<>(ratingsMatrix -> ratingsMatrix.isEmpty(), "empty ratings");
    }


}
