package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
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

    private javax.swing.Timer movimientoTimer;
    
    private int ballX = 300;
    private int ballY = 300;
    private int ballDiameter = 20;
    private int movX = 2;
    private int movY = -2;

    private bloques[][] bloquesArray;
    private final int filas = 5;
    private final int columnas = 8;
    private final int bloqueWidth = 60;
    private final int bloqueHeight = 20;
    private BufferedImage[] bloquesImg; // imágenes de los bloques


    private int marcianoX = 200; // posición horizontal inicial
    private int marcianoY = 100; // posición vertical fija
    private int marcianoWidth = 50;
    private int marcianoHeight = 50;
    private int marcianoDX = 2; // velocidad de movimiento
    private int marcianoDY = 2; 
    private BufferedImage marcianoImg; // imagen del marciano
    private BufferedImage pelotaImg; //imagen de la pelota

    private int puntaje = 0;
    private int nivel = 1; // empieza en nivel 1
    private int vidas = 3;
    private final int maxVidas = 3; 
    private boolean perdiste = false;

    private boolean enPantallaInicio = true;// controla si estamos en pantalla de inicio

    private Timer timer;
    private Random random = new Random();
    
    Set<Integer> teclasPresionadas = new HashSet<>();

    public JuegoPanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        movimientoPlataforma();	// funcion para permitir el movimiento de la plataforma
        
        bloquesImg = new BufferedImage[5];
        String[] nombres = {"bloque1.png","bloque2.png","bloque3.png",
                            "bloque4.png","bloque5.png", "bloque6.png"};
        for (int i = 0; i < bloquesImg.length; i++) {
            try {
                bloquesImg[i] = ImageIO.read(new File("media/" + nombres[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
            fondoJuego = ImageIO.read(new File("media/fondito.png")); // tu imagen de fondo para el juego
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            miFuente = Font.createFont(Font.TRUETYPE_FONT, new File("media/pixel.ttf")).deriveFont(15f);
        } catch (Exception e) {
            e.printStackTrace();
            miFuente = new Font("Arial", Font.BOLD, 40); // fuente de respaldo
        }
        try {
            marcianoImg = ImageIO.read(new File("media/alien.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            pelotaImg = ImageIO.read(new File("media/pelotarda.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        generarBloques(); // genera bloques random al iniciar

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!enPantallaInicio) { // solo mover y colisionar si ya empezó el juego
                    moverPelota();
                    revisarColisiones();
                    if(nivel >=2) {
                    	moverMarciano(); 	
                    }
                    
                }
                repaint();
            }
        }, 0, 10);
    }

    private void generarBloques() {
        bloquesArray = new bloques[filas][columnas];	// array de los bloques procediente de la clase bloques
        for (int i = 0; i < filas; i++) {		// ciclo for para recorrer por cada fila y columna la posicion de los bloques
            for (int j = 0; j < columnas; j++) {
                if (random.nextBoolean()) {		// condicion para alternar bloques de manera random por cada fila y columna
                    BufferedImage img = bloquesImg[random.nextInt(bloquesImg.length)];		// genero el bloque
                    bloquesArray[i][j] = new bloques(	// si es true, la clase bloques sera llenado con los parametros correspondientes
                            10 + j * (bloqueWidth + 10),
                            50 + i * (bloqueHeight + 10),
                            bloqueWidth,
                            bloqueHeight,
                            img		
                    );
                } else {
                    bloquesArray[i][j] = new bloques(	// en caso de ser false, el bloque no sera visible, lo cual corresponde al ultimo valor nulo
                            10 + j * (bloqueWidth + 10),	
                            50 + i * (bloqueHeight + 10),
                            bloqueWidth,
                            bloqueHeight,
                            null
                    );
                    bloquesArray[i][j].destruir(); // metodo de la clase bloques para destruir aquellos bloques no visibles
                }
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {		// funcion para dibujar o materializar componentes ya sean circulos o rectangulos
        super.paintComponent(g);

        // pantalla de inicio
        if (enPantallaInicio) {
            if (fondoInicio != null) {
                g.drawImage(fondoInicio, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Presiona ENTER para empezar", getWidth()/2 - 140, getHeight()/2 + 10);
            return;
        }

        // fondo del juego
        if (fondoJuego != null) {
            g.drawImage(fondoJuego, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // dibujar pala
        if (plataformaImg != null) {
            int alto = (int)((double)plataformaImg.getHeight() / plataformaImg.getWidth() * paddleWidth);
            g.drawImage(plataformaImg, paddleX, getHeight() - 70, paddleWidth, alto, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(paddleX, getHeight() - 70, paddleWidth, paddleHeight);
        }

        // dibujar pelota
        if (pelotaImg != null) {
            int ancho = ballDiameter;
            int alto = (int)((double)pelotaImg.getHeight() / pelotaImg.getWidth() * ancho); // mantiene proporción
            g.drawImage(pelotaImg, ballX, ballY, ancho, alto, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillOval(ballX, ballY, ballDiameter, ballDiameter);
        }


        // dibujar marciano cada 2 niveles
        if (nivel % 2 == 0) {
            if (marcianoImg != null) {
                g.drawImage(marcianoImg, marcianoX, marcianoY, marcianoWidth, marcianoHeight, null);
            } else {
                g.setColor(Color.GREEN);
                g.fillRect(marcianoX, marcianoY, marcianoWidth, marcianoHeight);
            }
        }

        // dibujar bloques
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                bloquesArray[i][j].dibujar(g);
            }
        }

        // dibujar vidas
        int vidaAncho = 40, vidaAlto = 20, espacio = 5;
        for (int i = 0; i < vidas; i++) {
            if (plataformaImg != null) {
                g.drawImage(plataformaImg, getWidth()/2 - ((vidas * (vidaAncho + espacio))/2) + i*(vidaAncho+espacio),
                            10, vidaAncho, vidaAlto, null);
            } else {
                g.setColor(Color.GREEN);
                g.fillRect(getWidth()/2 - ((vidas * (vidaAncho + espacio))/2) + i*(vidaAncho+espacio),
                           10, vidaAncho, vidaAlto);
            }
        }

        // puntaje y nivel
        if (miFuente != null) g.setFont(miFuente);
        int sombraOffset = 2;
        g.setColor(Color.BLACK);
        g.drawString("Puntaje: " + puntaje, 10 + sombraOffset, 20 + sombraOffset);
        g.drawString("Nivel: " + nivel, 500 + sombraOffset, 20 + sombraOffset);

        g.setColor(Color.WHITE);
        g.drawString("Puntaje: " + puntaje, 10, 20);
        g.drawString("Nivel: " + nivel, 500, 20);

        // mensaje de derrota
        if (perdiste) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("¡PERDISTE!", getWidth()/2 - 120, getHeight()/2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Presiona R para reiniciar", getWidth()/2 - 110, getHeight()/2 + 40);
        }
    }
    
    // funcion para iniciar el movimiento de la pelota
    private void moverPelota() {
        if (perdiste) return;	// condicion para detener la funcion en caso de que el jugador haya perdido

        ballX += movX;	// movimiento en eje x
        ballY += movY;	// movimiento en eje y

        // rebote en paredes
        if (ballX < 0 || ballX + ballDiameter > getWidth()) movX = -movX;
        if (ballY < 0) movY = -movY;

        // rebote en la pala
        if (ballY + ballDiameter >= getHeight() - 70 &&
        	    ballY + ballDiameter <= getHeight() - 70 + paddleHeight &&
        	    ballX + ballDiameter >= paddleX &&
        	    ballX <= paddleX + paddleWidth){
        	    movY = -movY;
        	}

        // si cae la pelota
        if (ballY > getHeight()) {
            if (vidas > 1) {
                vidas--;
                resetPelota();
            } else {
                vidas = 0;
                perdiste = true;
                movX = 0;
                movY = 0;
            }
        }

        // colisión con el marciano
        if (nivel % 2 == 0) {
            Rectangle ballRect = new Rectangle(ballX, ballY, ballDiameter, ballDiameter);
            Rectangle marcianoRect = new Rectangle(marcianoX, marcianoY, marcianoWidth, marcianoHeight);

            if (ballRect.intersects(marcianoRect)) {
                movY = -movY; // invierte dirección vertical
            }
        }
    }


    private void moverMarciano() {
        if (nivel % 2 > 0) return; // aparece cada dos niveles

        marcianoX += marcianoDX;
        marcianoY += marcianoDY;

        if (marcianoX < 0 || marcianoX + marcianoWidth > getWidth()) marcianoDX = -marcianoDX;
        if (marcianoY < 0 || marcianoY + marcianoHeight > getHeight() - 70) marcianoDY = -marcianoDY;
    }

    // funcion para las colisiones de la pelota con los bloques
    private void revisarColisiones() {
        if (perdiste) return;	

        boolean todosDestruidos = true;
        Rectangle ballRect = new Rectangle(ballX, ballY, ballDiameter, ballDiameter);	// para formar un perimetro en forma de rectangulo para el circulo y de esta forma detectar con mas facilidad colisiones
        for (int i = 0; i < filas; i++) {	// ciclo for para recorrer cada bloque
            for (int j = 0; j < columnas; j++) {
                bloques b = bloquesArray[i][j];	
                if (b.isVisible()) {	// condicion para saber si estan todos los bloques destruidos
                    todosDestruidos = false;
                    if (ballRect.intersects(b.getBounds())) {	// condicion para detectar colisiones con los bloques
                        b.destruir();	// metodo para destruir el bloque
                        movY = -movY;	// el rebote de la pelota es con el eje Y
                        puntaje += 10;	// sumamos puntaje pa
                    }
                }
            }
        }

        if (todosDestruidos) {	// en caso de estar todos destruidos
            nivel++;	// incrementamos el nivel
            generarBloques();	// llamamos a la funcion para generar bloques
            resetPelota();	// reseteamos la posicion de la pelota
            
            if (nivel % 3 ==0) {	// para el marciano
                // posición inicial random dentro del panel
                marcianoX = random.nextInt(getWidth() - marcianoWidth);
                marcianoY = random.nextInt(getHeight()/2); // arriba de la mitad de la ventana
                // velocidad random
                marcianoDX = 2 + random.nextInt(2); // 2 o 3 px por frame
                marcianoDY = 2 + random.nextInt(2);
            }

        }
    }

    private void resetPelota() {	// funcion para resetear la pelota
        ballX = 300;
        ballY = 300;
        movX = 2 + nivel - 1;
        movY = -2 - nivel + 1;
        paddleX = 250;
    }

    // funcion para modular el movimiento de la plataforma mediante el teclado
    private void movimientoPlataforma() {
        movimientoTimer = new javax.swing.Timer(26, t -> {
            if (!perdiste && !enPantallaInicio) {	// si no perdimos y no estamos en la pantalla inicial
                if (teclasPresionadas.contains(KeyEvent.VK_LEFT) && paddleX > 0) {	// movemos hacia la izquierda
                    paddleX -= 15;
                }
                if (teclasPresionadas.contains(KeyEvent.VK_RIGHT) && paddleX + paddleWidth < getWidth()) {	// movemos hacia la derecha
                    paddleX += 15;
                }
            }
        });
        movimientoTimer.start();	// timer para que el movimiento de la plataforma sea mas fluido
    }
    
    @Override
    public void keyPressed(KeyEvent e) {	// evento keypressed para algunos detalles
    	teclasPresionadas.add(e.getKeyCode());

        int key = e.getKeyCode();

        // iniciar juego desde pantalla de inicio
        if (enPantallaInicio && key == KeyEvent.VK_ENTER) {	
            enPantallaInicio = false;
            return;
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
    public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {
		teclasPresionadas.remove(e.getKeyCode());
	}

}
