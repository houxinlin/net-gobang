package com.h.game.ui;

import com.h.game.GobangNet;

import javax.swing.*;

public abstract class BaseGobangUI extends GobangNet {
    public BaseGobangUI() {
        init();
    }

    protected void init() {
        this.setSize(850, 850);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}
