package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class bloques {

    private int x, y, width, height;
    private Color color;
    private boolean visible;

    public bloques(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.visible = true;
    }

    public void dibujar(Graphics g) {
        if (!visible) return;
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isVisible() {
        return visible;
    }

    public void destruir() {
        visible = false;
    }
}
