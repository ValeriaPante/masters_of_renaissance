package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Enums.Resource;

public class ResourceIntMessage extends IntMessage{
    private final Resource resource;

    public ResourceIntMessage(int integer, Resource resource) {
        super(integer);
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }
}
