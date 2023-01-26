import java.util.*;

public class Field {
    private final char[][] field; // Finalized field that will be our reference
    private char[][] board;


    private int width;
    private int height;

    public Field(int difficulty){
        assert difficulty >= 1 && difficulty <= 3;

        switch (difficulty){
            case 1 -> { // Easy
                this.height = 8;
                this.width = 8;
            }
            case 2 -> { // Intermediate
                this.height = 16;
                this.width = 16;
            }
            case 3 -> { // Difficult
                this.height = 16;
                this.width = 30;
            }
        }
        this.field = new char[this.height][this.width];
        this.board = new char[this.height][this.width];
        this.fillBoard(difficulty);
    }

    private void fillBoard(int difficulty){
        switch (difficulty){
            case 1 -> this.setBoard(10); // Easy mode
            case 2 -> this.setBoard(40); // Intermediate mode
            case 3 -> this.setBoard(99); // Difficult mode
        }
    }
    private HashSet<String> randomizeMines(int mines){ // Method that returns a list of size mines with positions of the mines
        Random r = new Random();
        HashSet<String> picked = new HashSet<>(); // Best data-structure to use in our case, contains - O(1)

        while (mines > 0){

            int row = r.nextInt(this.height);
            int col = r.nextInt(this.width);
            String combination = row + "-" + col; // Generate a key

            while (picked.contains(combination)){
                row = r.nextInt(this.height);
                col = r.nextInt(this.width);
                combination = row + "-" + col;
            }

            picked.add(combination); // If the key is unique, add it to picked positions, and look for the next mine if needed
            mines--;
        }
        return picked;
    }
    private void setBoard(int mines){ // Filling out the reference field
        HashSet<String> minesPositions = randomizeMines(mines);
        for (char[] row : this.field){
            Arrays.fill(row,'N');
        }

        for (String combination : minesPositions){
            String[] parts = combination.split("-");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            this.field[row][col] = 'M';
        }

        this.setValues(); // Change 'N' by appropriate number
    }

    private void setValues(){
        for (int i = 0; i < this.height; i++){
            for (int j = 0; j < this.width; j++){
                if (this.field[i][j] != 'M'){ // If the current cell is not a mine cell, check how many mines are nearby
                    int around = this.minesAround(i,j);
                    this.field[i][j] = (char) (around + 48); // (char) (0 + 48) = '0', (char) (1 + 48) = '1' , ...
                }
            }
        }
    }
    private int minesAround(int row, int col){ // O(1) - since the loop will always have 9 iterations
        int count = 0;
        for (int i = row - 1; i < row + 2; i++){
            for(int j = col - 1; j < col + 2; j++){
                try {
                    count += this.field[i][j] == 'M' ? 1 : 0;
                }catch (IndexOutOfBoundsException ignored){ // Avoiding extra code for some edge cases
                }
            }
        }
        return count;
    }

    public boolean flag(int row, int col){
        assert row >= 0 && row < this.height;
        assert col >= 0 && col < this.width;

        if (this.board[row][col] != '\u0000' && this.board[row][col] != '!'){ // if the cell is already open why would we flag it?
            return false;
        }

        this.board[row][col] = this.board[row][col] == '!' ? '\u0000' : '!'; // aka FLAG in minesweeper
        return true;
    }

    public int reveal(int row, int col){
        assert row >= 0 && row < this.height;
        assert col >= 0 && col < this.width;

        if (this.isMine(row,col)){ // if the cell revealed is a mine cell, end the game
            this.revealMines(row,col);
            return -1; // code meaning that the game ended by user revealing a mine
        }
        if (this.board[row][col] != '\u0000' && this.board[row][col] != '!'){ // Why would you reveal a cell you already revealed?
            System.out.println("You have already revealed this cell. Try another");
            return 0; // code meaning that the currently picked cell is a waste of a move, hence ask for a new one
        }
        this.revealCell(row,col); // Reveal the given cell
        return 1; // cell revealed successfully
    }
    private void revealMines(int row, int col){ // Reveal all mines when user losses the game
        for (int i = 0; i < this.height; i ++){
            for (int j = 0; j < this.width; j++){
                if(this.field[i][j] == 'M'){
                    this.board[i][j] = 'M';
                }
            }
        }
        this.board[row][col] = 'X'; // Mark the mine cell user pressed on which lost him the game
    }
    private void revealCell(int row, int col){
        this.board[row][col] = this.field[row][col]; // Reveal the current cell
        if (this.field[row][col] == '0'){ // if the cell currently revealed has 0 mines around, reveal all the neighbors
            for (int i = row - 1; i < row + 2; i++){
                for (int j = col - 1; j < col + 2; j++){
                    try {
                        if(!(this.field[i][j] == 'M') && this.board[i][j] == '\u0000'){
                            this.revealCell(i,j); // Maybe the neighbor is 0 too
                        }
                    }catch (IndexOutOfBoundsException ignored){} // No extra code for edge cases
                }
            }
        }
    }
    private boolean isMine(int row, int col){ // is the given cell a mine cell
        assert row >= 0 && row < this.height;
        assert col >= 0 && col < this.width;
        return this.field[row][col] == 'M';
    }

    @Override
    public String toString(){ // Custom toString method, returns a String representation of the board
        StringBuilder result = new StringBuilder();
        result.append("row");
        result.append((" \u0332" + " \u0332" + " \u0332" + " \u0332").repeat(this.height));
        result.append(" \u0332 \n");
        for(int i=0;i<this.height;i++){
            result.append(" ").append(i).append(" ");
            for(int j=0;j<this.width;j++){
                result.append(" \u2502 ");
                if(this.board[i][j] == '!'){
                    result.append("!");
                    continue;
                }
                result.append(this.board[i][j] == '\u0000' ? ' ' : this.board[i][j]);
            }
            result.append(" \u2502 \n");
        }
        result.append("    \u203E");
        result.append(("\u203E" + "\u203E" + "\u203E" + "\u203E").repeat(this.height));
        result.append("\n" + "col   ");
        for(int i=0;i<this.height;i++){
            result.append(i).append("   ");
        }
        return result.toString();
    }


    public void revealAll(){ // The game ended in some way other than loss, reveal everything
        this.board = this.field;
    }
    public boolean isWin(){ // Check if the current state of the board is a win
        for (int i = 0; i < this.height; i++){
            for (int j = 0; j < this.width; j++){
                if (this.board[i][j] != this.field[i][j] && this.field[i][j] != 'M'){
                    return false;
                }
            }
        }
        return true;
    }
    public int getHeight(){
        return this.height;
    }
    public int getWidth(){
        return this.width;
    }
}
