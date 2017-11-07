module Main exposing (main)

import Html exposing (table, tr, th, td, div, button, text, program, Html)
import Html.Attributes exposing (class)
import Html.Events exposing (onClick)
import Dict exposing (Dict)
import Set exposing (Set)
import Http
import Json.Decode as Decode exposing (Decoder, decodeString, list, string, float, field, at, map, map2, map3)
import FormatNumber
import FormatNumber.Locales exposing (usLocale)
import UserRatings


main =
    program { init = init, update = update, view = view, subscriptions = \_ -> Sub.none }


type alias Item =
    { name : String }

type alias User =
    { name : String }


type alias OptimizationState =
    { users : List User
    , items : List Item
    , ratings : List (List Float)
    }


type alias Model =
    { userRatings: UserRatings.UserRatings
    , optimizationState : OptimizationState
    }


initialOptimizationState : OptimizationState
initialOptimizationState =
    { users = [], items = [], ratings = [] }


init : ( Model, Cmd Msg )
init =
    ( { userRatings = UserRatings.initialUserRatings, optimizationState = initialOptimizationState }, fetchInitialState )


type Msg
    = UserRatingsFetched (Result Http.Error UserRatings.UserRatings)
    | CurrentState (Result Http.Error OptimizationState)
    | Next


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of

        UserRatingsFetched (Ok userRatings) ->
            ( { model | userRatings = userRatings }, Cmd.none )

        UserRatingsFetched (Err msg) ->
            ( model, Cmd.none )

        CurrentState (Ok optimizationState) ->
            ( { model | optimizationState = optimizationState }, Cmd.none )

        CurrentState (Err msg) ->
            ( model, Cmd.none )

        Next ->
            (model, getNextOptimizationState)



withNewRatings : OptimizationState -> List (List Float) -> OptimizationState
withNewRatings state ratings =
    { state | ratings = ratings }


fetchInitialState : Cmd Msg
fetchInitialState =
    Cmd.batch
        [ fetchUserRatings
        , fetchCurrentOptimizationState
        ]

fetchUserRatings : Cmd Msg
fetchUserRatings =
    UserRatings.fetchUserRatings UserRatingsFetched

fetchCurrentOptimizationState : Cmd Msg
fetchCurrentOptimizationState =
    Http.get "/currentstate" decodeCurrentState
        |> Http.send CurrentState


getNextOptimizationState : Cmd Msg
getNextOptimizationState =
    Http.post "/currentstate/next" Http.emptyBody decodeCurrentState
        |> Http.send CurrentState


decodeCurrentState : Decoder OptimizationState
decodeCurrentState =
    map3 OptimizationState (field "users" (list user)) (field "items" (list item)) (field "ratings" (list (list float)))


user : Decoder User
user =
    map User (field "name" string)

item : Decoder Item
item =
    map Item (field "name" string)


view : Model -> Html Msg
view model =
        div []
            [ UserRatings.renderUserRatings model.userRatings
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
        [ button [ onClick Next ] [ text "Next" ] ]


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


heading : List String -> Html Msg
heading items =
    tr []
        (th [] [ text "" ] :: List.map (\item -> th [] [ text item ]) items)
