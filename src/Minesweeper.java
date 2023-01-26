import java.util.Scanner;

public class Minesweeper {
    public static void main(String[] args){
        Minesweeper m = new Minesweeper();
        m.start();
    }

    private Field field;

    private void start(){
        this.printInstructions();
        this.selectDifficulty();
        this.playGame();
    }
    private void printInstructions(){
        System.out.println("Welcome to minesweeper!");
        System.out.println("For those who don't know the rules of the game, I will briefly explain them.");
        System.out.println("First of all, minesweeper is a game where you are given an empty board, the size of the boards depends on the difficulty.");
        System.out.println("All you have to do, is to select a cell to reveal, if you are lucky, it will be a cell without a mine.");
        System.out.println("If the cell is a mine cell, you lose, cause you explode. Otherwise, you are good to go");
        System.out.println("For the sake of difficulty, since we can get lost on huge boards, you can mark cells, as if there is a mine there, or maybe you are wrong.");
        System.out.println("Therefore, I wish you good luck ;)");
    }
    private void selectDifficulty(){
        Scanner s=new Scanner(System.in);
        System.out.println("Now, select a difficulty you want to play on.");
        System.out.println("There are 3 difficulties: ");
        System.out.println("1) Easy - 8x8 board, 10 mines.");
        System.out.println("2) Intermediate -  16x16 board, 40 mines.");
        System.out.println("3) Extreme - 16x30 board, 99 mines.");
        System.out.print("Difficulty: ");
        int diff=s.nextInt();
        while(diff<1 || diff>3){
            System.out.println("Please, type in the number from 1 to 3");
            System.out.print("Difficulty: ");
            diff=s.nextInt();
        }
        this.field = new Field(diff);
    }

    private void printCommands(){
        System.out.println("These are the commands: ");
        System.out.println("1) To flag a cell type 'f' or 'flag'.");
        System.out.println("2) To reveal a cell type 'r' or 'reveal'.");
        System.out.println("3) To leave the game type 'q' or 'quit'.");
        System.out.println("P.S. Commands are not case sensitive");
    }
    private void playGame(){
        Scanner s = new Scanner(System.in);
        this.printCommands();

        System.out.println("This is the board state.");
        System.out.println(this.field);

        while (!this.field.isWin()){ // End the game only when the player wins
            System.out.println("What do you want to do?");
            System.out.print("Command: ");
            String line = s.nextLine();
            while(line.length() == 0){
                System.out.println("Invalid command.");
                this.printCommands();
                System.out.println("What do you want to do?");
                System.out.print("Command: ");
                line = s.nextLine();
            }
            char command = (line.toLowerCase()).charAt(0); // if the user types quit, qui, qu, qeuutqeuit or anything that can possibly mean quit
            switch (command){
                case 'q' -> { // quit command
                    this.printExit(0);
                    return;
                }
                case 'r' -> { // reveal command
                    if (!this.move(s)){
                        printExit(-1);
                        return;
                    }
                }
                case 'f' -> this.flag(s); // flag command
            }
            System.out.println(this.field); // print the board after every command
        }
        if (this.field.isWin()){ // If the we get out of the loop, check the reason
            this.printExit(1);
        }

    }
    private void flag(Scanner s){
        int row = this.getRow(s);
        int col = this.getCol(s);
        if (!this.field.flag(row,col)){
            flag(s);
        }
    }
    private boolean move(Scanner s){
        int row = this.getRow(s);
        int col = this.getCol(s);

        switch (this.field.reveal(row,col)){
            case -1 -> { // Mine was revealed, end the game
                return false;
            }
            case 0 -> { // Cell already was revealed, run this function again
                return this.move(s);
            }
            default -> { // Cell revealed successfully, return
                return true;
            }
        }
    }
    private int getRow(Scanner s){
        int maxRow = this.field.getHeight();
        System.out.printf("Pick a number between 0 and %d%n",maxRow);
        System.out.print("Row: ");
        String line = s.nextLine();
        int result;
        try {
            result = Integer.parseInt(line);
            if (result < 0 || result >= maxRow){
                throw new Exception();
            }
        }catch (Exception e){
            System.out.println("Invalid input, try again");
            result = getRow(s);
        }
        return result;
    }
    private int getCol(Scanner s){
        int maxCol = this.field.getWidth();
        System.out.printf("Pick a number between 0 and %d%n",maxCol);
        System.out.print("Col: ");
        String line = s.nextLine();
        int result;
        try {
            result = Integer.parseInt(line);
            if (result < 0 || result >= maxCol){
                throw new Exception();
            }
        }catch (Exception e){
            System.out.println("Invalid input, try again");
            result = getCol(s);
        }
        return result;
    }
    private void printExit(int code){ // Prints different ending scenarios
        switch (code){
            case 0 -> System.out.println("Too bad you left so early.");
            case -1 -> {
                System.out.println("Sorry man, you lost :/");
                System.out.println(this.field);
                return;
            }
            case 1 -> System.out.println("Hey, good job, you won!");
        }
        System.out.println("This is the solution for the board");
        this.field.revealAll();
        System.out.println(this.field);
    }
}
