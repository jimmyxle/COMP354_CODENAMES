package ca.concordia.encs.comp354.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import ca.concordia.encs.comp354.Promise;
import ca.concordia.encs.comp354.model.*;

/**
 * Operatives are Players that produce guesses from {@link SpyMaster}s' clues.
 * @author Alex Abrams
 * @author Mykyta Leonidov
 */

public class Operative extends Player {
	
	public interface Strategy {
	    /**
	     * Requests a guess from this strategy, given the current game state.
	     * @param owner  the owning player object
	     * @param state  a read-only view of the current game state
	     * @param clue   the last clue produced by this player's spymaster
	     * @return a Promise containing the guess
	     */
	    Promise<Coordinates> guessCard(Operative owner, ReadOnlyGameState state, Clue clue);
	}
	
	private final Strategy strategy;
	
	//constructor
	public Operative(Team team, Strategy strategy) {
		super(team);
		this.strategy = Objects.requireNonNull(strategy, "strategy");
	}
	
	Promise<Coordinates> guessCard(GameState state, Clue clue) {
		return 
	        strategy.guessCard(this, state, clue)
            .then(coords->{
                if (coords==null) {
                    throw new IllegalStateException("cannot produce another guess");
                }
            });
	}
	
	
	
	//====================
    //--------TEST--------
    //====================
	public static void main(String[] args) throws IOException {
		List<CodenameWord> codenameWords = Card.createRandomCodenameList(Paths.get("res/25wordswithcommonassociatedwords.txt"));
		Keycard keycard = Keycard.createRandomKeycard();
	    GameState state = new GameState(new Board(codenameWords, keycard));
	    Board board = state.boardProperty().get();
	 
	    //print out the card list, then return clue and guess for first 2 red cards
	    System.out.println("Printing out the game board: ");
	    System.out.println(state.boardProperty().get().toString());
	    
	    SpyMaster spy = new SpyMaster(Team.RED, new RandomSpyMasterStrategy());
	    Operative op = new Operative(Team.RED, new IterativeOperativeStrategy());
	    
	    Clue clue = spy.giveClue(state);
	    System.out.println("First clue: " + clue);
	    Promise<Coordinates> guess = op.guessCard(state, clue);
	    System.out.println("First guess: " + board.getCard(guess.get().getX(), guess.get().getY()).getCodename());
        
        clue = spy.giveClue(state);
        System.out.println("Second clue: " + clue);
        guess = op.guessCard(state, clue);
        System.out.println("Second guess: " + board.getCard(guess.get().getX(), guess.get().getY()).getCodename());
	    
	}



}


