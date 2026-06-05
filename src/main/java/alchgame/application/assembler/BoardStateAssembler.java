package alchgame.application.assembler;

import alchgame.application.dto.BoardStateDTO;
import alchgame.model.board.Board;
import alchgame.model.player.Player;

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
        for (String id : actionIds)
            declarantsByAction.put(id, board.getActionSpace(id).getDeclaredPlayers().stream()
                    .map(Player::getName)
                    .toList());

        return new BoardStateDTO(orderSlots, wakeUpOrder, actionIds, declarantsByAction);
    }
}
