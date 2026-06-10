package alchgame.application.observer;

import alchgame.application.dto.GameStateDTO;

public interface GameObserver {
    
    void onGameEvent(GameStateDTO state);
}

