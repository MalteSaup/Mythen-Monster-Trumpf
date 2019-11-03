package com.projectc.mythicalmonstermatch;

public class PlayerResponder implements PlayerChangeListener{

    private final GameActivity gameActivity;

    public PlayerResponder(GameActivity gameActivity){
        this.gameActivity = gameActivity;
    }

    @Override
    public void playerChanged() {
        this.gameActivity.update();
    }
}
