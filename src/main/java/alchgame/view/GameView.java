package alchgame.view;

import alchgame.model.*;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class GameView {

    private static final String RESET   = "\033[0m";
    private static final String BOLD    = "\033[1m";
    private static final String CYAN    = "\033[36m";
    private static final String YELLOW  = "\033[33m";
    private static final String GREEN   = "\033[32m";
    private static final String RED     = "\033[31m";
    private static final String MAGENTA = "\033[35m";
    private static final String DIM     = "\033[2m";

    private final Scanner scanner = new Scanner(System.in);

    // ── Utilities ────────────────────────────────────────────────────────────

    void clearScreen() { System.out.print("\033[H\033[2J"); System.out.flush(); }

    void pause(String msg) { if (!msg.isEmpty()) System.out.println(msg); scanner.nextLine(); }

    void printHeader() {
        System.out.println(MAGENTA + BOLD);
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║      ✦   Alchimisti Anonimi  ✦       ║");
        System.out.println(". ╚══════════════════════════════════════╝" + RESET);
    }

    void printStatus(int gold, int reputation, int experiments, int ingredients) {
        System.out.println("  " + DIM + "Oro: " + RESET + YELLOW + BOLD + gold + RESET +
                DIM + "  |  Reputazione: " + RESET + BOLD + reputation + RESET +
                DIM + "  |  Esperimenti: " + RESET + BOLD + experiments + RESET +
                DIM + "  |  Ingredienti: " + RESET + BOLD + ingredients + RESET);
        System.out.println();
    }

    void printSection(String title) {
        System.out.println(CYAN + BOLD + "  ══ " + title + " ══" + RESET + "\n");
    }

    void showError(String msg) {
        System.out.println(RED + "  ✗ " + msg + RESET);
        scanner.nextLine();
    }

    void showGoodbye() {
        System.out.println(GREEN + "\n  Arrivederci!\n" + RESET);
    }

    // ── Main menu ─────────────────────────────────────────────────────────────

    int showMainMenu() {
        System.out.println(CYAN + BOLD + "  ╔══════════════════════════════╗" + RESET);
        System.out.println(CYAN + BOLD + "  ║       COSA VUOI FARE?        ║" + RESET);
        System.out.println(CYAN + BOLD + "  ╠══════════════════════════════╣" + RESET);
        System.out.println(CYAN + BOLD + "  ║ " + RESET + YELLOW + "[1]" + RESET + " Inizia Esperimento       " + CYAN + BOLD + "║" + RESET);
        System.out.println(CYAN + BOLD + "  ║ " + RESET + YELLOW + "[2]" + RESET + " Vedi Laboratorio Privato " + CYAN + BOLD + "║" + RESET);
        System.out.println(CYAN + BOLD + "  ║ " + RESET + YELLOW + "[3]" + RESET + " Vedi Tabellone Pubblico  " + CYAN + BOLD + "║" + RESET);
        System.out.println(CYAN + BOLD + "  ║ " + RESET + YELLOW + "[4]" + RESET + " Vedi Stato Target        " + CYAN + BOLD + "║" + RESET);
        System.out.println(CYAN + BOLD + "  ║ " + RESET + RED    + "[0]" + RESET + " Esci                     " + CYAN + BOLD + "║" + RESET);
        System.out.println(CYAN + BOLD + "  ╚══════════════════════════════╝" + RESET);
        System.out.print(BOLD + "\n  Scelta > " + RESET);
        return switch (scanner.nextLine().trim()) {
            case "0" -> 0; case "1" -> 1; case "2" -> 2; case "3" -> 3; case "4" -> 4;
            default  -> -1;
        };
    }

    // ── Experiment flow ───────────────────────────────────────────────────────

    Integer askTargetChoice() {
        System.out.println("  Scegli il target:");
        System.out.println("  " + YELLOW + "[1]" + RESET + " Studente     " + DIM + "(gratuito)" + RESET);
        System.out.println("  " + YELLOW + "[2]" + RESET + " Su Te Stesso " + DIM + "(gratuito)" + RESET);
        System.out.println("  " + YELLOW + "[0]" + RESET + " Annulla");
        System.out.print(BOLD + "\n  Scelta > " + RESET);
        return switch (scanner.nextLine().trim()) {
            case "1" -> 1; case "2" -> 2; default -> null;
        };
    }

    boolean askPaymentConfirm(int currentGold) {
        System.out.println("\n  " + YELLOW + "⚠  Questo target richiede 1 moneta d'oro." + RESET);
        System.out.println("  Oro attuale: " + BOLD + currentGold + RESET);
        System.out.print("  Vuoi pagare? [s/n] > ");
        return scanner.nextLine().trim().equalsIgnoreCase("s");
    }

    void showPaymentSuccess(int remainingGold) {
        System.out.println("  " + GREEN + "✓ Pagamento effettuato. Oro rimasto: " + remainingGold + RESET);
    }

    void showIngredients(List<Ingredient> available) {
        System.out.println("\n  " + CYAN + "Ingredienti disponibili:" + RESET);
        for (int i = 0; i < available.size(); i++)
            System.out.println("  " + YELLOW + "[" + (i + 1) + "]" + RESET + " " + available.get(i).getName());
    }

    Ingredient pickIngredient(List<Ingredient> list, String prompt, Ingredient exclude) {
        while (true) {
            System.out.print(BOLD + prompt + RESET);
            String raw = scanner.nextLine().trim();
            if (raw.equals("0")) return null;
            try {
                int idx = Integer.parseInt(raw) - 1;
                if (idx < 0 || idx >= list.size()) throw new NumberFormatException();
                Ingredient chosen = list.get(idx);
                if (chosen == exclude) { System.out.println(RED + "  Non puoi scegliere lo stesso ingrediente due volte." + RESET); continue; }
                return chosen;
            } catch (NumberFormatException e) {
                System.out.println(RED + "  Inserisci un numero valido (o 0 per annullare)." + RESET);
            }
        }
    }

    void showExperimentResult(Ingredient i1, Ingredient i2, Potion potion) {
        System.out.println("  " + MAGENTA + BOLD + "  " + i1.getName() + " + " + i2.getName() + RESET);
        System.out.println("  ─────────────────────────────────");
        if (potion.isNeutral()) {
            System.out.println("  Pozione prodotta: " + DIM + BOLD + "NEUTRALE" + RESET);
            System.out.println("  Effetto:          " + DIM + "nessun effetto magico" + RESET);
        } else {
            String pc = potion.isNegative() ? RED : GREEN;
            System.out.println("  Pozione prodotta: " + pc + BOLD + potion.getColor().name() + " " + potion.getSign().name() + RESET);
            System.out.println("  Effetto:          " + (potion.isNegative() ? RED + "NEGATIVO ✗" : GREEN + "POSITIVO ✓") + RESET);
        }
    }

    void showStudentEffect(StudentState state) {
        System.out.println("  Stato Student:    " + BOLD + state + RESET);
    }

    void showPlayerEffect(int reputation) {
        System.out.println("  Tua reputazione:  " + BOLD + reputation + RESET);
    }

    // ── Views ─────────────────────────────────────────────────────────────────

    void showLaboratorio(List<Ingredient> ings, Map<Set<Ingredient>, Potion> triangle, List<String> exclusions) {
        System.out.println("  " + CYAN + "Ingredienti disponibili:" + RESET);
        if (ings.isEmpty()) System.out.println("  " + DIM + "(nessuno)" + RESET);
        else ings.forEach(i -> System.out.println("    • " + i.getName()));

        System.out.println("\n  " + CYAN + "Triangolo dei risultati:" + RESET);
        if (triangle.isEmpty()) System.out.println("  " + DIM + "(nessun esperimento ancora)" + RESET);
        else triangle.forEach((pair, potion) -> {
            List<String> names = pair.stream().map(Ingredient::getName).sorted().toList();
            String label = potion.isNeutral()
                    ? DIM + "NEUTRALE" + RESET
                    : (potion.isNegative() ? RED : GREEN) + BOLD + potion.getColor() + " " + potion.getSign() + RESET;
            System.out.println("    • " + names.get(0) + " + " + names.get(1) + " → " + label);
        });

        System.out.println("\n  " + CYAN + "DeductionGrid — alchemici esclusi per ingrediente:" + RESET);
        if (exclusions.isEmpty()) System.out.println("  " + DIM + "(nessuna esclusione ancora)" + RESET);
        else exclusions.forEach(a -> System.out.println("    • " + a));
    }

    void showTabellone(List<Potion> results) {
        if (results.isEmpty()) System.out.println("  " + DIM + "(nessun risultato ancora)" + RESET);
        else for (int i = 0; i < results.size(); i++) {
            Potion p = results.get(i);
            String label = p.isNeutral()
                    ? DIM + "NEUTRALE" + RESET
                    : (p.isNegative() ? RED : GREEN) + p.getColor() + " " + p.getSign() + RESET;
            System.out.println("  [" + (i + 1) + "] " + label);
        }
    }

    void showTargetStatus(StudentState studentState, int gold, int reputation) {
        System.out.println("  " + CYAN + "Student:" + RESET);
        String sc = studentState == StudentState.HAPPY ? GREEN : RED;
        System.out.println("    Stato → " + sc + BOLD + studentState + RESET);

        System.out.println("\n  " + CYAN + "Tu stesso:" + RESET);
        System.out.println("    Oro         → " + BOLD + gold + RESET);
        System.out.println("    Reputazione → " + BOLD + reputation + RESET);
    }
}
