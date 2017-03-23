package com.boonya.ben.ldproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boonya.ben.ldproject.adapter.ListViewAdapter;
import com.boonya.ben.ldproject.breakword.LongLexTo;
import com.boonya.ben.ldproject.model.Best_Spell_Model;
import com.boonya.ben.ldproject.model.WordBreak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SpeakClick {
    EditText searchText;
    final static String VAJA_TTS_ENGINE = "com.vajatts.nok";
    public static TextToSpeech tts;
    private static final String TAG = MainActivity.class.getSimpleName();
    MenuItem itemBlog;
    LongLexTo LexTo;
    private ListView wordList;
    private TextView no_search;
    private InputMethodManager imeManager;
    private LinearLayout textContainer;
    public static boolean initFlag = true;
    private View currentListItem,previousListItem;
    private static int currentPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= 14) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            LexTo = new LongLexTo(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textContainer = (LinearLayout)findViewById(R.id.textContainer);
        wordList = (ListView)findViewById(R.id.listView);
        no_search = (TextView)findViewById(R.id.nonsearch_text);

        searchText = (EditText)findViewById(R.id.edit_search);
        searchText.setVisibility(View.GONE);
        imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);


        checkTTSEngineInstalled(VAJA_TTS_ENGINE, "");

        wordList.addFooterView(new View(getApplicationContext()), null, true);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (searchText != null && searchText.getText() != null && searchText.getText().toString().trim().length() > 1) {
                    ArrayList<Best_Spell_Model> spellModels = null;
                    if (MySQLiteHelper.isEnglish) {
                        spellModels = ApplicationClass.database.getCallControllerContacts(searchText.getText().toString().trim());
                    } else {
                        Log.i(TAG, "**** test " + wordbreaker(searchText.getText().toString().trim()));
                        spellModels = ApplicationClass.database.getWords(wordbreaker(searchText.getText().toString().trim()));
                    }
                    if (spellModels != null && spellModels.size() > 0) {
                        showList(spellModels);
                    }

                } else if (searchText.getText().toString().trim().length() == 0) {
                    ArrayList<Best_Spell_Model> spellModels = new ArrayList<Best_Spell_Model>();
                    showList(spellModels);
                }

               initFlag = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        importDatabase();

    }

    private void showList(final ArrayList<Best_Spell_Model> spellModels) {

        ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, spellModels, this);
        wordList.setAdapter(adapter);
        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setCurrentPos(position);
                if(currentListItem!=null && previousListItem != null ){
                    currentListItem = view;
                    ImageView previousImg = (ImageView) previousListItem.findViewById(R.id.listen);
                    previousImg.setImageResource(R.drawable.book_close_icon);

                    ImageView currentImg = (ImageView)currentListItem.findViewById(R.id.listen);
                    currentImg.setImageResource(R.drawable.book_icon);
                    previousListItem = currentListItem;

                }

                else {
                    currentListItem = view;
                    ImageView nextImg = (ImageView) currentListItem.findViewById(R.id.listen);
                    nextImg.setImageResource(R.drawable.book_icon);
                    previousListItem =currentListItem;

                }

                Log.i(TAG, "position **** **** **** **** " + position);
                Log.i(TAG, "spellModels.get(position).getmSenseGroup() ::: " + spellModels.get(position).getmSenseGroup());
                view.setSelected(true);
                if (spellModels.get(position).getmDescription() == null || spellModels.get(position).getmDescription().trim().length() == 0) {
                    Log.i(TAG, "model.getmDescription()  :: is null or length is zero");
                } else {
                    Log.i(TAG, "model.getmDescription()  :: is " + spellModels.get(position).getmDescription());
                }

                tts.speak(spellModels.get(position).getmSenseGroup(), TextToSpeech.QUEUE_FLUSH, null);

                String copyText = spellModels.get(position).getmSenseGroup();
                Log.i(TAG, "on item long click position **** **** **** **** " + position);
                view.setSelected(true);
                android.content.ClipboardManager clipBoard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Clip", copyText);
                clipBoard.setPrimaryClip(clip);


                Snackbar snackbar = Snackbar.make(view, copyText + " ได้รับการคัดลอกแล้ว", Snackbar.LENGTH_LONG).setAction("Action", null);
                View view_snack = snackbar.getView();
                view_snack.setBackgroundColor(Color.parseColor("#FF4081"));
                snackbar.show();
            }
        });

    wordList.setOnScrollListener(new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            initFlag = false;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(tts == null) {
            checkTTSEngineInstalled(VAJA_TTS_ENGINE, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null)
            tts.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       itemBlog = menu.add(Menu.NONE, // Group ID
                R.id.action_search, // Item ID
                101, // Order
                "Search"); // Title
        MenuItemCompat.setShowAsAction(itemBlog, MenuItem.SHOW_AS_ACTION_ALWAYS);
        itemBlog.setIcon(R.drawable.search_icon);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_about_us) {

            Intent itn = new Intent(MainActivity.this,AboutUsActivity.class);
            startActivity(itn);
            return true;
        }
        else if(id == R.id.action_search){
            searchText.setVisibility(View.VISIBLE);
            itemBlog.setVisible(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            textContainer.setVisibility(View.INVISIBLE);
            no_search.setVisibility(View.INVISIBLE);
            wordList.setVisibility(View.VISIBLE);

            return true;
        }

        else if (id == android.R.id.home){

            itemBlog.setVisible(true);
            searchText.setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            textContainer.setVisibility(View.VISIBLE);
            no_search.setVisibility(View.VISIBLE);
            wordList.setVisibility(View.INVISIBLE);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

            return true;
        }

        else if(id == R.id.set_up_keyboard){
            imeManager.showInputMethodPicker();
        }

        else if(id == R.id.turn_on_keyboard){
            Intent turn_on = new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
            startActivityForResult(turn_on, 99);

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return app_installed;
    }

    public void checkTTSEngineInstalled(String packageName, String text) {
        boolean isSvoxInstalled = isAppInstalled(packageName);
        if (isSvoxInstalled) {
            if (tts == null) {
                tts = new TextToSpeech(MainActivity.this, null, packageName);

            }
            if (tts == null) {
                Log.i(TAG, "tts is null *** ***");
            } else {
                Log.i(TAG, "tts is not null *** ***");
            }

            if (text != null && text.trim().length() > 0) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        } else if (!isSvoxInstalled) {
            Toast.makeText(getApplicationContext()
                    , "VAJA Text-to-Speech Engine", Toast.LENGTH_LONG).show();
            Intent svoxIntent = new Intent(Intent.ACTION_VIEW);
            svoxIntent.setData(Uri.parse("market://details?id=" + packageName));
            startActivity(svoxIntent);
        }
    }


    private void importDatabase() {

                int value = 0;
                if (MySQLiteHelper.isEnglish) {
                    value = ApplicationClass.database.getTableCount(MySQLiteHelper.LEXITRON_EN_SENSE_GROUP);

                    if (value < MySQLiteHelper.LEXITRON_EN_SENSE_GROUP_COUNT) {
                            new LoadData().execute(R.raw.sense_group_en);
                    }
                    value = ApplicationClass.database.getTableCount(MySQLiteHelper.LEXITRON_EN_SENSE);
                    if (value < MySQLiteHelper.LEXITRON_EN_SENSE_COUNT) {
                            new LoadData().execute(R.raw.sense_en);
                    }
                    value = ApplicationClass.database.getTableCount(MySQLiteHelper.LEXITRON_EN_BEST_SPELL);
                    if (value < MySQLiteHelper.LEXITRON_EN_SENSE_BEST_SPELL_COUNT) {
                        new LoadData().execute(R.raw.best_spell_en);
                    }
                } else {

                    value = ApplicationClass.database.getTableCount(MySQLiteHelper.LEXITRON_TH_SENSE);
                    Log.i(TAG, value + " thai sense");
                    if (value < MySQLiteHelper.LEXITRON_TH_SENSE_COUNT) {
                        new LoadData().execute(R.raw.sense_th);
                    }
                    value = ApplicationClass.database.getTableCount(MySQLiteHelper.LEXITRON_TH_BEST_SPELL);
                    Log.i(TAG, value + " thai best spell");
                    if (value < MySQLiteHelper.LEXITRON_TH_SENSE_BEST_SPELL_COUNT) {
                        new LoadData().execute(R.raw.best_spell_th);
                    }
                }
            }

    public int insertFromFile(Context context, int resourceId) throws IOException {
        // Reseting Counter
        int result = 0;
        SQLiteDatabase database = ApplicationClass.database.getWritableDatabase();
        // Open the resource
        InputStream insertsStream = context.getResources().openRawResource(resourceId);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));
        int i = 0;
        // Iterate through lines (assuming each insert has its own line and theres no other stuff)
        String insertStmt = null;
        while (insertReader.ready()) {
            i++;
            if (insertStmt == null) {
                insertStmt = insertReader.readLine();
            } else {
                insertStmt = insertStmt + " " + insertReader.readLine();
            }
            if (i >= 15 && i < 25) {
                Log.i("line i :; " + i, insertStmt);
            }
            if (insertStmt.contains(";")) {
                try {
                    database.execSQL(insertStmt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                insertStmt = null;
            }
            result++;
        }

        insertReader.close();

        Log.i("result count ", "total count " + result);
        // returning number of inserted rows
        return result;
    }

    public WordBreak wordbreaker(String input) {

        WordBreak returnWord = null;
        int length=0;
        int inputLength = input.trim().length();

        String whereIn = "";
        if (input == null || inputLength == 0) {
            return returnWord;
        }
        String returnValue = input;
        Log.i(TAG, "****** wordbreaker ******");

        Locale thaiLocale = new Locale("th");

        BreakIterator boundary = BreakIterator.getWordInstance(thaiLocale);

        boundary.setText(input);
        Log.i(TAG, "Input ::: " + input);
        String list = null;
        try {
            list = printEachForward(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list.contains("-")) {
            String[] listValues = list.split("-");
            length = listValues.length;

            Log.i("Lenght of word: "," "+length);

            whereIn = whereIn+"(";

            for (int i=0;i<length;i++){
                if (i == length-1){
                    whereIn += "'"+listValues[i]+"'";
                }
                else{
                    whereIn += "'"+listValues[i]+"',";
                }
            }

            whereIn += ")";
            Log.i("SLQ WHERE IN",whereIn);

            if (listValues != null && length > 0) {
                if (listValues.length == 1 ) {
                    returnValue = list.replace("-", "");
                }
                else if(listValues.length==2){
                    returnValue = listValues[1];
                }
                else if (listValues.length > 2) {
                    returnValue = listValues[1];
                }
            } else {
                returnValue = list.replace("-", "");
            }
        }
        Log.i("breaking_result", returnValue);
        returnWord = new WordBreak(returnValue,length,whereIn,inputLength,input);
        return returnWord;
    }

    public String printEachForward(String source) throws IOException{

        int begin, end;
        String result = "";
        LexTo.wordInstance(source);
        begin = LexTo.first();

        while (LexTo.hasNext()) {
            end = LexTo.next();
            System.out.println("Begin: "+begin);
            System.out.println("End: "+end);
            result += source.substring(begin, end) + "-";

            begin = end;
        }
        Log.i("result of string",result);
        return result;
    }

    @Override
    public void speak(String value) {
        checkTTSEngineInstalled(null, value);
    }

    @Override
    public void copy(View view, String data) {
        registerForContextMenu(view);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("requestCode resultCode ", requestCode + " " + resultCode);
        if(requestCode == 99){
            Intent turn_on = new Intent(this,MainActivity.class);
            turn_on.putExtra("position",1);
            startActivity(turn_on);
            finish();
        }
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    private class LoadData extends AsyncTask<Integer, Integer, Void> {
        ProgressDialog pd;

        @Override
        protected Void doInBackground(Integer... params) {

            try {
                int insertCount = insertFromFile(MainActivity.this, params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            pd.setTitle("โปรดรอซักครู่");
            pd.setMessage("กำลังดาวโหลดฐานข้อมูล");
            pd.setCancelable(false);
            pd.show();

        }

        protected void onPostExecute(Void result) {
            pd.dismiss();
        }
    }
}
