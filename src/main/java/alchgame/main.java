package alchgame;

import alchgame.controller.ExperimentHandler;
import alchgame.model.*;
import alchgame.service.AlchemicAlgorithm;
import alchgame.service.AlchemicMapping;
import alchgame.service.GameContext;

import java.util.*;

public class main {

    static final String RESET  = "\033[0m";
    static final String BOLD   = "\033[1m";
    static final String CYAN   = "\033[36m";
    static final String YELLOW = "\033[33m";
    static final String GREEN  = "\033[32m";
    static final String RED    = "\033[31m";
    static final String MAGENTA= "\033[35m";
    static final String DIM    = "\033[2m";

    static Scanner scanner = new Scanner(System.in);

    static Player            player;
    static Student           student;
    static GameContext       gameContext;
    static ExperimentHandler handler;

    public static void main(String[] args) {
        setupGame();
        mainMenu();
    }

    static void setupGame() {
        // 8 ingredienti del gioco (i giocatori conoscono solo il nome)
        List<Ingredient> allIngredients = List.of(
            new Ingredient("Felce"),
            new Ingredient("Mandragora"),
            new Ingredient("Artiglio"),
            new Ingredient("Fiore"),
            new Ingredient("Fungo"),
            new Ingredient("Rospo"),
            new Ingredient("Piuma"),
            new Ingredient("Scorpione")
        );

        // 8 formule alchemiche fisse (una per ogni alchemico del gioco).
        // Ogni formula: un atomo RED, uno GREEN, uno BLUE con size e sign specifici.
        List<AlchemicFormula> allFormulas = List.of(
            // 1: R+G  G+G  B+G
            new AlchemicFormula(List.of(new Atom(Color.RED, Size.BIG,   Sign.POSITIVE), new Atom(Color.GREEN, Size.BIG,   Sign.POSITIVE), new Atom(Color.BLUE, Size.BIG,   Sign.POSITIVE))),
            // 2: R-s  G+s  B-G
            new AlchemicFormula(List.of(new Atom(Color.RED, Size.SMALL, Sign.NEGATIVE), new Atom(Color.GREEN, Size.SMALL, Sign.POSITIVE), new Atom(Color.BLUE, Size.BIG,   Sign.NEGATIVE))),
            // 3: R-G  G-G  B-G
            new AlchemicFormula(List.of(new Atom(Color.RED, Size.BIG,   Sign.NEGATIVE), new Atom(Color.GREEN, Size.BIG,   Sign.NEGATIVE), new Atom(Color.BLUE, Size.BIG,   Sign.NEGATIVE))),
            // 4: R-s  G+G  B+s
            new AlchemicFormula(List.of(new Atom(Color.RED, Size.SMALL, Sign.NEGATIVE), new Atom(Color.GREEN, Size.BIG,   Sign.POSITIVE), new Atom(Color.BLUE, Size.SMALL, Sign.POSITIVE))),
            // 5: R+G  G+s  B-s
            new AlchemicFormula(List.of(new Atom(Color.RED, Size.BIG,   Sign.POSITIVE), new Atom(Color.GREEN, Size.SMALL, Sign.POSITIVE), new Atom(Color.BLUE, Size.SMALL, Sign.NEGATIVE))),
            // 6: R+s  G-G  B-s
            new AlchemicFormula(List.of(new Atom(Color.RED, Size.SMALL, Sign.POSITIVE), new Atom(Color.GREEN, Size.BIG,   Sign.NEGATIVE), new Atom(Color.BLUE, Size.SMALL, Sign.NEGATIVE))),
            // 7: R+s  G-s  B+G
            new AlchemicFormula(List.of(new Atom(Color.RED, Size.SMALL, Sign.POSITIVE), new Atom(Color.GREEN, Size.SMALL, Sign.NEGATIVE), new Atom(Color.BLUE, Size.BIG,   Sign.POSITIVE))),
            // 8: R-G  G-s  B+G
            new AlchemicFormula(List.of(new Atom(Color.RED, Size.BIG,   Sign.NEGATIVE), new Atom(Color.GREEN, Size.SMALL, Sign.NEGATIVE), new Atom(Color.BLUE, Size.BIG,   Sign.POSITIVE)))
        );

        // Mapping randomico: ogni ingrediente riceve una formula diversa (shuffle)
        List<AlchemicFormula> shuffled = new ArrayList<>(allFormulas);
        Collections.shuffle(shuffled);
        Map<Ingredient, AlchemicFormula> rawMapping = new HashMap<>();
        for (int i = 0; i < allIngredients.size(); i++)
            rawMapping.put(allIngredients.get(i), shuffled.get(i));
        AlchemicMapping alchemicMapping = new AlchemicMapping(rawMapping);

        DeductionGrid   grid     = new DeductionGrid(allIngredients, allFormulas);
        ResultsTriangle triangle = new ResultsTriangle();
        PrivateLaboratory lab    = new PrivateLaboratory(
                new ArrayList<>(allIngredients), grid, triangle);

        PublicPlayerBoard board = new PublicPlayerBoard();
        player = new Player(5, 10, lab, board);

        student = new Student();

        gameContext = new GameContext(player, Map.of(
                "student-1", student,
                "self",      player));

        handler = new ExperimentHandler(gameContext, new AlchemicAlgorithm(alchemicMapping));
    }

    static void mainMenu() {
        while (true) {
            clearScreen();
            printHeader();
            printStatus();
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

            switch (scanner.nextLine().trim()) {
                case "1" -> flowIniziaEsperimento();
                case "2" -> viewLaboratorio();
                case "3" -> viewTabellone();
                case "4" -> viewTarget();
                case "0" -> { System.out.println(GREEN + "\n  Arrivederci!\n" + RESET); return; }
                default  -> pause(RED + "  Scelta non valida. Premi INVIO..." + RESET);
            }
        }
    }

    static void flowIniziaEsperimento() {
        clearScreen();
        printSection("INIZIA ESPERIMENTO");

        System.out.println("  Scegli il target:");
        System.out.println("  " + YELLOW + "[1]" + RESET + " Student      " + DIM + "(gratuito)" + RESET);
        System.out.println("  " + YELLOW + "[2]" + RESET + " Su Te Stesso " + DIM + "(gratuito)" + RESET);
        System.out.println("  " + YELLOW + "[0]" + RESET + " Annulla");
        System.out.print(BOLD + "\n  Scelta > " + RESET);

        String targetId = switch (scanner.nextLine().trim()) {
            case "1" -> "student-1";
            case "2" -> "self";
            default  -> null;
        };
        if (targetId == null) return;

        ExperimentStep result = handler.startExperiment(targetId);

        if (result instanceof PaymentRequest) {
            System.out.println("\n  " + YELLOW + "⚠  Questo target richiede 1 moneta d'oro." + RESET);
            System.out.println("  Oro attuale: " + BOLD + player.getGold() + RESET);
            System.out.print("  Vuoi pagare? [s/n] > ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
                pause("  Esperimento annullato. Premi INVIO..."); return;
            }
            try {
                result = handler.pagaOro();
                System.out.println("  " + GREEN + "✓ Pagamento effettuato. Oro rimasto: " + player.getGold() + RESET);
            } catch (IllegalStateException e) {
                pause(RED + "  ✗ " + e.getMessage() + " Premi INVIO..." + RESET); return;
            }
        }

        if (result instanceof IngredientsRequest req) {
            List<Ingredient> available = req.getIngredients();
            if (available.size() < 2) {
                pause(RED + "  ✗ Non hai abbastanza ingredienti! Premi INVIO..." + RESET); return;
            }

            System.out.println("\n  " + CYAN + "Ingredienti disponibili:" + RESET);
            for (int i = 0; i < available.size(); i++)
                System.out.println("  " + YELLOW + "[" + (i+1) + "]" + RESET + " " + available.get(i).getName());

            Ingredient i1 = pickIngredient(available, "  Scegli il 1° ingrediente > ", null);
            if (i1 == null) return;
            Ingredient i2 = pickIngredient(available, "  Scegli il 2° ingrediente > ", i1);
            if (i2 == null) return;

            System.out.println("\n  " + DIM + "Eseguendo l'esperimento..." + RESET);
            handler.conductExperiment(i1, i2);

            List<Experiment> exps = player.getConductedExperiments();
            Potion potion = exps.get(exps.size() - 1).getPotion();

            clearScreen();
            printSection("RISULTATO ESPERIMENTO");
            System.out.println("  " + MAGENTA + BOLD + "  " + i1.getName() + " + " + i2.getName() + RESET);
            System.out.println("  ─────────────────────────────────");
            String pc = potion.isNegative() ? RED : GREEN;
            System.out.println("  Pozione prodotta: " + pc + BOLD + potion.getColor().name() + " " + potion.getSign().name() + RESET);
            System.out.println("  Effetto:          " + (potion.isNegative() ? RED + "NEGATIVO ✗" : GREEN + "POSITIVO ✓") + RESET);
            if ("student-1".equals(targetId))
                System.out.println("  Stato Student:    " + BOLD + student.getState() + RESET);
            else
                System.out.println("  Tua reputazione:  " + BOLD + player.getReputation() + RESET);

            pause("\n  " + GREEN + "Premi INVIO per continuare..." + RESET);
        }
    }

    static void viewLaboratorio() {
        clearScreen();
        printSection("LABORATORIO PRIVATO");
        PrivateLaboratory lab = player.getPrivateLaboratory();

        System.out.println("  " + CYAN + "Ingredienti disponibili:" + RESET);
        List<Ingredient> ings = lab.getIngredients();
        if (ings.isEmpty()) System.out.println("  " + DIM + "(nessuno)" + RESET);
        else ings.forEach(i -> System.out.println("    • " + i.getName()));

        System.out.println("\n  " + CYAN + "Triangolo dei risultati:" + RESET);
        var triangleResults = lab.getResultsTriangle().getAllResults();
        if (triangleResults.isEmpty()) System.out.println("  " + DIM + "(nessun esperimento ancora)" + RESET);
        else triangleResults.forEach((pair, potion) -> {
            List<String> names = pair.stream().map(Ingredient::getName).sorted().toList();
            String pc = potion.isNegative() ? RED : GREEN;
            System.out.println("    • " + names.get(0) + " + " + names.get(1) +
                    " → " + pc + BOLD + potion.getColor() + " " + potion.getSign() + RESET);
        });

        System.out.println("\n  " + CYAN + "DeductionGrid — alchemici esclusi per ingrediente:" + RESET);
        List<String> excl = lab.getDeductionGrid().getExclusionsSummary();
        if (excl.isEmpty()) System.out.println("  " + DIM + "(nessuna esclusione ancora)" + RESET);
        else excl.forEach(a -> System.out.println("    • " + a));

        pause("\n  " + GREEN + "Premi INVIO per tornare..." + RESET);
    }

    static void viewTabellone() {
        clearScreen();
        printSection("TABELLONE PUBBLICO");
        List<Potion> results = player.getPublicPlayerBoard().getPublishedResults();
        if (results.isEmpty()) System.out.println("  " + DIM + "(nessun risultato ancora)" + RESET);
        else for (int i = 0; i < results.size(); i++) {
            Potion p = results.get(i);
            String c = p.isNegative() ? RED : GREEN;
            System.out.println("  [" + (i+1) + "] " + c + p.getColor() + " " + p.getSign() + RESET);
        }
        pause("\n  " + GREEN + "Premi INVIO per tornare..." + RESET);
    }

    static void viewTarget() {
        clearScreen();
        printSection("STATO DEI TARGET");
        System.out.println("  " + CYAN + "Student:" + RESET);
        String sc = student.getState() == StudentState.HAPPY ? GREEN : RED;
        System.out.println("    Stato → " + sc + BOLD + student.getState() + RESET);

        System.out.println("\n  " + CYAN + "Tu stesso:" + RESET);
        System.out.println("    Oro         → " + BOLD + player.getGold() + RESET);
        System.out.println("    Reputazione → " + BOLD + player.getReputation() + RESET);

        pause("\n  " + GREEN + "Premi INVIO per tornare..." + RESET);
    }

    static Ingredient pickIngredient(List<Ingredient> list, String prompt, Ingredient exclude) {
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

    static void printHeader() {
        System.out.println(MAGENTA + BOLD);
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║      ✦   Alchimisti Anonimi  ✦       ║");
        System.out.println(". ╚══════════════════════════════════════╝" + RESET);
    }

    static void printStatus() {
        System.out.println("  " + DIM + "Oro: " + RESET + YELLOW + BOLD + player.getGold() + RESET +
                DIM + "  |  Reputazione: " + RESET + BOLD + player.getReputation() + RESET +
                DIM + "  |  Esperimenti: " + RESET + BOLD + player.getConductedExperiments().size() + RESET +
                DIM + "  |  Ingredienti: " + RESET + BOLD + player.getPrivateLaboratory().getIngredients().size() + RESET);
        System.out.println();
    }

    static void printSection(String title) {
        System.out.println(CYAN + BOLD + "  ══ " + title + " ══" + RESET + "\n");
    }

    static void clearScreen() { System.out.print("\033[H\033[2J"); System.out.flush(); }
    static void pause(String msg) { if (!msg.isEmpty()) System.out.println(msg); scanner.nextLine(); }
}