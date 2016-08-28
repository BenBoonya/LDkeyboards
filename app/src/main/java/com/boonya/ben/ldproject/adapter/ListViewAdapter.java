package com.boonya.ben.ldproject.adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boonya.ben.ldproject.MainActivity;
import com.boonya.ben.ldproject.R;
import com.boonya.ben.ldproject.SpeakClick;
import com.boonya.ben.ldproject.model.Best_Spell_Model;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private static final String TAG = ListViewAdapter.class.getSimpleName() ;
    private Context context;
    private ArrayList<Best_Spell_Model> contactModels;
    private boolean[] checkedStates;
    private SpeakClick speak = null;
    private int viewPosition;

    public ListViewAdapter(Context context,
                           ArrayList<Best_Spell_Model> contactModels, SpeakClick speak) {
        this.context = context;
        this.contactModels = contactModels;
        checkedStates = new boolean[contactModels.size()];
        this.speak = speak;
    }

    public void addModel(Best_Spell_Model model) {
        this.contactModels.add(model);
        this.checkedStates = new boolean[contactModels.size()];
        notifyDataSetChanged();
    }

    public void resetCheckedStates() {
        checkedStates = new boolean[contactModels.size()];
    }

    public void removeModel(Best_Spell_Model model) {
        contactModels.remove(model);
    }

    public int getCount() {
        return contactModels.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final View view = convertView;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
            holder = new ViewHolder();
            viewPosition = new MainActivity().getCurrentPos();

            holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
           /* holder.txtSize = (TextView) convertView.findViewById(R.id.txtSize);*/
            holder.thumbImage = (ImageView) convertView.findViewById(R.id.listen);
            holder.thumbImage.setImageResource(R.drawable.book_close_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        final Best_Spell_Model model = contactModels.get(position);

       Log.i("ViewPosition", ""+viewPosition);

        if(position != viewPosition){
            holder.thumbImage.setImageResource(R.drawable.book_close_icon);
        }


//        else if(position == viewPosition ){
//            holder.thumbImage.setImageResource(R.drawable.book_icon);
//        }

        holder.txtTitle.setText(model.getmSenseGroup());
//        holder.txtSize.setText(model.getmG2P());
        if ((model.getmDescription() != null) && (model.getmDescription().trim().length() >1) ) {
            //Log.i(TAG, model.getmSenseGroup()+"  :: is null or length is zero" + model.getmDescription() );
            holder.thumbImage.setVisibility(View.VISIBLE);
        }

        if ((model.getmDescription() == null || model.getmDescription().trim().length() == 0) ) {
            Log.i(TAG, model.getmSenseGroup()+"  :: is null or length is zero" + model.getmDescription() );
            holder.thumbImage.setVisibility(View.INVISIBLE);
       }
        else if ((model.getmDescription() != null) && (model.getmDescription().trim().length() == 1) ) {
            Log.i(TAG, "model.getmDescription() is ::  "+model.getmDescription());
            if (model.getmDescription().equalsIgnoreCase("-")) {
                holder.thumbImage.setVisibility(View.INVISIBLE);
            }
        }
        holder.txtTitle.setTag(model.getmSenseGroup());




        holder.txtTitle.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                if (model.getmSenseGroup() != null) {
                    if (MainActivity.tts != null) {
                        Log.i("tts status :: ", "tts is not null *** ***");
                        MainActivity.tts.speak(model.getmSenseGroup(), TextToSpeech.QUEUE_FLUSH, null);
                       // Toast.makeText(context, model.getmSenseGroup(), Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("tts status :: ", "tts is null *** ***");
                        speak.speak(model.getmDescription());
                        view.setSelected(true);
                    }

                } else {
                    MainActivity.tts.speak("No Description is available." + model.getmSenseGroup(), TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });


        holder.thumbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (model.getmDescription() != null) {
//                    MainActivity.mTts.speak(model.getmDescription(), TextToSpeech.QUEUE_FLUSH, null);
//                } else {
//                    MainActivity.mTts.speak("No Description is available." + model.getmSenseGroup(), TextToSpeech.QUEUE_FLUSH, null);
//                }


                if (model.getmDescription() != null) {
//                    checkTTSEngineInstalled(SVOX_TTS_ENGINE);
//                    if (tts != null) {
//                        Log.i("tts status :: ", "tts is not null *** ***");
//                        ApplicationClass.tts.speak(model.getmDescription(), TextToSpeech.QUEUE_FLUSH, null);
//                        Toast.makeText(context, model.getmDescription(), Toast.LENGTH_LONG).show();
//                    } else {
                    Log.i("tts status :: ", "tts is null *** ***");
                   // speak.speak(model.getmDescription());
                    MainActivity.tts.speak(model.getmDescription(), TextToSpeech.QUEUE_FLUSH,null);
//                    }

//                    MainActivity.mTts.speak(model.getmDescription(), TextToSpeech.QUEUE_FLUSH, null);
                } else {
//                    ApplicationClass.tts.speak("No Description is available." + model.getmSenseGroup(), TextToSpeech.QUEUE_FLUSH, null);
                    MainActivity.tts.speak("No description", TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });

        // String[] proj = { MediaStore.Video.Media._ID,
        // MediaStore.Video.Media.DISPLAY_NAME,
        // MediaStore.Video.Media.DATA };
        // Cursor cursor = context.getContentResolver().query(
        // MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj,
        // MediaStore.Video.Media.DISPLAY_NAME + "=?",
        // new String[] { model.getName() }, null);
        // cursor.moveToFirst();
        // long ids = cursor.getLong(cursor
        // .getColumnIndex(MediaStore.Video.Media._ID));


        // if (isScrollStop) {
        // ContentResolver crThumb = context.getContentResolver();
        // BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inSampleSize = 1;
        // Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb,
        // model.getVideoId(), MediaStore.Video.Thumbnails.MICRO_KIND,
        // options);
        // holder.thumbImage.setImageBitmap(curThumb);
        // curThumb = null;
        // }
        // holder.ll.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        // // TODO Auto-generated method stub
        // //playVideo(model.getVideo_url());
        // }
        //
        // });
        // holder.thumbImage.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        // // TODO Auto-generated method stub
        // //playVideo(model.getVideo_url());
        // }
        // });
//        convertView.setTag(model.getmSenseGroup());
//convertView.set
        return convertView;
    }

    public int getSelectedCount() {
        int countTrue = 0;
        for (boolean b : checkedStates) {
            // if true: increment counter;
            if (b)
                countTrue++;
        }
        return countTrue;
    }

//    public ArrayList<ContactsModel> getSelectedVideos() {
//        int count = getSelectedCount();
//        System.out.println("Count ******** " + count);
//        ArrayList<ContactsModel> selectedModels = new ArrayList<ContactsModel>();
//        int selectedItemsCount = 0;
//        for (ContactsModel d : contactModels) {
//            if (d.isSelected()) {
//                selectedItemsCount++;
//                selectedModels.add(d);
//                if (count == selectedItemsCount) {
//                    break;
//                }
//            }
//            // something here
//        }
//        return selectedModels;
//    }

	/*
     * private void playVideo(String videoUrl) { // TODO Auto-generated method
	 * stub Intent intent = new Intent(context, ViewVideo.class);
	 * intent.putExtra("videofilename", videoUrl);
	 * context.startActivity(intent); }
	 */

    static class ViewHolder {
        TextView txtTitle;
        //        TextView txtSize;
        ImageView thumbImage;
    }
}
