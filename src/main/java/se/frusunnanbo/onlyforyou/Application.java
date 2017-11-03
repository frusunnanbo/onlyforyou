package se.frusunnanbo.onlyforyou;

import com.google.gson.Gson;
import se.frusunnanbo.onlyforyou.current.Calculation;
import se.frusunnanbo.onlyforyou.input.UserData;
import se.frusunnanbo.onlyforyou.model.State;
import spark.Spark;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class Application {
    public static void main(String[] args) {
        Gson gson = new Gson();
        Calculation current = Calculation.create(UserData.ratingsMatrix(UserData.users()));

        Spark.externalStaticFileLocation(System.getProperty("frontend.root"));
        staticFiles.registerMimeType("html", "text/html; charset=utf-8");

        get("/userdata", (req, res) -> UserData.users(), gson::toJson);
        get("/currentstate", (req, res) -> new State(current.loss(), current.estimations()), gson::toJson);
        post("/currentstate", (req, res) -> {
            current.next();
            return new State(current.loss(), current.estimations());
        }, gson::toJson);
    }

}
