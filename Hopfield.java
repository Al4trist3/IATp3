import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Hopfield {
    private int[][] pesos;
    private int capacidad;

    public Hopfield(int capacidad) {
        this.capacidad = capacidad;
        pesos = new int[capacidad][capacidad];
    }

    // Método para entrenar la red con un patrón
    public void entrenar(int[] patron) {
        for (int i = 0; i < capacidad; i++) {
            for (int j = 0; j < capacidad; j++) {
                if (i != j) {
                    pesos[i][j] += patron[i] * patron[j];
                }
            }
        }
    }

    // Método para recordar el patrón
    public int[] recordar(int[] entrada) {
        int[] salida = entrada.clone();
        boolean estable;
        do {
            estable = true;
            for (int i = 0; i < capacidad; i++) {
                int sum = 0;
                for (int j = 0; j < capacidad; j++) {
                    sum += pesos[i][j] * salida[j];
                }
                int nuevoValor = sum >= 0 ? 1 : -1;
                if (nuevoValor != salida[i]) {
                    salida[i] = nuevoValor;
                    estable = false;
                }
            }
        } while (!estable);
        return salida;
    }

    // Método para convertir la imagen en un patrón
    public static int[] imagenAPatron(BufferedImage imagen) {
        int[] pattern = new int[imagen.getWidth() * imagen.getHeight()];
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                Color color = new Color(imagen.getRGB(x, y));
                // Convertir color a -1 o 1
                pattern[y * imagen.getWidth() + x] = (color.getRed() > 128) ? 1 : -1; // Umbral de 128
            }
        }
        return pattern;
    }

    // Método para crear una imagen de patrón a partir del array
    public static BufferedImage patronAImagen(int[] patron, int ancho, int altura) {
        BufferedImage imagen = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < ancho; x++) {
                Color color = (patron[y * ancho + x] == 1) ? Color.WHITE : Color.BLACK;
                imagen.setRGB(x, y, color.getRGB());
            }
        }
        return imagen;
    }

    public static void main(String[] args) {
        try {
            // Crear un objeto Hopfield de 100 neuronas (10x10)
            Hopfield hopfield = new Hopfield(100);

            // Cargar la imagen de entrenamiento (un patrón de un cuadrado negro)
            BufferedImage imagenAEntrenar = ImageIO.read(new File("patron.png"));
            int[] patronAEntrenar = imagenAPatron(imagenAEntrenar);
            hopfield.entrenar(patronAEntrenar);

            // Cargar la imagen con ruido (un patrón de un cuadrado negro con lineas como ruido)
            BufferedImage imagenConRuido = ImageIO.read(new File("patron_ruido.png"));
            int[] patronConRuido = imagenAPatron(imagenConRuido);
            System.out.println("Patrón ruidoso:");
            imprimirPatron(patronConRuido);

            // Recordar el patrón
            int[] patron = hopfield.recordar(patronConRuido);
            System.out.println("Patrón recuperado:");
            imprimirPatron(patron);

            // Guardar la imagen recuperada
            BufferedImage imagenRecuperada = patronAImagen(patron, 10, 10);
            ImageIO.write(imagenRecuperada, "png", new File("patron_recuperado.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para imprimir el patrón en consola
    private static void imprimirPatron(int[] patron) {
        for (int i = 0; i < patron.length; i++) {
            if (i % 10 == 0 && i != 0) {
                System.out.println();
            }
            System.out.print(patron[i] == 1 ? "1 " : "0 ");
        }
        System.out.println();
    }
}
