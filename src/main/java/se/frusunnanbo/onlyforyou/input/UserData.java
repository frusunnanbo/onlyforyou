package se.frusunnanbo.onlyforyou.input;

import se.frusunnanbo.onlyforyou.model.User;

import java.util.Arrays;
import java.util.Collection;

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
}
