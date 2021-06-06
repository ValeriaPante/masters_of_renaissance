package it.polimi.ingsw.View.GUI.States;

import it.polimi.ingsw.View.GUI.ContainersScene;
import it.polimi.ingsw.View.GUI.GUI;
import it.polimi.ingsw.View.GUI.Transition;
import it.polimi.ingsw.View.GUI.TransmutationScene;

import java.util.ArrayList;

public class MarketState extends State{

    public MarketState(GUI gui){
        toDo = new ArrayList<>();
        done = new ArrayList<>();

        ArrayList<Integer> lc = gui.getModel().getPlayerFromId(gui.getModel().getLocalPlayerId()).getLeaderCards();
        int count = 0;
        for(int i = 0; i < lc.size(); i++){
            if(lc.get(i) > 56 && lc.get(i) < 61) count++;
        }

        if(count == 2){
            TransmutationScene transmutationScene = new TransmutationScene();
            transmutationScene.addObserver(gui);
            toDo.add(transmutationScene);
        }

        ContainersScene containersScene = new ContainersScene();
        containersScene.addObserver(gui);
        toDo.add(containersScene);
    }

    @Override
    public void goBack(){
        if(done.size() == 0){
            Transition.reshowDialog();
            return;
        }
        toDo.add(0, done.get(0));
        done.remove(0);
        Transition.setDialogScene(toDo.get(0).getRoot());
    }

    @Override
    public void refresh() {
        done.get(0).initialise();
        Transition.setDialogScene(toDo.get(0).getRoot());
    }
}
