package alchgame.presentation;

import alchgame.model.alchemy.*;
import alchgame.model.board.Favor;
import alchgame.model.board.Resources;
import alchgame.model.player.DeductionGrid;
import alchgame.model.player.Player;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GameView {

    // --- ANSI palette ---------------------------------------------------------
    private static final String RESET  = "[0m";
    private static final String BOLD   = "[1m";
    private static final String DIM    = "[2m";
    private static final String RED    = "[31m";
    private static final String GREEN  = "[32m";
    private static final String YELLOW = "[33m";
    private static final String BLUE   = "[34m";
    private static final String MAGENTA= "[35m";
    private static final String CYAN   = "[36m";
    private static final String WHITE  = "[97m";

    private static final int WIDTH = 64;
    private static final String PAD = "  "; // 2-space left indent

    // --- Animation timings ---
    private static final int CHAR_MS        = 15;  // typewriter per char
    private static final int LINE_MS        = 35;  // small inter-line reveal
    private static final int BANNER_LINE_MS = 80;  // dramatic banners (welcome, round, game over)

    private final Scanner scanner;
    private final PrintStream out;

    public GameView() {
        this(new Scanner(System.in), System.out);
    }

    public GameView(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    // --- Setup ---------------------------------------------------------------

    public void showWelcome() {
        String top   = "╔" + "═".repeat(WIDTH - 2) + "╗";
        String mid   = "║" + " ".repeat(WIDTH - 2) + "║";
        String bot   = "╚" + "═".repeat(WIDTH - 2) + "╝";
        out.println();
        revealLine(PAD + CYAN + BOLD + top + RESET, BANNER_LINE_MS);
        revealLine(PAD + CYAN + BOLD + mid + RESET, BANNER_LINE_MS);
        revealLine(PAD + CYAN + BOLD + "║" + center("A L C H I M I S T I   A N O N I M I", WIDTH - 2) + "║" + RESET, BANNER_LINE_MS);
        revealLine(PAD + CYAN +        "║" + center("un gioco di deduzione alchemica",     WIDTH - 2) + "║" + RESET, BANNER_LINE_MS);
        revealLine(PAD + CYAN + BOLD + mid + RESET, BANNER_LINE_MS);
        revealLine(PAD + CYAN + BOLD + bot + RESET, BANNER_LINE_MS);
        out.println();
    }

    public void promptPlayerCount(int min, int max) {
        out.printf("%s%s Quanti giocatori? %s(%d–%d)%s: ", PAD, "›", DIM, min, max, RESET);
    }

    public void promptPlayerName(int playerNumber) {
        out.printf("%s%s Nome giocatore %s%d%s: ", PAD, "›", BOLD, playerNumber, RESET);
    }

    public void showInvalidInput(String message) {
        out.println(PAD + RED + "⚠  " + message + RESET);
    }

    // --- Round / Phase -------------------------------------------------------

    public void showRoundStart(int current, int total) {
        String bar = "━".repeat(WIDTH);
        out.println();
        revealLine(PAD + YELLOW + BOLD + bar + RESET, BANNER_LINE_MS);
        revealLine(PAD + YELLOW + BOLD + center(String.format("ROUND  %d / %d", current, total), WIDTH) + RESET, BANNER_LINE_MS);
        revealLine(PAD + YELLOW + BOLD + bar + RESET, BANNER_LINE_MS);
    }

    public void showRoundEnd(int roundNumber) {
        out.println();
        String text = String.format(" fine round %d ", roundNumber);
        int side = (WIDTH - text.length()) / 2;
        out.println(PAD + DIM + "─".repeat(side) + text + "─".repeat(WIDTH - side - text.length()) + RESET);
    }

    public void showPhaseHeader(String phaseName) {
        String top  = "┌" + "─".repeat(WIDTH - 2) + "┐";
        String bot  = "└" + "─".repeat(WIDTH - 2) + "┘";
        String body = "│" + padRight("  ◆  " + phaseName, WIDTH - 2) + "│";
        out.println();
        revealLine(PAD + CYAN + top  + RESET, LINE_MS);
        revealLine(PAD + CYAN + BOLD + body + RESET, LINE_MS);
        revealLine(PAD + CYAN + bot  + RESET, LINE_MS);
    }

    public void showCurrentPlayer(String name) {
        out.println();
        typewrite(PAD + WHITE + BOLD + "▸ Turno di " + name.toUpperCase() + RESET);
        out.println(PAD + DIM + "─".repeat(WIDTH) + RESET);
    }

    public void showPlayerStatus(int gold, int reputation, int actionCubes) {
        out.printf("%s   %s💰 %2d oro%s   %s⭐ %2d rep%s   %s🎲 %d cubi%s%n",
                PAD,
                YELLOW,  gold,        RESET,
                CYAN,    reputation,  RESET,
                MAGENTA, actionCubes, RESET);
    }

    public void showFirstPlayer(String name) {
        out.println();
        out.println(PAD + YELLOW + "★ Primo giocatore: " + BOLD + name + RESET);
    }

    public void showPhaseOrder(String label, List<String> playerNames) {
        out.printf("%s   %s↳ Ordine %s:%s %s%n",
                PAD, DIM, label, RESET,
                String.join(DIM + " → " + RESET, playerNames));
    }

    public void showBoard(Map<String, Player> slots,
                          List<String> actionIds,
                          Map<String, List<String>> declarantsByAction) {
        final int BW = 78;          // board outer width
        final int IW = BW - 2;      // inner content width of the outer box

        String horizTop = "╔" + "═".repeat(IW) + "╗";
        String horizMid = "╠" + "═".repeat(IW) + "╣";
        String horizBot = "╚" + "═".repeat(IW) + "╝";
        String empty    = "║" + " ".repeat(IW) + "║";

        out.println();
        revealLine(PAD + CYAN + horizTop + RESET, LINE_MS);
        String title = "  ⚗  TABELLONE";
        revealLine(PAD + CYAN + "║" + BOLD + padRight(title, IW) + RESET + CYAN + "║" + RESET, LINE_MS);
        revealLine(PAD + CYAN + horizMid + RESET, LINE_MS);
        out.println(PAD + CYAN + empty + RESET);

        // --- Wake-up track box, centered
        final int WB_W = 40;
        final int wbInner = WB_W - 2;
        String wbTitle = " TRACCIATO DI RISVEGLIO ";
        int wbSides = (wbInner - wbTitle.length()) / 2;
        String wbTop = "┌" + "─".repeat(wbSides) + wbTitle + "─".repeat(wbInner - wbSides - wbTitle.length()) + "┐";
        String wbBot = "└" + "─".repeat(wbInner) + "┘";

        int wbLPad = (IW - WB_W) / 2;
        String wbLeft  = " ".repeat(wbLPad);
        String wbRight = " ".repeat(IW - wbLPad - WB_W);

        revealLine(PAD + CYAN + "║" + RESET + wbLeft + DIM + wbTop + RESET + wbRight + CYAN + "║" + RESET, LINE_MS);
        for (Map.Entry<String, Player> e : slots.entrySet()) {
            boolean taken = e.getValue() != null;
            String mark  = taken ? (GREEN + "◉" + RESET) : (DIM + "○" + RESET);
            String name  = taken ? truncate(e.getValue().getName(), 20) : "―";
            String body  = String.format(" %s  %-8s  %s", mark, e.getKey(), name);
            int visible  = 1 + 1 + 1 + 2 + 8 + 2 + (taken ? name.length() : 1);
            String row   = "│" + body + " ".repeat(Math.max(0, wbInner - visible)) + "│";
            revealLine(PAD + CYAN + "║" + RESET + wbLeft + row + wbRight + CYAN + "║" + RESET, LINE_MS);
        }
        revealLine(PAD + CYAN + "║" + RESET + wbLeft + DIM + wbBot + RESET + wbRight + CYAN + "║" + RESET, LINE_MS);
        out.println(PAD + CYAN + empty + RESET);

        // --- Action spaces, in a row in ACTION_ORDER (forage → transmute → experiment)
        final int AB_W   = 22;
        final int AB_GAP = 2;
        int totalA = AB_W * actionIds.size() + AB_GAP * Math.max(0, actionIds.size() - 1);
        int aLPad  = (IW - totalA) / 2;
        String aLeft  = " ".repeat(aLPad);
        String aRight = " ".repeat(IW - aLPad - totalA);

        int maxDecls = 0;
        for (String id : actionIds) {
            maxDecls = Math.max(maxDecls, declarantsByAction.getOrDefault(id, List.of()).size());
        }
        int contentRows = Math.max(1, maxDecls);

        StringBuilder topL = new StringBuilder();
        StringBuilder titleL = new StringBuilder();
        java.util.List<StringBuilder> contentLs = new java.util.ArrayList<>();
        for (int r = 0; r < contentRows; r++) contentLs.add(new StringBuilder());
        StringBuilder botL = new StringBuilder();

        for (int i = 0; i < actionIds.size(); i++) {
            if (i > 0) {
                String gap = " ".repeat(AB_GAP);
                topL.append(gap);
                titleL.append(gap);
                for (StringBuilder cl : contentLs) cl.append(gap);
                botL.append(gap);
            }
            String id    = actionIds.get(i);
            String icon  = actionIcon(id);
            String label = " " + icon + " " + id.toUpperCase();
            List<String> decls = declarantsByAction.getOrDefault(id, List.of());

            topL.append("┌").append("─".repeat(AB_W - 2)).append("┐");
            titleL.append("│").append(BOLD).append(padRight(label, AB_W - 2)).append(RESET).append("│");
            for (int r = 0; r < contentRows; r++) {
                String text;
                if (decls.isEmpty()) {
                    text = r == 0 ? " ―" : "";
                } else if (r < decls.size()) {
                    text = " 🎲 " + truncate(decls.get(r), AB_W - 6);
                } else {
                    text = "";
                }
                contentLs.get(r).append("│").append(DIM).append(padRight(text, AB_W - 2)).append(RESET).append("│");
            }
            botL.append("└").append("─".repeat(AB_W - 2)).append("┘");
        }

        revealLine(PAD + CYAN + "║" + RESET + aLeft + topL   + aRight + CYAN + "║" + RESET, LINE_MS);
        revealLine(PAD + CYAN + "║" + RESET + aLeft + titleL + aRight + CYAN + "║" + RESET, LINE_MS);
        for (StringBuilder cl : contentLs) {
            revealLine(PAD + CYAN + "║" + RESET + aLeft + cl + aRight + CYAN + "║" + RESET, LINE_MS);
        }
        revealLine(PAD + CYAN + "║" + RESET + aLeft + botL   + aRight + CYAN + "║" + RESET, LINE_MS);

        out.println(PAD + CYAN + empty + RESET);
        out.println(PAD + CYAN + horizBot + RESET);
    }

    private String actionIcon(String id) {
        return switch (id) {
            case "forage"     -> "❀";
            case "transmute"  -> "⚗";
            case "experiment" -> "⚛";
            default           -> "•";
        };
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 1)) + "…";
    }

    public void showFavors(List<Favor> favors) {
        out.println(PAD + "   " + BLUE + "✦ Carte favore:" + RESET);
        if (favors.isEmpty()) {
            out.println(PAD + "     " + DIM + "(nessuna carta favore)" + RESET);
            return;
        }
        for (int i = 0; i < favors.size(); i++) {
            out.printf("%s     %s[%d]%s %s%n",
                    PAD, DIM, i + 1, RESET, favors.get(i).getName());
        }
    }

    // --- ORDER phase ---------------------------------------------------------

    public void showAvailableSlots(List<String> slotIds) {
        out.println(PAD + "   " + "Scegli uno slot:");
        for (int i = 0; i < slotIds.size(); i++) {
            out.printf("%s     %s[%d]%s %s%n",
                    PAD, DIM, i + 1, RESET, slotIds.get(i));
        }
    }

    public void showOrderAssignments(Map<String, Player> assignments) {
        out.println(PAD + "   " + "Stato tracciato:");
        for (Map.Entry<String, Player> e : assignments.entrySet()) {
            String who = e.getValue() == null
                    ? DIM + "libero" + RESET
                    : BOLD + e.getValue().getName() + RESET;
            out.printf("%s     %s%s%s  →  %s%n",
                    PAD, CYAN, e.getKey(), RESET, who);
        }
    }

    public void showWakeUpOrder(List<String> playerNames) {
        out.println();
        out.println(PAD + GREEN + BOLD + "⇒ Wake-up order definitivo" + RESET);
        out.println(PAD + "  " + String.join(DIM + "  →  " + RESET, playerNames));
    }

    public void showResolutionStep(int stepIndex, int totalSteps, String actionId, String playerName) {
        out.println();
        typewrite(String.format("%s%s► [%d/%d]%s  azione: %s%s%s  ·  giocatore: %s%s%s",
                PAD, CYAN + BOLD, stepIndex, totalSteps, RESET,
                YELLOW + BOLD, actionId, RESET,
                WHITE  + BOLD, playerName, RESET));
        out.println(PAD + DIM + "─".repeat(WIDTH) + RESET);
    }

    public int promptSlotChoice(int maxIndex) {
        return promptBoundedInt("   ▸ Scegli slot", maxIndex);
    }

    public void showSlotChoiceResult(String slotId, Resources res) {
        out.printf("%s%s ✔ Slot %s%s%s → +%d ingredienti, +%d favori%s%n",
                PAD, GREEN, BOLD, slotId, GREEN, res.ingredientCount(), res.favorCount(), RESET);
    }

    // --- DECLARATION phase ---------------------------------------------------

    public void showActionList(List<String> actionIds) {
        out.println(PAD + "   " + "Azioni disponibili:");
        for (int i = 0; i < actionIds.size(); i++) {
            out.printf("%s     %s[%d]%s %s%n",
                    PAD, DIM, i + 1, RESET, actionIds.get(i));
        }
    }

    public void showActionListWithPass(List<String> actionIds) {
        out.println(PAD + "   " + "Azioni disponibili:");
        for (int i = 0; i < actionIds.size(); i++) {
            out.printf("%s     %s[%d]%s %s%s%s%n",
                    PAD, DIM, i + 1, RESET, BOLD, actionIds.get(i), RESET);
        }
        out.printf("%s     %s[0]%s %sPassa%s%n", PAD, DIM, RESET, DIM, RESET);
    }

    public int promptActionChoice(int maxIndex) {
        return promptBoundedInt("   ▸ Scegli azione", maxIndex);
    }

    public int promptActionOrPass(int maxIndex) {
        while (true) {
            out.printf("%s   ▸ Scelta %s(0-%d)%s: ", PAD, DIM, maxIndex, RESET);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 0 && choice <= maxIndex) return choice;
                out.printf("%s%s⚠  Inserisci un valore tra 0 e %d.%s%n", PAD, RED, maxIndex, RESET);
            } catch (NumberFormatException e) {
                out.printf("%s%s⚠  Inserisci un numero tra 0 e %d.%s%n", PAD, RED, maxIndex, RESET);
            }
        }
    }

    public void showDeclaredAction(String playerName, String actionId) {
        typewrite(String.format("%s%s ✔ %s%s%s → %s%s%s",
                PAD, GREEN, BOLD, playerName, GREEN, BOLD, actionId, RESET));
    }

    // --- EXPERIMENT ----------------------------------------------------------

    public void showTargetOptions(List<String> targetIds) {
        out.println(PAD + "   " + "Bersaglio dell'esperimento:");
        for (int i = 0; i < targetIds.size(); i++) {
            out.printf("%s     %s[%d]%s %s%n",
                    PAD, DIM, i + 1, RESET, targetIds.get(i));
        }
    }

    public String promptTargetChoice(List<String> targetIds) {
        while (true) {
            out.printf("%s   ▸ Scelta %s(1-%d)%s: ", PAD, DIM, targetIds.size(), RESET);
            String line = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(line) - 1;
                if (idx >= 0 && idx < targetIds.size()) return targetIds.get(idx);
            } catch (NumberFormatException ignored) {}
            out.printf("%s%s⚠  Inserisci un numero tra 1 e %d.%s%n", PAD, RED, targetIds.size(), RESET);
        }
    }

    public void showPaymentRequired() {
        out.println(PAD + YELLOW + "⚠  Lo studente è scontento — verrà addebitato 1 oro." + RESET);
    }

    public void showPaymentResult(int remainingGold) {
        out.printf("%s   %sOro rimasto: %s%d%s%n", PAD, DIM, YELLOW + BOLD, remainingGold, RESET);
    }

    public void showInsufficientGold() {
        out.println(PAD + RED + "✖ Oro insufficiente per condurre l'esperimento sullo studente." + RESET);
    }

    public void showIngredients(List<Ingredient> ingredients) {
        out.println(PAD + "   " + GREEN + "❀ Ingredienti nel laboratorio:" + RESET);
        if (ingredients.isEmpty()) {
            out.println(PAD + "     " + DIM + "(nessun ingrediente)" + RESET);
            return;
        }
        for (int i = 0; i < ingredients.size(); i++) {
            out.printf("%s     %s[%d]%s %s%n",
                    PAD, DIM, i + 1, RESET, ingredients.get(i).getName());
        }
    }

    public int promptIngredientChoice(String prompt, int maxIndex) {
        return promptBoundedInt("   ▸ " + prompt, maxIndex);
    }

    public void showPotionResult(Potion potion) {
        String label = potion.isNeutral()
                ? "NEUTRA"
                : potion.getColor().name() + " " + potion.getSign().name();

        String tint;
        if (potion.isNeutral()) tint = DIM;
        else tint = switch (potion.getColor()) {
            case RED   -> RED;
            case GREEN -> GREEN;
            case BLUE  -> BLUE;
            default    -> WHITE;
        };

        int innerW = WIDTH - 4;
        String top = "╔" + "═".repeat(innerW) + "╗";
        String mid = "║" + center("⚗  POZIONE: " + label + "  ⚗", innerW) + "║";
        String bot = "╚" + "═".repeat(innerW) + "╝";

        out.println();
        revealLine(PAD + tint + BOLD + top + RESET, LINE_MS);
        typewrite(PAD + tint + BOLD + mid + RESET);
        revealLine(PAD + tint + BOLD + bot + RESET, LINE_MS);
    }

    public boolean promptUpdateDeductionGrid() {
        out.printf("%s   ▸ Aggiornare la griglia di deduzione? %s(s/n)%s: ", PAD, DIM, RESET);
        return readYesNo();
    }

    public void showDeductionGrid(DeductionGrid grid) {
        List<Ingredient>      ingredients = grid.getIngredients();
        List<AlchemicFormula> alchemics   = grid.getAlchemics();

        final int COL   = 9;
        final int LABEL = 26;

        out.println();
        out.println(PAD + CYAN + BOLD + "▣ Griglia di deduzione" + RESET);
        out.println(PAD + DIM  + "   ✖ = escluso   · = possibile" + RESET);
        out.println(PAD + DIM  + "   R/G/B = colore   G = grande   P = piccolo   +/− = segno" + RESET);
        out.println();

        out.print(PAD);
        out.print(" ".repeat(LABEL));
        for (Ingredient ing : ingredients) {
            out.print(BOLD + centerTrunc(ing.getName(), COL) + RESET);
        }
        out.println();

        out.print(PAD);
        out.print(" ".repeat(LABEL));
        out.println(DIM + "─".repeat(ingredients.size() * COL) + RESET);

        for (int a = 0; a < alchemics.size(); a++) {
            String label = "  [" + (a + 1) + "]  " + formatFormula(alchemics.get(a));
            out.print(PAD);
            out.printf("%-" + LABEL + "s", label);
            for (Ingredient ing : ingredients) {
                boolean excluded = grid.isExcluded(ing, alchemics.get(a));
                String cell = excluded ? (RED + "✖" + RESET) : (DIM + "·" + RESET);
                out.print(centerColored(cell, excluded ? "✖" : "·", COL));
            }
            out.println();
        }
        out.println();
    }

    public void showExclusionResult(String ingredientName, String alchemicLabel) {
        out.printf("%s%s ✔ Escluso:%s %s%s%s non può essere %s%s%s%n",
                PAD, GREEN, RESET, BOLD, ingredientName, RESET, BOLD, alchemicLabel, RESET);
    }

    public int promptDeductionIngredientChoice(int max) {
        return promptBoundedInt("   ▸ Scegli ingrediente", max);
    }

    public int promptDeductionAlchemicChoice(int max) {
        return promptBoundedInt("   ▸ Scegli alchemico da escludere", max);
    }

    // --- Game over -----------------------------------------------------------

    public void showGameOver(List<Player> players) {
        String top = "╔" + "═".repeat(WIDTH - 2) + "╗";
        String mid = "║" + " ".repeat(WIDTH - 2) + "║";
        String bot = "╚" + "═".repeat(WIDTH - 2) + "╝";
        out.println();
        revealLine(PAD + YELLOW + BOLD + top + RESET, BANNER_LINE_MS);
        revealLine(PAD + YELLOW + BOLD + mid + RESET, BANNER_LINE_MS);
        revealLine(PAD + YELLOW + BOLD + "║" + center("F I N E   P A R T I T A", WIDTH - 2) + "║" + RESET, BANNER_LINE_MS);
        revealLine(PAD + YELLOW + BOLD + mid + RESET, BANNER_LINE_MS);
        revealLine(PAD + YELLOW + BOLD + bot + RESET, BANNER_LINE_MS);
        out.println();

        List<Player> ranking = players.stream()
                .sorted((a, b) -> b.getReputation() - a.getReputation())
                .toList();
        revealLine(PAD + BOLD + "Classifica finale" + RESET, LINE_MS);
        revealLine(PAD + DIM + "─".repeat(WIDTH) + RESET, LINE_MS);
        String[] medals = {"🥇", "🥈", "🥉"};
        for (int i = 0; i < ranking.size(); i++) {
            Player p = ranking.get(i);
            String medal = i < medals.length ? medals[i] : "   ";
            revealLine(String.format("%s %s  %s%-16s%s   %s⭐ %2d%s   %s💰 %2d%s",
                    PAD, medal,
                    BOLD, p.getName(), RESET,
                    CYAN,    p.getReputation(), RESET,
                    YELLOW,  p.getGold(),       RESET), BANNER_LINE_MS);
        }
        out.println();
    }

    // --- Input helpers -------------------------------------------------------

    public int readInt() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                out.printf("%s%s⚠  Inserisci un numero intero:%s ", PAD, RED, RESET);
            }
        }
    }

    public String readLine() {
        while (true) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            out.printf("%s%s⚠  Input non può essere vuoto:%s ", PAD, RED, RESET);
        }
    }

    private boolean readYesNo() {
        while (true) {
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("s")) return true;
            if (line.equals("n")) return false;
            out.printf("%s%s⚠  Inserisci 's' o 'n':%s ", PAD, RED, RESET);
        }
    }

    private int promptBoundedInt(String prompt, int max) {
        while (true) {
            out.printf("%s%s %s(1-%d)%s: ", PAD, prompt, DIM, max, RESET);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= max) return choice;
                out.printf("%s%s⚠  Inserisci un valore tra 1 e %d.%s%n", PAD, RED, max, RESET);
            } catch (NumberFormatException e) {
                out.printf("%s%s⚠  Inserisci un numero tra 1 e %d.%s%n", PAD, RED, max, RESET);
            }
        }
    }

    // --- Layout helpers ------------------------------------------------------

    private String center(String text, int width) {
        if (text.length() >= width) return text;
        int padding = width - text.length();
        int left = padding / 2;
        return " ".repeat(left) + text + " ".repeat(padding - left);
    }

    private String padRight(String text, int width) {
        if (text.length() >= width) return text.substring(0, width);
        return text + " ".repeat(width - text.length());
    }

    private String centerTrunc(String text, int width) {
        if (text.length() > width) text = text.substring(0, width);
        int padding = width - text.length();
        int left = padding / 2;
        return " ".repeat(left) + text + " ".repeat(padding - left);
    }

    private String centerColored(String coloredCell, String visibleCell, int width) {
        int padding = width - visibleCell.length();
        int left = padding / 2;
        return " ".repeat(left) + coloredCell + " ".repeat(padding - left);
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

    // --- TRANSMUTE -----------------------------------------------------------

    public void showTransmutationResult(int updatedGold) {
        out.printf("%s%s ✔ Ingrediente tramutato.%s  Oro attuale: %s%d%s%n",
                PAD, GREEN, RESET, YELLOW + BOLD, updatedGold, RESET);
    }

    // --- FORAGE --------------------------------------------------------------

    public void showForageResult() {
        out.println(PAD + GREEN + " ✔ Ingrediente aggiunto al laboratorio." + RESET);
    }

    // --- Animation helpers ---------------------------------------------------

    private void sleep(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /** Print a line char-by-char, but emit ANSI escape sequences atomically. */
    private void typewrite(String line) {
        int i = 0;
        int n = line.length();
        while (i < n) {
            char c = line.charAt(i);
            if (c == '' && i + 1 < n && line.charAt(i + 1) == '[') {
                int end = i + 2;
                while (end < n && !Character.isLetter(line.charAt(end))) end++;
                if (end < n) end++; // include the terminating letter
                out.print(line.substring(i, end));
                out.flush();
                i = end;
            } else {
                out.print(c);
                out.flush();
                sleep(CHAR_MS);
                i++;
            }
        }
        out.println();
    }

    private void revealLine(String line, int ms) {
        out.println(line);
        sleep(ms);
    }
}
