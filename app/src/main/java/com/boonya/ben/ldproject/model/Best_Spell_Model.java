package com.boonya.ben.ldproject.model;

import java.util.Comparator;

/**
 * Created by User on 12/4/2015.
 */
public class Best_Spell_Model {

    private int mId;
    private String mSenseGroup;
    private String mG2P;
    private String mDescription;
    private int Syllable;


    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmSenseGroup() {
        return mSenseGroup;
    }

    public void setmSenseGroup(String mSenseGroup) {
        this.mSenseGroup = mSenseGroup;
    }

    public String getmG2P() {
        return mG2P;
    }

    public void setmG2P(String mG2P) {
        this.mG2P = mG2P;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getSyllable() {
        return Syllable;
    }

    public void setSyllable(int syllable) {
        Syllable = syllable;
    }

    public static Comparator<Best_Spell_Model> comparator = new Comparator<Best_Spell_Model>() {

        public int compare(Best_Spell_Model s1, Best_Spell_Model s2) {

            int syllable1 = s1.getSyllable();
            int syllable2 = s2.getSyllable();

	   /*For ascending order*/
            return syllable1-syllable2;

	   /*For descending order*/
            //rollno2-rollno1;
        }};
}
