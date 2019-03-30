package ca.concordia.encs.comp354.view;

import ca.concordia.encs.comp354.controller.GameEvent;
import ca.concordia.encs.comp354.model.GameStep;
import ca.concordia.encs.comp354.model.Team;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import javafx.scene.layout.VBox; 
import javafx.scene.*;
import javafx.stage.Stage;

import static ca.concordia.encs.comp354.controller.GameEvent.*;

import ca.concordia.encs.comp354.controller.GameController;

/**
 * Displays "game over" event overlays.
 * @author Mykyta Leonidov
 *
 */
public class GameEventView extends StackPane {
    
    private final ObjectProperty<GameStep> step = new SimpleObjectProperty<GameStep>(this, "event", null) {
        @Override 
        protected void invalidated() {
            onGameEvent(getValue());
        }
    };
    
    private GameController controller;
    
    private final Label redWins, blueWins, assassin;

    private Animation lastAnim = null;
    
//    private final Button restart;
    
    private static final Duration FADE_DURATION  = Duration.millis(500);
    private static final Duration PAUSE_DURATION = Duration.millis(500);
    
    GameEventView() {
        redWins  = endLabel(GAME_OVER_RED_WON,  "Red wins!",  "red");
        blueWins = endLabel(GAME_OVER_BLUE_WON, "Blue wins!", "blue");
        assassin = endLabel(GAME_OVER_ASSASSIN, "Assassin!",  "assassin");
                
		Button restart = new Button("Restart");	
		Button exit = new Button("Exit");
//		restart.setOnAction(e->GameController.restartGame());
		restart.setOnAction(event);
		Label label = new Label("Would you like to play again?"); 
		
		Stage stage = new Stage();
		StackPane layout = new StackPane(label);
        layout.getChildren().add(restart);
//        layout.getChildren().add(exit);
        stage.setScene(new Scene(layout,300,250));
        stage.setTitle("Game over");
        stage.show();
        
        getChildren().addAll(redWins, blueWins, assassin);
    }
    
    ObjectProperty<GameStep> stepProperty() {
        return step;
    }
    
    private void onGameEvent(GameStep next) {
        if (lastAnim!=null) {
            lastAnim.stop();
            lastAnim = null;
        }
        
        for (Node k : getChildren()) {
            k.setVisible(false);
        }
        
        if (next==null) {
        	return;
        }
        
        switch (next.getEvent()) {
        case GAME_OVER_ASSASSIN:
            show(next.getAction().getTeam()==Team.RED?blueWins:redWins, true);
            break;
        case GAME_OVER_BLUE_WON:
            show(blueWins, false);
            break;
        case GAME_OVER_RED_WON:
            show(redWins, false);
            break;
        case END_TURN: // fallthrough
        case NONE:
            break;
        
        }
    }
    
    private void show(Label node, boolean showAssassin) {
        node.setVisible(true);
        node.setOpacity(0);
        
        FadeTransition ft = new FadeTransition();
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setDuration(FADE_DURATION);
        ft.setNode(node);
        ft.setAutoReverse(true);
        ft.setCycleCount(1);
        
        if (showAssassin) {
            assassin.setVisible(true);
            
            FadeTransition in = new FadeTransition();
            in.setFromValue(0);
            in.setToValue(1);
            in.setDuration(FADE_DURATION);
            in.setNode(assassin);
            
            FadeTransition out = new FadeTransition();
            out.setFromValue(1);
            out.setToValue(0);
            out.setDuration(FADE_DURATION);
            out.setNode(assassin);
            
            out.setOnFinished(e->assassin.setVisible(false));
            
            lastAnim = new SequentialTransition(in, new PauseTransition(PAUSE_DURATION), out, ft);
        } else {
            lastAnim = ft;
        }
        
        lastAnim.play();
    }

	private Label endLabel(GameEvent show, String label, String pseudoClass) {
        Label ret = new Label(label);
        
        ret.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ret.setTextAlignment(TextAlignment.CENTER);
        ret.getStyleClass().add("game-over");
        ret.pseudoClassStateChanged(PseudoClass.getPseudoClass(pseudoClass), true);               
        
        // show only for specific game event
        ret.setVisible(false);
        
        return ret;
    }
	
	 // action event 
    EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
        public void handle(ActionEvent e) 
        { 
        	System.out.println("inside restart");
        	controller.restartGame();
        } 
    }; 
	
}
