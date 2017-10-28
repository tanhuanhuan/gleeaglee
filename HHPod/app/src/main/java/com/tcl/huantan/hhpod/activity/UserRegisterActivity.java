package com.tcl.huantan.hhpod.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.constant.MusicPlayerConstant;
import com.tcl.huantan.hhpod.database.MusicDatabaseManager;
import com.tcl.huantan.hhpod.model.User;
import com.tcl.huantan.hhpod.util.MusicUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huantan on 8/16/16.
 * UserRegisterActivity to handle the user register's event
 */
public class UserRegisterActivity extends Activity implements OnClickListener {
    private static final String TAG = "UserRegisterActivity";

    private static final String REMEMBER_PASSWORD = "remember_password";
    private static final String AUTO_LOGIN = "auto_login";
    private static final String PASSWORD = "password";

    private EditText mUserNameEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordAgainEditText;
    private EditText mEmailEditText;
    private EditText mTelEditText;

    private MusicDatabaseManager mMusicDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initWidget();

    }

    // Init the widget
    private void initWidget() {
        mUserNameEditText = (EditText) findViewById(R.id.user_name);
        mPasswordEditText = (EditText) findViewById(R.id.password);
        mPasswordAgainEditText = (EditText) findViewById(R.id.password_again);
        mEmailEditText = (EditText) findViewById(R.id.email);
        mTelEditText = (EditText) findViewById(R.id.phone);
        Button mRegisterButton = (Button) findViewById(R.id.button_register);
        mRegisterButton.setOnClickListener(this);

        // Set listener to monitor whether the username is null
        mUserNameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If the username is null, show toast
                if (!hasFocus) {
                    if (mUserNameEditText.getText().toString().trim()
                            .equals("")) {
                        Toast.makeText(UserRegisterActivity.this,
                                R.string.username_null, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });

        // Set listener to monitor the PassWord's length
        // When it's length less than 6 or more than 16, it will show toast
        mPasswordEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String password = mPasswordEditText.getText().toString()
                            .trim();
                    if (password.length() < 6) {
                        Toast.makeText(UserRegisterActivity.this,
                                R.string.password_short, Toast.LENGTH_SHORT)
                                .show();
                    }
                    if (password.length() > 16) {
                        Toast.makeText(UserRegisterActivity.this,
                                R.string.password_long, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });

        // Set listener to Check whether the two input password is the same
        mPasswordAgainEditText
                .setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        // If the two input password is different, show toast
                        if (!hasFocus) {
                            String passwordAgain = mPasswordAgainEditText
                                    .getText().toString().trim();
                            String password = mPasswordEditText.getText()
                                    .toString().trim();
                            if (!passwordAgain.equals(password)) {
                                Toast.makeText(UserRegisterActivity.this,
                                        R.string.password_diffent,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        // Set listener to monitor whether the email is correct
        mEmailEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If the email is not null and is illegal, show toast
                if (!hasFocus) {
                    if (!mEmailEditText.getText().toString().trim().equals("")) {
                        if (!EmailFormat(mEmailEditText.getText().toString().trim())) {
                            Toast.makeText(UserRegisterActivity.this,
                                    R.string.email_illegal, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }
            }
        });

        // Set listener to monitor whether the phone number is correct
        mTelEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If the phone number is not null and is illegal, show toast
                if (!hasFocus) {
                    if (!mTelEditText.getText().toString().trim().equals("")) {
                        if (!isMobileNO(mTelEditText.getText().toString().trim())) {
                            Toast.makeText(UserRegisterActivity.this,
                                    R.string.phone_number_illegal, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }
            }
        });
    }

    // Set listener for register button
    @Override
    public void onClick(View v) {
         mMusicDatabaseManager = new MusicDatabaseManager(UserRegisterActivity.this);
        // Check whether the username is null
        if (mUserNameEditText.getText().toString().trim().equals("")) {
            Toast.makeText(UserRegisterActivity.this, R.string.username_null,
                    Toast.LENGTH_SHORT).show();
        }
        // Check whether the username is exist
        else {
            String mUsername = mUserNameEditText.getText().toString();
            if (mMusicDatabaseManager.queryByName(new String[]{
                    mUsername
            })) {
                Toast.makeText(UserRegisterActivity.this, R.string.user_exist,
                        Toast.LENGTH_SHORT).show();
            }
            // Check the password
            else {
                String mPassword = mPasswordEditText.getText().toString()
                        .trim();
                String mPasswordAgain = mPasswordAgainEditText.getText()
                        .toString().trim();
                if (!mPasswordAgain.equals(mPassword)) {
                    Toast.makeText(UserRegisterActivity.this,
                            R.string.password_diffent, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (mPassword.length() < 6 || mPassword.length() > 16) {
                        Toast.makeText(UserRegisterActivity.this,
                                R.string.password_error, Toast.LENGTH_SHORT)
                                .show();
                    }
                    else {
                        // Check the email format
                        if (!mEmailEditText.getText().toString().trim().equals("")
                                && !EmailFormat(mEmailEditText.getText().toString().trim())) {
                            Toast.makeText(UserRegisterActivity.this,
                                    R.string.email_illegal, Toast.LENGTH_SHORT)
                                    .show();
                        }
                        else {
                            // Check the phone number format
                            if (!mTelEditText.getText().toString().trim().equals("")
                                    && !isMobileNO(mTelEditText.getText().toString().trim())) {
                                Toast.makeText(UserRegisterActivity.this,
                                        R.string.phone_number_illegal, Toast.LENGTH_SHORT)
                                        .show();
                            }
                            else {
                                // Register the new user
                                String mName = mUserNameEditText.getText().toString().trim();
                                String mEmail = mEmailEditText.getText().toString().trim();
                                String mTel = mTelEditText.getText().toString().trim();
                                User registerUser = new User();
                                registerUser.setName(mName);
                                registerUser.setPassword(MusicUtil.getMD5Str(mPassword));
                                registerUser.setEmail(mEmail);
                                registerUser.setTel(mTel);
                                mMusicDatabaseManager.addUser(registerUser);
                                Log.i(TAG, "Register succeed!");
                                Toast.makeText(UserRegisterActivity.this, R.string.login_succeed,
                                        Toast.LENGTH_SHORT).show();
                                SharedPreferences mSharedPreferences = PreferenceManager
                                        .getDefaultSharedPreferences(UserRegisterActivity.this);
                                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                                mEditor.putBoolean(AUTO_LOGIN, true);
                                mEditor.putBoolean(REMEMBER_PASSWORD, true);
                                mEditor.putString(MusicPlayerConstant.PlayerMsg.ACCOUNT, mName);
                                mEditor.putString(PASSWORD, mPassword);
                                mEditor.apply();
                                Intent intentMusic = new Intent(UserRegisterActivity.this,
                                        MainActivity.class);
                                intentMusic.putExtra(MusicPlayerConstant.PlayerMsg.ACCOUNT, mName);
                                startActivity(intentMusic);
                                UserRegisterActivity.this.finish();
                            }
                        }
                    }
                }
            }
        }
    }

    // To check the email format
    private boolean EmailFormat(String email) {
        Pattern pattern = Pattern
                .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher mc = pattern.matcher(email);
        return mc.matches();
    }

    // To check the phone number format
    private boolean isMobileNO(String mobile) {
        String telRegex = "[1][358]\\d{9}";
        return mobile.matches(telRegex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
