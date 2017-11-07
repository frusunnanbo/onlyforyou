module UserRatings exposing (initialUserRatings, fetchUserRatings, renderUserRatings, UserRatings)

import Html exposing (table, tr, th, td, div, button, text, Html)
import Html.Attributes exposing (class)
import Http
import Array exposing (Array)
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


initialUserRatings : UserRatings
initialUserRatings =
    { users = [ "Britta" ], items = [ "Greta Gris" ], ratings = [] }


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
    map3 UserRating (field "userIndex" int) (field "itemIndex" int) (field "value" int)


renderUserRatings : UserRatings -> Html msg
renderUserRatings userRatings =
    table []
                (itemsHeading userRatings.items
                    :: ratingsRows userRatings.users userRatings.ratings
                )


itemsHeading : List String -> Html msg
itemsHeading items =
    tr []
        (th [] []
            :: List.map (\item -> th [] [ text item ]) items
        )


ratingsRows : List String -> List UserRating -> List (Html msg)
ratingsRows users ratings =
    List.map ratingsRow users


ratingsRow : String -> Html msg
ratingsRow user =
    tr []
        [ td [] [ text user] ]


