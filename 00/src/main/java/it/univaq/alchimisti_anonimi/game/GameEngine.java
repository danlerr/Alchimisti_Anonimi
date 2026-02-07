package it.univaq.alchimisti_anonimi.game;

import it.univaq.alchimisti_anonimi.domain.Experiment;
import it.univaq.alchimisti_anonimi.domain.ExperimentTarget;
import it.univaq.alchimisti_anonimi.domain.Player;
import it.univaq.alchimisti_anonimi.domain.PrivateLaboratory;
import it.univaq.alchimisti_anonimi.domain.PublicPlayerBoard;
import it.univaq.alchimisti_anonimi.domain.Student;
import it.univaq.alchimisti_anonimi.services.AlchemicAlgorithm;

import java.util.Objects;

public class GameEngine {
    private final PrivateLaboratory privateLaboratory;
    private final PublicPlayerBoard publicPlayerBoard;
    private final AlchemicAlgorithm alchemicAlgorithm;
    private final Student student;
    private Player currentPlayer;

    public GameEngine(PrivateLaboratory privateLaboratory,
                      PublicPlayerBoard publicPlayerBoard,
                      AlchemicAlgorithm alchemicAlgorithm,
                      Student student,
                      Player currentPlayer) {
        this.privateLaboratory = Objects.requireNonNull(privateLaboratory);
        this.publicPlayerBoard = Objects.requireNonNull(publicPlayerBoard);
        this.alchemicAlgorithm = Objects.requireNonNull(alchemicAlgorithm);
        this.student = Objects.requireNonNull(student);
        this.currentPlayer = Objects.requireNonNull(currentPlayer);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = Objects.requireNonNull(player);
    }

    public PrivateLaboratory getPrivateLaboratory() {
        return privateLaboratory;
    }

    public PublicPlayerBoard getPublicPlayerBoard() {
        return publicPlayerBoard;
    }

    public AlchemicAlgorithm getAlchemicAlgorithm() {
        return alchemicAlgorithm;
    }

    public Student getStudent() {
        return student;
    }

    public Experiment startExperiment(ExperimentTarget target, Player player) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(player);
        return new Experiment(target, target == ExperimentTarget.STUDENT ? student : null);
    }
}
