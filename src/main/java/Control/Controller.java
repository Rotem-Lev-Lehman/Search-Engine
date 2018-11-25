package Control;

import View.MainPageView;

import java.io.File;
import java.util.Observable;

/**
 * A concrete class representing the controller of the application
 */
public class Controller extends AController {
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MainPageView) {
            if (arg instanceof String) {
                if (arg.equals("Open Files"))
                    openDirectoryDialog((MainPageView) o);
                else if (arg.equals("Open Stop words"))
                    openStopWordsDialog((MainPageView) o);
            }
            else if(arg instanceof File){
                if(((File)arg).isDirectory())
                    openAllFiles((MainPageView)o,(File)arg);
                else if(((File)arg).isFile())
                    openStopWordsFile((MainPageView)o,(File)arg);
            }
        }
    }

    /** Open the stop words file given
     * @param mainPageView - The View in with to say when the process has finished
     * @param file - The file to open
     */
    private void openStopWordsFile(MainPageView mainPageView, File file) {
        model.SetStopWords(file);

        mainPageView.NotifyStopWordsLoaded();
    }

    /** Open a file dialog to choose the file with the stop words to open
     * @param mainPageView - The View in with to open the file dialog in
     */
    private void openStopWordsDialog(MainPageView mainPageView) {
        mainPageView.ChooseFile();
    }

    /** Open the files in the root Directory given
     * @param mainPageView - The View in with to say when the process has finished
     * @param root - The root Directory where all the file are in
     */
    private void openAllFiles(MainPageView mainPageView, File root) {
        String path = root.getAbsolutePath();
        model.GetAllDocuments(path);

        mainPageView.NotifyDone();
    }

    /** Open a directory dialog to choose the directory with the files to open
     * @param mainPageView - The View in with to open the directory dialog in
     */
    private void openDirectoryDialog(MainPageView mainPageView) {
        mainPageView.ChooseDirectory();
    }
}