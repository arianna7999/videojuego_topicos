package utils;

import static utils.Constantes.ConstantesJugador.*;

import juego.Juego;

public class Constantes {

    public static class Enviroment {
        public static final int FONDO_ARBOLES_WIDTH_DEFAULT = 960;
        public static final int FONDO_ARBOLES_HEIGHT_DEFAULT = 480;
        public static final int FONDO_ARBOLES_WIDTH = (int) (FONDO_ARBOLES_WIDTH_DEFAULT * Juego.SCALE);
        public static final int FONDO_ARBOLES_HEIGHT = (int) (FONDO_ARBOLES_HEIGHT_DEFAULT * Juego.SCALE);
                public static final int POSTE_DUENOS_HEIGHT_DEFAULT= 1568;
        public static final int POSTE_DUENOS_WIDTH_DEFAULT= 1400;
        public static final int POSTE_DUENOS_WIDTH = (int) (FONDO_ARBOLES_WIDTH_DEFAULT * Juego.SCALE);
        public static final int POSTE_DUENOS_HEIGHT = (int) (FONDO_ARBOLES_HEIGHT_DEFAULT * Juego.SCALE);
    }

    public static class Direccion {
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }

    public static class ConstantesJugador {
        public static final int INACTIVO = 0;
        public static final int CORRER = 1;
        public static final int ATACAR1 = 2;
        public static final int ATACAR2 = 3;
        public static final int ARCO = 4;
        public static final int RECIBIR_GOLPE = 5;
        public static final int MUERTO = 6;
        public static final int SALTAR = 1;
        public static final int CAYENDO = 1;
    }

    public static int GetNoSprite(int playerAction) {
        switch (playerAction) {
            case CORRER: // (y SALTAR / CAYENDO)
            case ARCO:
                return 8;
            case INACTIVO:
            case ATACAR1:
            case ATACAR2:
                return 6;
            case RECIBIR_GOLPE:
            case MUERTO:
                return 4;
            default:
                return 1;
        }
    }

    // enemigos
    public static class ConstantesEnemigos {
        public static final int ESQUELETO = 0;
        public static final int CABALLERO = 1;
        public static final int JEFE_FINAL = 2;
        public static final int ORCO = 3;

        public static final int INACTIVO = 0;
        public static final int CORRER = 1;
        public static final int ATACAR = 2;
        public static final int RECIBIR_GOLPE = 5;
        public static final int MUERTO = 6;

        public static int GetSpriteAmount(int enemy_type, int enemy_state) {
            switch (enemy_type) {
                case ESQUELETO:
                case CABALLERO:
                    switch (enemy_state) {
                        case INACTIVO:
                            return 6;
                        case CORRER:
                            return 8;
                        case ATACAR:
                            return 6;
                        case RECIBIR_GOLPE:
                            return 4;
                        case MUERTO:
                            return 4;
                    }
                    break;
                case JEFE_FINAL:
                    switch (enemy_state) {
                        case INACTIVO:
                            return 5;
                        case CORRER:
                            return 8;
                        case ATACAR:
                            return 7;
                        case RECIBIR_GOLPE:
                            return 4;
                        case MUERTO:
                            return 4;
                    }
                    break;
                case ORCO:
                    switch (enemy_state) {
                        case INACTIVO:
                            return 6;
                        case CORRER:
                            return 6;
                        case ATACAR:
                            return 7;
                        case RECIBIR_GOLPE:
                            return 4;
                        case MUERTO:
                            return 4;
                    }
                    break;
            }

            return 0;
        }
    }

    public static class ConstantesObjetos {
        public static final int BARRIL = 0;
        public static final int COFRE = 1;
        public static final int CORAZON = 2;
        public static final int PLATAFORMA = 4;
        public static final int LLAVE = 5;
        public static final int PUERTA = 6;

        public static final int INACTIVO = 0;
        public static final int ANIMACION = 1;

        public static int GetSpriteAmount(int tipo_objeto, int estado) {
            switch (tipo_objeto) {
                case BARRIL:
                case COFRE:
                    if (estado == INACTIVO)
                        return 1;
                    if (estado == ANIMACION)
                        return 8;
                case CORAZON:
                    return 12;
            }
            return 1;
        }
    }
}