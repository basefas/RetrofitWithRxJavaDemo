package com.basefas.rx.retrofitwithrxjavademo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.basefas.rx.retrofitwithrxjavademo.model.User;
import com.basefas.rx.retrofitwithrxjavademo.R;
import com.basefas.rx.retrofitwithrxjavademo.network.Http;
import com.basefas.rx.retrofitwithrxjavademo.network.RxSubscriber;
import com.basefas.rx.retrofitwithrxjavademo.utils.GlideUtils;

/**
 * MainActivity
 */
public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText editText;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.btn_get);
        editText = (EditText) findViewById(R.id.et_name);
        imageView = (ImageView) findViewById(R.id.iv_avatar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAvatar(editText.getText().toString().trim());
            }
        });
    }

    private void getAvatar(String name){
        Http.getInstance().user(new RxSubscriber<User>() {
            @Override
            public void onNext(User user) {
                super.onNext(user);
                GlideUtils.loadNetImage(MainActivity.this,user.getAvatar_url(),imageView);
            }
        },name);
    }
}
