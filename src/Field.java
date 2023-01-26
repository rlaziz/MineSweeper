import java.util.*;

public class Field {
    private final char[][] field;
    private char[][] board;


    private int width;
    private int height;

    public Field(int difficulty){
        assert difficulty >= 1 && difficulty <= 3;

        switch (difficulty){
            case 1 -> {
                this.height = 8;
                this.width = 8;
            }
            case 2 -> {
                this.height = 16;
                this.width = 16;
            }
            case 3 -> {
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
            case 1 -> this.setBoard(10);
            case 2 -> this.setBoard(40);
            case 3 -> this.setBoard(99);
        }
    }
    private HashSet<String> randomizeMines(int mines){
        Random r = new Random();
        HashSet<String> picked = new HashSet<>();

        while (mines > 0){
            int row = r.nextInt(this.height);
            int col = r.nextInt(this.width);
            String combination = row + "-" + col;
            while (picked.contains(combination)){
                row = r.nextInt(this.height);
                col = r.nextInt(this.width);
                combination = row + "-" + col;
            }
            picked.add(combination);
            mines--;
        }
        return picked;
    }
    private void setBoard(int mines){ //
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
        for (char[] row : this.field){
            System.out.println(Arrays.toString(row));
        }
        this.setValues();
    }

    private void setValues(){
        for (int i = 0; i < this.height; i++){
            for (int j = 0; j < this.width; j++){
                if (this.field[i][j] != 'M'){
                    int around = this.minesAround(i,j);
                    this.field[i][j] = (char) (around + 48);
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
                }catch (IndexOutOfBoundsException ignored){
                }
            }
        }
        return count;
    }

    public boolean flag(int row, int col){
        assert row >= 0 && row < this.height;
        assert col >= 0 && col < this.width;

        if (this.board[row][col] != '\u0000' && this.board[row][col] != '!'){
            return false;
        }

        this.board[row][col] = this.board[row][col] == '!' ? '\u0000' : '!'; // aka FLAG in minesweeper
        return true;
    }

    public int reveal(int row, int col){
        assert row >= 0 && row < this.height;
        assert col >= 0 && col < this.width;

        if (this.isMine(row,col)){
            this.revealMines(row,col);
            return -1;
        }
        if (this.board[row][col] != '\u0000' && this.board[row][col] != '!'){
            System.out.println("You have already revealed this cell. Try another");
            return 0;
        }
        this.revealCell(row,col);
        return 1;
    }
    private void revealMines(int row, int col){
        for (int i = 0; i < this.height; i ++){
            for (int j = 0; j < this.width; j++){
                if(this.field[i][j] == 'M'){
                    this.board[i][j] = 'M';
                }
            }
        }
        this.board[row][col] = 'X';
    }
    private void revealCell(int row, int col){
        this.board[row][col] = this.field[row][col];
        if (this.field[row][col] == '0'){
            for (int i = row - 1; i < row + 2; i++){
                for (int j = col - 1; j < col + 2; j++){
                    try {
                        if(!(this.field[i][j] == 'M') && this.board[i][j] == '\u0000'){
                            this.revealCell(i,j);
                        }
                    }catch (IndexOutOfBoundsException ignored){}
                }
            }
        }
    }
    private boolean isMine(int row, int col){
        assert row >= 0 && row < this.height;
        assert col >= 0 && col < this.width;
        return this.field[row][col] == 'M';
    }

    @Override
    public String toString(){
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


    public void revealAll(){
        this.board = this.field;
    }
    public boolean isWin(){
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
