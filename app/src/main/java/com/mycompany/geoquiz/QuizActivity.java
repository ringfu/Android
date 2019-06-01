// encode: UFT-8
package com.mycompany.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.BatchUpdateException;

public class QuizActivity extends AppCompatActivity {
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private TextView mrate;
    private String mscore;
    private static final String KEY_INDEX = "index";
    private static final String ANSWER_INDEX = "answer";
    private static final String SCORE_INDEX = "score";
    private static final int REQUEST_CODE_CHEAT = 0;
    private int[] answered = new int[6];
    private int right = 0;
    private int count = 0;
    private boolean mIsCheater;

    //some question stored in array.
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_canberra,true),
            new Question(R.string.question_land,false),
            new Question(R.string.question_ocean,true),
            new Question(R.string.question_sky,true),
            new Question(R.string.question_water,false),
            new Question(R.string.question_wood,true)
    };
    private int mCurrentIndex = 0;

    // initialize all view
    private void initView(){
        ///!!!!!!!!!!!!!!!!!! not now
        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mPrevButton = findViewById(R.id.prev_button);
        mNextButton = findViewById(R.id.next_button);
        mCheatButton = findViewById(R.id.cheat_button);
        mQuestionTextView = findViewById(R.id.question_text_view);
        mrate = findViewById(R.id.rate);

     }
    // save current question index when pause.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
        savedInstanceState.putIntArray(ANSWER_INDEX,answered);
        savedInstanceState.putString(SCORE_INDEX,mscore);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //recover mCurrentIndex from savedInstanceState.
        if(savedInstanceState!=null) {  //first time run, there's no savedInstanceState,so judge it.
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            answered =(int[]) savedInstanceState.getIntArray(ANSWER_INDEX);
            mscore =(String) savedInstanceState.getString(SCORE_INDEX);
        }
        //set question text by question id in mQuestionBank[]
        //mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex+1)%mQuestionBank.length;
                updateQuestion();
            }
        });
        //recover answered
        if(answered[mCurrentIndex] == 1){
            enableButton(false);
        }else {
            enableButton(true);
        }
        //recover score
//        mrate = (TextView) findViewById(R.id.rate);
        mrate.setText(mscore);

        //TRUE button
        //mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View view){
                answered[mCurrentIndex] = 1;
                checkAnswer(true);
                if(answered[mCurrentIndex] == 1){
                    enableButton(false);
                };
            }
        });
        //FALSE button
        //mFalseButton = (Button) findViewById(R.id.false_button); //FALSE button
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answered[mCurrentIndex] = 1;
                checkAnswer(false);
                if(answered[mCurrentIndex] == 1){
                    enableButton(false);
                };
//                enableButton(false);
            }
        });

        //PREV button
        //mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (Math.abs(mCurrentIndex-1))%mQuestionBank.length;
                //attention! use Math.abs() to avoid negative number
                updateQuestion();
                if(answered[mCurrentIndex] == 1){
                    enableButton(false);
                }else {
                    enableButton(true);
                }
            }
        });

        //NEXT button
        //mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex+1)%mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
                if(answered[mCurrentIndex] == 1){
                    enableButton(false);
                }else {
                    enableButton(true);
                }
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动另一个activity
                //Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this,answerIsTrue);
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
         updateQuestion();
    }

    // 根据当前问题是否回答，来设置答案按钮是否可见
    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if(answered[mCurrentIndex]==1){
            enableButton(false);
        }
    }
    // 设置按钮是否可见的逻辑
    private void enableButton(boolean tf){
        if(tf == true){
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }else{
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
    }

    //check the answer user give and the correct answer, then change the toast.
    private void checkAnswer(boolean userAnswer){
        count++;
        boolean trueAnswer = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if(mIsCheater){
            messageResId = R.string.judgment_toast;
        }else{
            if(userAnswer == trueAnswer){
                messageResId = R.string.correct_toast;
                right++;
            }else{
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast toast = Toast.makeText(QuizActivity.this,messageResId,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,200);
        toast.show();
        //rate TextView
        mscore =(String)( "score:"+Double.toString((right*1.0)/count));
        //mrate = (TextView) findViewById(R.id.rate);
        mrate.setText(mscore);
    }
    // 处理CheatActivity子进程返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }
}
