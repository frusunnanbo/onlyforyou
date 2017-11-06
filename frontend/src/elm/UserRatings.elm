module UserRatings exposing (fetchUserRatings, UserRatings)

import Http
import Json.Decode as Decode exposing (Decoder, decodeString, list, string, int, field, at, map, map2, map3)

type alias UserRating =
    { userIndex : Int
    , itemIndex : Int
    , score : Int
    }

type alias UserRatings =
    { users : List String
    , items : List String
    , ratings : List UserRating
    }

fetchUserRatings : (Result Http.Error UserRatings -> msg) -> Cmd msg
fetchUserRatings msg =
    Http.get "/userratings" decodeUserRatings
        |> Http.send msg

decodeUserRatings : Decoder UserRatings
decodeUserRatings =
    map3 UserRatings (field "users" (list user)) (field "items" (list item)) (field "ratings" (list userRating))


user : Decoder String
user =
    (field "name" string)

item : Decoder String
item =
    (field "name" string)

userRating : Decoder UserRating
userRating =
    map3 UserRating (field "userIndex" int) (field "itemIndex" int) (field "rating" int)
