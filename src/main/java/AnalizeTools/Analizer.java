package AnalizeTools;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Analizer {
    List<TermFreqTuple> termFreqTuples;
    public Analizer(){
        termFreqTuples = new LinkedList<>();
    }

    public void Analize(String path){
        File mainDir = new File(path);
        File[] files = mainDir.listFiles();
        for(int i = 0; i < files.length; i++){
            if(files[i].getName().equals("documents"))
                continue;
            File
        }
    }
}
