package com.tcl.huantan.hhpod.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.huantan.hhpod.R;
import com.tcl.huantan.hhpod.constant.MusicPlayerConstant;
import com.tcl.huantan.hhpod.database.MusicDatabaseManager;
import com.tcl.huantan.hhpod.model.User;
import com.tcl.huantan.hhpod.util.MusicUtil;

import java.util.ArrayList;

/**
 * Created by huantan on 8/16/16.
 *
 * LoginActivity to handle the user login's event
 */
public class LoginActivity extends Activity implements OnClickListener{
    private static final String TAG = "LoginActivity";

    private static final String AUTO_FLAG = "auto_flag";

    private EditText mLoginUserName;
    private EditText mLoginPassWord;
    private static CheckBox mRememberCheckBox;

    private MusicDatabaseManager mMusicDatabaseManager;

    public static final String AUTO_LOGIN = "auto_login";
    public static final String PASSWORD = "password";

    public static SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_login);
        initWidget();
        initDatabase();
    }

    /**
     * Initialize the widget and corresponding listeners
     */
    private void initWidget() {
        mLoginUserName = (EditText) findViewById(R.id.login_username);
        mLoginPassWord = (EditText) findViewById(R.id.login_password);
        mRememberCheckBox = (CheckBox) findViewById(R.id.checkbox_remember);
        mRememberCheckBox.setChecked(true);

        Button mUserLoginButton = (Button) findViewById(R.id.button_user_login);
        TextView mUserRegisterTextView = (TextView) findViewById(R.id.user_register);
        CheckBox mDisplayCheckBox = (CheckBox) findViewById(R.id.checkbox_display);
        mUserLoginButton.setOnClickListener(this);
        mUserRegisterTextView.setOnClickListener(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Auto login
        if (mSharedPreferences.getBoolean(AUTO_LOGIN, false)) {
            Log.i(TAG, "initWidget: ");
            Intent intentMusic = new Intent(LoginActivity.this, MainActivity.class);
            intentMusic.putExtra(MusicPlayerConstant.PlayerMsg.ACCOUNT, mSharedPreferences.getString(MusicPlayerConstant.PlayerMsg.ACCOUNT, ""));
            intentMusic.putExtra(AUTO_FLAG, mSharedPreferences.getBoolean(AUTO_LOGIN, false));
            startActivity(intentMusic);
            this.finish();
        }

        // Set listener to monitor whether the username is null
        mLoginUserName.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // If the username is null, show toast
                    if (mLoginUserName.getText().toString().trim().equals("")) {
                        Toast.makeText(LoginActivity.this, R.string.username_null,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Set listener to monitor the loginPassWord's length
        // When it's length less than 6 or more than 16, it will show toast
        mLoginPassWord.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String password = mLoginPassWord.getText().toString().trim();
                    if (password.length() < 6) {
                        Toast.makeText(LoginActivity.this, R.string.password_short,
                                Toast.LENGTH_SHORT).show();
                    }
                    if (password.length() > 16) {
                        Toast.makeText(LoginActivity.this, R.string.password_long,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // The listener for the display check box
        mDisplayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // Checked: display the password
                if (isChecked) {
                    mLoginPassWord.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // Otherwise: hide the password
                    mLoginPassWord.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

    }

    /**
     * Initialize the database If the database's table is null, initialize the
     * table.
     */
    public void initDatabase() {
        mMusicDatabaseManager = new MusicDatabaseManager(this);
        Log.d(TAG,"initDatabase : :");
        Boolean result = mMusicDatabaseManager.JudgeTables();
        if (!result) {
            ArrayList<User> users = new ArrayList<>();
            User user1 = new User("gleeaglee", "123456", "huan.tan@qq.com", "15708459400");
            User user2 = new User("th","111111","123@qq.com","15708458911");
            // Encrypt the password with MD5 before add them to the database
            user1.setPassword((MusicUtil.getMD5Str(user1.getPassword())));
            user2.setPassword((MusicUtil.getMD5Str(user2.getPassword())));
            users.add(user1);
            users.add(user2);
            mMusicDatabaseManager.addUsers(users);
        }
    }

    /**
     * Set listeners for buttons
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_user_login:
                if (checkEdit()) {
                    login();
                }
                break;
            case R.id.user_register:
                Intent intentRegister = new Intent(LoginActivity.this, UserRegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }

    /**
     * Check whether the input is reasonable
     *
     * @return Boolean If the input is not null, return true; else,return true;
     */
    private boolean checkEdit() {
        if (mLoginUserName.getText().toString().trim().equals("")) {
            Toast.makeText(LoginActivity.this, R.string.username_null, Toast.LENGTH_SHORT).show();
        }
        else if (mLoginPassWord.getText().toString().trim().equals("")) {
            Toast.makeText(LoginActivity.this, R.string.password_null, Toast.LENGTH_SHORT).show();
        }
        else {
            return true;
        }
        return false;
    }

    /**
     * User login
     */
    private void login() {
        String userName = mLoginUserName.getText().toString();
        // Encryption the password and then check
        String userPassword = MusicUtil.getMD5Str(mLoginPassWord.getText().toString());
        Boolean loginResultOfName = mMusicDatabaseManager.queryByNameAndPassword(new String[] {
                userName, userPassword
        });
        Boolean loginResultOfEmail = mMusicDatabaseManager.queryByEmailAndPassword(new String[] {
                userName, userPassword
        });
        Boolean loginResultOfTel = mMusicDatabaseManager.queryByTelAndPassword(new String[] {
                userName, userPassword
        });
        if (loginResultOfName || loginResultOfEmail || loginResultOfTel) {
            Log.i(TAG, "Login succeed!");
            // Remember the password and account
            // Put the user data to the editor
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            if (mRememberCheckBox.isChecked()) {
                mEditor.putBoolean(AUTO_LOGIN, true);
                mEditor.putString(MusicPlayerConstant.PlayerMsg.ACCOUNT, userName);
                mEditor.putString(PASSWORD, userPassword);
            } else {
                mEditor.clear();
            }
            mEditor.apply();

            Intent intentMusic = new Intent(LoginActivity.this, MainActivity.class);
            intentMusic.putExtra(MusicPlayerConstant.PlayerMsg.ACCOUNT, userName);
            startActivity(intentMusic);
            this.finish();
            Log.d(TAG,"finish!");
        }
        else {
            Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }
}
