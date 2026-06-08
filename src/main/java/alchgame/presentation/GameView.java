package alchgame.presentation;

import alchgame.application.dto.DeductionGridDTO;
import alchgame.application.dto.ExperimentResultDTO;
import alchgame.application.dto.IngredientDTO;
import alchgame.application.dto.PlayerDTO;
import alchgame.application.dto.PotionDTO;
import alchgame.application.dto.PublicPlayerBoardDTO;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Confine di output della presentazione.
 * Non importa nessun tipo del dominio: riceve solo primitivi, stringhe e DTO.
 */
public class GameView {

    // --- ANSI base
    private static final String RESET = "\033[0m";
    private static final String BOLD  = "\033[1m";
    private static final String DIM   = "\033[2m";

    // --- 256-color helpers (xterm-256). Tema: oro alchemico + neon viola/ciano.
    private static String fg(int n) { return "\033[38;5;" + n + "m"; }
    private static String bg(int n) { return "\033[48;5;" + n + "m"; }

    // palette nominale (mappata su 256-color per coerenza cromatica)
    private static final String RED     = fg(196);
    private static final String GREEN   = fg(framedGreen());
    private static final String YELLOW  = fg(220);
    private static final String BLUE    = fg(75);
    private static final String MAGENTA = fg(170);
    private static final String CYAN    = fg(80);
    private static final String WHITE   = fg(231);
    private static final String GREY    = fg(245);
    private static final String VIOLET  = fg(99);
    private static final String GOLD     = fg(214);
    private static final String EMBER    = fg(202);

    private static int framedGreen() { return 78; }

    // gradiente "oro fuso" per il logo (dall'alto verso il basso)
    private static final int[] LOGO_GRAD = {229, 221, 220, 214, 208};
    // gradiente per il banner di fine partita
    private static final int[] OVER_GRAD = {214, 208, 202, 196, 160};

    private static final int WIDTH = 64;
    private static final int INNER = WIDTH - 2;
    private static final String PAD = "  ";

    private static final int CHAR_MS        = 18;
    private static final int LINE_MS        = 22;
    private static final int BANNER_LINE_MS = 55;

    private final Scanner scanner;
    private final PrintStream out;
    private PlayerDTO cachedPlayer;

    public void cachePlayer(PlayerDTO p) { this.cachedPlayer = p; }

    public GameView() {
        this(new Scanner(System.in), System.out);
    }

    public GameView(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    // --- Block font (5 righe) per il logo del titolo ------------------------

    private static final Map<Character, String[]> FONT = buildFont();

    private static Map<Character, String[]> buildFont() {
        Map<Character, String[]> f = new LinkedHashMap<>();
        f.put('A', new String[]{ " ███ ", "█   █", "█████", "█   █", "█   █" });
        f.put('L', new String[]{ "█    ", "█    ", "█    ", "█    ", "█████" });
        f.put('C', new String[]{ " ████", "█    ", "█    ", "█    ", " ████" });
        f.put('H', new String[]{ "█   █", "█   █", "█████", "█   █", "█   █" });
        f.put('I', new String[]{ "█████", "  █  ", "  █  ", "  █  ", "█████" });
        f.put('M', new String[]{ "█   █", "██ ██", "█ █ █", "█   █", "█   █" });
        f.put('S', new String[]{ " ████", "█    ", " ███ ", "    █", "████ " });
        f.put('T', new String[]{ "█████", "  █  ", "  █  ", "  █  ", "  █  " });
        f.put(' ', new String[]{ "   ",   "   ",   "   ",   "   ",   "   "   });
        return f;
    }

    /** Costruisce le 5 righe di testo a blocchi per la stringa data. */
    private String[] bigText(String text) {
        String[] rows = {"", "", "", "", ""};
        char[] chars = text.toUpperCase().toCharArray();
        for (int c = 0; c < chars.length; c++) {
            String[] glyph = FONT.getOrDefault(chars[c], FONT.get(' '));
            for (int r = 0; r < 5; r++)
                rows[r] += glyph[r] + (c < chars.length - 1 ? " " : "");
        }
        return rows;
    }

    // --- Screen control -----------------------------------------------------

    public void clearScreen() {
        out.print("\033[2J\033[H");
        out.flush();
    }

    // --- Title screen + menu ------------------------------------------------

    public void showWelcome() {
        clearScreen();
        renderTitleScreen();

        while (true) {
            int choice = readMenuChoice();
            switch (choice) {
                case 1 -> { return; }                 // Nuova partita -> prosegue il setup
                case 2 -> { showRules();  redrawTitle(); }
                case 3 -> { showCredits(); redrawTitle(); }
                case 0 -> {
                    clearScreen();
                    out.println();
                    typewrite(PAD + VIOLET + BOLD + "  Che gli astri guidino le tue trasmutazioni. Addio." + RESET);
                    out.println();
                    out.flush();
                    System.exit(0);
                }
                default -> out.printf("%s%s[!] Scelta non valida.%s%n", PAD, RED, RESET);
            }
        }
    }

    private void redrawTitle() {
        clearScreen();
        renderTitleScreen();
    }

    private void renderTitleScreen() {
        String frame = VIOLET;
        out.println();
        revealLine(PAD + frame + "╔" + "═".repeat(INNER) + "╗" + RESET, BANNER_LINE_MS);
        revealLine(PAD + frame + "║" + " ".repeat(INNER) + "║" + RESET, LINE_MS);

        // logo a blocchi con gradiente oro fuso
        String[] logo = bigText("ALCHIMISTI");
        for (int r = 0; r < logo.length; r++) {
            String art = logo[r];
            int lead = Math.max(0, (INNER - art.length()) / 2);
            String body = " ".repeat(lead) + art + " ".repeat(Math.max(0, INNER - lead - art.length()));
            revealLine(PAD + frame + "║" + RESET + fg(LOGO_GRAD[r]) + BOLD + body + RESET + frame + "║" + RESET, LINE_MS);
        }

        revealLine(PAD + frame + "║" + RESET + GOLD + center("· A N O N I M I ·", INNER) + RESET + frame + "║" + RESET, LINE_MS);
        revealLine(PAD + frame + "║" + " ".repeat(INNER) + "║" + RESET, LINE_MS);
        revealLine(PAD + frame + "║" + RESET + GREY + center("un gioco di deduzione alchemica", INNER) + RESET + frame + "║" + RESET, LINE_MS);
        revealLine(PAD + frame + "║" + RESET + DIM + CYAN + center("*  .  o  O  o  .  *", INNER) + RESET + frame + "║" + RESET, LINE_MS);
        revealLine(PAD + frame + "║" + " ".repeat(INNER) + "║" + RESET, LINE_MS);

        revealLine(PAD + frame + "╠" + "═".repeat(INNER) + "╣" + RESET, LINE_MS);
        revealLine(PAD + frame + "║" + " ".repeat(INNER) + "║" + RESET, LINE_MS);
        menuItem(frame, "1", "NUOVA PARTITA",  GOLD);
        menuItem(frame, "2", "COME SI GIOCA",  CYAN);
        menuItem(frame, "3", "CREDITI",        VIOLET);
        menuItem(frame, "0", "ESCI",           GREY);
        revealLine(PAD + frame + "║" + " ".repeat(INNER) + "║" + RESET, LINE_MS);
        revealLine(PAD + frame + "╚" + "═".repeat(INNER) + "╝" + RESET, BANNER_LINE_MS);
        out.println();
    }

    private void menuItem(String frame, String key, String label, String accent) {
        String content = "     [" + key + "]   " + label;
        int visible = content.length();
        String line = frame + "║" + RESET
                + "     " + GREY + "[" + RESET + accent + BOLD + key + RESET + GREY + "]" + RESET
                + "   " + WHITE + BOLD + label + RESET
                + " ".repeat(Math.max(0, INNER - visible))
                + frame + "║" + RESET;
        revealLine(PAD + line, LINE_MS);
    }

    private int readMenuChoice() {
        out.printf("%s%s  »%s ", PAD, VIOLET + BOLD, RESET);
        out.flush();
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void showRules() {
        clearScreen();
        panelTop("COME SI GIOCA", CYAN);
        panelBlank(CYAN);
        panelText(CYAN, GOLD + BOLD + "OBIETTIVO" + RESET);
        panelText(CYAN, "  Sei un alchimista: scopri quale formula segreta");
        panelText(CYAN, "  si nasconde dietro ogni ingrediente e diventa il");
        panelText(CYAN, "  piu' rinomato dell'accademia.");
        panelBlank(CYAN);
        panelText(CYAN, GOLD + BOLD + "OGNI ROUND" + RESET);
        panelText(CYAN, "  " + GREEN + "1." + RESET + " Scegli la tua posizione nel tracciato di");
        panelText(CYAN, "     risveglio: chi si sveglia prima sceglie prima.");
        panelText(CYAN, "  " + GREEN + "2." + RESET + " Dichiara le azioni piazzando i cubi.");
        panelText(CYAN, "  " + GREEN + "3." + RESET + " Le azioni vengono risolte in ordine.");
        panelBlank(CYAN);
        panelText(CYAN, GOLD + BOLD + "LE AZIONI" + RESET);
        panelText(CYAN, "  " + fg(framedGreen()) + "%" + RESET + " RACCOGLIERE  ottieni nuovi ingredienti");
        panelText(CYAN, "  " + YELLOW + "~" + RESET + " TRASMUTARE   converti un ingrediente in oro");
        panelText(CYAN, "  " + VIOLET + "@" + RESET + " ESPERIMENTO  mescola due ingredienti e");
        panelText(CYAN, "                osserva la pozione per dedurre");
        panelBlank(CYAN);
        panelText(CYAN, GOLD + BOLD + "VITTORIA" + RESET);
        panelText(CYAN, "  Al termine dei round vince chi ha piu' " + CYAN + "reputazione" + RESET + ".");
        panelText(CYAN, "  In caso di parita', conta l'" + YELLOW + "oro" + RESET + ".");
        panelBlank(CYAN);
        panelBot(CYAN);
        pressEnter();
    }

    private void showCredits() {
        clearScreen();
        panelTop("CREDITI", VIOLET);
        panelBlank(VIOLET);
        panelCenter(VIOLET, GOLD + BOLD + "A L C H I M I S T I   A N O N I M I" + RESET);
        panelBlank(VIOLET);
        panelCenter(VIOLET, GREY + "un gioco di deduzione alchemica" + RESET);
        panelBlank(VIOLET);
        panelCenter(VIOLET, DIM + "ispirato al gioco da tavolo \"Alchemists\"" + RESET);
        panelBlank(VIOLET);
        panelCenter(VIOLET, CYAN + "Progetto di Software Engineering" + RESET);
        panelCenter(VIOLET, CYAN + "Team \"Alchimisti Anonimi\"" + RESET);
        panelBlank(VIOLET);
        panelCenter(VIOLET, DIM + "* . o O o . *" + RESET);
        panelBlank(VIOLET);
        panelBot(VIOLET);
        pressEnter();
    }

    private void pressEnter() {
        out.printf("%n%s%s   [ premi INVIO per tornare al menu ]%s ", PAD, DIM, RESET);
        out.flush();
        scanner.nextLine();
    }

    // generico pannello a cornice singola, larghezza INNER
    private void panelTop(String title, String color) {
        out.println();
        String t = "  " + title + "  ";
        int side = (INNER - t.length()) / 2;
        String top = "┌" + "─".repeat(side) + t + "─".repeat(INNER - side - t.length()) + "┐";
        revealLine(PAD + color + BOLD + top + RESET, LINE_MS);
    }
    private void panelBot(String color) {
        revealLine(PAD + color + BOLD + "└" + "─".repeat(INNER) + "┘" + RESET, LINE_MS);
    }
    private void panelBlank(String color) {
        out.println(PAD + color + "│" + RESET + " ".repeat(INNER) + color + "│" + RESET);
    }
    private void panelText(String color, String content) {
        int visible = visibleLength(content);
        String body = " " + content + " ".repeat(Math.max(0, INNER - 1 - visible));
        out.println(PAD + color + "│" + RESET + body + color + "│" + RESET);
    }
    private void panelCenter(String color, String content) {
        out.println(PAD + color + "│" + RESET + center(content, INNER) + color + "│" + RESET);
    }

    // --- Setup --------------------------------------------------------------

    public void promptPlayerCount(int min, int max) {
        out.println();
        out.printf("%s%s» Quanti alchimisti? %s(%d-%d)%s: ", PAD, GOLD + BOLD, RESET + DIM, min, max, RESET);
    }

    public void promptPlayerName(int playerNumber) {
        out.printf("%s%s» Nome alchimista %s#%d%s: ", PAD, GOLD + BOLD, RESET + BOLD, playerNumber, RESET);
    }

    public void showInvalidInput(String message) {
        out.println(PAD + RED + "[!] " + message + RESET);
    }

    // --- Round / Phase ------------------------------------------------------

    public void showRoundStart(int current, int total) {
        clearScreen();
        out.println();
        String bar = "═".repeat(WIDTH);
        revealLine(PAD + GOLD + BOLD + bar + RESET, BANNER_LINE_MS);
        String pips = roundPips(current, total);
        revealLine(PAD + GOLD + BOLD + center("ROUND  " + current + " / " + total, WIDTH) + RESET, BANNER_LINE_MS);
        revealLine(PAD + center(pips, WIDTH), LINE_MS);
        revealLine(PAD + GOLD + BOLD + bar + RESET, BANNER_LINE_MS);
        revealLine(PAD + DIM + center("Preparate i vostri laboratori...", WIDTH) + RESET, LINE_MS * 2);
    }

    private String roundPips(int current, int total) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= total; i++) {
            if (i > 1) sb.append("  ");
            sb.append(i <= current ? GOLD + "◆" + RESET : DIM + "◇" + RESET);
        }
        return sb.toString();
    }

    public void showRoundEnd(int roundNumber) {
        out.println();
        String text = "[ fine round " + roundNumber + " ]";
        int side = (WIDTH - text.length()) / 2;
        out.println(PAD + DIM + "─".repeat(side) + text + "─".repeat(WIDTH - side - text.length()) + RESET);
    }

    public void showPhaseHeader(String phaseName) {
        String top = "┌" + "─".repeat(WIDTH - 2) + "┐";
        String bot = "└" + "─".repeat(WIDTH - 2) + "┘";
        String body = "│" + padRight("  ◆  " + phaseName.toUpperCase(), WIDTH - 2) + "│";
        out.println();
        revealLine(PAD + VIOLET + top + RESET, LINE_MS);
        revealLine(PAD + VIOLET + BOLD + body + RESET, LINE_MS);
        revealLine(PAD + VIOLET + bot + RESET, LINE_MS);
    }

    public void showCurrentPlayer(String name) {
        out.println();
        typewrite(PAD + WHITE + BOLD + "▶ TURNO DI " + name.toUpperCase() + RESET);
        out.println(PAD + DIM + "─".repeat(WIDTH) + RESET);
    }

    public void showPlayerStatus(int gold, int reputation, int actionCubes) {
        out.printf("%s   %s %s$ %2d oro %s   %s %s* %2d rep %s   %s %s# %d cubi %s%n",
                PAD,
                bg(236), YELLOW + BOLD, gold,        RESET,
                bg(236), CYAN + BOLD,   reputation,  RESET,
                bg(236), MAGENTA + BOLD, actionCubes, RESET);
    }

    public void showPhaseOrder(String label, List<String> playerNames) {
        out.printf("%s   %s↳ Ordine %s:%s %s%n",
                PAD, DIM, label, RESET,
                String.join(DIM + " → " + RESET, playerNames));
    }

    public void showBoard(Map<String, String> orderSlots,
                          List<String> wakeUpOrder,
                          List<String> actionIds,
                          Map<String, List<String>> declarantsByAction) {
        final int BW = 78;
        final int IW = BW - 2;

        String horizTop = "╔" + "═".repeat(IW) + "╗";
        String horizMid = "╠" + "═".repeat(IW) + "╣";
        String horizBot = "╚" + "═".repeat(IW) + "╝";
        String empty    = "║" + " ".repeat(IW) + "║";
        String frame    = VIOLET;

        out.println();
        revealLine(PAD + frame + horizTop + RESET, LINE_MS);
        revealLine(PAD + frame + "║" + RESET + GOLD + BOLD + padRight("  ~  TABELLONE ALCHEMICO", IW) + RESET + frame + "║" + RESET, LINE_MS);
        revealLine(PAD + frame + horizMid + RESET, LINE_MS);
        out.println(PAD + frame + empty + RESET);

        // Tracciato di risveglio
        final int WB_W    = 46;
        final int wbInner = WB_W - 2;
        String wbTitle = " TRACCIATO DI RISVEGLIO ";
        int wbSides    = (wbInner - wbTitle.length()) / 2;
        String wbTop   = "┌" + "─".repeat(wbSides) + wbTitle + "─".repeat(wbInner - wbSides - wbTitle.length()) + "┐";
        String wbBot   = "└" + "─".repeat(wbInner) + "┘";
        int wbLPad     = (IW - WB_W) / 2;
        String wbLeft  = " ".repeat(wbLPad);
        String wbRight = " ".repeat(IW - wbLPad - WB_W);

        revealLine(PAD + frame + "║" + RESET + wbLeft + CYAN + wbTop + RESET + wbRight + frame + "║" + RESET, LINE_MS);

        if (wakeUpOrder != null && !wakeUpOrder.isEmpty()) {
            String chain    = " ▶ " + String.join("  ▸  ", wakeUpOrder);
            String chainRow = "│" + fg(framedGreen()) + padRight(truncate(chain, wbInner), wbInner) + RESET + "│";
            revealLine(PAD + frame + "║" + RESET + wbLeft + chainRow + wbRight + frame + "║" + RESET, LINE_MS);
        }

        for (Map.Entry<String, String> e : orderSlots.entrySet()) {
            boolean taken = e.getValue() != null && !e.getValue().isBlank();
            String mark   = taken ? (fg(framedGreen()) + "[*]" + RESET) : (DIM + "[ ]" + RESET);
            String name   = taken ? truncate(e.getValue(), 22) : "···";
            String body   = " " + mark + "  " + DIM + padRight(e.getKey(), 8) + RESET + "  " + (taken ? WHITE : DIM) + name + RESET;
            int visible   = 1 + 3 + 2 + 8 + 2 + name.length();
            String row    = "│" + body + " ".repeat(Math.max(0, wbInner - visible)) + "│";
            revealLine(PAD + frame + "║" + RESET + wbLeft + row + wbRight + frame + "║" + RESET, LINE_MS);
        }
        revealLine(PAD + frame + "║" + RESET + wbLeft + CYAN + wbBot + RESET + wbRight + frame + "║" + RESET, LINE_MS);
        out.println(PAD + frame + empty + RESET);

        // Spazi azione
        final int AB_W   = 22;
        final int AB_GAP = 3;
        int totalA = AB_W * actionIds.size() + AB_GAP * Math.max(0, actionIds.size() - 1);
        int aLPad  = Math.max(0, (IW - totalA) / 2);
        String aLeft  = " ".repeat(aLPad);
        String aRight = " ".repeat(Math.max(0, IW - aLPad - totalA));

        int maxDecls = 0;
        for (String id : actionIds)
            maxDecls = Math.max(maxDecls, declarantsByAction.getOrDefault(id, List.of()).size());
        int contentRows = Math.max(1, maxDecls);

        StringBuilder topL   = new StringBuilder();
        StringBuilder titleL = new StringBuilder();
        List<StringBuilder> contentLs = new ArrayList<>();
        for (int r = 0; r < contentRows; r++) contentLs.add(new StringBuilder());
        StringBuilder botL = new StringBuilder();

        for (int i = 0; i < actionIds.size(); i++) {
            if (i > 0) {
                String gap = " ".repeat(AB_GAP);
                topL.append(gap); titleL.append(gap);
                for (StringBuilder cl : contentLs) cl.append(gap);
                botL.append(gap);
            }
            String id     = actionIds.get(i);
            String icon   = actionIcon(id);
            String accent = actionColor(id);
            String label  = " " + icon + " " + id.toUpperCase();
            List<String> decls = declarantsByAction.getOrDefault(id, List.of());

            topL.append(accent).append("┌").append("─".repeat(AB_W - 2)).append("┐").append(RESET);
            titleL.append(accent).append("│").append(BOLD).append(padRight(label, AB_W - 2)).append(RESET)
                  .append(accent).append("│").append(RESET);
            for (int r = 0; r < contentRows; r++) {
                String text;
                if (decls.isEmpty()) {
                    text = r == 0 ? " ···" : "";
                } else if (r < decls.size()) {
                    text = " # " + truncate(decls.get(r), AB_W - 5);
                } else {
                    text = "";
                }
                contentLs.get(r).append(accent).append("│").append(RESET)
                         .append(GREY).append(padRight(text, AB_W - 2)).append(RESET)
                         .append(accent).append("│").append(RESET);
            }
            botL.append(accent).append("└").append("─".repeat(AB_W - 2)).append("┘").append(RESET);
        }

        revealLine(PAD + frame + "║" + RESET + aLeft + topL   + aRight + frame + "║" + RESET, LINE_MS);
        revealLine(PAD + frame + "║" + RESET + aLeft + titleL + aRight + frame + "║" + RESET, LINE_MS);
        for (StringBuilder cl : contentLs)
            revealLine(PAD + frame + "║" + RESET + aLeft + cl + aRight + frame + "║" + RESET, LINE_MS);
        revealLine(PAD + frame + "║" + RESET + aLeft + botL   + aRight + frame + "║" + RESET, LINE_MS);

        out.println(PAD + frame + empty + RESET);
        out.println(PAD + frame + horizBot + RESET);
    }

    private String actionIcon(String id) {
        return switch (id) {
            case "forage"     -> "%";
            case "transmute"  -> "~";
            case "experiment" -> "@";
            default           -> "?";
        };
    }

    private String actionColor(String id) {
        return switch (id) {
            case "forage"     -> fg(framedGreen());
            case "transmute"  -> YELLOW;
            case "experiment" -> VIOLET;
            default           -> GREY;
        };
    }

    // --- Order phase --------------------------------------------------------

    public void showAvailableSlots(List<String> slotIds) {
        out.println(PAD + "   " + GREY + "Scegli uno slot:" + RESET);
        for (int i = 0; i < slotIds.size(); i++)
            out.printf("%s     %s[%d]%s %s%n", PAD, CYAN, i + 1, RESET, slotIds.get(i));
    }

    public void showWakeUpOrder(List<String> playerNames) {
        out.println();
        out.println(PAD + fg(framedGreen()) + BOLD + "▶ Ordine di risveglio definitivo" + RESET);
        out.println(PAD + "   " + String.join(DIM + "  ▸  " + RESET, playerNames));
    }

    public void showSlotChoiceResult(String slotId, List<String> rewards, List<IngredientDTO> receivedIngredients) {
    String summary = rewards.isEmpty() ? "nessuna risorsa" : String.join(", ", rewards);
    out.printf("%s%s ✓ Slot %s%s%s → %s%s%n",
            PAD, fg(framedGreen()), BOLD, slotId, fg(framedGreen()), summary, RESET);
        
    if (!receivedIngredients.isEmpty()) {
        String names = receivedIngredients.stream().map(IngredientDTO::name).collect(Collectors.joining(", "));
        out.printf("%s%s   Ingredienti ricevuti: %s%s%n",
                PAD, fg(framedGreen()), names, RESET);
    }
    }

    public int promptSlotChoice(int maxIndex) {
        return promptBoundedInt("   » Scegli slot", maxIndex);
    }

    // --- Declaration phase --------------------------------------------------

    public void showActionListWithPass(List<String> actionIds) {
        out.println(PAD + "   " + GREY + "Azioni disponibili:" + RESET);
        for (int i = 0; i < actionIds.size(); i++) {
            String id     = actionIds.get(i);
            String accent = actionColor(id);
            out.printf("%s     %s[%d]%s %s%s%s %s%s%s%n",
                    PAD, CYAN, i + 1, RESET, accent, actionIcon(id), RESET, BOLD, id, RESET);
        }
        out.printf("%s     %s[0]%s %sPassa%s%n", PAD, CYAN, RESET, DIM, RESET);
    }

    public int promptActionOrPass(int maxIndex) {
        while (true) {
            out.printf("%s   » Scelta %s(0-%d | L lab)%s: ", PAD, DIM, maxIndex, RESET);
            String raw = scanner.nextLine().trim();
            if (raw.equalsIgnoreCase("L")) { showLabScreen(); continue; }
            try {
                int choice = Integer.parseInt(raw);
                if (choice >= 0 && choice <= maxIndex) return choice;
                out.printf("%s%s[!] Inserisci un valore tra 0 e %d.%s%n", PAD, RED, maxIndex, RESET);
            } catch (NumberFormatException e) {
                out.printf("%s%s[!] Inserisci un numero tra 0 e %d.%s%n", PAD, RED, maxIndex, RESET);
            }
        }
    }

    public void showDeclaredAction(String playerName, String actionId) {
        typewrite(String.format("%s%s ✓ %s%s%s → %s%s%s",
                PAD, fg(framedGreen()), BOLD, playerName, fg(framedGreen()), BOLD, actionId, RESET));
    }

    // --- Resolution phase ---------------------------------------------------

    public void showResolutionStep(String actionId, String playerName, int gold, int rep, int cubes) {
        out.println();
        typewrite(String.format("%s%s▶▶%s  azione: %s%s%s  ·  alchimista: %s%s%s",
                PAD, CYAN + BOLD, RESET,
                actionColor(actionId) + BOLD, actionId, RESET,
                WHITE + BOLD, playerName, RESET));
        showPlayerStatus(gold, rep, cubes);
        out.println(PAD + DIM + "─".repeat(WIDTH) + RESET);
    }

    // --- Experiment ---------------------------------------------------------

    public void showTargetOptions(List<String> targetIds) {
        out.println(PAD + "   " + GREY + "Bersaglio dell'esperimento:" + RESET);
        for (int i = 0; i < targetIds.size(); i++)
            out.printf("%s     %s[%d]%s %s%n", PAD, CYAN, i + 1, RESET, targetIds.get(i));
    }

    public String promptTargetChoice(List<String> targetIds) {
        while (true) {
            out.printf("%s   » Scelta %s(1-%d | L lab)%s: ", PAD, DIM, targetIds.size(), RESET);
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("L")) { showLabScreen(); continue; }
            try {
                int idx = Integer.parseInt(line) - 1;
                if (idx >= 0 && idx < targetIds.size()) return targetIds.get(idx);
            } catch (NumberFormatException ignored) {}
            out.printf("%s%s[!] Inserisci un numero tra 1 e %d.%s%n", PAD, RED, targetIds.size(), RESET);
        }
    }

    public void showPaymentRequired() {
        out.println(PAD + YELLOW + "[!] Lo studente e' scontento — verra' addebitato 1 oro." + RESET);
    }

    public void showPaymentResult(int remainingGold) {
        out.printf("%s   %sOro rimasto: %s%d%s%n", PAD, DIM, YELLOW + BOLD, remainingGold, RESET);
    }

    public void showInsufficientGold() {
        out.println(PAD + RED + "[X] Oro insufficiente per condurre l'esperimento sullo studente." + RESET);
    }

    public void showIngredients(List<String> ingredientNames) {
        out.println(PAD + "   " + fg(framedGreen()) + "% Ingredienti nel laboratorio:" + RESET);
        if (ingredientNames.isEmpty()) {
            out.println(PAD + "     " + DIM + "(nessun ingrediente)" + RESET);
            return;
        }
        printBadges(ingredientNames, CYAN);
    }

    public int promptIngredientChoice(String prompt, int maxIndex) {
        return promptBoundedInt("   » " + prompt, maxIndex);
    }

    public void showPotionResult(String label, String colorName) {
        String tint = switch (colorName) {
            case "RED"   -> RED;
            case "GREEN" -> fg(framedGreen());
            case "BLUE"  -> BLUE;
            default      -> GREY;
        };
        int innerW = WIDTH - 4;
        String top = "╔" + "═".repeat(innerW) + "╗";
        String mid = "║" + center("~  POZIONE: " + label + "  ~", innerW) + "║";
        String bot = "╚" + "═".repeat(innerW) + "╝";
        out.println();
        revealLine(PAD + tint + BOLD + top + RESET, LINE_MS);
        typewrite(PAD + tint + BOLD + mid + RESET);
        revealLine(PAD + tint + BOLD + bot + RESET, LINE_MS);
    }

    public boolean promptUpdateDeductionGrid() {
        out.printf("%s   » Aggiornare la griglia di deduzione? %s(s/n)%s: ", PAD, DIM, RESET);
        return readYesNo();
    }

    public void showDeductionGrid(List<String> ingredientNames,
                                  List<String> alchemicLabels,
                                  boolean[][] excluded) {
        final int LBL  = 18;   // colonna sinistra: indice + molecola dell'alchemico
        final int COLW = 5;    // larghezza di ogni colonna ingrediente
        final int ABBR = 3;    // lunghezza delle sigle ingrediente in intestazione
        final int nIng = ingredientNames.size();

        out.println();
        out.println(PAD + CYAN + BOLD + "▣ Griglia di deduzione" + RESET);
        out.println(PAD + DIM + "   righe = alchemici · colonne = ingredienti" + RESET);
        out.println(PAD + DIM + "   atomo "
                + RED + "●" + DIM + " "
                + fg(framedGreen()) + "●" + DIM + " "
                + BLUE + "●" + DIM + " (rosso verde blu)   "
                + WHITE + BOLD + "●" + RESET + DIM + " grande  "
                + WHITE + "•" + RESET + DIM + " piccolo  segno " + WHITE + "+ oppure − " + RESET);
        out.println(PAD + DIM + "   " + RED + "X" + DIM + " escluso   " + GREY + "·" + DIM + " possibile" + RESET);
        out.println();

        // --- Intestazione colonne: numero + sigla di ogni ingrediente
        out.print(PAD + " ".repeat(LBL));
        for (int i = 0; i < nIng; i++)
            out.print(CYAN + center(String.valueOf(i + 1), COLW) + RESET);
        out.println();
        out.print(PAD + " ".repeat(LBL));
        for (String name : ingredientNames)
            out.print(WHITE + BOLD + center(abbrev(name, ABBR), COLW) + RESET);
        out.println();
        out.println(PAD + " ".repeat(LBL) + DIM + "─".repeat(nIng * COLW) + RESET);

        // --- Corpo: una riga per alchemico = indice + molecola affiancata, poi le celle
        for (int a = 0; a < alchemicLabels.size(); a++) {
            String[] g = atomGlyphs(alchemicLabels.get(a));
            String head = CYAN + BOLD + "[" + (a + 1) + "]" + RESET + "  "
                    + g[0] + " " + g[1] + " " + g[2];
            out.print(PAD + head + " ".repeat(Math.max(1, LBL - visibleLength(head))));
            for (int i = 0; i < nIng; i++) {
                if (excluded[a][i]) out.print(RED + BOLD + center("X", COLW) + RESET);
                else                out.print(DIM + center("·", COLW) + RESET);
            }
            out.println();
        }
        out.println();

        // --- Legenda sigle → nomi completi (4 per riga per non eccedere in larghezza)
        out.print(PAD + DIM + "   ");
        for (int i = 0; i < nIng; i++) {
            if (i > 0 && i % 4 == 0) out.print(RESET + "\n" + PAD + DIM + "   ");
            out.print(WHITE + abbrev(ingredientNames.get(i), ABBR) + RESET
                    + DIM + "=" + ingredientNames.get(i) + "   ");
        }
        out.println(RESET);
        out.println();
    }

    /** Sigla di un nome: i primi {@code n} caratteri (o il nome intero se più corto). */
    private String abbrev(String s, int n) {
        if (s == null || s.isEmpty()) return "";
        return s.length() <= n ? s : s.substring(0, n);
    }

    /**
     * Estrae dalla label di un alchemico (es. "  [1]  R:G+ G:P- B:G~") i tre
     * cerchi colorati renderizzati, in ordine rosso/verde/blu: glifo della
     * grandezza giusta (● grande, • piccolo) seguito dalla carica (+ − ~).
     */
    private String[] atomGlyphs(String label) {
        String[] colorFg = { RED, fg(framedGreen()), BLUE };
        String[] cells = { GREY + "?" + RESET, GREY + "?" + RESET, GREY + "?" + RESET };
        int ci = 0;
        for (int k = 1; k + 2 < label.length() && ci < 3; k++) {
            if (label.charAt(k) != ':') continue;
            String glyph = label.charAt(k + 1) == 'G' ? "●" : "•";
            char sg = label.charAt(k + 2);
            String sign = sg == '+' ? "+" : sg == '-' ? "−" : "~";
            cells[ci] = colorFg[ci] + BOLD + glyph + RESET + WHITE + sign + RESET;
            ci++;
        }
        return cells;
    }

    public int promptDeductionIngredientChoice(int max) {
        return promptBoundedInt("   » Scegli ingrediente", max);
    }

    public int promptDeductionAlchemicChoice(int max) {
        return promptBoundedInt("   » Scegli alchemico da escludere", max);
    }

    public void showExclusionResult(String ingredientName, String alchemicLabel) {
        out.printf("%s%s ✓ Escluso:%s %s%s%s non puo' essere %s%s%s%n",
                PAD, fg(framedGreen()), RESET, BOLD, ingredientName, RESET, BOLD, alchemicLabel, RESET);
    }

    // --- Transmute ----------------------------------------------------------

    public void showTransmutationResult(int updatedGold) {
        out.printf("%s%s ✓ Ingrediente tramutato in oro.%s  Oro attuale: %s%d%s%n",
                PAD, fg(framedGreen()), RESET, YELLOW + BOLD, updatedGold, RESET);
    }

    // --- Forage -------------------------------------------------------------

    public void showForageResult(List<IngredientDTO> received) {
        String names = received.stream().map(IngredientDTO::name).collect(java.util.stream.Collectors.joining(", "));
        out.printf("%s%s ✓ Ingrediente aggiunto al laboratorio: %s%s%n",
            PAD, fg(framedGreen()), names, RESET);
    }

    // --- Laboratorio privato ------------------------------------------------

    public void showLabScreen() {
        if (cachedPlayer == null) {
            out.println(PAD + DIM + "(dati laboratorio non ancora disponibili)" + RESET);
            return;
        }
        clearScreen();
        while (true) {
            printLabMenu();
            int choice = -1;
            while (choice < 0) {
                out.printf("%s   » Scelta %s(0-3)%s: ", PAD, DIM, RESET);
                try {
                    choice = Integer.parseInt(scanner.nextLine().trim());
                    if (choice < 0 || choice > 3) { choice = -1; out.printf("%s%s[!] Inserisci un valore tra 0 e 3.%s%n", PAD, RED, RESET); }
                } catch (NumberFormatException e) {
                    out.printf("%s%s[!] Inserisci un valore tra 0 e 3.%s%n", PAD, RED, RESET);
                }
            }
            if (choice == 0) break;
            clearScreen();
            if (choice == 1) showLabIngredients();
            else if (choice == 2) showLabResults();
            else showLabDeductionGrid();
            out.printf("%n%s%sPremi INVIO per tornare al menu laboratorio...%s", PAD, DIM, RESET);
            scanner.nextLine();
            clearScreen();
        }
        out.println(PAD + DIM + "─".repeat(WIDTH) + RESET);
    }

    private void printLabMenu() {
        String top = "╔" + "═".repeat(INNER) + "╗";
        String sep = "╠" + "═".repeat(INNER) + "╣";
        String bot = "╚" + "═".repeat(INNER) + "╝";
        out.println();
        out.println(PAD + GOLD + BOLD + top + RESET);
        out.println(PAD + GOLD + BOLD + "║" + RESET + center("~ LABORATORIO PRIVATO ~  [ " + cachedPlayer.name() + " ]", INNER) + GOLD + BOLD + "║" + RESET);
        out.println(PAD + GOLD + BOLD + sep + RESET);
        out.println(PAD + GOLD + BOLD + "║" + RESET + "   " + CYAN + "[1]" + RESET + " Ingredienti" + " ".repeat(INNER - 18) + GOLD + BOLD + "║" + RESET);
        out.println(PAD + GOLD + BOLD + "║" + RESET + "   " + CYAN + "[2]" + RESET + " Triangolo dei risultati" + " ".repeat(INNER - 30) + GOLD + BOLD + "║" + RESET);
        out.println(PAD + GOLD + BOLD + "║" + RESET + "   " + CYAN + "[3]" + RESET + " Griglia di deduzione" + " ".repeat(INNER - 27) + GOLD + BOLD + "║" + RESET);
        out.println(PAD + GOLD + BOLD + "║" + RESET + "   " + CYAN + "[0]" + RESET + " Torna alla partita" + " ".repeat(INNER - 25) + GOLD + BOLD + "║" + RESET);
        out.println(PAD + GOLD + BOLD + bot + RESET);
        out.println();
    }

    private void showLabIngredients() {
        out.println();
        out.println(PAD + CYAN + BOLD + "% Ingredienti nel laboratorio" + RESET);
        List<String> names = cachedPlayer.ingredients().stream()
                .map(IngredientDTO::name)
                .toList();
        showIngredients(names);
    }

    private void showLabResults() {
        out.println();
        out.println(PAD + VIOLET + BOLD + "@ Triangolo dei risultati" + RESET);
        List<ExperimentResultDTO> results = cachedPlayer.experimentResults();
        if (results.isEmpty()) {
            out.println(PAD + "   " + DIM + "(nessun esperimento effettuato)" + RESET);
            out.println();
            return;
        }
        for (ExperimentResultDTO r : results) {
            String tint = switch (r.potion().colorName()) {
                case "RED"   -> RED;
                case "GREEN" -> fg(framedGreen());
                case "BLUE"  -> BLUE;
                default      -> GREY;
            };
            out.printf("%s   %s%-14s%s + %s%-14s%s --> %s%s%s%n",
                    PAD,
                    WHITE, r.ingredient1().name(), RESET,
                    WHITE, r.ingredient2().name(), RESET,
                    tint + BOLD, r.potion().label(), RESET);
        }
        out.println();
    }

    private void showLabDeductionGrid() {
        DeductionGridDTO grid = cachedPlayer.deductionGrid();
        List<String> ingredientNames = grid.ingredients().stream()
                .map(IngredientDTO::name)
                .toList();
        showDeductionGrid(ingredientNames, grid.alchemicLabels(), grid.excluded());
    }

    // --- Favors -------------------------------------------------------------

    public int promptFavorOrSkip(int maxIndex) {
        while (true) {
            out.printf("%s   » Attiva carta %s(0 per saltare, 1-%d | L lab)%s: ", PAD, DIM, maxIndex, RESET);
            String raw = scanner.nextLine().trim();
            if (raw.equalsIgnoreCase("L")) { showLabScreen(); continue; }
            try {
                int choice = Integer.parseInt(raw);
                if (choice >= 0 && choice <= maxIndex) return choice;
                out.printf("%s%s[!] Inserisci un valore tra 0 e %d.%s%n", PAD, RED, maxIndex, RESET);
            } catch (NumberFormatException e) {
                out.printf("%s%s[!] Inserisci un numero tra 0 e %d.%s%n", PAD, RED, maxIndex, RESET);
            }
        }
    }

    public void showFavors(List<String> favorNames) {
        out.println(PAD + "   " + BLUE + "+ Carte favore:" + RESET);
        if (favorNames.isEmpty()) {
            out.println(PAD + "     " + DIM + "(nessuna carta favore)" + RESET);
            return;
        }
        printBadges(favorNames, BLUE);
    }

    /** Stampa una lista come badge orizzontali numerati con wrapping. */
    private void printBadges(List<String> items, String accent) {
        int indent = PAD.length() + 5;
        StringBuilder line = new StringBuilder(PAD + "     ");
        int lineLen = indent;
        for (int i = 0; i < items.size(); i++) {
            String plain = "[" + (i + 1) + "] " + items.get(i);
            if (i > 0) {
                if (lineLen + 2 + plain.length() > WIDTH) {
                    out.println(line);
                    line = new StringBuilder(PAD + "     ");
                    lineLen = indent;
                } else {
                    line.append("  ");
                    lineLen += 2;
                }
            }
            line.append(GREY).append("[").append(RESET)
                .append(accent).append(BOLD).append(i + 1).append(RESET)
                .append(GREY).append("]").append(RESET)
                .append(" ").append(WHITE).append(items.get(i)).append(RESET);
            lineLen += plain.length();
        }
        out.println(line);
    }

    // --- Public player boards -----------------------------------------------

    /** Striscia di card pubbliche (sotto il tabellone): nome, oro e pozioni prodotte. */
    public void showPublicBoards(List<PublicPlayerBoardDTO> boards) {
        if (boards == null || boards.isEmpty()) return;

        final int CW = 24;            // larghezza interna di ogni card
        final String accent = VIOLET;

        List<List<String>> cards = new ArrayList<>();
        int height = 0;
        for (PublicPlayerBoardDTO b : boards) {
            List<String> card = buildPublicCard(b, CW, accent);
            cards.add(card);
            height = Math.max(height, card.size());
        }

        String filler = " ".repeat(CW + 2);
        out.println();
        out.println(PAD + DIM + "── alchimisti in gioco ──" + RESET);
        for (int row = 0; row < height; row++) {
            StringBuilder line = new StringBuilder(PAD);
            for (int c = 0; c < cards.size(); c++) {
                if (c > 0) line.append("  ");
                List<String> card = cards.get(c);
                line.append(row < card.size() ? card.get(row) : filler);
            }
            out.println(line);
        }
    }

    private List<String> buildPublicCard(PublicPlayerBoardDTO b, int cw, String accent) {
        List<String> lines = new ArrayList<>();

        // bordo superiore con il nome
        String title = " " + truncate(b.name().toUpperCase(), cw - 4) + " ";
        int dash = Math.max(0, cw - title.length());
        lines.add(accent + "┌" + RESET + WHITE + BOLD + title + RESET
                + accent + "─".repeat(dash) + "┐" + RESET);

        // oro
        lines.add(cardRow(accent, " " + YELLOW + "$ " + b.gold() + " oro" + RESET, cw));

        // intestazione pozioni + token colorati mandati a capo entro la card
        lines.add(cardRow(accent, " " + GREY + "pozioni:" + RESET, cw));
        List<String> potionRows = wrapPotions(b.potions(), cw - 2);
        if (potionRows.isEmpty()) {
            lines.add(cardRow(accent, "  " + DIM + "(nessuna)" + RESET, cw));
        } else {
            for (String pr : potionRows) lines.add(cardRow(accent, "  " + pr, cw));
        }

        // bordo inferiore
        lines.add(accent + "└" + "─".repeat(cw) + "┘" + RESET);
        return lines;
    }

    /** Riga interna della card, col contenuto riempito a larghezza visibile cw. */
    private String cardRow(String accent, String content, int cw) {
        int pad = Math.max(0, cw - visibleLength(content));
        return accent + "│" + RESET + content + " ".repeat(pad) + accent + "│" + RESET;
    }

    /** Token pozione colorati, mandati a capo entro maxWidth (larghezza visibile). */
    private List<String> wrapPotions(List<PotionDTO> potions, int maxWidth) {
        List<String> rows = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        int curW = 0;
        for (PotionDTO p : potions) {
            String token = potionToken(p);
            int tw = visibleLength(token);
            if (curW > 0 && curW + 1 + tw > maxWidth) {
                rows.add(cur.toString());
                cur = new StringBuilder();
                curW = 0;
            }
            if (curW > 0) { cur.append(" "); curW += 1; }
            cur.append(token);
            curW += tw;
        }
        if (curW > 0) rows.add(cur.toString());
        return rows;
    }

    /** Un token = pallino colorato + segno (+ positiva / − negativa / · neutra). */
    private String potionToken(PotionDTO p) {
        String tint = switch (p.colorName()) {
            case "RED"   -> RED;
            case "GREEN" -> fg(framedGreen());
            case "BLUE"  -> BLUE;
            default      -> GREY;
        };
        String sign = p.label().contains("POSITIVE") ? "+"
                    : p.label().contains("NEGATIVE") ? "−"
                    : "·";
        return tint + "●" + RESET + WHITE + sign + RESET;
    }

    // --- Game over ----------------------------------------------------------

    public void showGameOver(List<PlayerDTO> players) {
        clearScreen();
        out.println();
        String top = "╔" + "═".repeat(WIDTH - 2) + "╗";
        String mid = "║" + " ".repeat(WIDTH - 2) + "║";
        String bot = "╚" + "═".repeat(WIDTH - 2) + "╝";
        revealLine(PAD + fg(OVER_GRAD[0]) + BOLD + top + RESET, BANNER_LINE_MS);
        revealLine(PAD + fg(OVER_GRAD[1]) + BOLD + mid + RESET, BANNER_LINE_MS);
        revealLine(PAD + fg(OVER_GRAD[2]) + BOLD + "║" + center("F I N E   P A R T I T A", WIDTH - 2) + "║" + RESET, BANNER_LINE_MS);
        revealLine(PAD + fg(OVER_GRAD[3]) + BOLD + mid + RESET, BANNER_LINE_MS);
        revealLine(PAD + fg(OVER_GRAD[4]) + BOLD + bot + RESET, BANNER_LINE_MS);
        out.println();

        if (!players.isEmpty()) {
            typewrite(PAD + GOLD + BOLD + "  ★ VINCITORE: " + players.get(0).name().toUpperCase() + " ★" + RESET);
            out.println();
        }

        revealLine(PAD + WHITE + BOLD + "  Classifica finale" + RESET, LINE_MS);
        revealLine(PAD + DIM + "─".repeat(WIDTH) + RESET, LINE_MS);
        String[] medals = {fg(220) + "(1)" + RESET, fg(250) + "(2)" + RESET, fg(173) + "(3)" + RESET};
        for (int i = 0; i < players.size(); i++) {
            PlayerDTO p = players.get(i);
            String pos = i < medals.length ? medals[i] : "   ";
            String hl  = i == 0 ? GOLD + BOLD : WHITE;
            revealLine(String.format("%s  %s  %s%-18s%s   %s* %2d%s   %s$ %2d%s",
                    PAD, pos,
                    hl, p.name(), RESET,
                    CYAN,   p.reputation(), RESET,
                    YELLOW, p.gold(),       RESET), BANNER_LINE_MS);
        }
        out.println();
    }

    public void promptContinue() {
        out.printf("%n%s%sPremi INVIO per continuare...%s", PAD, DIM, RESET);
        scanner.nextLine();
        out.println();
    }

    // --- Input helpers ------------------------------------------------------

    public int readInt() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                out.printf("%s%s[!] Inserisci un numero intero:%s ", PAD, RED, RESET);
            }
        }
    }

    public String readLine() {
        while (true) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            out.printf("%s%s[!] Input non puo' essere vuoto:%s ", PAD, RED, RESET);
        }
    }

    private boolean readYesNo() {
        while (true) {
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("s")) return true;
            if (line.equals("n")) return false;
            out.printf("%s%s[!] Inserisci 's' o 'n':%s ", PAD, RED, RESET);
        }
    }

    private int promptBoundedInt(String prompt, int max) {
        while (true) {
            out.printf("%s%s %s(1-%d | L lab)%s: ", PAD, prompt, DIM, max, RESET);
            String raw = scanner.nextLine().trim();
            if (raw.equalsIgnoreCase("L")) { showLabScreen(); continue; }
            try {
                int choice = Integer.parseInt(raw);
                if (choice >= 1 && choice <= max) return choice;
                out.printf("%s%s[!] Inserisci un valore tra 1 e %d.%s%n", PAD, RED, max, RESET);
            } catch (NumberFormatException e) {
                out.printf("%s%s[!] Inserisci un numero tra 1 e %d.%s%n", PAD, RED, max, RESET);
            }
        }
    }

    // --- Layout helpers -----------------------------------------------------

    /** Lunghezza visibile di una stringa, ignorando le sequenze ANSI. */
    private int visibleLength(String s) {
        int len = 0;
        int i = 0;
        int n = s.length();
        while (i < n) {
            char c = s.charAt(i);
            if (c == '\033' && i + 1 < n && s.charAt(i + 1) == '[') {
                i += 2;
                while (i < n && !Character.isLetter(s.charAt(i))) i++;
                if (i < n) i++;
            } else {
                len++;
                i++;
            }
        }
        return len;
    }

    private String center(String text, int width) {
        int vis = visibleLength(text);
        if (vis >= width) return text;
        int padding = width - vis;
        int left = padding / 2;
        return " ".repeat(left) + text + " ".repeat(padding - left);
    }

    private String padRight(String text, int width) {
        if (text.length() >= width) return text.substring(0, width);
        return text + " ".repeat(width - text.length());
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 1)) + "~";
    }

    // --- Animation helpers --------------------------------------------------

    private void sleep(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void typewrite(String line) {
        int i = 0;
        int n = line.length();
        while (i < n) {
            char c = line.charAt(i);
            if (c == '\033' && i + 1 < n && line.charAt(i + 1) == '[') {
                int end = i + 2;
                while (end < n && !Character.isLetter(line.charAt(end))) end++;
                if (end < n) end++;
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
