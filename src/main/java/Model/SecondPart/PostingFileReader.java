package Model.SecondPart;

import Model.PostingRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class PostingFileReader {
    private PostingRow current;
    private boolean done;
    private Scanner scanner;
    private int currentLineNum;

    public PostingFileReader(File postingFile) {
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(postingFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        done = false;
        current = null;
        currentLineNum = -1;
    }

    public void close() {
        scanner.close();
    }

    public boolean isDone() {
        return done;
    }

    public PostingRow getCurrent(){
        return current;
    }

    public void readLineNum(int index) {
        if (index <= currentLineNum) {
            try {
                throw new Exception("Tried to search for a line that we have already passed!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        //skip all of the lines in between:
        for (int i = currentLineNum; i < index; i++) {
            if (scanner.hasNext())
                scanner.nextLine();
            else {
                done = true;
                try {
                    throw new Exception("The index given is bigger then the max index in the posting file!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        //read the wanted line:
        if (scanner.hasNext()) {
            String wantedLine = scanner.nextLine();
            current = PostingRow.ParsePostingRow(wantedLine);
            currentLineNum = index;

        } else {
            done = true;
            try {
                throw new Exception("The index given is bigger then the max index in the posting file!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
