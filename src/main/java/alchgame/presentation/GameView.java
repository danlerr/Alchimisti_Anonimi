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
        out.println();
        out.println("  ╔══════════════════════════════════════╗");
        out.println("  ║                                      ║");
        out.println("  ║        ALCHIMISTI  ANONIMI           ║");
        out.println("  ║                                      ║");
        out.println("  ╚══════════════════════════════════════╝");
        out.println();
    }

    public void promptPlayerCount(int min, int max) {
        out.printf("  Quanti giocatori? (%d-%d): ", min, max);
    }

    public void promptPlayerName(int playerNumber) {
        out.printf("  Nome giocatore %d: ", playerNumber);
    }

    public void showInvalidInput(String message) {
        out.println("  ⚠  " + message);
    }

    // --- Round / Phase ---

    public void showRoundStart(int current, int total) {
        out.println();
        out.println("  ┌─────────────────────────────────────────┐");
        out.printf( "  │           ROUND  %d  di  %d               │%n", current, total);
        out.println("  └─────────────────────────────────────────┘");
    }

    public void showRoundEnd(int roundNumber) {
        out.println();
        out.printf("  ─── Fine round %d ───────────────────────────%n", roundNumber);
    }

    public void showPhaseHeader(String phaseName) {
        out.println();
        out.printf("  ◆ %s%n", phaseName);
        out.println("  " + "─".repeat(38));
    }

    public void showCurrentPlayer(String name) {
        out.println();
        out.printf("  ► Turno di: %s%n", name);
    }

    public void showPlayerStatus(String name, int gold, int reputation, int actionCubes) {
        out.printf("    %-16s  💰 %2d oro   ⭐ %2d rep   🎲 %d cubi%n",
                name, gold, reputation, actionCubes);
    }

    // --- ORDER phase ---

    public void showAvailableSlots(List<String> slotIds) {
        out.println("  Slot disponibili:");
        for (int i = 0; i < slotIds.size(); i++) {
            out.printf("    [%d] %s%n", i + 1, slotIds.get(i));
        }
    }

    public int promptSlotChoice(int maxIndex) {
        return promptBoundedInt("  Scegli slot", maxIndex);
    }

    public void showSlotChoiceResult(String slotId, Resources res) {
        out.printf("  ✔ Slot %s → +%d ingredienti, +%d carte favore%n",
                slotId, res.ingredientCount(), res.favorCount());
    }

    // --- DECLARATION phase ---

    public void showActionList(List<String> actionIds) {
        out.println("  Azioni disponibili:");
        for (int i = 0; i < actionIds.size(); i++) {
            out.printf("    [%d] %s%n", i + 1, actionIds.get(i));
        }
    }

    public void showActionListWithPass(List<String> actionIds) {
        out.println("  Azioni disponibili:");
        for (int i = 0; i < actionIds.size(); i++) {
            out.printf("    [%d] %s%n", i + 1, actionIds.get(i));
        }
        out.println("    [0] Passa");
    }

    public int promptActionChoice(int maxIndex) {
        return promptBoundedInt("  Scegli azione", maxIndex);
    }

    public int promptActionOrPass(int maxIndex) {
        while (true) {
            out.printf("  Scelta (0-%d): ", maxIndex);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 0 && choice <= maxIndex) return choice;
                out.printf("  ⚠  Inserisci un valore tra 0 e %d.%n", maxIndex);
            } catch (NumberFormatException e) {
                out.printf("  ⚠  Inserisci un numero tra 0 e %d.%n", maxIndex);
            }
        }
    }

    public void showDeclaredAction(String playerName, String actionId) {
        out.printf("  ✔ %s → %s%n", playerName, actionId);
    }

    // --- EXPERIMENT ---

    public void showTargetOptions(List<String> targetIds) {
        out.println("  Bersaglio dell'esperimento:");
        for (int i = 0; i < targetIds.size(); i++) {
            out.printf("    [%d] %s%n", i + 1, targetIds.get(i));
        }
    }

    public String promptTargetChoice(List<String> targetIds) {
        while (true) {
            out.printf("  Scelta (1-%d): ", targetIds.size());
            String line = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(line) - 1;
                if (idx >= 0 && idx < targetIds.size()) return targetIds.get(idx);
            } catch (NumberFormatException ignored) {}
            out.printf("  ⚠  Inserisci un numero tra 1 e %d.%n", targetIds.size());
        }
    }

    public void showPaymentRequired() {
        out.println("  ⚠  Lo studente è scontento — verranno addebitati 1 oro.");
    }

    public void showPaymentResult(int remainingGold) {
        out.printf("  Oro rimasto: %d%n", remainingGold);
    }

    public void showInsufficientGold() {
        out.println("  ✖ Oro insufficiente per condurre l'esperimento sullo studente.");
    }

    public void showIngredients(List<Ingredient> ingredients) {
        out.println("  Ingredienti nel laboratorio:");
        for (int i = 0; i < ingredients.size(); i++) {
            out.printf("    [%d] %s%n", i + 1, ingredients.get(i).getName());
        }
    }

    public int promptIngredientChoice(String prompt, int maxIndex) {
        return promptBoundedInt(prompt, maxIndex);
    }

    public void showPotionResult(Potion potion) {
        out.println();
        if (potion.isNeutral()) {
            out.println("  ┌─────────────────────────────┐");
            out.println("  │   Pozione:  NEUTRA           │");
            out.println("  └─────────────────────────────┘");
        } else {
            out.println("  ┌─────────────────────────────┐");
            out.printf( "  │   Pozione:  %-8s %-7s│%n",
                    potion.getColor().name().toLowerCase(),
                    potion.getSign().name().toLowerCase());
            out.println("  └─────────────────────────────┘");
        }
    }

    public boolean promptUpdateDeductionGrid() {
        out.print("  Aggiornare la griglia di deduzione? (s/n): ");
        return readYesNo();
    }

    public void showDeductionGrid(DeductionGrid grid) {
        List<Ingredient>      ingredients = grid.getIngredients();
        List<AlchemicFormula> alchemics   = grid.getAlchemics();

        final int COL   = 9;
        final int LABEL = 26;

        out.println();
        out.println("  ┌─ Griglia di deduzione ──────────────────────────────────────────┐");
        out.println("  │  X = escluso   · = possibile                                    │");
        out.println("  │  R/G/B = colore   G = grande   P = piccolo   +/- = segno        │");
        out.println("  └─────────────────────────────────────────────────────────────────┘");
        out.println();

        out.print(" ".repeat(LABEL));
        for (Ingredient ing : ingredients) {
            out.print(centerTrunc(ing.getName(), COL));
        }
        out.println();

        out.print(" ".repeat(LABEL));
        out.println("─".repeat(ingredients.size() * COL));

        for (int a = 0; a < alchemics.size(); a++) {
            String label = "  [" + (a + 1) + "]  " + formatFormula(alchemics.get(a));
            out.printf("%-" + LABEL + "s", label);
            for (Ingredient ing : ingredients) {
                boolean excluded = grid.isExcluded(ing, alchemics.get(a));
                out.print(centerTrunc(excluded ? "✖" : "·", COL));
            }
            out.println();
        }
        out.println();
    }

    public void showExclusionResult(String ingredientName, String alchemicLabel) {
        out.printf("  ✔ Escluso: %s non può essere %s%n", ingredientName, alchemicLabel);
    }

    public int promptDeductionIngredientChoice(int max) {
        return promptBoundedInt("  Scegli ingrediente", max);
    }

    public int promptDeductionAlchemicChoice(int max) {
        return promptBoundedInt("  Scegli alchemico da escludere", max);
    }

    // --- Game over ---

    public void showGameOver(List<Player> players) {
        out.println();
        out.println("  ╔══════════════════════════════════════╗");
        out.println("  ║           FINE  PARTITA              ║");
        out.println("  ╚══════════════════════════════════════╝");
        out.println();
        List<Player> ranking = players.stream()
                .sorted((a, b) -> b.getReputation() - a.getReputation())
                .toList();
        out.println("  Classifica finale:");
        out.println("  " + "─".repeat(42));
        String[] medals = {"🥇", "🥈", "🥉"};
        for (int i = 0; i < ranking.size(); i++) {
            Player p = ranking.get(i);
            String medal = i < medals.length ? medals[i] : "   ";
            out.printf("  %s  %-16s  reputazione: %2d   oro: %2d%n",
                    medal, p.getName(), p.getReputation(), p.getGold());
        }
        out.println();
    }

    // --- Input helpers ---

    public int readInt() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                out.print("  ⚠  Inserisci un numero intero: ");
            }
        }
    }

    public String readLine() {
        while (true) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            out.print("  ⚠  Input non può essere vuoto: ");
        }
    }

    private boolean readYesNo() {
        while (true) {
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("s")) return true;
            if (line.equals("n")) return false;
            out.print("  ⚠  Inserisci 's' o 'n': ");
        }
    }

    private int promptBoundedInt(String prompt, int max) {
        while (true) {
            out.printf("%s (1-%d): ", prompt, max);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= max) return choice;
                out.printf("  ⚠  Inserisci un valore tra 1 e %d.%n", max);
            } catch (NumberFormatException e) {
                out.printf("  ⚠  Inserisci un numero tra 1 e %d.%n", max);
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
