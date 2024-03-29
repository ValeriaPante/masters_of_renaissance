package ControllerTest;

import it.polimi.ingsw.Enums.MicroTurnType;
import it.polimi.ingsw.Exceptions.GameOver;
import it.polimi.ingsw.Model.Cards.DevCardType;
import it.polimi.ingsw.Model.Cards.LeaderCard;
import it.polimi.ingsw.Controller.BuyDevCardController;
import it.polimi.ingsw.Controller.FaithTrackController;
import it.polimi.ingsw.Controller.LeaderController;
import it.polimi.ingsw.Enums.LeaderCardType;
import it.polimi.ingsw.Enums.MacroTurnType;
import it.polimi.ingsw.Enums.Resource;
import it.polimi.ingsw.Model.Game.Table;
import it.polimi.ingsw.Model.Player.RealPlayer;
import it.polimi.ingsw.PreGameModel.User;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BuyDevCardControllerTest {

    @Test
    public void validBuying(){
        Table table = new Table(2);
        table.addPlayer(new RealPlayer(new User("user1", new FakeConnectionHandler(1))));
        table.addPlayer(new RealPlayer(new User("user1", new FakeConnectionHandler(2))));
        table.turnOf().setMacroTurnType(MacroTurnType.NONE);

        EnumMap<Resource, Integer> resourceOwned = new EnumMap<>(Resource.class);
        resourceOwned.put(Resource.SERVANT, 50);
        resourceOwned.put(Resource.COIN, 50);
        resourceOwned.put(Resource.SHIELD, 50);
        resourceOwned.put(Resource.STONE, 50);

        table.turnOf().getStrongBox().addEnumMap(resourceOwned);

        BuyDevCardController controller = new BuyDevCardController(new FaithTrackController(table));
        controller.chooseDevCard(2);
        controller.chooseDevCard(3);
        int id = table.getDevDecks()[2].getTopCard().getId();
        EnumMap<Resource, Integer> cost = table.getDevDecks()[2].getTopCard().getCost();

        controller.buyDevCard();
        table.turnOf().getStrongBox().mapSelection(cost);
        controller.paySelected();
        controller.chooseDevSlot(2);
        assertTrue(table.turnOf().getDevSlots()[0].isEmpty());
        assertFalse(table.turnOf().getDevSlots()[1].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[2].isEmpty());
        assertEquals(id, table.turnOf().getDevSlots()[1].topCard().getId());

        for(EnumMap.Entry<Resource, Integer> entry: cost.entrySet())
            assertEquals(50 - entry.getValue(), table.turnOf().getStrongBox().content().get(entry.getKey()));

        table.turnOf().setMacroTurnType(MacroTurnType.NONE);
        table.turnOf().setMicroTurnType(MicroTurnType.NONE);
        controller.chooseDevCard(1);
        cost = table.getDevDecks()[0].getTopCard().getCost();
        controller.buyDevCard();
        table.turnOf().getStrongBox().clearSelection();
        table.turnOf().getStrongBox().mapSelection(cost);
        controller.paySelected();
        controller.chooseDevSlot(1);

        table.turnOf().setMacroTurnType(MacroTurnType.NONE);
        table.turnOf().setMicroTurnType(MicroTurnType.NONE);
        controller.chooseDevCard(2);
        cost = table.getDevDecks()[1].getTopCard().getCost();
        controller.buyDevCard();
        table.turnOf().getStrongBox().mapSelection(cost);
        controller.paySelected();
        controller.chooseDevSlot(3);

        table.turnOf().setMacroTurnType(MacroTurnType.NONE);
        table.turnOf().setMicroTurnType(MicroTurnType.NONE);
        controller.chooseDevCard(5);
        cost = table.getDevDecks()[4].getTopCard().getCost();
        controller.buyDevCard();
        table.turnOf().getStrongBox().mapSelection(cost);
        controller.paySelected();
        controller.chooseDevSlot(1);

        table.turnOf().setMacroTurnType(MacroTurnType.NONE);
        table.turnOf().setMicroTurnType(MicroTurnType.NONE);
        controller.chooseDevCard(5);
        cost = table.getDevDecks()[4].getTopCard().getCost();
        controller.buyDevCard();
        table.turnOf().getStrongBox().mapSelection(cost);
        controller.paySelected();
        controller.chooseDevSlot(2);

        table.turnOf().setMacroTurnType(MacroTurnType.NONE);
        table.turnOf().setMicroTurnType(MicroTurnType.NONE);
        controller.chooseDevCard(5);
        cost = table.getDevDecks()[4].getTopCard().getCost();
        controller.buyDevCard();
        table.turnOf().getStrongBox().mapSelection(cost);
        controller.paySelected();
        controller.chooseDevSlot(3);

        table.turnOf().setMacroTurnType(MacroTurnType.NONE);
        table.turnOf().setMicroTurnType(MicroTurnType.NONE);
        controller.chooseDevCard(9);
        cost = table.getDevDecks()[8].getTopCard().getCost();
        controller.buyDevCard();
        table.turnOf().getStrongBox().mapSelection(cost);
        controller.paySelected();
        assertThrows(GameOver.class, () -> controller.chooseDevSlot(3));
    }

    @Test
    public void invalidBuying(){
        Table table = new Table(2);
        table.addPlayer(new RealPlayer(new User("user1", new FakeConnectionHandler(1))));
        table.addPlayer(new RealPlayer(new User("user1", new FakeConnectionHandler(2))));
        table.turnOf().setMacroTurnType(MacroTurnType.NONE);

        EnumMap<Resource, Integer> resourceOwned = new EnumMap<>(Resource.class);
        resourceOwned.put(Resource.SERVANT, 50);
        resourceOwned.put(Resource.COIN, 50);
        resourceOwned.put(Resource.SHIELD, 50);
        resourceOwned.put(Resource.STONE, 50);

        table.turnOf().getStrongBox().addEnumMap(resourceOwned);

        BuyDevCardController controller = new BuyDevCardController(new FaithTrackController(table));

        //1st invalid buying: wrong deck index
        controller.chooseDevCard(14);
        controller.buyDevCard();
        controller.paySelected();
        controller.chooseDevSlot(2);
        assertTrue(table.turnOf().getDevSlots()[0].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[1].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[2].isEmpty());
        for(int i = 0; i < table.getDevDecks().length; i++)
            assertEquals(4, table.getDevDecks()[i].size());

        //2nd invalid buying: no selection
        controller.chooseDevCard(4);
        controller.buyDevCard();
        controller.paySelected();
        controller.chooseDevSlot(2);
        assertTrue(table.turnOf().getDevSlots()[0].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[1].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[2].isEmpty());
        for(int i = 0; i < table.getDevDecks().length; i++)
            assertEquals(4, table.getDevDecks()[i].size());

        //3nd invalid buying: wrong deck
        controller.chooseDevCard(20);
        controller.buyDevCard();
        controller.paySelected();
        controller.chooseDevSlot(2);
        assertTrue(table.turnOf().getDevSlots()[0].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[1].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[2].isEmpty());
        for(int i = 0; i < table.getDevDecks().length; i++)
            assertEquals(4, table.getDevDecks()[i].size());


        //4nd invalid buying: wrong type of turn
        table.turnOf().setMacroTurnType(MacroTurnType.PRODUCTION);
        table.turnOf().setMicroTurnType(MicroTurnType.SETTING_UP);
        controller.chooseDevCard(1);
        controller.buyDevCard();
        controller.paySelected();
        controller.chooseDevSlot(2);
        assertTrue(table.turnOf().getDevSlots()[0].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[1].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[2].isEmpty());

        //5nd invalid buying: empty deck
        table.getDevDecks()[0].draw();
        table.getDevDecks()[0].draw();
        table.getDevDecks()[0].draw();
        table.getDevDecks()[0].draw();
        controller.chooseDevCard(1);
        controller.buyDevCard();
        controller.paySelected();
        controller.chooseDevSlot(2);
        assertTrue(table.turnOf().getDevSlots()[0].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[1].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[2].isEmpty());


    }

    @Test
    public void invalidPlacing(){
        Table table = new Table(2);
        table.addPlayer(new RealPlayer(new User("user1", new FakeConnectionHandler(1))));
        table.addPlayer(new RealPlayer(new User("user1", new FakeConnectionHandler(2))));
        table.turnOf().setMacroTurnType(MacroTurnType.NONE);

        EnumMap<Resource, Integer> resourceOwned = new EnumMap<>(Resource.class);
        resourceOwned.put(Resource.SERVANT, 50);
        resourceOwned.put(Resource.COIN, 50);
        resourceOwned.put(Resource.SHIELD, 50);
        resourceOwned.put(Resource.STONE, 50);

        table.turnOf().getStrongBox().addEnumMap(resourceOwned);

        BuyDevCardController controller = new BuyDevCardController(new FaithTrackController(table));

        //chooses another card with level 3, which he can't place anywhere
        controller.chooseDevCard(11);
        EnumMap<Resource, Integer> cost = table.getDevDecks()[2].getTopCard().getCost();
        controller.buyDevCard();
        table.turnOf().getStrongBox().mapSelection(cost);
        controller.paySelected();
        controller.chooseDevSlot(1);
        assertTrue(table.turnOf().getDevSlots()[0].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[1].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[2].isEmpty());
        for(EnumMap.Entry<Resource, Integer> entry: table.turnOf().getStrongBox().content().entrySet())
            assertEquals(50, entry.getValue());

        table.turnOf().getStrongBox().clearSelection();

        controller.chooseDevCard(3);
        int id = table.getDevDecks()[2].getTopCard().getId();
        cost = table.getDevDecks()[2].getTopCard().getCost();

        controller.buyDevCard();
        table.turnOf().getStrongBox().mapSelection(cost);
        controller.paySelected();
        controller.chooseDevSlot(5);
        assertTrue(table.turnOf().getDevSlots()[0].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[1].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[2].isEmpty());
        for(EnumMap.Entry<Resource, Integer> entry: cost.entrySet())
            assertEquals(50 - entry.getValue(), table.turnOf().getStrongBox().content().get(entry.getKey()));

        controller.chooseDevSlot(2);
        assertTrue(table.turnOf().getDevSlots()[0].isEmpty());
        assertFalse(table.turnOf().getDevSlots()[1].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[2].isEmpty());
        assertEquals(id, table.turnOf().getDevSlots()[1].topCard().getId());
    }

    @Test
    public void discountUsage(){
        Table table = new Table(2);
        table.addPlayer(new RealPlayer(new User("user1", new FakeConnectionHandler(1))));
        table.addPlayer(new RealPlayer(new User("user1", new FakeConnectionHandler(2))));
        table.turnOf().setMacroTurnType(MacroTurnType.NONE);

        EnumMap<Resource, Integer> resourceOwned = new EnumMap<>(Resource.class);
        resourceOwned.put(Resource.SERVANT, 50);
        resourceOwned.put(Resource.COIN, 50);
        resourceOwned.put(Resource.SHIELD, 50);
        resourceOwned.put(Resource.STONE, 50);

        table.turnOf().getStrongBox().addEnumMap(resourceOwned);

        EnumMap<Resource, Integer> resourceReq = new EnumMap<>(Resource.class);
        Map<DevCardType, Integer> devCardReq = new HashMap<>();
        EnumMap<Resource, Integer> input = new EnumMap<>(Resource.class);

        input.put(Resource.SERVANT, 1);
        input.put(Resource.COIN, 1);
        input.put(Resource.SHIELD, 1);
        input.put(Resource.STONE, 1);

        table.turnOf().addLeaderCard(new LeaderCard(2, resourceReq, devCardReq, LeaderCardType.DISCOUNT, input, new EnumMap<>(Resource.class),21));
        table.turnOf().addLeaderCard(new LeaderCard(2, resourceReq, devCardReq, LeaderCardType.DISCOUNT, input, new EnumMap<>(Resource.class),45));

        FaithTrackController ftc = new FaithTrackController(table);
        LeaderController lController = new LeaderController(ftc);
        lController.actionOnLeaderCard(45, false);

        BuyDevCardController controller = new BuyDevCardController(ftc);
        controller.chooseDevCard(3);
        int id = table.getDevDecks()[2].getTopCard().getId();
        EnumMap<Resource, Integer> cost = table.getDevDecks()[2].getTopCard().getCost();

        controller.buyDevCard();
        controller.applyDiscountAbility(21);
        assertEquals(cost, table.turnOf().getSupportContainer().content());

        controller.applyDiscountAbility(45);
        assertNotEquals(cost, table.turnOf().getSupportContainer().content());

        if(table.turnOf().getSupportContainer().content() != null)
            table.turnOf().getStrongBox().mapSelection(table.turnOf().getSupportContainer().content());
        controller.paySelected();
        controller.chooseDevSlot(2);
        assertTrue(table.turnOf().getDevSlots()[0].isEmpty());
        assertFalse(table.turnOf().getDevSlots()[1].isEmpty());
        assertTrue(table.turnOf().getDevSlots()[2].isEmpty());
        assertEquals(id, table.turnOf().getDevSlots()[1].topCard().getId());

        for(EnumMap.Entry<Resource, Integer> entry: cost.entrySet())
            assertEquals(50 - entry.getValue() + 1, table.turnOf().getStrongBox().content().get(entry.getKey()));
    }

    @Test
    public void testsSelection(){
        Table table = new Table(2);
        table.addPlayer(new RealPlayer(new User("user1", new FakeConnectionHandler(1))));
        table.addPlayer(new RealPlayer(new User("user2", new FakeConnectionHandler(2))));
        table.turnOf().setMacroTurnType(MacroTurnType.NONE);

        EnumMap<Resource, Integer> resourceOwned = new EnumMap<>(Resource.class);
        resourceOwned.put(Resource.SERVANT, 50);
        resourceOwned.put(Resource.COIN, 50);
        resourceOwned.put(Resource.SHIELD, 50);
        resourceOwned.put(Resource.STONE, 50);

        table.turnOf().getStrongBox().addEnumMap(resourceOwned);

        FaithTrackController ftc = new FaithTrackController(table);
        BuyDevCardController controller = new BuyDevCardController(ftc);

        assertFalse(controller.selectionFromShelf(Resource.COIN, 1));
        assertFalse(controller.selectionFromStrongBox(Resource.COIN,2));
        assertFalse(controller.deselectionFromShelf(Resource.COIN, 1));
        assertFalse(controller.deselectionFromStrongBox(Resource.COIN, 1));
        assertFalse(controller.selectionFromLeaderStorage(Resource.COIN, 56, 1));

        controller.chooseDevCard(1);
        table.turnOf().setMacroTurnType(MacroTurnType.BUY_NEW_CARD);
        table.turnOf().setMicroTurnType(MicroTurnType.SETTING_UP);
        assertTrue(controller.selectionFromShelf(Resource.COIN, 1));
        assertTrue(controller.selectionFromStrongBox(Resource.COIN,2));
        assertTrue(controller.deselectionFromShelf(Resource.COIN, 1));
        assertTrue(controller.deselectionFromStrongBox(Resource.COIN, 1));
        assertTrue(controller.selectionFromLeaderStorage(Resource.COIN, 56, 1));


    }
}