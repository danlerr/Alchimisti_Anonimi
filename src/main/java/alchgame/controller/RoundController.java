package alchgame.controller;

import alchgame.dto.ActionResolution;
import alchgame.dto.OrderSlotDTO;
import alchgame.dto.ResourceGainDTO;
import alchgame.dto.SlotAssignmentDTO;
import alchgame.model.board.Resources;
import alchgame.model.game.Round;
import alchgame.model.player.Player;
import alchgame.resources.GameConfig;

import java.util.List;
import java.util.function.Supplier;

public class RoundController {

    private final Supplier<Round> round;

    public RoundController(Supplier<Round> roundSupplier) {
        this.round = roundSupplier;
    }

    public List<String> getOrderPhaseOrder() {
        return round.get().getOrderPhaseOrder().stream()
                .map(Player::getName)
                .toList();
    }

    public List<OrderSlotDTO> getAvailableSlots() {
        return round.get().getAvailableSlotIds().stream()
                .map(slotId -> new OrderSlotDTO(slotId, resourcesFor(slotId)))
                .toList();
    }

    public SlotAssignmentDTO chooseSlot(String orderSlotID) {
        String playerName = round.get().getCurrentPlayer().getName();
        Resources resources = round.get().chooseSlot(orderSlotID);
        return new SlotAssignmentDTO(playerName, orderSlotID, resourceGain(resources));
    }

    public List<String> getDeclarationPhaseOrder() {
        return round.get().getDeclarationPhaseOrder().stream()
                .map(Player::getName)
                .toList();
    }

    public List<String> getActionList() {
        return GameConfig.ACTION_ORDER;
    }

    public void declareAction(String actionSpaceId) {
        round.get().declareAction(actionSpaceId);
    }

    public void advancePhase() {
        round.get().advancePhase();
    }

    public void setCurrentPlayerByName(String playerName) {
        round.get().setCurrentPlayerByName(playerName);
    }

    public int getCurrentPlayerActionCubes() {
        return round.get().getCurrentPlayer().getActionCubes();
    }

    public List<String> getWakeUpOrder() {
        return round.get().getWakeUpOrderNames();
    }

    private List<String> getResolutionOrderFor(String actionSpaceId) {
        return round.get().getResolutionOrder(actionSpaceId).stream()
                .map(Player::getName)
                .toList();
    }

    public ActionResolution prepareActionResolution(String actionSpaceId) {
        return new ActionResolution(actionSpaceId, getResolutionOrderFor(actionSpaceId));
    }

    private ResourceGainDTO resourcesFor(String slotId) {
        return GameConfig.SLOTS.stream()
                .filter(spec -> spec.id().equals(slotId))
                .findFirst()
                .map(spec -> new ResourceGainDTO(spec.ingredientCount(), spec.favorCount()))
                .orElse(new ResourceGainDTO(0, 0));
    }

    private ResourceGainDTO resourceGain(Resources resources) {
        return new ResourceGainDTO(resources.ingredientCount(), resources.favorCount());
    }
}
