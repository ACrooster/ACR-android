package nl.acr.rooster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import go.framework.Framework;

public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fab;

    EditText code;
    Button login;

    ScrollView loginView;
    LinearLayout progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        code = (EditText)findViewById(R.id.code);
        login = (Button) findViewById(R.id.loginButton);

        loginView = (ScrollView)findViewById(R.id.loginView);
        progressView = (LinearLayout)findViewById(R.id.progressView);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // Set the school name
        Framework.SetSchool("amstelveencollege");
    }

    // TODO: This needs to be handled better
    private void attemptLogin() {
        showProgress(true);

        Log.w("Code", code.getText().toString());

        // TODO: Figure out a way to do this in the background, app currently freezes
        String token = Framework.GetToken(code.getText().toString());
//      String token = null;

        // NOTE: This is temporary test output
        TextView textView = (TextView) findViewById(R.id.codeView);
        switch ((int)Framework.GetError()) {
            case (int)Framework.ERROR_NONE:
                Log.w("Token", token);
                break;

            case (int)Framework.ERROR_CONNECTION:
                showProgress(false);
                textView.setText("Connection error");
                break;

            case (int)Framework.ERROR_UNKNOWN:
                showProgress(false);
                textView.setText("Unknown error");
                break;
        }
    }

    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });


        progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }
}
