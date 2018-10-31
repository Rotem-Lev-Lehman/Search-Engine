package Model;

import java.util.List;

/**
 * A concrete class for parsing documents
 */
public class Parse implements IParse {

    private List<String> stopWords;

    public Parse(List<String> stopWords) {
        this.stopWords = stopWords;
    }

    @Override
    public List<Term> Parse(Document document) {
        String[] basicTerms = document.getTEXT().split(" ");

        for (int i = 0; i < basicTerms.length; i++) {
            //($ v lambda)(N.N v (N (lambda v N/N))) (lambda v KMBT) ((% v Dollar v lambda)in condition that there is no $ in the start)
            //(W v N)-(W v N)(-(W v N))* v between N and N
            if (isNumber(basicTerms[i])) {
                //Check if a solo number
                if (i + 1 < basicTerms.length) {
                    if (isFraction(basicTerms[i + 1])) {
                        //So far its a Num Fraction
                        i++;
                        if (i + 1 < basicTerms.length) {
                            if (isKMBT(basicTerms[i + 1])) {

                            }

                            if (isDollar(basicTerms[i + 1])) {
                                //So it is Num Fraction Dollar
                            }
                        }
                    }
                }
            }
        }


    }

    private boolean isKMBT(String str) {
    }

    private boolean isNumber(String str){

    }

    private boolean isDecimalNumber(String str){

    }

    private boolean isFraction(String str){

    }

    private boolean isPercent(String str){

    }

    private boolean isDollar(String str){

    }

    private boolean isDate(String str){

    }

    private boolean isPhraseOrRange(String str){

    }
}
