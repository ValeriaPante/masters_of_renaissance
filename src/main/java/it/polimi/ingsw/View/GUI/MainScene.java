package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.Messages.InGameMessages.ConcreteMessages.EndTurnMessage;
import it.polimi.ingsw.Messages.InGameMessages.ConcreteMessages.LeaderCardActionMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Scene displaying the main panel with the boards of the players (multi-player case)
 */
public class MainScene extends ObservableByGUI{
    private Pane root;
    private static final double[][] positions = {
            {34,52}, {57,52}, {59, 33}, {58, 14}, {81,14}, {103,14}, {125,14},
            {145,14}, {170,14}, {170, 33}, {170,54}, {191,54}, {217,54}, {240,54},
            {263, 54}, {285,54}, {285,31}, {285,14}, {308,14}, {332,14}, {354,14},
            {376,14}, {398,14}, {421,14}
    };

    public MainScene(GUI gui){
        addObserver(gui);
        try {
            root = FXMLLoader.load(getClass().getResource("/Scenes/mainScene.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridPane gridPane = (GridPane) root.lookup("#grid");
        Pane player = null;

        ArrayList<String> usernames = observer.getModel().getUsernames();

        for(int i = 0; i < observer.getModel().getNumberOfPlayers(); i++){
            if(observer.getModel().getLocalPlayerIndex() == i){
                try {
                    player= FXMLLoader.load(getClass().getResource("/Scenes/localPlayerPane.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert player != null;
                AnchorPane background = (AnchorPane) player.lookup("#background");
                ImageView backgroundImage = new ImageView();
                try {
                    File fullPath = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                    FileInputStream fileInputStream = new FileInputStream(fullPath.getParentFile().getPath() + File.separator +"assets"+ File.separator +"imgs"+ File.separator + "colored.png");
                    backgroundImage.setImage(new Image(fileInputStream));

                } catch(Exception ignored) {
                }
                backgroundImage.setFitWidth(464.0);
                backgroundImage.setFitHeight(302.0);
                backgroundImage.setVisible(true);
                background.getChildren().add(backgroundImage);

                if(i > 0) player.lookup("#calamaio").setVisible(false);
                ((Label) player.lookup("#username")).setText(usernames.get(i)+"(you)");
                ArrayList<Integer> lc = observer.getModel().getPlayerFromId(observer.getModel().getLocalPlayerId()).getLeaderCards();
                Pane grid = (Pane) player.lookup("#pane");
                AnchorPane card1 = (AnchorPane) grid.lookup("#card1");
                AnchorPane card2 = (AnchorPane) grid.lookup("#card2");
                ImageView image1 = new ImageView();
                ImageView image2 = new ImageView();
                try {
                    File fullPath = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                    FileInputStream fileInputStream1 = new FileInputStream(fullPath.getParentFile().getPath() + File.separator +"assets"+ File.separator +"imgs"+ File.separator+lc.get(0)+".png");
                    image1.setImage(new Image(fileInputStream1));
                    FileInputStream fileInputStream2 = new FileInputStream(fullPath.getParentFile().getPath() + File.separator +"assets"+ File.separator +"imgs"+ File.separator+lc.get(1)+".png");
                    image2.setImage(new Image(fileInputStream2));

                } catch(Exception ignored) {
                }
                image1.setFitWidth(85);
                image2.setFitWidth(85);
                image1.setPreserveRatio(true);
                image2.setPreserveRatio(true);
                card1.getChildren().add(image1);
                card2.getChildren().add(image2);
                gridPane.add(player, i%2, i/2);
                card1.setId(String.valueOf(lc.get(0)));
                card2.setId(String.valueOf(lc.get(1)));

                player.lookup("#lc11").setId("lc"+lc.get(0)+"1");
                player.lookup("#lc12").setId("lc"+lc.get(0)+"2");
                player.lookup("#lc21").setId("lc"+lc.get(1)+"1");
                player.lookup("#lc22").setId("lc"+lc.get(1)+"2");


                Button activate1 = (Button) player.lookup("#activate1");
                activate1.setId(lc.get(0).toString());
                activate1.setOnAction(actionEvent -> new Thread(() -> sendMessage(new LeaderCardActionMessage(Integer.parseInt(((Button) actionEvent.getSource()).getId().substring(8)),false))).start());
                activate1.setId("activate"+lc.get(0));

                Button discard1 = (Button) player.lookup("#discard1");
                discard1.setId(lc.get(0).toString());
                discard1.setOnAction(actionEvent -> new Thread(() -> sendMessage(new LeaderCardActionMessage(Integer.parseInt(((Button) actionEvent.getSource()).getId().substring(7)),true))).start());
                discard1.setId("discard"+lc.get(0));

                Button activate2 = (Button) player.lookup("#activate2");
                activate2.setId(lc.get(1).toString());
                activate2.setOnAction(actionEvent -> new Thread(() -> sendMessage(new LeaderCardActionMessage(Integer.parseInt(((Button) actionEvent.getSource()).getId().substring(8)),false))).start());
                activate2.setId("activate"+lc.get(1));

                Button discard2 = (Button) player.lookup("#discard2");
                discard2.setId(lc.get(1).toString());
                discard2.setOnAction(actionEvent -> new Thread(() -> sendMessage(new LeaderCardActionMessage(Integer.parseInt(((Button) actionEvent.getSource()).getId().substring(7)),true))).start());
                discard2.setId("discard"+lc.get(1));

                for (int k = 1; k < 4; k++){
                    InputStream in = getClass().getResourceAsStream("/constantAssets/pope" +k+".png");
                    ImageView image = new ImageView();
                    image.setImage(new Image(in));
                    image.setFitWidth(31);
                    image.setPreserveRatio(true);
                    ((AnchorPane) player.lookup("#pope"+k)).getChildren().add(image);
                }

                Region strongbox = (Region) player.lookup("#strongbox");
                Tooltip inside = new Tooltip();
                Pane tooltip = null;
                try {
                    tooltip = FXMLLoader.load(getClass().getResource("/Scenes/strongboxPane.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert tooltip != null;
                ((Label)tooltip.lookup("#coin")).setText("0");
                ((Label)tooltip.lookup("#shield")).setText("0");
                ((Label)tooltip.lookup("#stone")).setText("0");
                ((Label)tooltip.lookup("#servant")).setText("0");
                inside.setGraphic(tooltip);
                Tooltip.install(strongbox, inside);

            } else {
                try {
                    player= FXMLLoader.load(getClass().getResource("/Scenes/playerPane.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert player != null;
                AnchorPane background = (AnchorPane) player.lookup("#background");
                ImageView backgroundImage = new ImageView();
                try {
                    File fullPath = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                    FileInputStream fileInputStream = new FileInputStream(fullPath.getParentFile().getPath() + File.separator +"assets"+ File.separator +"imgs"+ File.separator + "colored.png");
                    backgroundImage.setImage(new Image(fileInputStream));

                } catch(Exception ignored) {
                }
                backgroundImage.setFitWidth(464.0);
                backgroundImage.setFitHeight(309.0);
                backgroundImage.setVisible(true);
                background.getChildren().add(backgroundImage);

                if(i > 0) player.lookup("#calamaio").setVisible(false);
                ((Label) player.lookup("#username")).setText(usernames.get(i));
                AnchorPane card1 = (AnchorPane) player.lookup("#lc1");
                AnchorPane card2 = (AnchorPane) player.lookup("#lc2");
                InputStream in3 = getClass().getResourceAsStream("/constantAssets/retro.jpg");
                InputStream in4 = getClass().getResourceAsStream("/constantAssets/retro.jpg");
                ImageView image3 = new ImageView();
                ImageView image4 = new ImageView();
                image3.setImage(new Image(in3));
                image4.setImage(new Image(in4));
                image3.setFitWidth(100);
                image4.setFitWidth(100);
                image3.setPreserveRatio(true);
                image4.setPreserveRatio(true);
                card1.getChildren().add(image3);
                card2.getChildren().add(image4);

                for (int k = 1; k < 4; k++){
                    InputStream in = getClass().getResourceAsStream("/constantAssets/pope" +k+".png");
                    ImageView image = new ImageView();
                    image.setImage(new Image(in));
                    image.setFitWidth(31);
                    image.setPreserveRatio(true);
                    ((AnchorPane) player.lookup("#pope"+k)).getChildren().add(image);
                }

                gridPane.add(player,i%2, i/2);

                Region strongbox = (Region) player.lookup("#strongbox");
                Tooltip inside = new Tooltip();
                Pane tooltip = null;
                try {
                    tooltip = FXMLLoader.load(getClass().getResource("/Scenes/strongboxPane.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert tooltip != null;
                ((Label)tooltip.lookup("#coin")).setText("0");
                ((Label)tooltip.lookup("#shield")).setText("0");
                ((Label)tooltip.lookup("#stone")).setText("0");
                ((Label)tooltip.lookup("#servant")).setText("0");
                inside.setGraphic(tooltip);
                Tooltip.install(strongbox, inside);
            }
        }

        if(observer.getModel().getNumberOfPlayers() == 2) root.setPrefHeight(330);

        MenuBar menuBar = (MenuBar) root.getChildren().get(0);
        Menu menu = menuBar.getMenus().get(0);
        menu.getItems().get(0).setOnAction(actionEvent -> observer.showMarket());
        menu.getItems().get(1).setOnAction(actionEvent -> observer.showDevDecks());
        menu.getItems().get(2).setOnAction(actionEvent -> observer.activateProduction());
        menu.getItems().get(3).setOnAction(actionEvent -> {
            Transition.setOnContainersScene(false);
            new Thread(() -> sendMessage(new EndTurnMessage())).start();
        });
        if(observer.getModel().getPlayerIndex(observer.getModel().getLocalPlayerId()) > 0) menu.setVisible(false);
    }

    public Pane getRoot() {
        return root;
    }

    public static double[][] getPositions() {
        return positions;
    }

}
