package elementos.managers;

public class ScoreManager {
    private int scoreTotal = 0;
    private int multiplicadorCombate = 1;

    private boolean insigniaAlquimista = false;
    private boolean insigniaVerdugo = false;
    private boolean insigniaSuperviviente = false;

    public ScoreManager() {
    }

    public void addPuntosEnemigoDerrotado(int puntosBase) {
        scoreTotal += (puntosBase * multiplicadorCombate);
    }

    public void addPuntosExploracion(int puntos) {
        scoreTotal += puntos;
    }

    public int getScoreTotal() {
        return scoreTotal;
    }

    public void checkInsigniaVerdugo(int golpesAcertados, int enemigosDerrotados) {
        if (!insigniaVerdugo && enemigosDerrotados >= 5) {
            float eficiencia = (float) golpesAcertados / enemigosDerrotados;
            if (eficiencia <= 3.0f) { 
                insigniaVerdugo = true;
                System.out.println("¡Insignia VERDUGO IMPLACABLE desbloqueada!");
            }
        }
    }

    public void checkInsigniaSuperviviente(int nivel, int usosHabilidadRestantes, int maxUsos) {
        if (!insigniaSuperviviente && usosHabilidadRestantes == maxUsos) {
            insigniaSuperviviente = true;
            System.out.println("¡Insignia SUPERVIVIENTE NATO desbloqueada!");
        }
    }

    public void otorgarInsigniaAlquimista() {
        if (!insigniaAlquimista) {
            insigniaAlquimista = true;
            System.out.println("¡Insignia ALQUIMISTA MAESTRO desbloqueada!");
        }
    }

    public boolean hasInsigniaAlquimista() { return insigniaAlquimista; }
    public boolean hasInsigniaVerdugo() { return insigniaVerdugo; }
    public boolean hasInsigniaSuperviviente() { return insigniaSuperviviente; }

    public void resetScore() {
        scoreTotal = 0;
        multiplicadorCombate = 1;
        insigniaAlquimista = false;
        insigniaVerdugo = false;
        insigniaSuperviviente = false;
    }
}