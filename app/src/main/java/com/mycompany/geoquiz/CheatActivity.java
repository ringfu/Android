// encode: UTF-8
package com.mycompany.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.BatchUpdateException;

import static com.mycompany.geoquiz.R.id.answer_text_view;

public class CheatActivity extends AppCompatActivity {
    private boolean mAnswerIsTrue;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private static final String EXTRA_ANSWER_IS_TRUE = "com.mycompany.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.mycompany.geoquiz.answer_shown";
    // 初始化各种view
    private void initView(){
        mAnswerTextView = (TextView) findViewById(answer_text_view);
        mShowAnswerButton = (Button)findViewById(R.id.show_answer_button);
    }
    // 自定义intent，用于子进程和父进程传递数据
    public static Intent newIntent(Context packageContext, boolean answerIsTrue){
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return intent;
    }
    //解析结果intent
    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setContentView(R.layout.activity_cheat);
        // 获取父进程传递的数据：当前问题的答案
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false);
        // 子进程偷看答案的逻辑
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAnswerIsTrue){
                    mAnswerTextView.setText(R.string.true_button);
                }else{
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);
            }
        });
    }
    // 返回子进程是否偷看答案，给父进程
    private void setAnswerShownResult(boolean isAnswerShown){
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }
}
