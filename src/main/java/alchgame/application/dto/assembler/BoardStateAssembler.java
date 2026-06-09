package alchgame.application.dto.assembler;

import alchgame.application.dto.BoardStateDTO;
import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BoardStateAssembler {

    public static BoardStateDTO toDTO(Board board) {
        Map<String, String> orderSlots = new LinkedHashMap<>();
        board.getOrderAssignments().forEach((slot, player) ->
                orderSlots.put(slot, player != null ? player.getName() : null));

        List<String> wakeUpOrder = board.getWakeUpOrder().stream()
                .map(Player::getName)
                .toList();

        List<String> actionIds = board.getActionSpaceIds();

        Map<String, List<String>> declarantsByAction = new LinkedHashMap<>();
        for (String id : actionIds) {
            List<Player> declared = new ArrayList<>(board.getDeclaredPlayers(id));
            Collections.reverse(declared);
            declarantsByAction.put(id, declared.stream().map(Player::getName).toList());
        }

        Map<String, List<String>> slotRewardDescriptions = new LinkedHashMap<>();
        for (String id : board.getAvailableSlotIds()) {
            slotRewardDescriptions.put(id, board.getSlotRewardDescriptions(id));
}
        return new BoardStateDTO(orderSlots, wakeUpOrder, actionIds, declarantsByAction, slotRewardDescriptions);
    }
}
