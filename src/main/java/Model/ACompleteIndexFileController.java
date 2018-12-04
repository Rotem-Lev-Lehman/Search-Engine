package Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class ACompleteIndexFileController {
    protected BufferedWriter dictionaryWriter;
    protected BufferedWriter postingWriter;

    public void OpenFile(String folderName){
        File dictionaryFile = new File(folderName + "\\" + "dic.data");
        File postingFile = new File(folderName + "\\" + "post.data");
        try {
            dictionaryWriter = new BufferedWriter(new FileWriter(dictionaryFile));
            postingWriter = new BufferedWriter(new FileWriter(postingFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveRow(ADictionaryEntrance entrance, PostingRow row){
        String dictionaryEntrance = CreateDictionaryEntrance(entrance);
        String postingRow = CreatePostingRow(row);
        dictionaryWriter.write(dictionaryEntrance);
    }

    public void SaveRest(AIndexFileController controller){
        do{
            SaveRow(controller.getDictionaryEntrance(), controller.getPostingRow());
            controller.getNextRow();
        }
        while (!controller.done());
    }
}
