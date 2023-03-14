package practica1;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Practica1 {

    private static String[] nomsDones = {"JOANA", "MARIA", "ALICIA", "MARTA", "MIQUELA", "ANTONIA"};
    private static String[] nomsHomes = {"MIQUEL", "JOAN", "MARTI", "TOMEU", "MARC", "ANTONI"};
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String RESET_COLOR = "\u001B[0m";
    private static Semaphore sc = new Semaphore(1);
    private static Semaphore banyBuit = new Semaphore(1);
    private static Random ran = new Random();
    private static final int NUMIDAS = 3;
    private static final int MAXBANY = 3;
    private static final int NUMGENERO = 6;

    static class Hombre implements Runnable {

        static volatile int numHombres = 0; // total de hombres que van a entrar en el baño
        static Semaphore contadorH = new Semaphore(MAXBANY);
        static Semaphore hombres = new Semaphore(1);
        static volatile int baño = 0;
        String id;

        public Hombre(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println(BLUE + id + " arriba al seu despatx" + RESET_COLOR);
                for (int i = 0; i < NUMIDAS; i++) {
                    System.out.println(BLUE + id + " està treballant..." + RESET_COLOR);
                    Thread.sleep(ran.nextInt(500));
                    hombres.acquire();
                    if (numHombres == 0) {
                        //banyBuit
                        banyBuit.acquire();
                        System.out.println("****** BANY BUIT ******");
                    }
                    numHombres++;
                    hombres.release();
                    contadorH.acquire();
                    sc.acquire();
                    baño++;
                    System.out.println(BLUE + id + " ha entrat al bany (" + (i + 1) + "\\"
                            + NUMIDAS + "). Homes al bany: " + (baño) + RESET_COLOR);
                    sc.release();
                    //el baño
                    Thread.sleep(ran.nextInt(250));
                    sc.acquire();
                    baño--;
                    System.out.println(BLUE + id + " surt del bany" + RESET_COLOR);
                    sc.release();
                    contadorH.release();
                    hombres.acquire();
                    numHombres--;
                    if (numHombres == 0) {
                        banyBuit.release();
                    }
                    hombres.release();
                }
                System.out.println(BLUE + id + " ha acabat de treballar" + RESET_COLOR);

            } catch (InterruptedException ex) {
                Logger.getLogger(Practica1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    static class Mujer implements Runnable {

        static Semaphore mujeres = new Semaphore(1);
        static volatile int numMujeres = 0; // total de hombres que van a entrar en el baño
        static Semaphore contadorM = new Semaphore(MAXBANY);
        static volatile int baño  = 0;
        String id;

        public Mujer(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println(RED + id + " arriba al seu despatx" + RESET_COLOR);
                for (int i = 0; i < NUMIDAS; i++) {
                    System.out.println(RED + id + " està treballant..." + RESET_COLOR);
                    Thread.sleep(ran.nextInt(500));
                    mujeres.acquire();
                    if (numMujeres == 0) {
                        //banyBuit
                        banyBuit.acquire();
                        System.out.println("****** BANY BUIT ******");
                    }
                    numMujeres++;
                    mujeres.release();
                    contadorM.acquire();
                    sc.acquire();
                    baño++;
                    System.out.println(RED + id + " ha entrat al bany (" + (i + 1) + "\\"
                            + NUMIDAS + "). Homes al bany: " + (baño) + RESET_COLOR);
                    sc.release();
                    //el baño
                    Thread.sleep(ran.nextInt(250));
                    sc.acquire();
                    baño--;
                    System.out.println(RED + id + " surt del bany" + RESET_COLOR);
                    sc.release();
                    contadorM.release();
                    mujeres.acquire();
                    numMujeres--;
                    if (numMujeres == 0) {
                        banyBuit.release();
                    }
                    mujeres.release();
                }
                System.out.println(RED + id + " ha acabat de treballar" + RESET_COLOR);

            } catch (InterruptedException ex) {
                Logger.getLogger(Practica1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] hombres = new Thread[NUMGENERO];
        Thread[] mujeres = new Thread[NUMGENERO];
        int i;
        for (i = 0; i < NUMGENERO; i++) {
            hombres[i] = new Thread(new Hombre(nomsHomes[i]));
            hombres[i].start();
        }
        for (i = 0; i < NUMGENERO; i++) {
            mujeres[i] = new Thread(new Mujer(nomsDones[i]));
            mujeres[i].start();
        }
        for (i = 0; i < NUMGENERO; i++) {
            hombres[i].join();
        }
        for (i = 0; i < NUMGENERO; i++) {
            mujeres[i].join();
        }
    }
}
