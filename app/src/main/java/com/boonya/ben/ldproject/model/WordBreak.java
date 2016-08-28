package com.boonya.ben.ldproject.model;

/**
 * Created by apple on 1/8/16.
 */
public class WordBreak {

    private String SearchingWord;
    private int length;
    private String whereIn;
    private String inputValue;
    private int charCount;

    public WordBreak(String SearchingWord, int length,String whereIn,int charCount,String inputValue){
        setLength(length);
        setSearchingWord(SearchingWord);
        this.whereIn = whereIn;
        setCharCount(charCount);
        setInputValue(inputValue);





    }

    public String getWhereIn(){
        return whereIn;
    }

    public String getSearchingWord() {
        return SearchingWord;
    }

    public void setSearchingWord(String searchingWord) {
        SearchingWord = searchingWord;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCharCount() {
        return charCount;
    }

    public void setCharCount(int charCount) {
        this.charCount = charCount;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }
}
