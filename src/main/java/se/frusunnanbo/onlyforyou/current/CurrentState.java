package se.frusunnanbo.onlyforyou.current;

import se.frusunnanbo.onlyforyou.model.State;

public class CurrentState {

    public static State currentState() {
        final double[][] current = {
                {5.2, 4.3, 8.1},
                {0.1, 5.3, 9.0},
                {1.2, 5.6, 3.5}
        };
        return new State(0.3, current);
    }
}
