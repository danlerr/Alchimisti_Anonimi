package alchgame.presentation;

import alchgame.model.alchemy.*;
import alchgame.model.board.Resources;
import alchgame.model.player.DeductionGrid;
import alchgame.model.player.Player;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class GameView {

    private final Scanner scanner;
    private final PrintStream out;

    public GameView() {
        this(new Scanner(System.in), System.out);
    }

    public GameView(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    // --- Setup ---

    public void showWelcome() {
        out.println("╔══════════════════════════════╗");
        out.println("║   ALCHIMISTI ANONIMI         ║");
        out.println("╚══════════════════════════════╝");
    }

    public void promptPlayerCount(int min, int max) {
        out.printf("Quanti giocatori? (%d-%d): ", min, max);
    }

    public void promptPlayerName(int playerNumber) {
        out.printf("Nome giocatore %d: ", playerNumber);
    }

    public void showInvalidInput(String message) {
        out.println("[!] " + message);
    }

    // --- Round / Phase ---

    public void showRoundStart(int current, int total) {
        out.println();
        out.printf("══════════════ ROUND %d / %d ══════════════%n", current, total);
    }

    public void showRoundEnd(int roundNumber) {
        out.printf("────────── Fine round %d ──────────%n", roundNumber);
    }

    public void showPhaseHeader(String phaseName) {
        out.println();
        out.println("  ▶ Fase: " + phaseName);
        out.println("  " + "─".repeat(30));
    }

    public void showCurrentPlayer(String name) {
        out.println();
        out.println("  Turno di: " + name);
    }

    public void showPlayerStatus(String name, int gold, int reputation, int actionCubes) {
        out.printf("  [%s] oro: %d  reputazione: %d  cubi azione: %d%n",
                name, gold, reputation, actionCubes);
    }

    // --- ORDER phase ---

    public void showAvailableSlots(List<String> slotIds) {
        out.println("  Slot disponibili nel tracciato:");
        for (int i = 0; i < slotIds.size(); i++) {
            out.printf("    %d. %s%n", i + 1, slotIds.get(i));
        }
    }

    public int promptSlotChoice(int maxIndex) {
        return promptBoundedInt("  Scegli uno slot", maxIndex);
    }

    public void showSlotChoiceResult(String slotId, Resources res) {
        out.printf("  Slot scelto: %s → +%d ingredienti, +%d carte favore%n",
                slotId, res.ingredientCount(), res.favorCount());
    }

    // --- DECLARATION phase ---

    public void showActionList(List<String> actionIds) {
        out.println("  Azioni disponibili:");
        for (int i = 0; i < actionIds.size(); i++) {
            out.printf("    %d. %s%n", i + 1, actionIds.get(i));
        }
    }

    public int promptActionChoice(int maxIndex) {
        return promptBoundedInt("  Scegli un'azione", maxIndex);
    }

    public void showDeclaredAction(String playerName, String actionId) {
        out.printf("  %s ha dichiarato: %s%n", playerName, actionId);
    }

    // --- EXPERIMENT ---

    public void showTargetOptions(String selfId, String studentId) {
        out.println("  Scegli il bersaglio dell'esperimento:");
        out.printf("    1. Me stesso (%s)%n", selfId);
        out.printf("    2. Studente (%s)%n", studentId);
    }

    public String promptTargetChoice(String selfId, String studentId) {
        while (true) {
            out.print("  Scelta (1/2): ");
            String line = scanner.nextLine().trim();
            if (line.equals("1")) return selfId;
            if (line.equals("2")) return studentId;
            out.println("[!] Inserisci 1 o 2.");
        }
    }

    public void showPaymentRequired() {
        out.println("  Lo studente è scontento. Pagherai 1 oro.");
    }

    public void showPaymentResult(int remainingGold) {
        out.printf("  Oro rimasto: %d%n", remainingGold);
    }

    public void showInsufficientGold() {
        out.println("[!] Oro insufficiente per condurre l'esperimento sullo studente.");
    }

    public void showIngredients(List<Ingredient> ingredients) {
        out.println("  Ingredienti nel laboratorio:");
        for (int i = 0; i < ingredients.size(); i++) {
            out.printf("    %d. %s%n", i + 1, ingredients.get(i).getName());
        }
    }

    public int promptIngredientChoice(String prompt, int maxIndex) {
        return promptBoundedInt(prompt, maxIndex);
    }

    public void showPotionResult(Potion potion) {
        if (potion.isNeutral()) {
            out.println("  Risultato: pozione NEUTRA.");
        } else {
            out.printf("  Risultato: pozione %s %s%n",
                    potion.getColor().name().toLowerCase(),
                    potion.getSign().name().toLowerCase());
        }
    }

    public boolean promptUpdateDeductionGrid() {
        out.print("  Vuoi aggiornare la griglia di deduzione? (s/n): ");
        return readYesNo();
    }

    public void showDeductionGrid(DeductionGrid grid) {
        List<Ingredient>      ingredients = grid.getIngredients();
        List<AlchemicFormula> alchemics   = grid.getAlchemics();

        final int COL   = 8;   // larghezza colonna ingrediente
        final int LABEL = 24;  // larghezza etichetta riga: "  [N]  R:G+ G:P- B:G+"

        out.println();
        out.println("  Griglia di deduzione  (X=escluso  ·=possibile)");
        out.println("  R/G/B=colore  G=grande  P=piccolo  +=positivo  -=negativo");
        out.println();

        // Intestazione: nomi ingredienti centrati
        out.print(" ".repeat(LABEL));
        for (Ingredient ing : ingredients) {
            out.print(centerTrunc(ing.getName(), COL));
        }
        out.println();

        // Separatore
        out.print(" ".repeat(LABEL));
        out.println("─".repeat(ingredients.size() * COL));

        // Righe: una per alchemico
        for (int a = 0; a < alchemics.size(); a++) {
            String label = "  [" + (a + 1) + "]  " + formatFormula(alchemics.get(a));
            out.printf("%-" + LABEL + "s", label);
            for (Ingredient ing : ingredients) {
                boolean excluded = grid.isExcluded(ing, alchemics.get(a));
                out.print(centerTrunc(excluded ? "X" : "·", COL));
            }
            out.println();
        }
        out.println();
    }

    public void showExclusionResult(String ingredientName, String alchemicLabel) {
        out.printf("  ✓ Escluso: %s non può essere %s%n", ingredientName, alchemicLabel);
    }

    public int promptDeductionIngredientChoice(int max) {
        return promptBoundedInt("  Scegli ingrediente [indice]", max);
    }

    public int promptDeductionAlchemicChoice(int max) {
        return promptBoundedInt("  Scegli alchemico da escludere [indice]", max);
    }

    // --- Game over ---

    public void showGameOver(List<Player> players) {
        out.println();
        out.println("══════════════ FINE PARTITA ══════════════");
        List<Player> ranking = players.stream()
                .sorted((a, b) -> b.getReputation() - a.getReputation())
                .toList();
        out.println("  Classifica finale:");
        for (int i = 0; i < ranking.size(); i++) {
            Player p = ranking.get(i);
            out.printf("  %d. %s — reputazione: %d, oro: %d%n",
                    i + 1, p.getName(), p.getReputation(), p.getGold());
        }
    }

    // --- Input helpers ---

    public int readInt() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                out.print("[!] Inserisci un numero intero: ");
            }
        }
    }

    public String readLine() {
        while (true) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            out.print("[!] Input non può essere vuoto: ");
        }
    }

    private boolean readYesNo() {
        while (true) {
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("s")) return true;
            if (line.equals("n")) return false;
            out.print("[!] Inserisci 's' o 'n': ");
        }
    }

    private int promptBoundedInt(String prompt, int max) {
        while (true) {
            out.printf("%s (1-%d): ", prompt, max);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= max) return choice;
                out.printf("[!] Inserisci un valore tra 1 e %d.%n", max);
            } catch (NumberFormatException e) {
                out.printf("[!] Inserisci un numero tra 1 e %d.%n", max);
            }
        }
    }

    private String centerTrunc(String text, int width) {
        if (text.length() > width) text = text.substring(0, width);
        int padding = width - text.length();
        int left = padding / 2;
        return " ".repeat(left) + text + " ".repeat(padding - left);
    }

    private String formatFormula(AlchemicFormula formula) {
        StringBuilder sb = new StringBuilder();
        for (Color color : Color.real()) {
            Atom atom = formula.getAtomByColor(color);
            if (atom == null) continue;
            if (!sb.isEmpty()) sb.append(' ');
            sb.append(colorChar(color))
              .append(':')
              .append(sizeChar(atom.getSize()))
              .append(signChar(atom.getSign()));
        }
        return sb.toString();
    }

    private char colorChar(Color color) {
        return switch (color) {
            case RED   -> 'R';
            case GREEN -> 'G';
            case BLUE  -> 'B';
            default    -> '?';
        };
    }

    private char sizeChar(Size size) {
        return switch (size) {
            case BIG   -> 'G';
            case SMALL -> 'P';
        };
    }

    private char signChar(Sign sign) {
        return switch (sign) {
            case POSITIVE -> '+';
            case NEGATIVE -> '-';
            case NEUTRAL  -> '~';
        };
    }
}
