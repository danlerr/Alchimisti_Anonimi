title UC09
user->system:getTargets()
user<--system:targets
user->system:paymentCheck(targetId)
user<--system:paymentRequired
opt payment == true & payment confirmed
    user->system:payGold(targetId)
    user<--system:gold
end
user->system:getIngredients()
user<--system:ingridientsList
user->system:conductExperiment(targetId, ingredientId1, ingredientId2)
user<--system:potion
opt update deduction grid
    user->system: getPlayerDeductionGrid()
    user<--system: grid
    user->system: updateDeductionGrid(ingredient, formula)
    user<--system: done
end

title UC01
user->system:getAvailableSlots()
user<--system:availableSlots
user->system:chooseSlot(slotId)
user<--system:resources

title UC02
user->system:getActionsList()
user<--system:actionsList
user->system:declareAction(actionId)

title UC03
user->system:forageIngredient(ingredientId)

title UC04
user->system:getIngredients()
user<--system:ingridientsList
user->system:trasmuteIngredient(ingredientId)