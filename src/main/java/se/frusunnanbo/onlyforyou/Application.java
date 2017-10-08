package se.frusunnanbo.onlyforyou;

import com.google.gson.Gson;
import se.frusunnanbo.onlyforyou.input.UserData;

import static spark.Spark.get;

public class Application {
    public static void main(String[] args) {
        Gson gson = new Gson();

        get("/userdata", (req, res) -> UserData.users(), gson::toJson);
    }
}
