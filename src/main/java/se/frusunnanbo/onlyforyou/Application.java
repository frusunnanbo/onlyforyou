package se.frusunnanbo.onlyforyou;

import com.google.gson.Gson;
import se.frusunnanbo.onlyforyou.current.Calculation;
import se.frusunnanbo.onlyforyou.input.UserInput;
import se.frusunnanbo.onlyforyou.model.Item;
import se.frusunnanbo.onlyforyou.model.State;
import se.frusunnanbo.onlyforyou.model.User;
import se.frusunnanbo.onlyforyou.model.UserRatingInput;
import spark.Spark;

import java.util.List;

import static se.frusunnanbo.onlyforyou.input.UserInput.userInput;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class Application {
    public static void main(String[] args) {
        Gson gson = new Gson();
        final UserInput userInput = userInput();
        final List<User> users = userInput.users();
        final List<Item> items = userInput.items();
        Calculation current = Calculation.create(userInput.toRatingsMatrix());

        Spark.externalStaticFileLocation(System.getProperty("frontend.root"));
        staticFiles.registerMimeType("html", "text/html; charset=utf-8");

        get("/userratings", (req, res) -> new UserRatingInput(users, items, userInput.userRatings()), gson::toJson);
        get("/currentstate", (req, res) -> new State(users, items, current.loss(), current.estimations()), gson::toJson);
        post("/currentstate/next", (req, res) -> {
            current.next();
            return new State(users, items, current.loss(), current.estimations());
        }, gson::toJson);
    }

}
