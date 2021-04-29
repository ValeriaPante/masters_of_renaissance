package ControllerTest;

import it.polimi.ingsw.Cards.LeaderCard;
import it.polimi.ingsw.Controller.CardActionController;
import it.polimi.ingsw.Deposit.Payable;
import it.polimi.ingsw.Enums.LeaderCardType;
import it.polimi.ingsw.Enums.Resource;
import it.polimi.ingsw.Game.Table;
import it.polimi.ingsw.Player.RealPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

public class CardActionControllerTest {

    private CardActionController cardActionController;
    private Table table;

    @BeforeEach
    public void init(){
        this.table = new Table(2);
        RealPlayer player1 = new RealPlayer("player1");
        player1.getStrongBox().addEnumMap(new EnumMap<>(Resource.class){{
            put(Resource.SERVANT, 3);
            put(Resource.COIN, 6);
        }});
        player1.getShelves()[0].singleAdd(Resource.SHIELD);
        player1.getShelves()[1].singleAdd(Resource.COIN);
        player1.getShelves()[2].singleAdd(Resource.SERVANT);
        player1.getShelves()[2].singleAdd(Resource.SERVANT);
        EnumMap<Resource, Integer> capacity = new EnumMap<>(Resource.class){{
            put(Resource.STONE, 2);
            put(Resource.SERVANT, 1);
        }};
        player1.addLeaderCard(new LeaderCard(3, new EnumMap<>(Resource.class), new HashMap<>(), LeaderCardType.STORAGE, capacity, 11));
        player1.getLeaderCards()[0].play();
        player1.getLeaderCards()[0].getAbility().add(Resource.STONE);
        player1.getLeaderCards()[0].getAbility().add(Resource.SERVANT);

        //NB questa non viene giocata
        capacity = new EnumMap<>(Resource.class){{
            put(Resource.COIN, 2);
        }};
        player1.addLeaderCard(new LeaderCard(3, new EnumMap<>(Resource.class), new HashMap<>(), LeaderCardType.STORAGE, capacity, 12));

        //NB questa non è di tipo storage
        player1.addLeaderCard(new LeaderCard(3, new EnumMap<>(Resource.class), new HashMap<>(), LeaderCardType.PRODPOWER, capacity, 13));

        this.table.addPlayer(player1);
        this.cardActionController = new CardActionController(this.table);
    }

    @Test
    @DisplayName("Method getPayableWithSelection() test")
    public void getPayableWithSelection(){
        Method getPayableWithSelections;
        ArrayList<Payable> result;

        this.table.turnOf().getShelves()[0].singleSelection();

        try {
            getPayableWithSelections = CardActionController.class.getDeclaredMethod("getPayableWithSelection", null);
            getPayableWithSelections.setAccessible(true);
            result = (ArrayList<Payable>) getPayableWithSelections.invoke(this.cardActionController, null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            fail();
            return;
        }

        assertFalse(result.isEmpty());


    }

}
