package gui;

import java.awt.*;
import java.awt.image.BufferedImage;

public class bloques {
    private int x, y, width, height;
    private boolean visible;
    private BufferedImage imgBloque; // imagen del bloque

    public bloques(int x, int y, int width, int height, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.imgBloque = img;
        this.visible = img != null; // si no hay imagen, el bloque no es visible
    }

    public void dibujar(Graphics g) {
        if (!visible) return;

        if (imgBloque != null) {
            g.drawImage(imgBloque, x, y, width, height, null);
        } else {
            g.setColor(Color.BLACK); // fallback por si algo sale mal
            g.fillRect(x, y, width, height);
        }
    }

    public void destruir() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
