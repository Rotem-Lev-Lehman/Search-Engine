package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * A concrete class for getting a list of all the Documents in the files
 */
public class ReadFile implements IReadFile {
    /**
     * The DocumentFactory we will use
     */
    private IDocumentFactory documentFactory;

    private String url;

    private Parse parser; //only for parsing the number of the population size

    private JSONArray jsonAPI;

    /**
     * The files in the given folder
     */
    private List<File> files;

    private Map<String, CityInfo> citiesThatWeSaw;

    /** A constructor for the ReadFile class
     * @param documentFactory - The DocumentFactory we will use
     */
    public ReadFile(IDocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
        citiesThatWeSaw = new HashMap<>();
        url = "http://getcitydetails.geobytes.com/GetCityDetails?fqcn=";
        parser = new Parse();
        try {
            //ClassLoader classLoader = getClass().getClassLoader();
            //File jFile = new File(getClass().getResource("citiesAPI.json").toURI());
            InputStream is = getClass().getResourceAsStream("citiesAPI.json");
            Scanner scanner = new Scanner(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line = scanner.nextLine();
            scanner.close();
            jsonAPI = new JSONArray(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param path - The path of the root directory of all of the files
     * @param documents - the queue to insert to
     * @param lock - a lock for the queue
     * Read the file from the path and add all the documents from it to documents list.
     */
    @Override
    public void ReadFile(String path, Queue<Document> documents, Object lock, Semaphore empty, Semaphore full) {
        final File folder = new File(path);
        files = new ArrayList<File>();
        listFilesForFolder(folder);

        for (File file : files) {
            //System.out.println("current file = " + file.getName() + ", ");
            List<Document> docs = GetAllDocuments(file);
            //int bias = 700;
            for (int i = 0; i < docs.size(); i++) {
                //boolean wait = false;
                try {
                    full.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock){
                    //if(documents.size() > bias)
                        //wait = true;
                    //else
                    documents.add(docs.get(i));
                }
                empty.release();
                /*
                if(wait) {
                    try {
                        i--;
                        Thread.sleep(70);
                        bias /= 2;
                        if(bias < 100)
                            bias = 700;
                        continue;
                        //Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                bias = 700;
                */
            }
        }
    }

    /** Gets all of the Documents from a given file
     * @param file - The given file
     * @return The list of the Documents in the file
     */
    private List<Document> GetAllDocuments(File file) {
        List<Document> documents = new ArrayList<Document>();
        String content = ReadAGivenFile(file);
        String[] docs = content.split("<DOC>");
        for (int i = 1; i < docs.length; i++) {
            docs[i] = "<DOC>" + docs[i];
            Document document = documentFactory.CreateDocument(docs[i], file.getName());
            String city = document.getCity();
            if(city != null && !city.equals("")){
                //get document's info:
                CityInfo info = citiesThatWeSaw.get(city);
                if(info == null){
                    //we need to get the city from the API
                    //info = getCityInfoFromAPI(city);
                    info = getCityInfoFromJsonAPI(city);
                    citiesThatWeSaw.put(city, info);
                }
                document.setCityInfo(info);
            }
            documents.add(document);
        }
        return documents;
    }

    private CityInfo getCityInfoFromJsonAPI(String city){
        String lowerCity = city.toLowerCase();
        lowerCity = Character.toUpperCase(lowerCity.charAt(0)) + lowerCity.substring(1);

        String countryName = "#";
        String currencyCode = "#";
        String populationSize = "#";

        for (int i = 0; i < jsonAPI.length(); i++){
            JSONObject current = (JSONObject) jsonAPI.get(i);
            String currCity = (String)current.get("capital");
            if(currCity.equals(lowerCity)){
                countryName = (String)current.get("name");
                JSONArray currencyData = current.getJSONArray("currencies");
                JSONObject first = currencyData.getJSONObject(0);
                currencyCode = (String)first.get("code");
                String population = ((Integer)current.get("population")).toString();
                String[] term = new String[1];
                term[0] = population;
                Term num = parser.getNumber(term, 0);
                if (num != null)
                    populationSize = num.getValue();
            }
        }
        CityInfo info = new CityInfo(city, countryName, currencyCode, populationSize);
        return info;
    }

    private CityInfo getCityInfoFromAPI(String city) {
        String countryName = "#";
        String currencyCode = "#";
        String populationSize = "#";
        try {
            URL u = new URL(url + city);
            URLConnection connection = u.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = reader.readLine();

            JSONObject jsonObject = new JSONObject(result);
            String currency = (String) jsonObject.get("geobytescurrencycode");
            String country = (String) jsonObject.get("geobytescountry");
            String population = (String) jsonObject.get("geobytespopulation");

            if (population.length() != 0) {
                String[] term = new String[1];
                term[0] = population;
                Term num = parser.getNumber(term, 0);
                if (num != null)
                    populationSize = num.getValue();
            }
            if (currency.length() != 0)
                currencyCode = currency;
            if (country.length() != 0)
                countryName = country;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CityInfo(city, countryName, currencyCode, populationSize);
    }

    /** Reads a given file
     * @param file - The file that needs to be read
     * @return The file's content
     */
    private String ReadAGivenFile(File file){
        String content = null;
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    /** Creates the list of the files in the given folder
     * @param folder - The folder to search files in
     */
    private void listFilesForFolder(final File folder) {
        if(folder != null) {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    listFilesForFolder(fileEntry);
                } else {
                    files.add(fileEntry);
                }
            }
        }
    }
}
