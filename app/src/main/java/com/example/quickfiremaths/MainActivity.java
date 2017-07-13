package com.example.quickfiremaths;

import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int width;
    private int height;
    private ViewGroup global;
    private RelativeLayout home;
    private boolean gameOver;
    private TextView questionSymbol;
    private TextView answerNumber;
    private TextView secondNumber;
    private TextView firstNumber;
    private int bias;
    private final static Random r = new Random();
    private LoadingBar loadingBar;
    private boolean isCorrect;
    private boolean waitingForMove;
    int scoreCount;
    private TextView score;
    private RelativeLayout gameOverScreen;
    private RelativeLayout gameScreen;
    private TextView equalSymbol;
    private ValueAnimator slideAnimator;
    private ArrayList<String> sayings;
    private View rules;
    private ImageView soundButton;
    private boolean soundOn;
    private MusicHelper musicPlayer;
    private Animation shake;
    private Animation slideInOut;
    private String[] colors;
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";
    private InterstitialAd mInterstitialAd;
    private int countOfGames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generateSaying();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        slideAnimator = new ValueAnimator();
        soundOn = true;
        rules = findViewById(R.id.rules);

        musicPlayer = new MusicHelper(this, this);

        firstNumber = (TextView) findViewById(R.id.first_number);
        secondNumber = (TextView) findViewById(R.id.second_number);
        answerNumber = (TextView) findViewById(R.id.answer);
        questionSymbol = (TextView) findViewById(R.id.symbol);
        equalSymbol = (TextView) findViewById(R.id.equal_symbol);
        clearBoard();

        (findViewById(R.id.title)).bringToFront();

        global = (ViewGroup) findViewById(R.id.global);
        home = (RelativeLayout) findViewById(R.id.home);
        loadingBar = new LoadingBar(width, this);


        firstNumber = (TextView) findViewById(R.id.first_number);
        secondNumber = (TextView) findViewById(R.id.second_number);
        answerNumber = (TextView) findViewById(R.id.answer);
        questionSymbol = (TextView) findViewById(R.id.symbol);
        equalSymbol = (TextView) findViewById(R.id.equal_symbol);


        score = (TextView) findViewById(R.id.score);

        soundButton = (ImageView) findViewById(R.id.sound);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundOn) {
                    soundButton.setImageResource(R.drawable.ic_volume_off_black_24dp);
                } else {
                    soundButton.setImageResource(R.drawable.ic_volume_up_black_24dp);
                }
                soundOn = !soundOn;
            }
        });
        gameOverScreen = (RelativeLayout) findViewById(R.id.gameOver);
        gameOverScreen.setTranslationY(-(height));
        gameScreen = (RelativeLayout) findViewById(R.id.game);
        setUpGame(null);
        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanim);
        slideInOut = AnimationUtils.loadAnimation(this, R.anim.slide_in_out);

        colors = new String[]{"blue", "green", "orange", "red", "purple"};

        // Load an ad into the AdMob banner view.
        setAdvert((AdView) findViewById(R.id.adView));
        setAdvert((AdView) findViewById(R.id.gameAdView));
        setAdvert((AdView) findViewById(R.id.rulesAdView));

        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
    }

    private void clearBoard() {
        firstNumber.setText("");
        secondNumber.setText("");
        answerNumber.setText("");
        questionSymbol.setText("");
        equalSymbol.setText("");
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                signalGameOverScreen();
            }

            @Override
            public void onAdClosed() {
                signalGameOverScreen();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            mInterstitialAd = newInterstitialAd();
            loadInterstitial();
        }
    }

    private void loadInterstitial() {
        // Disable the next level button and load the ad.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void setAdvert(AdView ad) {
        AdView adView = ad;
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
    }

    public void setUpGame(View view) {
        gameOver = false;
        waitingForMove = false;
        scoreCount = 0;
        score.setText("" + scoreCount);
        questionSymbol.setText("+");
        equalSymbol.setText("=");
        if (view != null) {
            final int resId = getResources().getIdentifier(colors[r.nextInt(colors.length)] + "_background", "drawable", getPackageName());
            global.setBackgroundResource(resId);
            clearBoard();
            gameOverScreen.setTranslationY(-(height));
            Animation slideInOutTransition = slideInOut;
            slideInOutTransition.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    gameScreen.setBackgroundResource(resId);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            gameScreen.startAnimation(slideInOutTransition);
            questionSymbol.setText("+");
            equalSymbol.setText("=");
            loadingBar.load(300);
            new MathLauncher().execute();
            if (soundOn) {
                musicPlayer.playButtonClick();
            }
        }
    }

    public void start(View view) {
        gameOverScreen.setTranslationY(-(height));
        setUpGame(null);
        global.removeView(rules);
        transitionPageOut(home);
        int resId = getResources().getIdentifier(colors[r.nextInt(colors.length)] + "_background", "drawable", getPackageName());
        gameScreen.setBackgroundResource(resId);
    }

    private void transitionPageOut(final RelativeLayout homeScreen) {
        Transition pageOut = new AutoTransition();
        pageOut.setDuration(500);
        pageOut.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                rules.setAlpha(0);
                clearBoard();
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                questionSymbol.setText("+");
                equalSymbol.setText("=");
                global.removeView(homeScreen);
                gameScreen.bringToFront();
                if (global.findViewById(R.id.rules) == null) {
                    rules.setAlpha(1);
                }
                loadingBar.load(0);
                new MathLauncher().execute();

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        TransitionManager.beginDelayedTransition(global, pageOut);
        RelativeLayout.LayoutParams spec = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        spec.bottomMargin = height;
        homeScreen.setLayoutParams(spec);
    }

    private void transitionPageToSide(final RelativeLayout homeScreen, int target) {
        Transition transitionPageFromSide = new AutoTransition();
        transitionPageFromSide.setDuration(500);
        TransitionManager.beginDelayedTransition(global, transitionPageFromSide);
        RelativeLayout.LayoutParams spec = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        spec.leftMargin = target;
        homeScreen.setLayoutParams(spec);

    }

    private void transitionPageIn(RelativeLayout homeScreen) {
        global.addView(homeScreen);
        Transition transitionPageIn = new AutoTransition();
        transitionPageIn.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {}

            @Override
            public void onTransitionEnd(Transition transition) {
                if (global.findViewById(R.id.rules) == null) {
                    global.addView(rules);
                }
                home.bringToFront();
            }

            @Override
            public void onTransitionCancel(Transition transition) {}

            @Override
            public void onTransitionPause(Transition transition) {}

            @Override
            public void onTransitionResume(Transition transition) {}
        });
        transitionPageIn.setDuration(500);
        TransitionManager.beginDelayedTransition(global, transitionPageIn);
        RelativeLayout.LayoutParams spec = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        spec.bottomMargin = 0;
        homeScreen.setLayoutParams(spec);
    }

    public void transitionBackToMenu(View view) {
        if (soundOn) {
            musicPlayer.playButtonClick();
        }
        transitionPageIn(home);
    }

    public void signalGameOver() {
        gameOver = true;
    }

    private class MathLauncher extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int currentCycleCount = 0;
            while (!gameOver) {
                while (!waitingForMove) {
                    if (currentCycleCount == 2) {
                        currentCycleCount = 0;
                        bias++;
                    }
                    int[] values = calculateValues(1);
                    publishProgress(values[0], values[1], values[2]);
                    waitingForMove = true;
                    currentCycleCount++;
                }
            }
            publishProgress(0);
            return null;

        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values[0] == 0) {
                countOfGames++;
                if (countOfGames % 5 == 0) {
                    showInterstitial();
                } else {
                    signalGameOverScreen();
                }
            } else {
                if (!gameOver) {
                    loadingBar.playTimer();
                }
                firstNumber.setText("" + values[0]);
                secondNumber.setText("" + values[1]);
                answerNumber.setText("" + values[2]);
            }
        }
    }

    private void signalGameOverScreen() {
        loadingBar.stopTimer();
        updateHighScore();
        slideAnimator.removeAllListeners();
        slideAnimator.setFloatValues(0, height / 2);
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                gameOverScreen.setTranslationY((Float) animation.getAnimatedValue() - (height / 2));
            }
        });
        slideAnimator.start();
        gameOverScreen.setTranslationY(0f);
    }

    private void updateHighScore() {
        clearBoard();
        ((TextView) gameOverScreen.findViewById(R.id.saying)).setText(sayings.get(r.nextInt(sayings.size())));
        ((TextView) gameOverScreen.findViewById(R.id.finalScore)).setText("" + scoreCount);
        int highScore = HighScoreHelper.getTopScore(getApplicationContext());
        if (highScore < scoreCount) {
            HighScoreHelper.setTopScore(getApplicationContext(), scoreCount);
            ((TextView) gameOverScreen.findViewById(R.id.bestScore)).setText("" + scoreCount);
        } else {
            ((TextView) gameOverScreen.findViewById(R.id.bestScore)).setText("" + highScore);
        }
    }

    public void answerCorrect(View v) {
        if (!gameOver) {
            if (!isCorrect) {
                gameScreen.startAnimation(shake);
                if (soundOn) {
                    musicPlayer.playIncorrectSound();
                }
                gameOver = true;
            } else {
                scoreCount++;
                if (soundOn) {
                    musicPlayer.playSwoosh();
                }
            }
            waitingForMove = false;
            score.setText("" + scoreCount);
        }
    }

    public void answerWrong(View v) {
        if (!gameOver) {
            if (isCorrect) {
                gameScreen.startAnimation(shake);
                gameOver = true;
                if (soundOn) {
                    musicPlayer.playIncorrectSound();
                }
            } else {
                scoreCount++;
                if (soundOn) {
                    musicPlayer.playSwoosh();
                }
            }
            waitingForMove = false;
            score.setText("" + scoreCount);
        }
    }


    public int[] calculateValues(int difficulty) {
        int valueOne = r.nextInt(5) + 1 + difficulty;
        int valuetwo = r.nextInt(5) + 1 + difficulty;
        int valueThree = valueOne + valuetwo;
        isCorrect = r.nextBoolean();
        if (!isCorrect) {
            int marginOfError = (int) Math.floor(valueThree / 5);
            if (marginOfError == 0) {
                marginOfError = 1;
            }
            valueThree = (r.nextBoolean() ? (valueThree - (r.nextInt(marginOfError) + 1)) : (valueThree + r.nextInt(marginOfError) + 1));
        }
        return new int[]{valueOne, valuetwo, valueThree};
    }


    private void generateSaying() {
        sayings = new ArrayList<String>();
        sayings.add("Mistakes are proof you are trying or are you! ");
        sayings.add("Problems are not stop signs, they are guidelines; so try again!");
        sayings.add("Failure is not falling down, its refusing to get back up!");
        sayings.add("The only way to learn mathematics, is to do mathematics!");

    }

    public void backToMenuFromHelp(View view) {
        transitionPageToSide(home, 0);
    }

    public void goToHelp(View view) {
        transitionPageToSide(home, width);
    }
}
