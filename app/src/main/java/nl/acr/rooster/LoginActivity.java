package nl.acr.rooster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import go.framework.Framework;

public class LoginActivity extends AppCompatActivity {
    Toolbar toolbar;

    EditText codeText;
    Button login;

    ScrollView loginView;
    LinearLayout progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        codeText = (EditText) findViewById(R.id.codeText);
        login = (Button) findViewById(R.id.loginButton);

        loginView = (ScrollView)findViewById(R.id.loginView);
        progressView = (LinearLayout)findViewById(R.id.progressView);

        setSupportActionBar(toolbar);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        codeText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        codeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(v.getId() == R.id.codeText && !hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        // Set the school name
        Framework.SetSchool("amstelveencollege");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // TODO: This needs to be handled better
    private void attemptLogin() {
        // Check the length of the code to make sure it is valid
        if(codeText.getText().length() != 12) {
            codeText.setError(getString(R.string.code_invalid_length));
            return;
        }

        TokenTask tokenTask = new TokenTask(codeText.getText().toString());
        tokenTask.execute((Void) null);
    }

    public class TokenTask extends AsyncTask<Void, Void, Void> {
        private String code = null;
        private String token = null;

        TokenTask(String mCode) {
            code = mCode;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            token = Framework.GetToken(code);

            return null;
        }

        @Override
        protected void onPostExecute(final Void success) {
            TextView textView = (TextView)findViewById(R.id.codeView);
            switch ((int) Framework.GetError()) {
                case (int) Framework.ERROR_NONE:
                    if (token == null) {
                        // NOTE: This should never ever happen, but just in case
                        Log.e("Token", "Received null token");
                        showProgress(false);
                        break;
                    }
                    // TODO: Switch to next activity
                    // TODO: Make sure the token gets stored somewhere
                    Log.w("Token", token);
                    Intent goToSchedule = new Intent(getApplicationContext(), ScheduleActivity.class);
                    startActivity(goToSchedule);
                    break;

                case (int) Framework.ERROR_CONNECTION:
                    textView.setText("Connection error");
                    break;

                case (int) Framework.ERROR_UNKNOWN:
                    textView.setText("Unknown error");
                    break;
            }

            // If the login was unsuccessful display the login screen again
            if (Framework.GetError() != Framework.ERROR_NONE) {
                showProgress(false);
            }
        }

        @Override
        protected void onCancelled() {
            token = null;
            showProgress(false);
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
}
