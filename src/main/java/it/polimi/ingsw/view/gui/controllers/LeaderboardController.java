package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.GuiView;
import it.polimi.ingsw.view.lightmodel.GameView;
import it.polimi.ingsw.view.lightmodel.PlayerView;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class LeaderboardController implements Initializable, InvalidationListener {

    @FXML
    Label current1, current2, current3, current4, player1, player2, player3, player4;

    @FXML
    AnchorPane one, two, three, four;

    @FXML
    TextField numCoins1, numCoins2, numCoins3, numCoins4,
          numShields1, numShields2, numShields3, numShields4,
          numServ1, numServ2, numServ3, numServ4,
          numStones1, numStones2, numStones3, numStones4;

    @FXML
    TextField numPoints1, numPoints2, numPoints3, numPoints4;

    GameView players;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiView.getGuiView().players.addListener(this);
        current1.setVisible(false);
        current2.setVisible(false);
        current3.setVisible(false);
        current4.setVisible(false);
        this.invalidated(null);
        if(GuiView.getGuiView().players.players.length>=2) {
            if(GuiView.getGuiView().players.players.length>=3) {
                if(GuiView.getGuiView().players.players.length<4) {
                    four.setVisible(false);
                    player4.setVisible(false);
                }
            }
            else {
                three.setVisible(false);
                player3.setVisible(false);
                four.setVisible(false);
                player4.setVisible(false);
            }
        }
        else {
            two.setVisible(false);
            player2.setVisible(false);
            three.setVisible(false);
            player3.setVisible(false);
            four.setVisible(false);
            player4.setVisible(false);
        }
    }

    public void cancel(ActionEvent event) {
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> GuiView.getGuiView().playerboard.showMenu());
    }

    public void update(int player, ResourcePack resources, int points, String nick) {

        TextField coins, stones, shields, servants, numPoints;
        Label name;

        if(player==1) {
            coins=numCoins1;
            stones=numStones1;
            shields=numShields1;
            servants=numServ1;
            numPoints=numPoints1;
            name = player1;
        }
        else if(player==2) {
            coins=numCoins2;
            stones=numStones2;
            shields=numShields2;
            servants=numServ2;
            numPoints=numPoints2;
            name = player2;
        }
        else if(player==3) {
            coins=numCoins3;
            stones=numStones3;
            shields=numShields3;
            servants=numServ3;
            numPoints=numPoints3;
            name = player3;
        }
        else {
            coins=numCoins4;
            stones=numStones4;
            shields=numShields4;
            servants=numServ4;
            numPoints=numPoints4;
            name = player4;
        }

        coins.setText("" + resources.get(Resource.COIN));
        servants.setText("" + resources.get(Resource.SERVANT));
        shields.setText("" + resources.get(Resource.SHIELD));
        stones.setText("" + resources.get(Resource.STONE));
        numPoints.setText(""+points);
        name.setText(nick);
    }

    @Override
    public void invalidated(Observable observable) {
        this.players = GuiView.getGuiView().players;

        int i = 1;
        for(PlayerView player : players.players) {
            if(i==1) {
                player1.setVisible(true);
                one.setVisible(true);
                update(1, player.getResources(), player.getVictoryPoints(), player.getNickname());
                if(players.getCurrentPlayerID()==1) current1.setVisible(true);
                i++;
            }
            else if(i==2) {
                player2.setVisible(true);
                two.setVisible(true);
                update(2, player.getResources(), player.getVictoryPoints(), player.getNickname());
                i++;
            }
            else if(i==3) {
                player3.setVisible(true);
                three.setVisible(true);
                update(3, player.getResources(), player.getVictoryPoints(), player.getNickname());
                i++;
            }
            else if(i==4) {
                player4.setVisible(true);
                four.setVisible(true);
                update(4, player.getResources(), player.getVictoryPoints(), player.getNickname());
                i++;
            }
            if(GuiView.getGuiView().currentPlayer.equals(player1.getText())) current1.setVisible(true);
            if(GuiView.getGuiView().currentPlayer.equals(player2.getText())) current2.setVisible(true);
            if(GuiView.getGuiView().currentPlayer.equals(player3.getText())) current3.setVisible(true);
            if(GuiView.getGuiView().currentPlayer.equals(player4.getText())) current4.setVisible(true);
        }
    }
}
