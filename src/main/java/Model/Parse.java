package Model;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * A concrete class for parsing documents
 */
public class Parse implements IParse {

    private HashSet stopWords; // change to hash set
    private String[] tokens;
    private int currentIndex;
    private int amountOfTokens;
    private List<Term> terms;
    private List<String> months;
    private Dictionary<String, String> monthNumber;
    private List<Character> possibleChars;
    private SnowballStemmer stemmer;

    public Parse() {
        stemmer = new porterStemmer();
        initializePossibleChars();
        initializeMonth();
    }

    @Override
    public void CreateStopWords(File file) {
        String content = ReadAGivenFile(file);
        stopWords = new HashSet();
        String[] words = content.split("\n");
        List<String> w = Arrays.asList(words);

        stopWords.addAll(w);
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

    private void initializePossibleChars() {
        Character[] chars = new Character[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '$', '%', '-'};
        possibleChars = Arrays.asList(chars);
    }

    private void initializeMonth() {
        //create months
        String[] month = new String[]{"JANUARY", "JAN", "FEBRUARY", "FEB", "MARCH", "MAR", "APRIL", "APR", "MAY", "JUNE", "JUN",
                "JULY", "JUL", "AUGUST", "AUG", "SEPTEMBER", "SEP", "OCTOBER", "OCT", "NOVEMBER", "NOV", "DECEMBER", "DEC"};
        months = Arrays.asList(month);

        //create monthNumber
        monthNumber = new Hashtable<>();
        monthNumber.put("JANUARY", "01");
        monthNumber.put("JAN", "01");
        monthNumber.put("FEBRUARY", "02");
        monthNumber.put("FEB", "02");
        monthNumber.put("MARCH", "03");
        monthNumber.put("MAR", "03");
        monthNumber.put("APRIL", "04");
        monthNumber.put("APR", "04");
        monthNumber.put("MAY", "05");
        monthNumber.put("JUNE", "06");
        monthNumber.put("JUN", "06");
        monthNumber.put("JULY", "07");
        monthNumber.put("JUL", "07");
        monthNumber.put("AUGUST", "08");
        monthNumber.put("AUG", "08");
        monthNumber.put("SEPTEMBER", "09");
        monthNumber.put("SEP", "09");
        monthNumber.put("OCTOBER", "10");
        monthNumber.put("OCT", "10");
        monthNumber.put("NOVEMBER", "11");
        monthNumber.put("NOV", "11");
        monthNumber.put("DECEMBER", "12");
        monthNumber.put("DEC", "12");
    }

    @Override
    public List<Term> Parse(Document document) {
        tokens = document.getTEXT().split(" ");
        terms = new ArrayList<Term>();
        amountOfTokens = tokens.length;

        for (currentIndex = 0; currentIndex < amountOfTokens; currentIndex++) {
            if(tokens[currentIndex].equals(""))
                continue;
            if (!tokens[currentIndex].toUpperCase().equals("BETWEEN")) {
                if (stopWords.contains(tokens[currentIndex].toLowerCase()))
                    continue;
                if (isRegularTerm(tokens[currentIndex])) {
                    saveRegularTerm(tokens[currentIndex]);
                    continue;
                }
            }
            getNextTerm();
        }
        return terms;
    }

    private void getNextTerm() {

        //Check between
        if (parseBetween())
            return;

        if (parseDates())
            return;

        if (parseRangeOrPhrase())
            return;

        if (parseNumbers())
            return;

        saveRegularTerm(tokens[currentIndex]);
        //Done
    }

    /**
     * Check if the next term is a number/price/percentage term, and save it if so
     *
     * @return true if parsed anything and false otherwise
     */
    private boolean parseNumbers() {
        Term number = getNumber(tokens,currentIndex);
        if(number != null){
            int nextIndex = currentIndex + number.getNumOfTokensParsed();
            if (nextIndex < tokens.length) {
                if (tokens[nextIndex].toUpperCase().equals("PERCENT") || tokens[nextIndex].toUpperCase().equals("PERCENTAGE")) {
                    String token = number.getValue() + "%";
                    saveCompleteTerm(token);
                    currentIndex = nextIndex;
                    return true;
                }
                int amountOfDollar = getAmountOfDollar(nextIndex);
                if(amountOfDollar > 0) {
                    //There is a dollar
                    String token = ChangeToPriceFormat(number.getValue()) + " Dollars";
                    saveCompleteTerm(token);
                    currentIndex = nextIndex + amountOfDollar - 1;
                    return true;
                }
            }
            //Its a regular number...
            saveTerm(number);
            currentIndex = nextIndex - 1;
            return true;
        }
        if(tokens[currentIndex].contains("%")){
            //number%
            saveCompleteTerm(tokens[currentIndex]);
            return true;
        }
        if(tokens[currentIndex].charAt(0) == '$'){
            String dollarNum = tokens[currentIndex].substring(1);
            if(!dollarNum.equals("")) {
                int count = amountOfTokens - currentIndex - 1;
                int min = Math.min(count, 2);
                if (min > 0) {
                    String[] tempForCheckingNumber = new String[min + 1];
                    tempForCheckingNumber[0] = dollarNum;
                    for (int j = 0; j < min; j++)
                        tempForCheckingNumber[j + 1] = tokens[currentIndex + j + 1];
                    Term numberOfDollar = getNumber(tempForCheckingNumber, 0);
                    if (numberOfDollar != null) {
                        currentIndex += numberOfDollar.getNumOfTokensParsed() - 1;
                        saveCompleteTerm("$" + ChangeToPriceFormat(numberOfDollar.getValue()));
                        return true;
                    } else
                        return false;
                }
                Term numberOfDollar = getNumber(new String[]{dollarNum}, 0);
                if (numberOfDollar != null) {
                    currentIndex += numberOfDollar.getNumOfTokensParsed() - 1;
                    saveCompleteTerm("$" + ChangeToPriceFormat(numberOfDollar.getValue()));
                    return true;
                }
            }
        }
        return false; // I'm not your number, stupid dollar
    }

    /**
     * Changes a number in regular format to the price format
     *
     * @param number - number to be changed
     * @return a price format of the given number
     */
    private String ChangeToPriceFormat(String number) {
        char kmbt = number.charAt(number.length() - 1);
        try {
            Integer.parseInt(kmbt + "");
            //No KMBT
            //That means we are less then 1000
            //Then we need to return the same number that we received
            return number;
        } catch (Exception e) {
            //There is a KMBT
            //There is no fraction in this case (removed them in the multiply function :) )
            String num2 = number.substring(0, number.length() - 1);
            if (kmbt == 'M') {
                num2 += " M";
                return num2;
            }
            //Parse other then M
            double parsed = Double.parseDouble(num2);
            if (kmbt == 'K') {
                parsed *= 1000;
                if (parsed == (int) parsed)
                    return (int) parsed + "";
                return parsed + "";
            }
            if (kmbt == 'B') {
                parsed /= 1000;
                if (parsed == (long) parsed)
                    return (long) parsed + " M";
                return parsed + " M";
            }
        }
        return number; //will not reach here...
    }

    /**
     * Gets the amount of dollar indicator (can be U.S. Dollars / Dollars / none)
     *
     * @param i - the index where to check from
     * @return the amount of dollar indicator (0 if none)
     */
    private int getAmountOfDollar(int i) {
        if(tokens[i].toUpperCase().equals("DOLLARS"))
            return 1;
        if(tokens[i].toUpperCase().equals("U.S.") && i + 1 < amountOfTokens && tokens[i + 1].toUpperCase().equals("DOLLARS"))
            return 2;
        return 0;
    }

    /**
     * Check if the next term is a date term, and save it if so
     *
     * @return true if parsed anything and false otherwise
     */
    private boolean parseDates(){
        //Check Dates
        if (months.contains(tokens[currentIndex].toUpperCase())) {
            //Month DD or Month Year
            if (currentIndex + 1 < tokens.length) {
                if (isDayOfMonth(tokens[currentIndex + 1])) {
                    //Month DD
                    String token = monthNumber.get(tokens[currentIndex].toUpperCase()) + "-" + tokens[currentIndex + 1];
                    saveCompleteTerm(token);
                    return true;
                }
                if (isYear(tokens[currentIndex + 1])) {
                    //Month Year
                    String token = tokens[currentIndex + 1] + "-" + monthNumber.get(tokens[currentIndex].toUpperCase());
                    saveCompleteTerm(token);
                    return true;
                }
            }
        } else if (isDayOfMonth(tokens[currentIndex])) {
            //DD Month?
            if (currentIndex + 1 < tokens.length) {
                if (months.contains(tokens[currentIndex + 1].toUpperCase())) {
                    //DD Month
                    String token = monthNumber.get(tokens[currentIndex + 1].toUpperCase()) + "-" + tokens[currentIndex];
                    saveCompleteTerm(token);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isYear(String token) {
        try{
            int year = Integer.parseInt(token);
            return (1000 <= year && year <= 9999);
        }
        catch (Exception e){
            return false;
        }
    }

    private boolean isDayOfMonth(String token){
        try{
            int day = Integer.parseInt(token);
            return (1 <= day && day <= 31);
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * Check if the next term is Between number and number
     *
     * @return true if parsed anything and false otherwise
     */
    private boolean parseBetween() {
        //check between number and number
        if (tokens[currentIndex].toUpperCase().equals("BETWEEN")) {
            if (currentIndex + 1 < amountOfTokens) {
                Term num = getNumber(tokens,currentIndex + 1);
                if (num == null)
                    return true;
                else {
                    saveTerm(num);
                    int andIndex = currentIndex + num.getNumOfTokensParsed() + 1;
                    if (andIndex < amountOfTokens) {
                        if (tokens[andIndex].toUpperCase().equals("AND")) {
                            if (andIndex + 1 < amountOfTokens) {
                                Term num2 = getNumber(tokens,andIndex + 1);
                                if (num2 != null) {
                                    saveTerm(num2);
                                    saveCompleteTerm(num.getValue() + "-" + num2.getValue());
                                    currentIndex = andIndex + num2.getNumOfTokensParsed();
                                    return true;
                                }
                            }
                            currentIndex = andIndex;
                            return true;
                        }
                    }
                    currentIndex = currentIndex + num.getNumOfTokensParsed();
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Check if the next term is one of the options:
     * Word-Word
     * Word-Word-Word
     * Word-Word-Word-...-Word
     * Number-Word
     * Word-Number
     * Number-Number
     *
     * @return true if parsed anything and false otherwise
     */
    private boolean parseRangeOrPhrase() {
        //check Word-something
        if (tokens[currentIndex].substring(1).contains("-")) {
            String[] split = tokens[currentIndex].split("-");
            String phrase = "";
            for (int i = 0; i < split.length; i++) {
                Term num = getNumber(split,i);
                if (num == null) {
                    //its a word
                    phrase += split[i];

                } else {
                    //its a number
                    if (i == split.length - 1) {
                        int count = amountOfTokens - currentIndex - 1;
                        int min = Math.min(count, 2);
                        if (min > 0) {
                            String[] tempForCheckingNumber = new String[min + 1];
                            tempForCheckingNumber[0] = split[i];
                            for (int j = 0; j < min; j++)
                                tempForCheckingNumber[j + 1] = tokens[currentIndex + j + 1];
                            Term number = getNumber(tempForCheckingNumber, 0);
                            //must not be null, because of the 'else'
                            currentIndex += number.getNumOfTokensParsed() - 1;
                            saveTerm(number);
                            phrase += number.getValue();
                            saveCompleteTerm(phrase);
                            return true;
                        }
                        saveTerm(num);
                        phrase += num.getValue();
                        saveCompleteTerm(phrase);
                        return true;
                    }
                    saveTerm(num);
                    phrase += num;
                }
                phrase += "-";
            }
            if(phrase.length() > 0) {
                phrase = phrase.substring(0, phrase.length() - 1);
                saveCompleteTerm(phrase);
                return true;
            }
        }
        //until here its all good
        //need to handle number-number, number-word
        Term number = getNumber(tokens, currentIndex);
        if(number != null){
            if(number.isEndedWithHyphen())
            {
                saveTerm(number);

                int splitIndex = currentIndex + number.getNumOfTokensParsed() - 1;
                //in this index there must be a hyphen.
                String[] split = tokens[splitIndex].split("-");
                if(split.length < 2)
                    return true;

                int count = amountOfTokens - splitIndex - 1;
                int min = Math.min(count, 2);
                if (min > 0) {
                    String[] tempForCheckingNumber = new String[min + 1];
                    tempForCheckingNumber[0] = split[1];
                    for (int j = 0; j < min; j++)
                        tempForCheckingNumber[j + 1] = tokens[currentIndex + j + 1];
                    Term number2 = getNumber(tempForCheckingNumber, 0);

                    if(number2!= null){
                        saveTerm(number2);
                        saveCompleteTerm(number.getValue() + "-" + number2.getValue());
                        currentIndex += splitIndex +number2.getNumOfTokensParsed() - 1;
                        return true;
                    }
                    saveCompleteTerm(number.getValue() + "-" + split[1]);
                    currentIndex += splitIndex;
                    return true;
                }
                Term number2 = getNumber(split, 1);

                if(number2 != null){
                    saveTerm(number2);
                    saveCompleteTerm(number.getValue() + "-" + number2.getValue());
                    currentIndex += splitIndex + number2.getNumOfTokensParsed() - 1;
                    return true;
                }
                saveCompleteTerm(number.getValue() + "-" + split[1]);
                currentIndex += splitIndex;
                return true;
            }
        }
        return false;
    }

    /**
     * Parse the number from i, in the array tokens
     *
     * @param tokens - the array of tokens we want to check if a number is in
     * @param i - start index
     * @return Term of the parsed number, or null if not number
     */
    private Term getNumber(String[] tokens, int i) {
        if (isNumber(tokens[i])) {
            //Check if a solo number
            if (i + 1 < tokens.length) {
                if(tokens[i+1].contains("-") && tokens[i+1].replace("-","").length()!=0){
                    String next = tokens[i+1].split("-")[0]; //we care about only the first one...
                    if(isFraction(next)){
                        //number fraction-
                        String totalNumber = multiply(tokens[i], next, "1");
                        return new Term(totalNumber, 2, true);
                    }
                    String kmbt = getKMBT(next);
                    if (kmbt != null) {
                        //Num KMBT-
                        String totalNumber = multiply(tokens[i], "", kmbt);
                        return new Term(totalNumber, 2, true);
                    }
                    String totalNumber = multiply(tokens[i], "", "1");
                    return new Term(totalNumber, 1);
                }
                if (isFraction(tokens[i + 1])) {
                    //So far its a Num Fraction
                    //currentIndex++;
                    if (i + 2 < tokens.length) {
                        if(tokens[i+2].contains("-") && tokens[i+2].replace("-","").length()!=0) {
                            String next = tokens[i+2].split("-")[0]; //we care about only the first one...
                            String kmbt = getKMBT(next);
                            if (kmbt != null) {
                                //Num fraction KMBT-
                                String totalNumber = multiply(tokens[i], tokens[i + 1], kmbt);
                                return new Term(totalNumber, 3, true);
                            }
                            String totalNumber = multiply(tokens[i], tokens[i + 1], "1");
                            return new Term(totalNumber, 2);
                        }
                        String kmbt = getKMBT(tokens[i + 2]);
                        if (kmbt != null) {
                            //Num Fraction KMBT
                            String totalNumber = multiply(tokens[i], tokens[i + 1], kmbt);
                            return new Term(totalNumber, 3);
                        }
                    }
                    //Num Fraction
                    String totalNumber = multiply(tokens[i], tokens[i + 1], "1");
                    return new Term(totalNumber, 2);
                }
                String kmbt = getKMBT(tokens[i + 1]);
                if (kmbt != null) {
                    //Num KMBT
                    String totalNumber = multiply(tokens[i], "", kmbt);
                    return new Term(totalNumber, 2);
                }
            }
            //Only num
            String totalNumber = multiply(tokens[i], "", "1");
            return new Term(totalNumber, 1);
        }
        if (isDecimalNumber(tokens[i])) {
            //Check if a solo number
            if (i + 1 < tokens.length) {
                if(tokens[i+1].contains("-") && tokens[i+1].replace("-","").length()!=0){
                    String next = tokens[i+1].split("-")[0]; //we care about only the first one...
                    String kmbt = getKMBT(next);
                    if (kmbt != null) {
                        //Num KMBT-
                        String totalNumber = multiplyDecimal(tokens[i], kmbt);
                        return new Term(totalNumber, 2, true);
                    }
                    String totalNumber = multiplyDecimal(tokens[i], "1");
                    return new Term(totalNumber, 1);
                }
                String kmbt = getKMBT(tokens[i + 1]);
                if (kmbt != null) {
                    //Num KMBT
                    String totalNumber = multiplyDecimal(tokens[i], kmbt);
                    return new Term(totalNumber, 2);
                }
            }
            //Only num
            String totalNumber = multiplyDecimal(tokens[i], "1");
            return new Term(totalNumber, 1);
        }
        if(isFraction(tokens[i])){
            //So its a Fraction
            String fraction = tokens[i].replace(",","");
            return new Term(tokens[i], 1);
        }
        return null;
    }

    private String multiply(String number, String fraction, String kmbt) {
        long num = Long.parseLong(number.replace(",", ""));
        long KMBT = Long.parseLong(kmbt);
        String frac = fraction.replace(",","");

        // Apply all of the rules to negative numbers too
        int flag = 1;
        if(num < 0) {
            num *= -1;
            flag = -1;
        }

        long num_kmbt = num * KMBT;

        if (0 <= num_kmbt && num_kmbt < 1000) {
            //kmbt must be 1
            return num + " " + frac;
        }
        if (1000 <= num_kmbt && num_kmbt < 1000000) {
            double n = num_kmbt / 1000.0;
            if (n == (int) n)
                return ((int) n * flag) + "K";
            return n * flag + "K";
        }
        if (1000000 <= num_kmbt && num_kmbt < 1000000000) {
            double n = num_kmbt / 1000000.0;
            if (n == (int) n)
                return ((int) n * flag)+ "M";
            return n * flag + "M";
        }
        if (1000000000 <= num_kmbt) {
            double n = num_kmbt / 1000000000.0;
            if (n == (long) n)
                return ((long) n * flag) + "B";
            return n * flag + "B";
        }
        return num + " " + frac; //will not reach here anyway, so why not?
    }

    private String multiplyDecimal(String decimalNumber, String kmbt) {
        double num = Double.parseDouble(decimalNumber.replace(",", ""));
        long KMBT = Long.parseLong(kmbt);

        // Apply all of the rules to negative numbers too
        int flag = 1;
        if (num < 0) {
            num *= -1;
            flag = -1;
        }

        double num_kmbt = num * KMBT;

        if (0 <= num_kmbt && num_kmbt < 1000) {
            if (num_kmbt == (int) num_kmbt)
                return ((int) num_kmbt * flag) + "";
            return num_kmbt * flag + "";
        }
        if (1000 <= num_kmbt && num_kmbt < 1000000) {
            double n = num_kmbt / 1000.0;
            if (n == (int) n)
                return ((int) n * flag) + "K";
            return n * flag + "K";
        }
        if (1000000 <= num_kmbt && num_kmbt < 1000000000) {
            double n = num_kmbt / 1000000.0;
            if (n == (int) n)
                return ((int) n * flag) + "M";
            return n * flag + "M";
        }
        if (1000000000 <= num_kmbt) {
            double n = num_kmbt / 1000000000.0;
            if (n == (long) n)
                return ((long) n * flag) + "B";
            return n * flag + "B";
        }
        return num + ""; //will not reach here anyway, so why not?
    }

    private String getKMBT(String token) {
        if (token.toUpperCase().equals("THOUSAND"))
            return "1000";
        if (token.toUpperCase().equals("MILLION"))
            return "1000000";
        if (token.toUpperCase().equals("BILLION") || token.equals("bn"))
            return "1000000000";
        if (token.toUpperCase().equals("TRILLION"))
            return "1000000000000";
        return null;
    }

    private void saveRegularTerm(String token) {
        stemmer.setCurrent(token.toLowerCase());
        String stemmed;
        if (stemmer.stem())
            stemmed = stemmer.getCurrent();
        else
            stemmed = token;
        if(Character.isUpperCase(token.charAt(0)))
            stemmed = stemmed.toUpperCase();
        saveCompleteTerm(stemmed);
    }

    private void saveCompleteTerm(String token) {
        terms.add(new Term(token));
    }

    private void saveTerm(Term term) {
        terms.add(term);
    }

    /**
     * check if a token is a regular term
     *
     * @param token - the token to check if is regular term
     * @return true if the token is a regular term, and false otherwise
     */
    private boolean isRegularTerm(String token) {
        char firstChar = token.charAt(0);
        return !(possibleChars.contains(firstChar) || months.contains(token) || token.contains(",") || token.contains(".") || token.contains("/") || token.contains("%") || token.contains("-"));
    }

    /**
     * if it is a regular number (non decimal) return true else return false
     * also check negative numbers
     *
     * @param str - string to check if is number
     * @return true if non decimal number, false otherwise
     */
    private boolean isNumber(String str) {
        String num = str;
        try {
            long num2 = Long.parseLong(num.replace(",", ""));
            //Its ok...
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * if it is a decimal number return true else return false
     * also check negative decimal numbers
     *
     * @param str - string to check if is decimal number
     * @return true if decimal number, false otherwise
     */
    private boolean isDecimalNumber(String str) {
        String num = str;
        if(!str.contains("."))
            return false; //only pure decimal numbers are welcome here!

        try {
            double num2 = Double.parseDouble(num.replace(",", ""));
            //Its ok...
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * if it is a fraction return true else return false
     *
     * @param str - string to check if is fraction
     * @return true if fraction, false otherwise
     */
    private boolean isFraction(String str) {
        if(str.contains("-"))
            return false;
        String[] split = str.split("/");
        if(split.length != 2)
            return false;
        for (String num : split) {
            if (!isNumber(num))
                return false;
        }
        return true;
    }
}
