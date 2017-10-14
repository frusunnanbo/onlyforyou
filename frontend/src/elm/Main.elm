module Main exposing (main)

import Html exposing (table, tr, th, td, text, beginnerProgram, Html)
import Dict exposing (Dict)


main =
    beginnerProgram { model = init, update = update, view = view }


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


init : List User
init =
    [ User "Anna" [ Rating "Greta Gris" 5, Rating "Bon" 3 ]
    , User "Britta" [ Rating "Bon" 2, Rating "Nobelfesten" 5 ]
    , User "Cilla" [ Rating "Vår tid är nu" 1, Rating "Medan vi dör" 5 ]
    , User "Daniela" [ Rating "Skam" 3, Rating "Vår tid är nu" 3 ]
    ]


type Msg
    = Next


update : Msg -> Model -> Model
update msg model =
    case msg of
        Next ->
            model


view : Model -> Html Msg
view model =
    let
        items = [ "Greta Gris", "Bon", "Nobelfesten", "Vår tid är nu", "Medan vi dör", "Skam" ]
    in
        table []
            (heading items :: List.map (row items) model)

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
