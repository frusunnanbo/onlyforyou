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


type alias User =
    { name : String
    , ratings : List Rating
    }


type alias Model =
    List User


init : (Model, Cmd Msg)
init =
    ([ User "Anna" [ Rating "Greta Gris" 5, Rating "Bon" 3 ]
    , User "Britta" [ Rating "Bon" 2, Rating "Nobelfesten" 5 ]
    , User "Cilla" [ Rating "Vår tid är nu" 1, Rating "Medan vi dör" 5 ]
    , User "Daniela" [ Rating "Skam" 3, Rating "Vår tid är nu" 3 ]
    ], fetchNextState)


type Msg
    = Next
    | NewState (Result Http.Error Model)

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
    case msg of
        Next ->
            (model, fetchNextState)
        NewState (Ok newModel) ->
            (newModel, Cmd.none)
        NewState (Err msg) ->
            (model, Cmd.none)


fetchNextState : Cmd Msg
fetchNextState =
    Http.get "/userdata" decodeModel
        |> Http.send NewState

decodeModel: Decoder Model
decodeModel =
    list user

user : Decoder User
user =
    map2 User (field "name" string) (field "ratings" (list decodeRating))


decodeRating : Decoder Rating
decodeRating =
    map2 Rating (at ["video", "name"] string) (at ["score", "score"] float)

view : Model -> Html Msg
view model =
    let
        items = extractItems model
    in
        table []
            (heading items :: List.map (row items) model)

extractItems : List User -> List String
extractItems users =
    List.concatMap extractUserItems users
        |> Set.fromList
        |> Set.toList

extractUserItems : User -> List String
extractUserItems user =
    List.map (\rating -> rating.item) user.ratings

heading : List Item -> Html Msg
heading items =
    tr []
        (th [] [text ""] :: List.map (\item ->  th [] [ text item ]) items)

row : List Item -> User -> Html Msg
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
