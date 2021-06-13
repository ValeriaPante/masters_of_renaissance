package it.polimi.ingsw.Model.Abilities.ProductionPower;

import it.polimi.ingsw.Enums.Resource;

import java.util.EnumMap;

public class WithProductionPowerBehavior implements ProductionPowerBehavior {
    private final EnumMap<Resource, Integer> input;
    private final EnumMap<Resource, Integer> output;

    @Override
    public ProductionPower getProductionPower() {
        return new ProductionPower(this.input, this.output);
    }

    public WithProductionPowerBehavior(EnumMap<Resource, Integer> input, EnumMap<Resource, Integer> output){
        this.input = input.clone();

        if(output.isEmpty()){
            this.output = new EnumMap<>(Resource.class);
            this.output.put(Resource.ANY, 1);
            this.output.put(Resource.FAITH,1);
        } else {
            this.output = output.clone();
        }

    }
}
