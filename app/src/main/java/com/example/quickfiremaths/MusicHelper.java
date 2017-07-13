package com.example.quickfiremaths;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by Amans on 21/05/2017.
 * Class for music and any sound effects
 */

public class MusicHelper {
    private SoundPool mSoundPool;
    private int mCorrectId;
    private int mWrongId;
    private int mButtonClick;
    private boolean mLoaded;
    private float mVolume;


    public MusicHelper(Activity activity, Context c){

        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolume = actVolume / maxVolume;

        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(6).build();
        } else {
            //noinspection deprecation
            mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mLoaded = true;
            }
        });
        mCorrectId = mSoundPool.load(activity, R.raw.right, 1);
        mWrongId = mSoundPool.load(activity, R.raw.wrong, 1);
        mButtonClick = mSoundPool.load(activity, R.raw.click, 1);
    }

    public void playSwoosh() {
        if (mLoaded) {
            mSoundPool.play(mCorrectId, 1f, 1f, 1, 0, 1f);
        }
    }

    public void playIncorrectSound(){
        if (mLoaded) {
            mSoundPool.play(mWrongId, 0.5f, 1f, 1, 0, 1f);
        }
    }

    public void playButtonClick(){
        if (mLoaded) {
            mSoundPool.play(mButtonClick, 1f, 1f, 1, 0, 1f);
        }
    }





}
