package it.polimi.ingsw.Messages.InGameMessages.ConcreteMessages;

import it.polimi.ingsw.Controller.InGameControllerSwitch;
import it.polimi.ingsw.Messages.InGameMessages.InGameMessage;
import it.polimi.ingsw.Messages.InGameMessages.MessageConverterToJSON;

public class DeselectAllResources extends InGameMessage {
    @Override
    public void readThrough(InGameControllerSwitch inGameControllerSwitch) {
        inGameControllerSwitch.actionOnMessage(this);
    }

    @Override
    public String toJson() {
        return MessageConverterToJSON.convertToJson(this);
    }
}
