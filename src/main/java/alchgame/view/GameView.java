package alchgame.view;

import alchgame.view.viewmodel.*;

import java.util.List;
import java.util.Scanner;

public class GameView {

    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String DIM = "\033[2m";

    private static final String CYAN = "\033[36m";
    private static final String YELLOW = "\033[33m";
    private static final String GREEN = "\033[32m";
    private static final String RED = "\033[31m";
    private static final String MAGENTA = "\033[35m";

    private static final int DEDUCTION_NAME_WIDTH = 14;
    private static final int DEDUCTION_CELL_WIDTH = 5;

    private final Scanner scanner = new Scanner(System.in);

    // ---------------------------------------------------------------------
    // Layout generale
    // ---------------------------------------------------------------------

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void pause(String message) {
        if (!message.isEmpty()) {
            System.out.println(message);
        }
        scanner.nextLine();
    }

    public void printHeader() {
        System.out.println(MAGENTA + BOLD);
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║      ✦   Alchimisti Anonimi  ✦       ║");
        System.out.println(". ╚══════════════════════════════════════╝" + RESET);
    }

    public void printSection(String title) {
        System.out.println(CYAN + BOLD + "  ══ " + title + " ══" + RESET + "\n");
    }

    public void printStatus(int gold, int reputation, int experiments, int ingredients) {
        System.out.println(
            "  " + DIM + "Oro: " + RESET + YELLOW + BOLD + gold + RESET +
            DIM + "  |  Reputazione: " + RESET + BOLD + reputation + RESET +
            DIM + "  |  Esperimenti: " + RESET + BOLD + experiments + RESET +
            DIM + "  |  Ingredienti: " + RESET + BOLD + ingredients + RESET
        );
        System.out.println();
    }

    public void showError(String message) {
        System.out.println(RED + "  ✗ " + message + RESET);
        scanner.nextLine();
    }

    public void showGoodbye() {
        System.out.println(GREEN + "\n  Arrivederci!\n" + RESET);
    }

    // ---------------------------------------------------------------------
    // Setup partita
    // ---------------------------------------------------------------------

    public int askPlayerCount(int min, int max) {
        while (true) {
            Integer value = askInteger("  Quanti giocatori? (" + min + "-" + max + ") > ");
            if (value != null && value >= min && value <= max) {
                return value;
            }
            printInputError("Inserisci un numero tra " + min + " e " + max + ".");
        }
    }

    public String askPlayerName(int playerNumber) {
        while (true) {
            String name = askText("  Nome giocatore " + playerNumber + " > ");
            if (!name.isEmpty()) {
                return name;
            }
            printInputError("Il nome non può essere vuoto.");
        }
    }

    // ---------------------------------------------------------------------
    // Menu legacy
    // ---------------------------------------------------------------------

    public int showMainMenu() {
        printBoxHeader("COSA VUOI FARE?");
        printMenuRow("1", "Inizia Esperimento");
        printMenuRow("2", "Vedi Laboratorio Privato");
        printMenuRow("3", "Vedi Tabellone Pubblico");
        printMenuRow("4", "Vedi Stato Target");
        printMenuRow("0", "Esci");
        printBoxFooter();

        return switch (askText("\n  Scelta > ")) {
            case "0" -> 0;
            case "1" -> 1;
            case "2" -> 2;
            case "3" -> 3;
            case "4" -> 4;
            default -> -1;
        };
    }

    // ---------------------------------------------------------------------
    // Fase ordine
    // ---------------------------------------------------------------------

    public void showRoundStart(int round, int total) {
        System.out.println("\n" + MAGENTA + BOLD + "  ══════════════════════════════");
        System.out.println("       ROUND " + round + " / " + total);
        System.out.println("  ══════════════════════════════" + RESET + "\n");
    }

    public void showOrderTurn(String playerName) {
        System.out.println("\n  " + YELLOW + BOLD + "▶ " + playerName + RESET + " — scegli il tuo slot:");
    }

    public String askSlotChoice(List<OrderSlotView> availableSlots) {
        printAvailableSlots(availableSlots);

        while (true) {
            Integer choice = askInteger("  Scelta > ");
            if (choice != null && choice >= 1 && choice <= availableSlots.size()) {
                return availableSlots.get(choice - 1).id();
            }
            printInputError("Scelta non valida.");
        }
    }

    public void showSlotAssigned(SlotAssignmentView assignment) {
        System.out.println(
            "  " + GREEN + "✓ " + assignment.playerName() + " → " + assignment.slotId() +
            "  " + resourceGain(assignment.resources()) + RESET
        );
    }

    public void showWakeUpOrder(List<String> playerNames) {
        System.out.println("\n  " + CYAN + "Ordine di risveglio:" + RESET);
        for (int i = 0; i < playerNames.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + BOLD + playerNames.get(i) + RESET);
        }
    }

    // ---------------------------------------------------------------------
    // Fase dichiarazione
    // ---------------------------------------------------------------------

    public String askActionDeclaration(String playerName, List<String> actionIds, int cubesLeft) {
        System.out.println(
            "\n  " + YELLOW + BOLD + "▶ " + playerName + RESET +
            "  (cubi rimasti: " + BOLD + cubesLeft + RESET + ") — dichiara un'azione:"
        );

        printNumberedOptions(actionIds);
        System.out.println("  " + DIM + "[0] Passa" + RESET);

        Integer choice = askInteger("  Scelta > ");
        if (choice == null || choice < 1 || choice > actionIds.size()) {
            return null;
        }
        return actionIds.get(choice - 1);
    }

    // ---------------------------------------------------------------------
    // Fase risoluzione
    // ---------------------------------------------------------------------

    public void showResolutionStart(String actionSpaceId) {
        System.out.println("\n  " + CYAN + BOLD + "  → Risoluzione: " + actionSpaceId + RESET);
    }

    public void showResolvingPlayer(String playerName, String actionSpaceId) {
        System.out.println("  " + DIM + "  " + playerName + " esegue " + actionSpaceId + "..." + RESET);
    }

    public void showRoundEnd(int round) {
        System.out.println("\n  " + DIM + "Fine Round " + round + "." + RESET);
        pause("  Premi INVIO per continuare...");
    }

    // ---------------------------------------------------------------------
    // Esperimento
    // ---------------------------------------------------------------------

    public Integer askTargetChoice() {
        System.out.println("  Scegli il target:");
        System.out.println(menuOption("1", "Studente") + "     " + muted("(gratuito)"));
        System.out.println(menuOption("2", "Su Te Stesso") + " " + muted("(gratuito)"));
        System.out.println(menuOption("0", "Annulla"));

        return switch (askText("\n  Scelta > ")) {
            case "1" -> 1;
            case "2" -> 2;
            default -> null;
        };
    }

    public boolean askPaymentConfirm(int currentGold) {
        System.out.println("\n  " + YELLOW + "⚠  Questo target richiede 1 moneta d'oro." + RESET);
        System.out.println("  Oro attuale: " + BOLD + currentGold + RESET);
        return askText("  Vuoi pagare? [s/n] > ").equalsIgnoreCase("s");
    }

    public void showPaymentSuccess(int remainingGold) {
        System.out.println(success("Pagamento effettuato. Oro rimasto: " + remainingGold));
    }

    public void showIngredients(List<String> ingredientNames) {
        System.out.println("\n  " + CYAN + "Ingredienti disponibili:" + RESET);
        for (int i = 0; i < ingredientNames.size(); i++) {
            System.out.println("  " + optionLabel(i + 1) + " " + ingredientNames.get(i));
        }
    }

    public Integer pickIngredient(List<String> ingredientNames, String prompt, Integer excludedIndex) {
        while (true) {
            Integer choice = askIntegerRaw(prompt);
            if (choice == null) {
                printInputError("Inserisci un numero valido (o 0 per annullare).");
                continue;
            }
            if (choice == 0) {
                return null;
            }
            if (choice < 1 || choice > ingredientNames.size()) {
                printInputError("Inserisci un numero valido (o 0 per annullare).");
                continue;
            }

            int chosenIndex = choice - 1;
            if (excludedIndex != null && chosenIndex == excludedIndex) {
                printInputError("Non puoi scegliere lo stesso ingrediente due volte.");
                continue;
            }
            return chosenIndex;
        }
    }

    public void showExperimentResult(ExperimentResultView result) {
        System.out.println(
            "  " + MAGENTA + BOLD + "  " +
            result.firstIngredientName() + " + " + result.secondIngredientName() + RESET
        );
        System.out.println("  ─────────────────────────────────");

        if (result.potion().neutral()) {
            System.out.println("  Pozione prodotta: " + mutedBold("NEUTRALE"));
            System.out.println("  Effetto:          " + muted("nessun effetto magico"));
            return;
        }

        System.out.println("  Pozione prodotta: " + potionLabel(result.potion()));
        System.out.println("  Effetto:          " + effectLabel(result.potion()));
    }

    public void showStudentEffect(String state) {
        System.out.println("  Stato Student:    " + BOLD + state + RESET);
    }

    public void showPlayerEffect(int reputation) {
        System.out.println("  Tua reputazione:  " + BOLD + reputation + RESET);
    }

    // ---------------------------------------------------------------------
    // Laboratorio, tabellone e target
    // ---------------------------------------------------------------------

    public void showLaboratorio(LaboratoryView laboratory) {
        printLaboratoryIngredients(laboratory.ingredientNames());
        printResultsTriangle(laboratory.experimentResults());

        System.out.println("\n  " + CYAN + "Griglia di deduzione:" + RESET);
        showDeductionGrid(laboratory.deductionGrid());
    }

    public void showTabellone(List<PotionView> results) {
        if (results.isEmpty()) {
            System.out.println("  " + DIM + "(nessun risultato ancora)" + RESET);
            return;
        }

        for (int i = 0; i < results.size(); i++) {
            System.out.println("  [" + (i + 1) + "] " + potionLabel(results.get(i)));
        }
    }

    public void showTargetStatus(TargetStatusView status) {
        String studentColor = status.studentHappy() ? GREEN : RED;

        System.out.println("  " + CYAN + "Student:" + RESET);
        System.out.println("    Stato → " + studentColor + BOLD + status.studentState() + RESET);

        System.out.println("\n  " + CYAN + "Tu stesso:" + RESET);
        System.out.println("    Oro         → " + BOLD + status.gold() + RESET);
        System.out.println("    Reputazione → " + BOLD + status.reputation() + RESET);
    }

    // ---------------------------------------------------------------------
    // Deduzione
    // ---------------------------------------------------------------------

    public void showDeductionGrid(DeductionGridView grid) {
        printDeductionHeader(grid.alchemicLabels().size());
        printDeductionRows(grid);
        printFormulaLegend(grid.alchemicLabels());
    }

    public boolean askDeductionConfirm() {
        return askText(CYAN + "\n  Vuoi aggiornare la griglia di deduzione? [s/n] > " + RESET)
            .equalsIgnoreCase("s");
    }

    public int askIngredientIndex(int max) {
        return askIndexOrCancel("  Ingrediente (1-" + max + ", 0=annulla) > ", max);
    }

    public int askAlchemicIndex(int max) {
        return askIndexOrCancel("  Alchemico da escludere (1-" + max + ", 0=annulla) > ", max);
    }

    public void showDeductionSuccess(String ingredientName, int alchemicIndex) {
        System.out.println(success("Alchemico [" + alchemicIndex + "] escluso per " + ingredientName + "."));
    }

    // ---------------------------------------------------------------------
    // Helper di stampa: round e menu
    // ---------------------------------------------------------------------

    private void printAvailableSlots(List<OrderSlotView> availableSlots) {
        for (int i = 0; i < availableSlots.size(); i++) {
            OrderSlotView slot = availableSlots.get(i);
            System.out.println("  " + optionLabel(i + 1) + " " + slot.id() + "  " + muted(resourceGain(slot.resources())));
        }
    }

    private String resourceGain(ResourceGainView resources) {
        return "(+" + resources.ingredientCount() + " ing, +" + resources.favorCount() + " fav)";
    }

    private void printNumberedOptions(List<String> options) {
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + optionLabel(i + 1) + " " + options.get(i));
        }
    }

    private void printBoxHeader(String title) {
        System.out.println(CYAN + BOLD + "  ╔══════════════════════════════╗" + RESET);
        System.out.printf("%s  ║%s       %-22s%s║%s%n", CYAN + BOLD, RESET, title, CYAN + BOLD, RESET);
        System.out.println(CYAN + BOLD + "  ╠══════════════════════════════╣" + RESET);
    }

    private void printMenuRow(String key, String label) {
        String coloredKey = key.equals("0") ? RED + "[" + key + "]" + RESET : YELLOW + "[" + key + "]" + RESET;
        System.out.printf("%s  ║ %s %-23s%s║%s%n", CYAN + BOLD, coloredKey, label, CYAN + BOLD, RESET);
    }

    private void printBoxFooter() {
        System.out.println(CYAN + BOLD + "  ╚══════════════════════════════╝" + RESET);
    }

    // ---------------------------------------------------------------------
    // Helper di stampa: laboratorio e risultati
    // ---------------------------------------------------------------------

    private void printLaboratoryIngredients(List<String> ingredientNames) {
        System.out.println("  " + CYAN + "Ingredienti disponibili:" + RESET);

        if (ingredientNames.isEmpty()) {
            System.out.println("  " + DIM + "(nessuno)" + RESET);
            return;
        }

        ingredientNames.forEach(name -> System.out.println("    • " + name));
    }

    private void printResultsTriangle(List<ExperimentResultView> results) {
        System.out.println("\n  " + CYAN + "Triangolo dei risultati:" + RESET);

        if (results.isEmpty()) {
            System.out.println("  " + DIM + "(nessun esperimento ancora)" + RESET);
            return;
        }

        results.forEach(result -> System.out.println(
            "    • " + result.firstIngredientName() + " + " + result.secondIngredientName() +
            " → " + potionLabel(result.potion())
        ));
    }

    private String potionLabel(PotionView potion) {
        if (potion.neutral()) {
            return mutedBold("NEUTRALE");
        }

        String color = potion.negative() ? RED : GREEN;
        return color + BOLD + potion.label() + RESET;
    }

    private String effectLabel(PotionView potion) {
        if (potion.negative()) {
            return RED + "NEGATIVO ✗" + RESET;
        }
        return GREEN + "POSITIVO ✓" + RESET;
    }

    // ---------------------------------------------------------------------
    // Helper di stampa: griglia deduzione
    // ---------------------------------------------------------------------

    private void printDeductionHeader(int alchemicCount) {
        System.out.print("  " + padRight("", DEDUCTION_NAME_WIDTH) + "|");
        for (int i = 0; i < alchemicCount; i++) {
            System.out.print(CYAN + center("[" + (i + 1) + "]", DEDUCTION_CELL_WIDTH) + RESET + "|");
        }
        System.out.println();

        System.out.print("  " + padRight("", DEDUCTION_NAME_WIDTH) + "+");
        for (int i = 0; i < alchemicCount; i++) {
            System.out.print("-".repeat(DEDUCTION_CELL_WIDTH) + "+");
        }
        System.out.println();
    }

    private void printDeductionRows(DeductionGridView grid) {
        for (int ingredientIndex = 0; ingredientIndex < grid.ingredientNames().size(); ingredientIndex++) {
            System.out.print(
                "  " + BOLD + padRight(grid.ingredientNames().get(ingredientIndex), DEDUCTION_NAME_WIDTH) + RESET + "|"
            );
            for (int alchemicIndex = 0; alchemicIndex < grid.alchemicLabels().size(); alchemicIndex++) {
                System.out.print(deductionCell(grid.excluded().get(ingredientIndex).get(alchemicIndex)) + "|");
            }
            System.out.println();
        }
    }

    private String deductionCell(boolean excluded) {
        String value = excluded ? "X" : "·";
        String color = excluded ? RED : DIM;
        return color + center(value, DEDUCTION_CELL_WIDTH) + RESET;
    }

    private void printFormulaLegend(List<String> alchemicLabels) {
        System.out.println("\n  " + DIM + "Legenda alchemici:" + RESET);
        for (int i = 0; i < alchemicLabels.size(); i++) {
            System.out.println("  " + optionLabel(i + 1) + " " + alchemicLabels.get(i));
        }
    }

    // ---------------------------------------------------------------------
    // Helper di input
    // ---------------------------------------------------------------------

    private String askText(String prompt) {
        System.out.print(BOLD + prompt + RESET);
        return scanner.nextLine().trim();
    }

    private Integer askInteger(String prompt) {
        return parseInteger(askText(prompt));
    }

    private Integer askIntegerRaw(String prompt) {
        System.out.print(BOLD + prompt + RESET);
        return parseInteger(scanner.nextLine().trim());
    }

    private Integer parseInteger(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int askIndexOrCancel(String prompt, int max) {
        Integer value = askInteger(prompt);
        if (value == null || value < 0 || value > max) {
            return -1;
        }
        return value;
    }

    private void printInputError(String message) {
        System.out.println(RED + "  " + message + RESET);
    }

    // ---------------------------------------------------------------------
    // Helper di formattazione
    // ---------------------------------------------------------------------

    private String optionLabel(int index) {
        return YELLOW + "[" + index + "]" + RESET;
    }

    private String menuOption(String key, String label) {
        return "  " + YELLOW + "[" + key + "]" + RESET + " " + label;
    }

    private String success(String message) {
        return "  " + GREEN + "✓ " + message + RESET;
    }

    private String muted(String text) {
        return DIM + text + RESET;
    }

    private String mutedBold(String text) {
        return DIM + BOLD + text + RESET;
    }

    private String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }

    private String center(String text, int width) {
        int padding = width - text.length();
        int left = padding / 2;
        return " ".repeat(left) + text + " ".repeat(padding - left);
    }
}
