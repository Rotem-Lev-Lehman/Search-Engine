package Control;

import View.MainPageView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Observable;

/**
 * A concrete class representing the controller of the application
 */
public class Controller extends AController {
    private String root;
    private String dest;
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MainPageView) {
            if (arg instanceof String) {
                if (arg.equals("Given Source Files"))
                    openDirectoryDialog((MainPageView) o, "SRC");
                else if(arg.equals("Destination Files"))
                    openDirectoryDialog((MainPageView) o , "DST");
                else if (arg.equals("start"))
                    letsStart((MainPageView) o);
            }
            else if(arg instanceof File){
                if(((File)arg).isDirectory()){
                    openAllFiles((MainPageView)o,(File)arg);}
            }
        }
    }

    //TO DO
    /** Open the files in the root Directory given
     * @param mainPageView - The View in with to say when the process has finished
     * @param file - The root Directory where all the file are in
     */
    private void openAllFiles(MainPageView mainPageView, File file) {

        mainPageView.NotifySrcLoaded();
        File[] matchingFiles = file.listFiles();
        //System.out.println(matchingFiles[0]);
        //System.out.println(matchingFiles[1]);
        if(matchingFiles.length==2) {
            if (matchingFiles[0].isFile()) {
                model.SetStopWords(matchingFiles[0]);
                root = matchingFiles[1].getAbsolutePath();
            } else if (matchingFiles[1].isFile()) {
                model.SetStopWords(matchingFiles[1]);
                root = matchingFiles[0].getAbsolutePath();
            }
        }
        else {
            if (matchingFiles.length == 0) {
                dest= file.getAbsolutePath();
            }
            else{
                System.out.println("Input error, Please read the README file again and start over the program.");
            }
        }

    }

    private void letsStart(MainPageView mainPageView){

        mainPageView.NotifyDone();
        model.SetDestinationPath(dest);
        System.out.println(root);
        model.GetAllDocuments(root);

    }

    /** Open a directory dialog to choose the directory with the files to open
     * @param mainPageView - The View in with to open the directory dialog in
     */
    private void openDirectoryDialog(MainPageView mainPageView, String pathType) {
        mainPageView.ChooseDirectory(pathType);
    }
}