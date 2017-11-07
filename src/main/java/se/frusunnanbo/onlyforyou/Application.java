package se.frusunnanbo.onlyforyou;

import com.google.gson.Gson;
import se.frusunnanbo.onlyforyou.current.Calculation;
import se.frusunnanbo.onlyforyou.input.UserData;
import se.frusunnanbo.onlyforyou.model.Item;
import se.frusunnanbo.onlyforyou.model.State;
import se.frusunnanbo.onlyforyou.model.User;
import se.frusunnanbo.onlyforyou.model.UserRatingInput;
import se.frusunnanbo.onlyforyou.model.UserRatings;
import spark.Spark;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class Application {
    public static void main(String[] args) {
        Gson gson = new Gson();
        final List<UserRatings> userRatings = UserData.userRatings();
        final List<User> users = UserData.users();
        final List<Item> items = UserData.items(userRatings);
        Calculation current = Calculation.create(UserData.ratingsMatrix(userRatings));

        Spark.externalStaticFileLocation(System.getProperty("frontend.root"));
        staticFiles.registerMimeType("html", "text/html; charset=utf-8");

        get("/userratings", (req, res) -> new UserRatingInput(users, items, UserData.userRatings(userRatings, items)), gson::toJson);
        get("/currentstate", (req, res) -> new State(users, items, current.loss(), current.estimations()), gson::toJson);
        post("/currentstate/next", (req, res) -> {
            current.next();
            return new State(users, items, current.loss(), current.estimations());
        }, gson::toJson);
    }

}
