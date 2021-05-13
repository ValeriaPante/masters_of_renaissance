package it.polimi.ingsw.Messages.InGameMessages;

import it.polimi.ingsw.Controller.ControllerSwitch;
import it.polimi.ingsw.Messages.IntMessage;

public class DiscountAbilityMessage extends IntMessage {
    public DiscountAbilityMessage(int integer) {
        super(integer);
    }

    public void readThrough(ControllerSwitch controllerSwitch){
        controllerSwitch.actionOnMessage(this);
    }
}