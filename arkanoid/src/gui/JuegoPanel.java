package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class JuegoPanel extends JPanel implements KeyListener {

    private int paddleX = 250;
    private final int paddleWidth = 100;
    private final int paddleHeight = 10;
    private BufferedImage plataformaImg; // Imagen de la plataforma
    private BufferedImage fondoInicio; // Imagen de fondo de la pantalla de inicio
    private BufferedImage fondoJuego;// Imagen de fondo del juego
    private Font miFuente; 


    private int ballX = 300;
    private int ballY = 300;
    private int ballDiameter = 20;
    private int ballDX = 2;
    private int ballDY = -2;

    private bloques[][] bloquesArray;
    private final int filas = 5;
    private final int columnas = 8;
    private final int bloqueWidth = 60;
    private final int bloqueHeight = 20;

    private int puntaje = 0;
    private int nivel = 1; // empieza en nivel 1
    private int vidas = 3;
    private final int maxVidas = 3; 
    private boolean perdiste = false;

    private boolean enPantallaInicio = true;// controla si estamos en pantalla de inicio

    private Timer timer;
    private Random random = new Random();

    public JuegoPanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // cargar imagen de la plataforma
        try {
            plataformaImg = ImageIO.read(new File("media/plata.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // cargar imagen de fondo de inicio
        try {
            fondoInicio = ImageIO.read(new File("media/DarkNoid.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fondoJuego = ImageIO.read(new File("media/Fondo_Garka.png")); // tu imagen de fondo para el juego
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            miFuente = Font.createFont(Font.TRUETYPE_FONT, new File("media/pixel.ttf")).deriveFont(15f);
        } catch (Exception e) {
            e.printStackTrace();
            miFuente = new Font("Arial", Font.BOLD, 40); // fuente de respaldo
        }

        generarBloques(); // genera bloques random al iniciar

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!enPantallaInicio) { // solo mover y colisionar si ya empezó el juego
                    moverPelota();
                    revisarColisiones();
                }
                repaint();
            }
        }, 0, 10);
    }

    private void generarBloques() {
        bloquesArray = new bloques[filas][columnas];
        Color[] colores = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.MAGENTA};
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (random.nextBoolean()) {
                    bloquesArray[i][j] = new bloques(
                            10 + j * (bloqueWidth + 10),
                            50 + i * (bloqueHeight + 10),
                            bloqueWidth,
                            bloqueHeight,
                            colores[random.nextInt(colores.length)]
                    );
                } else {
                    bloquesArray[i][j] = new bloques(
                            10 + j * (bloqueWidth + 10),
                            50 + i * (bloqueHeight + 10),
                            bloqueWidth,
                            bloqueHeight,
                            Color.BLACK
                    );
                    bloquesArray[i][j].destruir(); // invisible
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // pantalla de inicio
        if (enPantallaInicio) {
            // dibujar imagen de fondo si existe
            if (fondoInicio != null) {
                g.drawImage(fondoInicio, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            // dibujar texto encima del fondo
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Presiona ENTER para empezar", getWidth()/2 - 140, getHeight()/2 + 10);
            return;
        }
        if (!enPantallaInicio) {
            if (fondoJuego != null) {
                g.drawImage(fondoJuego, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.setColor(Color.BLACK); // fondo negro por defecto
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }


        // dibujar pala con imagen manteniendo proporciones
        if (plataformaImg != null) {
            int ancho = paddleWidth;
            int alto = (int) ((double) plataformaImg.getHeight() / plataformaImg.getWidth() * ancho);
            g.drawImage(plataformaImg, paddleX, getHeight() - 70, ancho, alto, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(paddleX, getHeight() - 70, paddleWidth, paddleHeight);
        }

        // dibujar pelota
        g.setColor(Color.WHITE);
        g.fillOval(ballX, ballY, ballDiameter, ballDiameter);

        // dibujar bloques
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                bloquesArray[i][j].dibujar(g);
            }
        }

        // dibujar vidas, puntaje y nivel
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
     // dibujar vidas como mini plataformas
        int vidaAncho = 40;
        int vidaAlto = 20;
        int espacio = 5; // espacio entre cada vida

        for (int i = 0; i < vidas; i++) {
            if (plataformaImg != null) {
                g.drawImage(plataformaImg, getWidth()/2 - ((vidas * (vidaAncho + espacio))/2) + i * (vidaAncho + espacio),
                            10, vidaAncho, vidaAlto, null);
            } else {
                g.setColor(Color.GREEN);
                g.fillRect(getWidth()/2 - ((vidas * (vidaAncho + espacio))/2) + i * (vidaAncho + espacio),
                           10, vidaAncho, vidaAlto);
            }
        }

        if (miFuente != null) {
            g.setFont(miFuente);
        }
        int sombraOffset = 2; // distancia de la sombra en píxeles
        g.setColor(Color.BLACK); // color de la sombra
        g.drawString("Puntaje: " + puntaje, 10 + sombraOffset, 20 + sombraOffset);
        g.drawString("Nivel: " + nivel, 500 + sombraOffset, 20 + sombraOffset);

        g.setColor(Color.WHITE); // texto principal
        g.drawString("Puntaje: " + puntaje, 10, 20);
        g.drawString("Nivel: " + nivel, 500, 20);

       
        // si perdiste, mostrar mensaje
        if (perdiste) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("¡PERDISTE!", getWidth()/2 - 120, getHeight()/2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Presiona R para reiniciar", getWidth()/2 - 110, getHeight()/2 + 40);
        }
    }

    private void moverPelota() {
        if (perdiste) return;

        ballX += ballDX;
        ballY += ballDY;

        // rebote en paredes
        if (ballX < 0 || ballX + ballDiameter > getWidth()) ballDX = -ballDX;
        if (ballY < 0) ballDY = -ballDY;

        // rebote en la pala
        if (ballY + ballDiameter >= getHeight() - 70 &&
                ballX + ballDiameter >= paddleX &&
                ballX <= paddleX + paddleWidth) {
            ballDY = -ballDY;
        }

        // si cae la pelota
        if (ballY > getHeight()) {
            if (vidas > 1) {
                vidas--;
                resetPelota();
            } else {
                vidas = 0;
                perdiste = true;
                ballDX = 0;
                ballDY = 0;
            }
        }
    }

    private void revisarColisiones() {
        if (perdiste) return;

        boolean todosDestruidos = true;
        Rectangle ballRect = new Rectangle(ballX, ballY, ballDiameter, ballDiameter);
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                bloques b = bloquesArray[i][j];
                if (b.isVisible()) {
                    todosDestruidos = false;
                    if (ballRect.intersects(b.getBounds())) {
                        b.destruir();
                        ballDY = -ballDY;
                        puntaje += 10;
                    }
                }
            }
        }

        if (todosDestruidos) {
            nivel++;
            generarBloques();
            resetPelota();
        }
    }

    private void resetPelota() {
        ballX = 300;
        ballY = 300;
        ballDX = 2 + nivel - 1;
        ballDY = -2 - nivel + 1;
        paddleX = 250;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // iniciar juego desde pantalla de inicio
        if (enPantallaInicio && key == KeyEvent.VK_ENTER) {
            enPantallaInicio = false;
            return;
        }

        if (!perdiste && !enPantallaInicio) {
            if (key == KeyEvent.VK_LEFT && paddleX > 0) paddleX -= 20;
            if (key == KeyEvent.VK_RIGHT && paddleX + paddleWidth < getWidth()) paddleX += 20;
        }

        // reiniciar juego si perdiste
        if (perdiste && key == KeyEvent.VK_R) {
            puntaje = 0;
            nivel = 1;
            vidas = maxVidas;
            perdiste = false;
            generarBloques();
            resetPelota();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
