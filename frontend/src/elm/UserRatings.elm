module UserRatings exposing (initialUserRatings, fetchUserRatings, renderUserRatings, toSparseMatrix, UserRatings)

import Html exposing (table, tr, th, td, div, button, text, Html)
import Html.Attributes exposing (class)
import Http
import Array exposing (Array)
import Matrix exposing (matrix, Matrix)
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
    { users = [], items = [], ratings = [] }


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
                    :: ratingsRows userRatings.users (toSparseMatrix userRatings)
                )


itemsHeading : List String -> Html msg
itemsHeading items =
    tr []
        (th [] []
            :: List.map (\item -> th [] [ text item ]) items
        )


ratingsRows : List String -> Matrix (Maybe Int) -> List (Html msg)
ratingsRows users ratings =
    List.map2 ratingsRow users (Array.toList ratings)


ratingsRow : String -> Array (Maybe Int) -> Html msg
ratingsRow user ratings =
    tr []
        ( td [] [ text user]
        :: List.map (\rating -> td [] [ text (formatRating rating) ]) (Array.toList ratings))

formatRating : Maybe Int -> String
formatRating rating =
    rating
        |> Maybe.map toString
        |> Maybe.withDefault ""

toSparseMatrix : UserRatings -> Matrix (Maybe Int)
toSparseMatrix userRatings =
    matrix (List.length userRatings.users) (List.length userRatings.items) (valueAt userRatings.ratings)

valueAt : List UserRating -> Matrix.Location -> Maybe Int
valueAt userRatings location =
    List.filter (\rating -> (Matrix.row location) == rating.userIndex && (Matrix.col location) == rating.itemIndex) userRatings
        |> List.head
        |> Maybe.map (\rating -> rating.score)


