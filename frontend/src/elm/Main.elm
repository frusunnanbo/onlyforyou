module Main exposing (main)

import Html exposing (table, tr, th, td, div, button, text, program, Html)
import Html.Attributes exposing (class)
import Dict exposing (Dict)
import Set exposing (Set)
import Http
import Json.Decode as Decode exposing (Decoder, decodeString, list, string, float, field, at, map, map2, map3)
import FormatNumber
import FormatNumber.Locales exposing (usLocale)


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


initialOptimizationState : OptimizationState
initialOptimizationState =
    { users = [], items = [], ratings = [] }


init : ( Model, Cmd Msg )
init =
    ( { actualRatings = [], optimizationState = initialOptimizationState }, fetchInitialState )


type Msg
    = InitialData (Result Http.Error (List UserRatings))
    | CurrentState (Result Http.Error OptimizationState)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        InitialData (Ok newUserData) ->
            ( { model | actualRatings = newUserData }, Cmd.none )

        InitialData (Err msg) ->
            ( model, Cmd.none )

        CurrentState (Ok optimizationState) ->
            ( { model | optimizationState = optimizationState }, Cmd.none )

        CurrentState (Err msg) ->
            ( model, Cmd.none )


withNewRatings : OptimizationState -> List (List Float) -> OptimizationState
withNewRatings state ratings =
    { state | ratings = ratings }


fetchInitialState : Cmd Msg
fetchInitialState =
    Cmd.batch
        [ fetchInitialUserData
        , fetchCurrentOptimizationState
        ]


fetchCurrentOptimizationState : Cmd Msg
fetchCurrentOptimizationState =
    Http.get "/currentstate" decodeCurrentState
        |> Http.send CurrentState


fetchInitialUserData : Cmd Msg
fetchInitialUserData =
    Http.get "/userdata" decodeUserData
        |> Http.send InitialData


decodeCurrentState : Decoder OptimizationState
decodeCurrentState =
    map3 OptimizationState (field "users" (list user)) (field "items" (list item)) (field "ratings" (list (list float)))


user : Decoder User
user =
    map User (field "name" string)


decodeUserData : Decoder (List UserRatings)
decodeUserData =
    list userRatings


userRatings : Decoder UserRatings
userRatings =
    map2 UserRatings (field "name" string) (field "ratings" (list decodeRating))


decodeRating : Decoder Rating
decodeRating =
    map2 Rating (field "video" item) (at [ "score", "score" ] float)


item : Decoder Item
item =
    map Item (field "name" string)


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
    div [ class "optimizationView" ]
        [ table []
            (itemsHeading optimizationState.items
                :: ratingsRows optimizationState.users optimizationState.ratings
            )
        , nextButton
        ]


nextButton : Html Msg
nextButton =
    div [ class "nextbutton" ]
        [ button [] [ text "Next" ] ]


itemsHeading : List Item -> Html Msg
itemsHeading items =
    tr []
        (th [] []
            :: List.map (\item -> th [] [ text item.name ]) items
        )


ratingsRows : List User -> List (List Float) -> List (Html Msg)
ratingsRows users ratings =
    List.map2 ratingsRow users ratings


ratingsRow : User -> List Float -> Html Msg
ratingsRow user ratings =
    tr []
        (td [] [ text user.name ]
            :: List.map (\rating -> td [ class "rating" ] [ text (formatRating rating) ]) ratings
        )


formatRating : Float -> String
formatRating rating =
    FormatNumber.format usLocale rating


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
    td [ class "rating" ] [ score ratings item ]


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
