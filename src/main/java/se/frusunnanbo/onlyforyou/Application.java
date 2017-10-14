package se.frusunnanbo.onlyforyou;

import com.google.gson.Gson;
import se.frusunnanbo.onlyforyou.input.UserData;
import spark.Spark;

import static spark.Spark.get;
import static spark.Spark.staticFiles;

public class Application {
    public static void main(String[] args) {
        Gson gson = new Gson();

        Spark.externalStaticFileLocation(System.getProperty("frontend.root"));
        staticFiles.registerMimeType("html", "text/html; charset=utf-8");

        get("/userdata", (req, res) -> UserData.users(), gson::toJson);
    }
}
