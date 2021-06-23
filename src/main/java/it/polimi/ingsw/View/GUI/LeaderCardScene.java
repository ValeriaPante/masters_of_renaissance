package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.Messages.InGameMessages.ConcreteMessages.LeaderDiscardMessage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.*;
import java.util.ArrayList;

public class LeaderCardScene extends ObservableByGUI{

    private Pane root;
    private int count;

    public LeaderCardScene(GUI gui){
        addObserver(gui);
        try {
            root = FXMLLoader.load(getClass().getResource("/Scenes/leaderCardScene.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridPane gridPane = (GridPane) root.lookup("#gridPane");
        ArrayList<Integer> leaderCards = observer.getModel().getPlayerFromId(observer.getModel().getLocalPlayerId()).getLeaderCards();
        for(int i = 0; i  < leaderCards.size(); i++){
            ImageView image = new ImageView();
            try {
                File fullPath = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                FileInputStream fileInputStream = new FileInputStream(fullPath.getParentFile().getPath() + "\\assets\\imgs\\" +leaderCards.get(i)+".png");
                image.setImage(new Image(fileInputStream));

            } catch(Exception e) {
            }
            image.setFitWidth(200);
            image.setPreserveRatio(true);
            image.setId(String.valueOf(i));
            image.setOnMouseClicked(mouseEvent -> {
                int index = Integer.parseInt(((ImageView) mouseEvent.getSource()).getId());
                new Thread(() -> sendMessage(new LeaderDiscardMessage(observer.getModel().getPlayerFromId(observer.getModel().getLocalPlayerId()).getLeaderCards().get(index)))).start();
                observer.getModel().getPlayerFromId(observer.getModel().getLocalPlayerId()).getLeaderCards().remove(index);
                Transition.updateLeaderCards(Math.max(index - count, 0));
                count++;
                if (count == 2){
                    observer.setGamePhase(1);
                    Platform.runLater(Transition::toLoadingScene);
                }
            });
            gridPane.addColumn(i, image);
        }

    }

    public Pane getRoot() {
        return root;
    }
}
