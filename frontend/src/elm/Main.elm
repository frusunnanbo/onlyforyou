module Main exposing (main)

import Html exposing (table, tr, th, td, text, program, Html)
import Dict exposing (Dict)
import Set exposing (Set)
import Http
import Json.Decode as Decode exposing (Decoder, decodeString, list, string, float, field, at, map2)


main =
    program { init = init, update = update, view = view, subscriptions = \_ -> Sub.none }


type alias Item = String

type alias Rating =
    { item : Item
    , score : Float
    }


type alias UserRatings =
    { name : String
    , ratings : List Rating
    }

type alias User =
    { name: String }

type alias OptimizationState =
    { users: List User
    , items: List Item
    , ratings: List (List Float)
    }

type alias Model =
    { actualRatings: List UserRatings
    , optimizationState: OptimizationState
    }

initialOptimizationState : OptimizationState
initialOptimizationState = { users = [], items = [], ratings = [] }

init : (Model, Cmd Msg)
init =
    ({
    actualRatings = []
    , optimizationState = initialOptimizationState
    }, fetchNextState)


type Msg
    = Next
    | NewState (Result Http.Error (List UserRatings))

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
    case msg of
        Next ->
            (model, fetchNextState)
        NewState (Ok newUserData) ->
            ({ model | actualRatings = newUserData}, Cmd.none)
        NewState (Err msg) ->
            (model, Cmd.none)


fetchNextState : Cmd Msg
fetchNextState =
    Http.get "/userdata" decodeUserData
        |> Http.send NewState

decodeUserData: Decoder (List UserRatings)
decodeUserData =
    list user

user : Decoder UserRatings
user =
    map2 UserRatings (field "name" string) (field "ratings" (list decodeRating))


decodeRating : Decoder Rating
decodeRating =
    map2 Rating (at ["video", "name"] string) (at ["score", "score"] float)

view : Model -> Html Msg
view model =
    let
        userRatings = model.actualRatings
        items = extractItems userRatings
    in
        table []
            (heading items :: List.map (row items) userRatings)

extractItems : List UserRatings -> List String
extractItems users =
    List.concatMap extractUserItems users
        |> Set.fromList
        |> Set.toList

extractUserItems : UserRatings -> List String
extractUserItems user =
    List.map (\rating -> rating.item) user.ratings

heading : List Item -> Html Msg
heading items =
    tr []
        (th [] [text ""] :: List.map (\item ->  th [] [ text item ]) items)

row : List Item -> UserRatings -> Html Msg
row items user =
    tr []
        (td [] [ text user.name ] :: (ratings items) user.ratings)

ratings : List Item -> List Rating -> List (Html Msg)
ratings items ratings =
   List.map (rating ratings) items

rating : List Rating -> Item -> Html Msg
rating ratings item =
    td [] [ score ratings item ]

score : List Rating -> Item -> Html Msg
score ratings item =
    Dict.get item (toDict ratings)
        |> Maybe.map toString
        |> Maybe.withDefault ""
        |> text

toDict : List Rating -> Dict Item Float
toDict ratings =
    ratings
        |> List.map (\rating -> (rating.item, rating.score))
        |> Dict.fromList
