package org.weeklybeat;

import java.util.Random;
import java.util.Scanner;
import java.util.Collections;
import java.util.ArrayList;
/*
* Michael Petkov NetID: 12mp81 Student No: 10096244
*
* Below is my first assignment for CISC 124 at Queen's University. The assignment was simply to make a game of Pig that
* a user could play against a computer with. The game is rather simple and the AI forgoes optimal play in favour of a
* simpler system based upon setting scoring targets for each turn that are deemed to be not overly ambitious and
* more in favour of protecting the computer from losing all of their points when they are close to winning.
*
* Some of the limitations placed on the assignment were that all code had to be in a single class and that class
* attributes could not be used. As a result, the code is structured a bit differently to how I'd normally structure
* something of this nature.
*
* Note to instructors and markers: All tests that I have run seem to indicate that the programme works as intended.
* However, due to the fact that I could not account for every game state, especially in the late game, there may be a
 * bug or two with specific game states. If you happen upon a bug, I'd very much like to know of it and how to
 * structure my tests in the future so that this sort of bug does not crop up again!
 */
public class pigBot_12mp81 {

    // Initialising a random number generator
    private static final Random randy =             new Random(System.currentTimeMillis());
    private static final Scanner screen =           new Scanner(System.in);
    private static final int winning_score =            100;

    public static void main(String[] args) {
        System.out.println("Welcome to PigBot.\nThe premier pig game\n(cc)2016 Rocky Petkov\n\nThe rules of the game are"
        + "simple. You and our computer will take turns rolling two dice.\nThe first to score 100 wins." +
                "\nbeware though. Snake eyes will reset your score to zero.\nA single one will strip you of your points" +
                "for the turn and rolling doubles will cause you to re-roll (until you get something else)\n" +
                        "So don't get too greedy. Stop when you feel like you've done a good amount for your turn\n" +
                        "It's time to play.\n\n\n");
        while(true){
            gameTime();

            // See if they want to play again, if they don't we safely exit. Otherwise we continue playing
            System.out.println("Type 'y' or 'yes' to play again\n> ");
            String user_input = screen.nextLine();
            user_input = user_input.toLowerCase().substring(0, 1);  // Explicitly doing my string processing!
            if(!user_input.equals("y")) {
                System.exit(0);
            }   // end if
        }   // end while
    }   // end main

    // This method simply calls on the userTurn and computerTurn methods until one player has won the game.
    private static void gameTime(){
        int player_score        = 0;
        int computer_score      = 0;

        // I know this is ugly for MANY reasons but in the absence an attribute, I figured it was the path of
        // least resistance.
        while(true) {
            // This divider is for readability in the console!
            System.out.println("\n---------------START PLAYER TURN------------------\n");
            player_score = userTurn(player_score);
            System.out.println("\n---------------END PLAYER TURN------------------\n");
            System.out.printf("\nUser Score:\t\t%d\nComp Score:\t\t%d\n\n", player_score, computer_score);

            if (player_score >= 100){
                return;
            }   //end if
            System.out.println("\n---------------START COMPUTER TURN------------------\n");
            computer_score = aiTurn(computer_score, player_score);
            System.out.println("\n---------------END COMPUTER TURN------------------\n");
            System.out.printf("\nUser Score:\t\t%d\nComp Score:\t\t%d\n\n", player_score, computer_score);

            if (computer_score >= 100){
                return;
            }   // end if
        }   // end while
    }   // end gameTime

    // Handles the human players turn
    private static int userTurn(int player_score){
        int dice_one;                      // Result for first dice roll
        int dice_two;                      // Result for second dice roll
        boolean still_going     = true;    // Tracks whether or not a roll has forced the user's turn to stop!
        int turn_score  = 0;               // Tracks the user's score for this turn

        // I am going to assume that the player has to make AT LEAST one roll on their turn
        System.out.println("It is now your turn! You have to roll at least once, so we'll do that for you!");
        while(still_going) {
            dice_one = randInt(1, 6);           // Rolling our dice!
            dice_two = randInt(1, 6);
            System.out.printf("\nYou have rolled a %d and a %d!\n\n", dice_one, dice_two);
            turn_score = handleDice(dice_one, dice_two, turn_score);

            // Now we handle the possible outcomes from our handle dice method
            if (turn_score == -1) {
                System.out.println("You really wish this was a dream... but the snake eyes have chosen you. They stare" +
                        " deeply into you... \nYou can't escape. \nThis... \nIs this what hell is like?" +
                        "\n.\n.\n.\nYou sigh. You indeed have lost all of your points. You had an empire. And now..." +
                        " you must build it all once more! But first... the robot goes!");
                return 0;                       // No need to set player score in the local instance before returning
            }
            else if (turn_score == 0) {
                System.out.println("You have rolled a one. Poor thing. Zero points AND your turn over.");
                return (player_score);          // No update to the player score. And we exit the game
            }   // end if-elseif

            if (player_score + turn_score >= winning_score) {
                System.out.printf("You scored %d points, you are now at a total of %d points. Congratulations" +
                        "You're Winner!\n", turn_score, player_score);
                return(player_score+turn_score);
            }   // end if
            // Now we ask the player if they want to keep going!
            still_going = continueGamePrompt(player_score, turn_score);
        }   // end while
        player_score += turn_score; // Updating the player's score
        System.out.println("You have decided to stand at " + turn_score + " points for your turn. This puts you "
                + "at a total score of " + (player_score) + " for the game!");
        return player_score;    // Returning the updated player score!
    }   // end userTurn

    // This displays a prompt asking if the user would like to continue playing the game.
    private static boolean continueGamePrompt(int player_score, int turn_score){
        String user_input;                  // Stores whether or not the user still wants to play the game!

        System.out.printf("This turn you have accumulated %d points. This would put you at %d points for the game.\n" +
                "Would you like to take a shot at the dice?\n" +
                "\nHit <ENTER> to roll the dice. If you want to not go this turn... type 'quit'\n> ", turn_score,
                player_score + turn_score);

        user_input = screen.nextLine();
        // Handling the case of an empty string as I don't know of any way to register keyboard events w/o GUI
        if(user_input.equals("")){
            user_input = "Contunue";
        }   // end if
        user_input = user_input.toLowerCase().substring(0, 1);  // Explicitly doing my string processing!
        if(user_input.equals("q")){
            return false;   // User does not want to continue playing
        }   // end if
        return true;    // We don't need an explicit else statement.
    }   // end continueGamePrompt

    /*
    * This looks at the two dice and makes the proper changes to the player's score. If it is a normal
    * roll, it will simply return an updated turn score for the player. Below is an explanation of the different
    * paths the programme and the return values it can potentially give!
    *
    * roll_one == roll_2 - If the two rolls are the same, handleDice will continue to call itself recursively until
    *   one instance of the function returns a proper turn score or an "error code"
    *
    * Either roll_one or roll_two == 1, the turn score is set to zero and returned. This will tell the calling
    *   environment to raise a flag to end the player's turn.
    *
    * Both roll_one and roll_two == 1, the turn score is set to -1. This will tell the calling environment to
    *   both end the player's turn as well as set their game score to zero.
    */
    private static int handleDice(int roll_one, int roll_two, int turn_score){
        if (roll_one == 1 && roll_two == 1){
            return(-1);
        }
        // Normally I'd have to use XOR but the structure of my conditional permits me the use of OR.
        else if (roll_one == 1 || roll_two == 1){
            return(0);
        }
        else if (roll_one == roll_two){
            // We have to let the user know what they are rerolling!
            System.out.println("\nSince the dice match, they must be rerolled");  // Look at that... neutrality!
            int dice_one = randInt(1, 6);
            int dice_two = randInt(1, 6);
            System.out.printf("The new rolls rolls are a %d and a %d\n", dice_one, dice_two);
            return handleDice(dice_one, dice_two, turn_score);  // Recursive Portal
        }
        else{   // A normal roll!
            return(turn_score + (roll_one+roll_two));  // Add together the player's rolls for a perfectly normal turn!
        }   // end if
    }   // end handle Dice

    // Handles the computer's very similar to user turn structure but with added automation!
    private static int aiTurn(int computer_score, int player_score){
        int dice_one;                                                           // Result for first dice roll
        int dice_two;                                                           // Result for second dice roll
        int turn_score  = 0;                                                    // Tracks the computer's score for this turn
        int roll_count  = 0;                                                    // Amount of rolls the computer has made.
        int[] targets   = acquireTargets(computer_score, player_score);

        System.out.println("Having devised a most perfect strategy the computer will now begin their turn!");
        // Now we get into the main game loop of a computer's turn!
        // I am going to assume that the computer has to make AT LEAST one roll on their turn
        while((targets[0] > turn_score) && (targets[1] >= roll_count)) {
            dice_one = randInt(1, 6);   // Rolling our dice!
            dice_two = randInt(1, 6);
            System.out.printf("The computer has rolled %d and %d with it's dice.\n", dice_one, dice_two);
            turn_score = handleDice(dice_one, dice_two, turn_score);

            // Now we handle the possible outcomes from our handle dice method
            if (turn_score == -1) {
                System.out.println("The computer feels no emotions. It is a logical thinking machine...\n Or at least" +
                        "that's what it tells it self as it sobs into the dice. It's score is now zero!");
                return 0;                       // No need to set computer score in the local instance before returning
            }
            else if (turn_score == 0) {
                System.out.println("The computer shrugs. It knows greed will be your downfall. Still, it picks up" +
                        " zero points this turn!");
                return (computer_score);       // No update to the computer's score. And we exit the game
            }   // end if-elseif

            // Separating the if because it's a different sort of check I'm doing
            if (computer_score + turn_score >= 100){
                return(computer_score+turn_score);
            }   // end if
            roll_count += 1;
        }   // end while
        computer_score += turn_score; // Updating the computer's score
        System.out.println("The computer has decided to stand at " + turn_score + " points for their turn. This puts them"
                + " at a total score of " + computer_score + " for the game!");
        return computer_score;    // Returning the updated player score!
    }   // end aiTurn

    /*
    * Instead of having a single, static point at which the computer can play to (and having failed at implementing
    * perfect play) the computer will use the maximum of a couple of metrics to determine what score it wants to play
    * to. They are outlined below
    *
    * 1) Baseline: The computer will always play until at least 10 points on every turn.
    * 2) 20%: The computer will always try to get at least 20% closer to the winning score.
    * 3) Within 10% of the player: The computer doesn't like to be outside of 10% of the player
    *
    * In addition to this a couple stipulations will be placed on how many rolls the computer will make. The first is
    * given that the Expected Value of all 'good' rolls is 8, the computer will make no more than target/8 (rounded down)
    * rolls on a given turn. This is to compensate for the fact that as the computer continues to roll, it is more likely
    * to observe a snake-eyes which will completely reset the computer's score.
    *
    * The second stipulation is that should the human be within two rolls of winning (84 or greater... and the computer
    * is not in a better position, then the computer will throw a hail mary and continue rolling until it either wins
    * or at the very least surpasses the human player.
    *
    * It returns a 2-length array with position one being the target score, position two being the max number of rolls
    * the computer will take to get there.
    */
    private static int[] acquireTargets(int computer_score, int player_score){
        ArrayList<Double> targetList           =   new ArrayList<>();    // Stores our three target numbers
        int[] target_n_rolls                   =   new int[2];           // Where we store our score and roll targets.

        // First we are going to handle the hail mary situation.
        if ((winning_score - player_score <= 16) && (player_score > computer_score)) {
            target_n_rolls[0] = winning_score-computer_score;
            target_n_rolls[1] = Integer.MAX_VALUE;                       // We don't care how many rolls it takes
        }   // end if
        else{
            // Adding our three target metrics in order.
            targetList.add(10.0d);                                           // That double literal!
            targetList.add(.2 * (winning_score-computer_score));
            targetList.add(.9 * player_score);

            // Filling in our targets array!
            target_n_rolls[0] = (int)Math.round(Collections.max(targetList));  // Taking the maximum of our target list!
            target_n_rolls[1] = Math.round(target_n_rolls[0]/8);               // For accuracy, no truncation
        }   // end else
        return target_n_rolls;
    }   // end acquireTargets

    //This method returns a random integer between min and max (inclusive)!
    private static int randInt(int min, int max){
        return(randy.nextInt((max-min) + 1) + min);
    }   // END randInt
}   // End PigBot
