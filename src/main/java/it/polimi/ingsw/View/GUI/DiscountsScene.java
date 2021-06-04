package it.polimi.ingsw.View.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DiscountsScene extends Initializable{

    public DiscountsScene(){
        try {
            root = FXMLLoader.load(getClass().getResource("/Scenes/discountsScene.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialise(){
        Button button = (Button) root.lookup("#button");
        button.setOnAction(event -> {
            observer.getCurrentState().next();
        });

        ArrayList<Integer> lc = observer.getModel().getPlayerFromId(observer.getModel().getLocalPlayerId()).getLeaderCards();
        ArrayList<Integer> discountLC = new ArrayList<>();

        for(int i = 0; i < lc.size(); i++){
            if(lc.get(i) > 48 && lc.get(i) < 53) discountLC.add(lc.get(i));
        }

        root.getChildren().get(5).setVisible(false);
        root.getChildren().get(6).setVisible(false);
        root.getChildren().get(7).setVisible(false);

        if(discountLC.size() == 2){
            AnchorPane card1 = (AnchorPane) root.getChildren().get(1);
            InputStream in1 = getClass().getResourceAsStream("/Images/"+discountLC.get(0)+".png");
            ImageView image1 = new ImageView();
            image1.setImage(new Image(in1));
            image1.setFitWidth(255);
            image1.setPreserveRatio(true);
            image1.setOnMouseClicked(mouseEvent -> {
                root.getChildren().get(5).setVisible(true);
            });
            card1.getChildren().add(image1);

            AnchorPane card2 = (AnchorPane) root.getChildren().get(2);
            InputStream in2 = getClass().getResourceAsStream("/Images/"+discountLC.get(1)+".png");
            ImageView image2 = new ImageView();
            image2.setImage(new Image(in2));
            image2.setFitWidth(255);
            image2.setPreserveRatio(true);
            image2.setOnMouseClicked(mouseEvent -> {
                root.getChildren().get(6).setVisible(true);
            });
            card2.getChildren().add(image2);

        } else {
            AnchorPane card = (AnchorPane) root.getChildren().get(3);
            InputStream in = getClass().getResourceAsStream("/Images/"+discountLC.get(0)+".png");
            ImageView image = new ImageView();
            image.setImage(new Image(in));
            image.setFitWidth(255);
            image.setPreserveRatio(true);
            image.setOnMouseClicked(mouseEvent -> {
                root.getChildren().get(7).setVisible(true);
            });
            card.getChildren().add(image);

        }

    }
}