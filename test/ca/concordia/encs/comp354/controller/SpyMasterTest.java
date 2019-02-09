package ca.concordia.encs.comp354.controller;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.concordia.encs.comp354.model.Board;
import ca.concordia.encs.comp354.model.Card;
import ca.concordia.encs.comp354.model.CardValue;
import ca.concordia.encs.comp354.model.CodenameWord;
import ca.concordia.encs.comp354.model.CodenameWord.AssociatedWord;
import ca.concordia.encs.comp354.model.GameState;
import ca.concordia.encs.comp354.model.Keycard;
import ca.concordia.encs.comp354.model.Team;


public class SpyMasterTest {
	
	final SpyMaster seqSpy;
	final SpyMaster randSpy;
	final GameState model;
	//final GameController controller;
	
	
	public SpyMasterTest() throws IOException {
//		List<Card> cards    = new ArrayList<>();
//		Set<Clue>  expected = new HashSet<>();
//		Set<Clue>  actual   = new HashSet<>();
//		char word = 'a';
//		for (int i=0; i<7; i++) {
//		    String clue = ""+word;
//		    cards.add(new Card(clue, Arrays.asList(new AssociatedWord(clue, 1)), CardValue.RED));
//		    expected.add(new Clue(clue, 1));
//		    word++;
//		}
		
		List<CodenameWord> codenameWords = Card.generateRandomCodenameList(Paths.get("res/words.txt"));
		List<Keycard> keycards = Keycard.generateKeyCards(Keycard.NUMBER_OF_KEYCARDS);
		
		model = new GameState(Board.createBoard(codenameWords, keycards));
		seqSpy = new SpyMaster(Team.RED, new SequentialSpyMasterStrategy());
		randSpy = new SpyMaster(Team.BLUE, new RandomSpyMasterStrategy());
	}
	
	
	//Checks Sequential Strategy to make sure the Spymaster returns the first clue of the right CardValue (in this case RED)
	@Test
	public void sequentialPicksFirstClue() {
		int x = 0;
		int y = 0;
		Board board = model.getBoard();
		Card firstCard = board.getCard(x,y);
		while (firstCard.getValue() != CardValue.RED) {
			x++;
			if (x == 5) {
				x = 0;
				y++;
			}
			firstCard = board.getCard(x,y);
		}
		List<AssociatedWord> boardClueList = firstCard.getAssociatedWords();
		String[] boardWords = new String[boardClueList.size()];
		for (int i = 0; i < boardWords.length; i++) {
			boardWords[i] = boardClueList.get(i).getWord();
		}
		List<String> boardWordsList = Arrays.asList(boardWords);
		
		Clue testClue = seqSpy.giveClue(model);
		String testClueWord = testClue.getWord();
		
		assertTrue(testClue instanceof Clue);
		assertTrue(boardWordsList.contains(testClueWord));		
	}
	
	@Test
	public void randomReturnsClue() {
		Clue testClue = randSpy.giveClue(model);
		assertTrue(testClue instanceof Clue);
	}
	

}


//controller.advanceTurn();
//Board board = model.getBoard();
//Card firstCard = board.getCard(0, 0);
//List<AssociatedWord> boardClueList = firstCard.getAssociatedWords();
//String[] boardWords = new String[boardClueList.size()];
//for (int i = 0; i < boardWords.length; i++) {
//	boardWords[i] = boardClueList.get(i).getWord();
//}
//List<String> boardWordsList = Arrays.asList(boardWords);
//
//Clue testClue = model.lastClueProperty().get();
//String testClueWord = testClue.getWord();
//
//assertTrue(boardWordsList.contains(testClueWord));