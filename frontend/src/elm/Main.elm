module Main exposing (main)

import Html exposing (table, tr, th, td, div, text, program, Html)
import Dict exposing (Dict)
import Set exposing (Set)
import Http
import Json.Decode as Decode exposing (Decoder, decodeString, list, string, float, field, at, map, map2)


main =
    program { init = init, update = update, view = view, subscriptions = \_ -> Sub.none }


type alias Item =
    { name : String
    }


type alias Rating =
    { item : Item
    , score : Float
    }


type alias UserRatings =
    { name : String
    , ratings : List Rating
    }


type alias User =
    { name : String }


type alias OptimizationState =
    { users : List User
    , items : List Item
    , ratings : List (List Float)
    }


type alias Model =
    { actualRatings : List UserRatings
    , optimizationState : OptimizationState
    }


initialUsers : List User
initialUsers =
    [ User "Anna", User "Britta", User "Carin", User "Dilba", User "Eva" ]


initialItems : List Item
initialItems =
    [ Item "Greta Gris", Item "Hela Sverige Bakar" ]

initialRatings : List (List Float)
initialRatings =
    [ [ 0.3, 0.5 ]
    , [ 3.4, 1.5 ]
    , [ 5.0, 3.3 ]
    , [ 4.4, 1.2 ]
    , [ 2.4, 2.5 ]
    ]


initialOptimizationState : OptimizationState
initialOptimizationState =
    { users = initialUsers, items = initialItems, ratings = initialRatings }


init : ( Model, Cmd Msg )
init =
    ( { actualRatings = [], optimizationState = initialOptimizationState }, fetchNextState )


type Msg
    = Next
    | NewState (Result Http.Error (List UserRatings))


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        Next ->
            ( model, fetchNextState )

        NewState (Ok newUserData) ->
            ( { model | actualRatings = newUserData }, Cmd.none )

        NewState (Err msg) ->
            ( model, Cmd.none )


fetchNextState : Cmd Msg
fetchNextState =
    Http.get "/userdata" decodeUserData
        |> Http.send NewState


decodeUserData : Decoder (List UserRatings)
decodeUserData =
    list user


user : Decoder UserRatings
user =
    map2 UserRatings (field "name" string) (field "ratings" (list decodeRating))


decodeRating : Decoder Rating
decodeRating =
    map2 Rating item (at [ "score", "score" ] float)


item : Decoder Item
item =
    map Item (at [ "video", "name" ] string)


view : Model -> Html Msg
view model =
    let
        userRatings =
            model.actualRatings

        items =
            extractItems userRatings
    in
        div []
            [ table []
                (heading items :: List.map (row items) userRatings)
            , optimizationView model.optimizationState
            ]


optimizationView : OptimizationState -> Html Msg
optimizationView optimizationState =
    table []
          (itemsHeading optimizationState.items
          :: ratingsRows optimizationState.users optimizationState.ratings)

itemsHeading : List Item -> Html Msg
itemsHeading items =
    tr []
    (th [] []
    :: List.map (\item -> th [] [ text item.name ]) items)

ratingsRows : List User -> List (List Float) -> List (Html Msg)
ratingsRows users ratings =
    List.map2 ratingsRow users ratings

ratingsRow : User -> List Float -> Html Msg
ratingsRow user ratings =
    tr [] [ td [] [ text user.name ]]

extractItems : List UserRatings -> List String
extractItems users =
    List.concatMap extractUserItems users
        |> Set.fromList
        |> Set.toList


extractUserItems : UserRatings -> List String
extractUserItems user =
    List.map (\rating -> rating.item.name) user.ratings


heading : List String -> Html Msg
heading items =
    tr []
        (th [] [ text "" ] :: List.map (\item -> th [] [ text item ]) items)


row : List String -> UserRatings -> Html Msg
row items user =
    tr []
        (td [] [ text user.name ] :: (ratings items) user.ratings)


ratings : List String -> List Rating -> List (Html Msg)
ratings items ratings =
    List.map (rating ratings) items


rating : List Rating -> String -> Html Msg
rating ratings item =
    td [] [ score ratings item ]


score : List Rating -> String -> Html Msg
score ratings itemName =
    Dict.get itemName (toDict ratings)
        |> Maybe.map toString
        |> Maybe.withDefault ""
        |> text


toDict : List Rating -> Dict String Float
toDict ratings =
    ratings
        |> List.map (\rating -> ( rating.item.name, rating.score ))
        |> Dict.fromList
