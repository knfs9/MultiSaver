package app.course.gdg.multisaver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;

import app.course.gdg.multisaver.Utils.Constants;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private DropboxAPI<AndroidAuthSession> dropboxAPI;
    private boolean isLoggedIn;
    Button loginButton;
    EditText login;
    EditText password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        login = (EditText) findViewById(R.id.loginTextView);
        password = (EditText) findViewById(R.id.passwordTextView);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        AppKeyPair appKeyPair = new AppKeyPair(Constants.APP_KEY, Constants.APP_KEY_SEC);
        AndroidAuthSession session;

        SharedPreferences prefs = getSharedPreferences(Constants.DROPBOX_NAME, 0);
        String key = prefs.getString(Constants.APP_KEY, null);
        String secret = prefs.getString(Constants.APP_KEY_SEC, null);

        if(key != null && secret != null){
            AccessTokenPair accessTokenPair = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(appKeyPair, accessTokenPair);
        }else{
            session = new AndroidAuthSession(appKeyPair);
        }
        dropboxAPI = new DropboxAPI(session);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button :
                dropboxAPI.getSession().startOAuth2Authentication(this);
                Intent intent = new Intent(AuthActivity.this, DbxActivity.class);
                intent.putExtra("login", login.getText());
                intent.putExtra("password", password.getText());
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = dropboxAPI.getSession();

        if(session.authenticationSuccessful()){
            try {
                session.finishAuthentication();
                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(Constants.DROPBOX_NAME, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constants.APP_KEY, tokens.key);
                editor.putString(Constants.APP_KEY_SEC, tokens.key);
                editor.commit();
                loggedIn(true);
            }catch (IllegalStateException e){
                Toast.makeText(this, "Error during Dropbox authentification",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loggedIn(boolean isLogged){
        isLoggedIn = isLogged;
    }
}
