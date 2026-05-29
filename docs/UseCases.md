title UC09
user->system:getTargets()
user<--system:targets
user->system:paymentCheck()
user<--system:paymentRequired
alt payment == true & payment confirmed
user->system:payGold()
user<--system:gold
else payment == true & payment not confirmed
user->system:refuseExperiment() & breakUC
end
user->system:getIngredients()
user<--system:ingridientsList
user->system:conductExperiment(targetId, ingredientId1, ingredientId2)
user<--system:potion

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