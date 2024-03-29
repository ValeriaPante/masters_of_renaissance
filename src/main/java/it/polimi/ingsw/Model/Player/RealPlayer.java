package it.polimi.ingsw.Model.Player;

import it.polimi.ingsw.Exceptions.WrongLeaderCardType;
import it.polimi.ingsw.Model.Abilities.ProductionPower.ProductionPower;
import it.polimi.ingsw.Model.Cards.LeaderCard;
import it.polimi.ingsw.Model.Cards.PopeFavorCard;
import it.polimi.ingsw.Model.Deposit.Shelf;
import it.polimi.ingsw.Model.Deposit.Depot;
import it.polimi.ingsw.Model.Deposit.StrongBox;
import it.polimi.ingsw.Enums.MacroTurnType;
import it.polimi.ingsw.Enums.MicroTurnType;
import it.polimi.ingsw.Enums.Resource;
import it.polimi.ingsw.Network.Client.Messages.ErrorMessage;
import it.polimi.ingsw.Network.Client.Messages.FromServerMessage;
import it.polimi.ingsw.Network.Client.Messages.SelectionErrorMessage;
import it.polimi.ingsw.PreGameModel.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;

public class RealPlayer extends Player{

    private final User connection;
    private DevSlot[] devSlots;
    private Shelf[] shelves;
    private final StrongBox strongBox;
    private final ArrayList<LeaderCard> leaderCards;
    private PopeFavorCard[] popeFavorCards;
    private final BasicProductionPower basicProductionPower;
    private final TurnType turnType;
    private final StrongBox supportContainer;
    private String errorMessage;

    private void initialiseShelves(){
        this.shelves = new Shelf[]{
                new Shelf(1),
                new Shelf(2),
                new Shelf(3),
        };
    }

    private void initialiseDevSlots(){
        this.devSlots = new DevSlot[]{
                new DevSlot(),
                new DevSlot(),
                new DevSlot(),
        };
    }

    private void initialisePopeFavorCards(){
        this.popeFavorCards = new PopeFavorCard[]{
                new PopeFavorCard(2),
                new PopeFavorCard(3),
                new PopeFavorCard(4),
        };
    }

    public RealPlayer(User user){
       super(user.getUsername());
       this.connection = user;
       this.initialiseDevSlots();
       this.initialiseShelves();
       this.strongBox = new StrongBox();
       this.leaderCards = new ArrayList<>();
       this.initialisePopeFavorCards();
       this.basicProductionPower = new BasicProductionPower();
       this.turnType = new TurnType();
       this.supportContainer = new StrongBox();
       this.errorMessage = null;

    }
    public void addLeaderCard(LeaderCard leaderCard){
        this.leaderCards.add(leaderCard);
    }
    public void discardLeaderCard(LeaderCard leaderCard){
        this.leaderCards.remove(leaderCard);
    }

    public BasicProductionPower getBasicProductionPower(){
        return this.basicProductionPower;
    }
    public DevSlot[] getDevSlots(){
        return Arrays.copyOf(this.devSlots, this.devSlots.length);
    }
    public Shelf[] getShelves(){
        return Arrays.copyOf(this.shelves, this.shelves.length);
    }
    public StrongBox getStrongBox(){
        return this.strongBox;
    }
    public LeaderCard[] getLeaderCards(){
        return this.leaderCards.toArray(new LeaderCard[0]);
    }
    public LinkedList<ProductionPower> getAllProductionPowers(){
        return this.calculateAllProductionPowers();
    }
    public EnumMap<Resource, Integer> getResourcesOwned(){
        return this.resourcesOwned();
    }
    public MacroTurnType getMacroTurnType(){
        return this.turnType.getMacroTurnType();
    }
    public MicroTurnType getMicroTurnType(){
        return this.turnType.getMicroTurnType();
    }
    public PopeFavorCard[] getPopeFavorCards(){
        return Arrays.copyOf(this.popeFavorCards, this.popeFavorCards.length);
    }
    public int getNumberOfDevCardOwned(){
        int result = 0;
        for (DevSlot devSlot : devSlots) {
            result += devSlot.getDevCardTypeContained().size();
        }
        return result;
    }


    public void setMacroTurnType(MacroTurnType type){
        this.turnType.setMacroTurnType(type);
    }
    public void setMicroTurnType(MicroTurnType type){
        this.turnType.setMicroTurnType(type);
    }

    /**
     * Gets all the resources owned by this player
     * @return null if the player owns no Resources, otherwise it will return an EnumMap with the copy of all resources
     */
    private EnumMap<Resource, Integer> resourcesOwned() {
        Depot allResources = new Depot();

        if (!this.strongBox.isEmpty())
            allResources.addEnumMap(this.strongBox.content());

        for (Shelf shelf : this.shelves)
            if (!shelf.isEmpty())
                allResources.addEnumMap(shelf.content());

        for (LeaderCard leaderCard : leaderCards) {
            if (leaderCard.hasBeenPlayed()) {
                try {
                    if (!leaderCard.getAbility().isEmpty())
                        allResources.addEnumMap(leaderCard.getAbility().getContent());
                } catch (WrongLeaderCardType ignored) {
                }
            }
        }
        return (allResources.content() == null) ? new EnumMap<>(Resource.class) : allResources.content();
    }

    /**
     * Gets all the production powers of this player
     * @return all the production powers that the player has
     */
    private LinkedList<ProductionPower> calculateAllProductionPowers(){
        LinkedList<ProductionPower> allProductionPowers = new LinkedList<>();

        allProductionPowers.add(this.basicProductionPower.getProductionPower());

        for (DevSlot devSlot : this.devSlots){
            if (!devSlot.isEmpty()){
                allProductionPowers.add(devSlot.topCard().getProdPower());
            }
        }

        for (LeaderCard leaderCard : this.leaderCards){
            if (leaderCard.hasBeenPlayed()) {
                try {
                    allProductionPowers.add(leaderCard.getAbility().getProductionPower());
                } catch (WrongLeaderCardType ignored) {
                }
            }
        }
        return allProductionPowers;
    }

    public StrongBox getSupportContainer() {
        return this.supportContainer;
    }

    public void sendMessage(FromServerMessage message){
        this.connection.send(message);
    }

    public void setErrorMessage(String newErrorMessage) {
        this.errorMessage = newErrorMessage;
        ErrorMessage errorMessage = new ErrorMessage(this.errorMessage);
        this.connection.send(errorMessage);
    }

    public void setErrorMessage(String newErrorMessage, int wrongCardId){
        this.errorMessage = newErrorMessage;
        SelectionErrorMessage errorMessage = new SelectionErrorMessage(this.errorMessage, wrongCardId);
        this.connection.send(errorMessage);
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void clearErrorMessage(){
        this.errorMessage = null;
    }

    @Override
    public int getId() {
        return this.connection.getId();
    }

    public void closeConnection(){
        this.connection.closeConnection();
    }
}
