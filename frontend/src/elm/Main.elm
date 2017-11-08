module Main exposing (main)

import Html exposing (table, tr, th, td, div, button, text, program, Html)
import Html.Attributes exposing (class)
import Html.Events exposing (onClick)
import Dict exposing (Dict)
import Set exposing (Set)
import Array exposing (Array)
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
    { ratings : List (List Float)
    }


type alias Model =
    { users : List String
    , items : List String
    , userRatings : UserRatings.UserRatings
    , optimizationState : OptimizationState
    }


initialOptimizationState : OptimizationState
initialOptimizationState =
    { ratings = [] }


init : ( Model, Cmd Msg )
init =
    ( { users = []
      , items = []
      , userRatings = UserRatings.initialUserRatings
      , optimizationState = initialOptimizationState
      }
    , fetchInitialState
    )


type Msg
    = UserRatingsFetched (Result Http.Error UserRatings.UserRatings)
    | CurrentState (Result Http.Error OptimizationState)
    | Next


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        UserRatingsFetched (Ok userRatings) ->
            ( model
                |> withUsers userRatings.users
                |> withItems userRatings.items
                |> withRatings userRatings
            , Cmd.none
            )

        UserRatingsFetched (Err msg) ->
            ( model, Cmd.none )

        CurrentState (Ok optimizationState) ->
            ( { model | optimizationState = optimizationState }, Cmd.none )

        CurrentState (Err msg) ->
            ( model, Cmd.none )

        Next ->
            ( model, getNextOptimizationState )


withUsers : List String -> Model -> Model
withUsers users model =
    { model | users = users }


withItems : List String -> Model -> Model
withItems items model =
    { model | items = items }


withRatings : UserRatings.UserRatings -> Model -> Model
withRatings userRatings model =
    { model | userRatings = userRatings }


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
    map OptimizationState (field "ratings" (list (list float)))

view : Model -> Html Msg
view model =
    div []
        [ UserRatings.renderUserRatings model.userRatings
        , optimizationView model
        ]


optimizationView : Model -> Html Msg
optimizationView model =
    let
        actualRatings = model.userRatings
                        |> UserRatings.toSparseMatrix
                        |> Array.map (\row -> Array.toList row)
                        |> Array.toList
    in
        div [ class "optimizationView" ]
            [ table []
                (itemsHeading model.items
                :: ratingsRows model.users model.optimizationState.ratings actualRatings
                )
            , nextButton
            ]


nextButton : Html Msg
nextButton =
    div [ class "nextbutton" ]
        [ button [ onClick Next ] [ text "Next" ] ]


itemsHeading : List String -> Html Msg
itemsHeading items =
    tr []
        (th [] []
            :: List.map (\item -> th [] [ text item ]) items
        )


ratingsRows : List String -> List (List Float) -> List (List (Maybe Int)) -> List (Html Msg)
ratingsRows users estimatedRatings actualRatings =
    List.map3 ratingsRow users estimatedRatings actualRatings


ratingsRow : String -> List Float -> List (Maybe Int) -> Html Msg
ratingsRow user estimatedRatings actualRatings =
    tr []
        (td [] [ text user ]
            :: List.map2 rating estimatedRatings actualRatings
        )

rating : Float -> Maybe Int -> Html Msg
rating estimated actual =
    td
        [ class "rating", class (ratingAccuracy estimated actual) ]
        [ text (formatRating estimated) ]

formatRating : Float -> String
formatRating rating =
    FormatNumber.format usLocale rating

ratingAccuracy : Float -> Maybe Int -> String
ratingAccuracy estimated actual =

        actual
            |> Maybe.map (getError estimated)
            |> Maybe.map ratingClass
            |> Maybe.withDefault ""


getError : Float -> Int -> Float
getError estimated actual =
     abs (estimated - (toFloat actual))

ratingClass : Float -> String
ratingClass error =
    if (error < 0.01) then
                "accurate"
            else if (error < 0.05) then
                "rightish"
            else if (error < 1) then
                "wrongish"
            else
                "wayoff"

heading : List String -> Html Msg
heading items =
    tr []
        (th [] [ text "" ] :: List.map (\item -> th [] [ text item ]) items)
