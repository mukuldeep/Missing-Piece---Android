package com.raabnits.missingpiece;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Random;

public class Typewriter extends androidx.appcompat.widget.AppCompatTextView {

    private CharSequence mText,srch_ele;
    private int mIndex,dIndex,repeater,maxReapeater,itr,is_blinking,max_blink,blink_delay;
    private long mDelay = 500; //Default 500ms delay
    private String alph="FUaKbOcW$8EdHeXf;7gBh'PiJ<j6YkQ>?%lmL_n5o,9G.p:R&qCr0ZstI|uMvAS1T(2!)wN}+-xVDy{3z uansd234 9 87)(@#jsdhbfj";

    public Typewriter(Context context) {
        super(context);
    }

    public Typewriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if(mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            }
        }
    };

    private Runnable randomCharacterAdder = new Runnable() {
        @Override
        public void run() {
            mText=mText+String.valueOf(random_char());
            setText(mText+"_");
                if (mIndex <= dIndex) {
                    repeater++;
                    if (repeater == maxReapeater) {
                        repeater = 0;
                        mIndex++;
                    } else {
                        mText = mText.subSequence(0, mText.length() - 1);
                    }
                    mHandler.postDelayed(randomCharacterAdder, mDelay);
                }
        }
    };

    private Runnable searchCharacterAdder = new Runnable() {
        @Override
        public void run() {
            mText=mText+String.valueOf(random_char());
            setText(mText+"_");

            if (mIndex < dIndex) {
                repeater++;
                mText = mText.subSequence(0, mText.length() - 1);
                if (repeater == maxReapeater) {
                    repeater = 0;
                    mIndex++;
                    mText=mText+String.valueOf(srch_ele.charAt(itr));
                    itr++;
                }
                mHandler.postDelayed(searchCharacterAdder, mDelay);
            }else{
                mText = mText.subSequence(0, mText.length() - 1);
                mText=mText+String.valueOf(srch_ele.charAt(itr));
                setText(mText+"_");
                mHandler.postDelayed(_blinker, blink_delay);
            }
        }
    };
    private Runnable _blinker = new Runnable() {
        @Override
        public void run() {
            if(is_blinking<max_blink){
                if(is_blinking%2==0){
                    setText(mText + " ");
                }else {
                    setText(mText + "_");
                }
                is_blinking++;
                mHandler.postDelayed(_blinker, blink_delay);
            }else{
                setText(mText);
            }
        }
    };

    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;

        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void animate_search_random(String msg,int char_delay,int max_repeat,int maxm_blink,int blink_delay_){
        srch_ele=msg;
        mDelay=char_delay;
        is_blinking=0;
        max_blink=maxm_blink;
        blink_delay=blink_delay_;
        itr=0;
        mText=" ";
        mIndex=0;
        dIndex=msg.length()-1;
        repeater=0;
        maxReapeater=max_repeat;
        setText("");
        mHandler.removeCallbacks(searchCharacterAdder);
        mHandler.postDelayed(searchCharacterAdder, mDelay);
    }

    public void animate_random(){
        mText=" ";
        mIndex=0;
        dIndex=25;
        repeater=0;
        maxReapeater=10;
        setText("");
        mHandler.removeCallbacks(randomCharacterAdder);
        mHandler.postDelayed(randomCharacterAdder, mDelay);
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }
    private int random_in_range(int x,int y){
        Random rand = new Random();
        return x+rand.nextInt(y-x);
    }
    private char random_char(){
        return alph.charAt(random_in_range(0,alph.length()));
    }
}