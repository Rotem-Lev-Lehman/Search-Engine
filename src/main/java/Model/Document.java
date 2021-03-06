package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a Document
 */
public class Document {
    /**
     * DOCNO - The id of the Document
     */
    private String DOCNO;

    private List<String> cities;

    private CityInfo cityInfo;

    private String filename;
    /**
     * DATE1 - The date that the Document was published
     */
    private LocalDate DATE1;
    /**
     * TI - The headline of the Document
     */
    private String TI;
    /**
     * TEXT - The content of the Document
     */
    private String TEXT;

    /**
     * An empty constructor
     */
    public Document() {
        this.DOCNO = null;
        this.DATE1 = null;
        this.TI = null;
        this.TEXT = null;
        this.cities = new ArrayList<>();
        this.cityInfo = null;
    }

    /**
     * A full constructor
     * @param DOCNO - The id of the Document
     * @param DATE1 - The date that the Document was published
     * @param TI - The headline of the Document
     * @param TEXT - The content of the Document
     */
    public Document(String DOCNO, LocalDate DATE1, String TI, String TEXT, String filename, List<String> cities) {
        this.DOCNO = DOCNO;
        this.DATE1 = DATE1;
        this.TI = TI;
        this.TEXT = TEXT;
        this.filename = filename;
        this.cities = cities;
        this.cityInfo = null;
    }

    public String getFilename(){
        return filename;
    }

    /** A getter for the DOCNO
     * @return The Document's DOCNO
     */
    public String getDOCNO() {
        return DOCNO;
    }

    /** A setter for the DOCNO
     * @param DOCNO - The id of the Document
     */
    public void setDOCNO(String DOCNO) {
        this.DOCNO = DOCNO;
    }

    /** A getter for the DATE1
     * @return The Document's DATE1
     */
    public LocalDate getDATE1() {
        return DATE1;
    }

    /** A setter for the DATE1
     * @param DATE1 - The date that the Document was published
     */
    public void setDATE1(LocalDate DATE1) {
        this.DATE1 = DATE1;
    }

    /** A getter for the TI
     * @return The Document's TI
     */
    public String getTI() {
        return TI;
    }

    /** A setter for the TI
     * @param TI - The headline of the Document
     */
    public void setTI(String TI) {
        this.TI = TI;
    }

    /** A getter for the TEXT
     * @return The Document's TEXT
     */
    public String getTEXT() {
        return TEXT;
    }

    /** A setter for the TEXT
     * @param TEXT - The content of the Document
     */
    public void setTEXT(String TEXT) {
        this.TEXT = TEXT;
    }

    public List<String> getCity() {
        return cities;
    }

    public String getOnlyCity(){
        if(cities.size() == 0)
            return null;
        return cities.get(0);
    }

    public void setCity(List<String> cities) {
        this.cities = cities;
    }

    public CityInfo getCityInfo() {
        return cityInfo;
    }

    public void setCityInfo(CityInfo cityInfo) {
        this.cityInfo = cityInfo;
    }
}
