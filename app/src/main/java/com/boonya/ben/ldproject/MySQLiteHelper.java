package com.boonya.ben.ldproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.boonya.ben.ldproject.model.Best_Spell_Model;
import com.boonya.ben.ldproject.model.WordBreak;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by User on 12/3/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    private static final int mLimit = 30;
    private static final String DATABASE_NAME = "spellcheck.db";
    private static final String TAG = "MySQLiteHelper";
    public static String LEXITRON_EN_SENSE = "LEXITRON_EN_SENSE";
    public static int LEXITRON_EN_SENSE_COUNT = 83800;
    public static String LEXITRON_EN_SENSE_GROUP = "LEXITRON_EN_SENSEGROUP";
    public static int LEXITRON_EN_SENSE_GROUP_COUNT = 53560;
    public static String LEXITRON_EN_BEST_SPELL = "BEST_SPELL_EN";
    public static int LEXITRON_EN_SENSE_BEST_SPELL_COUNT = 35097;


    public static String LEXITRON_TH_SENSE = "LEXITRON_TH_SENSE";
    public static int LEXITRON_TH_SENSE_COUNT = 51472;
    public static String LEXITRON_TH_BEST_SPELL = "BEST_SPELL_TH";
    public static int LEXITRON_TH_SENSE_BEST_SPELL_COUNT = 38842;
    public static String LEXITRON_TH_SENSE_GROUP = "LEXITRON_EN_SENSEGROUP";
    public static int LEXITRON_TH_SENSE_GROUP_COUNT = 53560;


    public static boolean isEnglish = false;
    private static String mID = "ID";
    private static String mSENSEGROUP = "SENSEGROUP";
    private static String mG2P = "G2P";
    private static String mSSEARCH = "SSEARCH";
    private static String mSDEF = "SDEF";
    private static String mTENTRY = "TENTRY";
    private Context context;
    private boolean databaseLogs = true;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.i("mysql lite", "mysql constructor");
    }

    public static void closeCursor(Cursor c) {
        if (c != null && !c.isClosed()) {
            c.deactivate();
            c.close();
            c = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<String> getAllThaiWords() {
        ArrayList<String> thaiWords = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + mSENSEGROUP + " FROM " + LEXITRON_TH_BEST_SPELL, null);

        if (cursor.moveToFirst()) {
            do {
                thaiWords.add(cursor.getString(0));

            }
            while (cursor.moveToNext());
        }
        database.close();

        return thaiWords;

    }

    public int getTableCount(String tablename) {
//        LEXITRON_TH_SENSE

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor c = null;
        int count = 0;
        try {
            String selectQuery;
            database.beginTransaction();
//            selectQuery = "SELECT count(*) FROM " + TABLE_SPELLL;
            selectQuery = "SELECT count(*) FROM " + tablename;

            if (databaseLogs) {
                Log.e("SQL Query", selectQuery);
            }
            c = database.rawQuery(selectQuery, null);
            if (c == null)
                return count;
            else if (c.getCount() == 0) {
                c.close();
                return count;
            }

            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }

        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                dataBaseEnd(database);
            }
        }
        if (databaseLogs) {
            Log.v("getTableCount  ::: ", tablename + " : " + count + "");
        }
        return count;
    }

    private void dataBaseEnd(SQLiteDatabase database) {
        try {
            if (database != null) {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
        } catch (Exception e) {
        }
    }

    public ArrayList<Best_Spell_Model> getCallControllerContacts(String value) {

        ArrayList<Best_Spell_Model> models = new ArrayList<Best_Spell_Model>();
        SQLiteDatabase database = this.getReadableDatabase();


        String selectQuery = "Select " + mG2P + " from " + LEXITRON_EN_BEST_SPELL + " where " + mSENSEGROUP + " = '" + value + "'";
//        String selectQuery = "Select substr(" + mG2P + ",|,1) from " + LEXITRON_EN_BEST_SPELL + " where " + mSENSEGROUP + " = '" + value + "'";
//        String selectQuery = "Select * from " + LEXITRON_EN_BEST_SPELL + " where " + mSENSEGROUP + " = '" + value+"'";
//        String selectQuery = "SELECT * FROM " + LEXITRON_EN_BEST_SPELL
//                + " where " + mG2P + " like '%" + "(Select G2p from " + LEXITRON_EN_BEST_SPELL + " where " + mSENSEGROUP + " = " + value + ")%'";
        if (databaseLogs) {
            Log.e("SQL Query", selectQuery);
        }

        Cursor c = null;
        try {
            database.beginTransaction();
            c = database.rawQuery(selectQuery, null);
            if (c == null)
                return models;
            else if (c.getCount() == 0) {
                c.close();
                return models;
            }
            String g2pValue = null;
            if (c.getCount() > 0) {
                c.moveToFirst();
                Log.i(TAG, " mG2P value " + c.getString(0));

                g2pValue = c.getString(0);
            }
            if (g2pValue != null) {
                if (g2pValue.contains("|")) {
                    String[] values = g2pValue.split("\\|");
                    if (values.length > 0) {
                        Log.i(TAG, " length of values is : " + values.length);
//                        for (int i = 0; i < values.length; i++) {
//                            Log.i(TAG, " i :: " + i + " " + values[i]);
//                        }
                        g2pValue = values[0];
                    } else {
                        Log.i(TAG, " length after split not greater than 0 ");
                    }
                } else {
                    Log.i(TAG, " mG2P value not contains |::: ");
                }
            }
            Log.i(TAG, " mG2P value ::: " + g2pValue);
            models.clear();
            if (g2pValue != null && g2pValue.trim().length() > 0) {

                selectQuery = "SELECT * FROM " + LEXITRON_EN_BEST_SPELL
                        + " where " + mG2P + " like '%" + g2pValue + "%'";
                Log.i(TAG, selectQuery);
                c = database.rawQuery(selectQuery, null);
                models.addAll(getBSpellModels(c));
            }

            if (models.size() > 0) {
                String dsc = null;
                for (int i = 0; i < models.size(); i++) {
                    selectQuery = "SELECT " + mSDEF + " FROM " + LEXITRON_EN_SENSE
                            + " where " + mSSEARCH + " = '" + models.get(i).getmSenseGroup() + "'";
                    Log.i(TAG, selectQuery);
                    c = database.rawQuery(selectQuery, null);

                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        Log.i(TAG, " mG2P value " + c.getString(0));
                        dsc = c.getString(0);
                    }
//                    if (dsc == null) {
//                        dsc = i + "";
//                    }
                    if (dsc != null && dsc.trim().length() != 0) {
                        models.get(i).setmDescription(dsc);
                    }
                }

            }


            if (models.size() > 0) {
                for (int i = 0; i < models.size(); i++) {
                    Log.i(TAG, models.get(i).getmSenseGroup() + " " + models.get(i).getmDescription());
                }
            }


//            models.addAll(getBSpellModels(c));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
            dataBaseEnd(database);
        }
        Log.i(TAG, "models size :: " + models.size());
        return models;
    }

    private ArrayList<Best_Spell_Model> getBSpellModels(Cursor c) {
        ArrayList<Best_Spell_Model> models = new ArrayList<Best_Spell_Model>();
        try {
            if (c.moveToFirst()) {
                do {
                    Best_Spell_Model model = new Best_Spell_Model();
                    model.setmId(c.getInt(c.getColumnIndex(mID)));
                    model.setmG2P(c.getString(c.getColumnIndex(mG2P)));
                    model.setmSenseGroup(c.getString(c.getColumnIndex(mSENSEGROUP)));
                    Log.i(TAG, "name :: " + c.getString(c.getColumnIndex(mSENSEGROUP)));
                    Log.i(TAG, "g2p :: " + c.getString(c.getColumnIndex(mG2P)));
                    int syllable = model.getmG2P().split("\\|").length;
                    Log.i("Syllable", Integer.toString(syllable));
                    model.setSyllable(syllable);


                    models.add(model);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            c.close();
        }
        return models;
    }


    public ArrayList<Best_Spell_Model> getWords(WordBreak value) {

        ArrayList<Best_Spell_Model> models = new ArrayList<Best_Spell_Model>();
        SQLiteDatabase database = this.getReadableDatabase();
        int wordLength = value.getCharCount();
        String inputWord = value.getInputValue();
        ArrayList<String> g2pList = new ArrayList<>();
        String selectQuery = "";


        Log.i("WordLength", wordLength + "");

        if (wordLength < 4) {
            String testQuery = "Select " + mG2P + " from " + LEXITRON_TH_BEST_SPELL + " where " + mSENSEGROUP + " IN " + "('" + inputWord + "')";
            Cursor s = database.rawQuery(testQuery, null);

            if (s.getCount() != 0) {
                selectQuery = testQuery;
            } else {
                String possibleBegin = MappingTH(inputWord.charAt(0));
                for (int i = 0; i < possibleBegin.length(); i++) {
                    char begin = possibleBegin.charAt(i);
                    String newWord = begin + inputWord.substring(1);
                    Log.i("NewWord", newWord);
                    String query = "Select " + mG2P + " from " + LEXITRON_TH_BEST_SPELL + " where " + mSENSEGROUP + " IN " + "('" + newWord + "')";
                    Log.i("Query NewWord", query);
                    s = database.rawQuery(query, null);
                    if (s.getCount() != 0) {
                        selectQuery = query;
                    }
                }
                s.close();
            }

        } else {
            selectQuery = "Select " + mG2P + " from " + LEXITRON_TH_BEST_SPELL + " where " + mSENSEGROUP + " IN " + value.getWhereIn();
        }
        // String selectQuery = "Select " + mG2P + " from " + LEXITRON_TH_BEST_SPELL + " where " + mSENSEGROUP + " = '" + value.getSearchingWord() + "'";
        // String selectQuery = "Select substr(" + mG2P + ",|,1) from " + LEXITRON_EN_BEST_SPELL + " where " + mSENSEGROUP + " = '" + value.getSearchingWord() + "'";
//        String selectQuery = "Select * from " + LEXITRON_EN_BEST_SPELL + " where " + mSENSEGROUP + " = '" + value+"'";
//        String selectQuery = "SELECT * FROM " + LEXITRON_EN_BEST_SPELL
//                + " where " + mG2P + " like '%" + "(Select G2p from " + LEXITRON_EN_BEST_SPELL + " where " + mSENSEGROUP + " = " + value + ")%'";
        if (databaseLogs) {
            Log.e("SQL Query", selectQuery);
        }

        Cursor c = null;
        try {
            database.beginTransaction();
            c = database.rawQuery(selectQuery, null);
            if (c == null)
                return models;
            else if (c.getCount() == 0) {
                c.close();
                return models;
            }
            String g2pValue = "";
            //String g2pValueN = null;


            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        Log.i(TAG, " mG2P value " + c.getString(0));
                        g2pList.add(c.getString(0));
                    }
                    while (c.moveToNext());
                }

                for (int i = 0; i < g2pList.size(); i++) {

                    if (i == g2pList.size() - 1) {
                        g2pValue += mG2P + " like '%" + g2pList.get(i) + "%'";
                    } else {
                        g2pValue += mG2P + " like '%" + g2pList.get(i) + "%'" + " AND ";

                    }
                }
            }
            if (g2pValue != null) {
                if (g2pValue.contains("|")) {
                    String[] values = g2pValue.split("\\|");
                    if (values.length > 0) {
                        Log.i(TAG, " length of values is : " + values.length);
                        for (int i = 0; i < values.length; i++) {
                            Log.i(TAG, " i :: " + i + " " + values[i]);
                        }
//                        if (values.length >= 2) {
//                           // g2pValue = values[1];
//                            //g2pValueN = values[0];
//                        } else {
//                            //g2pValue = values[0];
//                        }
                    } else {
                        Log.i(TAG, " length after split not greater than 0 ");
                    }
                } else {
                    Log.i(TAG, " mG2P value not contains |::: ");
                }
            }
            Log.i(TAG, " mG2P value ::: " + g2pValue);
            models.clear();
            if (g2pValue != null && g2pValue.trim().length() > 0) {

//                selectQuery = "SELECT * FROM " + LEXITRON_TH_BEST_SPELL
//                        + " where " + mG2P + " like '%" + g2pValue + "%' LIMIT 50";

                selectQuery = "SELECT * FROM " + LEXITRON_TH_BEST_SPELL + " where " + g2pValue + " LIMIT " + mLimit;

                Log.i(TAG, selectQuery);
                c = database.rawQuery(selectQuery, null);
                models.addAll(getBSpellModels(c));
                c.close();
            }


            Log.i(TAG, "models size  " + models.size());
            if (models.size() > 0) {
                Log.i(TAG, "models size greater than 0 " + models.size());
                String dsc = null;
                for (int i = 0; i < models.size(); i++) {
//                    if (models.size() <= mLimit) {
                    selectQuery = "SELECT " + mSDEF + " FROM " + LEXITRON_TH_SENSE
                            + " where " + mSSEARCH + " = '" + models.get(i).getmSenseGroup() + "'";
                    Log.i(TAG, selectQuery);
                    c = database.rawQuery(selectQuery, null);

                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        Log.i(TAG, " mG2P value " + c.getString(0));
                        dsc = c.getString(0);
                    }
//                    if (dsc == null) {
//                        dsc = i + "";
//                    }
                    if (dsc != null && dsc.trim().length() != 0) {
                        models.get(i).setmDescription(dsc);
                    }

//                        if (models.size() > mLimit) {
//                            break;
//                        }
//                    } else {
//                        break;
//                    }
                    c.close();
                }

            }


            if (models.size() > 0) {
                for (int i = 0; i < models.size(); i++) {
                    Log.i(TAG, models.get(i).getmSenseGroup() + " " + models.get(i).getmDescription());
                }
            }


//            models.addAll(getBSpellModels(c));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
            dataBaseEnd(database);
        }
        Log.i(TAG, "models size :: " + models.size());

        Collections.sort(models, Best_Spell_Model.comparator);

        return models;
    }

    private String MappingTH(char key) {
        switch (key) {
            case 'ก':
                return "กภถฤดบมย";
            case 'ข':
                return "ขชซบมพ";
            case 'ค':
                return "คตดศกถ";
            case 'ฆ':
                return "ฆซฑทม";
            case 'ง':
                return "งเนมบดก";
            case 'จ':
                return "จดทก";
            case 'ฉ':
                return "ฉนณฌ";
            case 'ช':
                return "ชซธยสล";
            case 'ซ':
                return "ซชข";
            case 'ฌ':
                return "ฌญณถภ";
            case 'ญ':
                return "ญถณฌภ";
            case 'ฎ':
                return "ฎฏฐภถ";
            case 'ฏ':
                return "ฏฎฐภถ";
            case 'ฐ':
                return "ฐรฏฎธ";
            case 'ฑ':
                return "ฑทหซ";
            case 'ฒ':
                return "ฒตดมน";
            case 'ณ':
                return "ณญฌถนม";
            case 'ด':
                return "ดคตกนบม";
            case 'ต':
                return "ตฒดค";
            case 'ถ':
                return "ถฤญกภศ";
            case 'ท':
                return "ทฑหพถลสศซชกด";
            case 'ธ':
                return "ธรฐทพ";
            case 'น':
                return "นณฉมยบตกร";
            case 'บ':
                return "บปษมนกดต";
            case 'ป':
                return "ปบฝผมพ";
            case 'ผ':
                return "ผยพฝฟปบ";
            case 'ฝ':
                return "ฝฟผพยปบ";
            case 'พ':
                return "พผยฝบมนดตก";
            case 'ฟ':
                return "ฟพฬฝผ";
            case 'ภ':
                return "ภกถฎฏฤคพผฟ";
            case 'ม':
                return "มนณฉฒบป";
            case 'ย':
                return "ยผฝบปพฟมน";
            case 'ร':
                return "รธฐโวล";
            case 'ฤ':
                return "ฤถกภณฌญฎฏ";
            case 'ล':
                return "ลสศคดตรจนบ";
            case 'ว':
                return "วาำอชง";
            case 'ศ':
                return "ศคสดนบษ";
            case 'ษ':
                return "ัษฯ";
            case 'ส':
                return "สศลซช";
            case 'ห':
                return "หทฑ";
            case 'ฬ':
                return "ฬฟพ";
            case 'อ':
                return "อฮกลว";
            case 'ฮ':
                return "ฮอฬ";
            case 'ฯ':
                return "ฯษ";
            case 'ะ':
                return "ะั";
            case 'ั':
                return "ั้ะ";
            case 'า':
                return "าำว";
            case 'ำ':
                return "ำาว";
            case 'ิ':
                return "ิึืี";
            case 'ี':
                return "ิึืี";
            case 'ึ':
                return "ิึืี";
            case 'ื':
                return "ิึืี";
            case 'ุ':
                return "ุู";
            case 'ู':
                return "ุู";
            case 'เ':
                return "เแ";
            case 'แ':
                return "แเ";
            case 'โ':
                return "โใไเธร";
            case 'ใ':
                return "ใไโเ";
            case 'ไ':
                return "ไใโเ";
            case 'ๆ':
                return "ๆ";
            case '็':
                return "็๊์้";
            case '่':
                return "้่๋ี";
            case '้':
                return "้่๊์็ั";
            case '๊':
                return "๊้็์";
            case '๋':
                return "๋่";
            case '์':
                return "์้็๊";
        }
        return "กขคฆงจฉชซฌญฎฏฐฑฒณดตถทธนบปผฝพฟภมยรฤลวศษสหฬอฮฯะัาำิีึืุูแเโใไๆ็่้๊๋์";
    }

}
