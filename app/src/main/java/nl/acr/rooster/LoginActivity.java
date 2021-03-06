package nl.acr.rooster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
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
    TextInputLayout codeTextLayout;
    Button login;

    ScrollView loginView;
    LinearLayout progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        codeText = (EditText) findViewById(R.id.codeText);
        codeTextLayout = (TextInputLayout) findViewById(R.id.codeTextLayout);
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

                if (v.getId() == R.id.codeText && !hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        SharedPreferences settings = getSharedPreferences(StartActivity.PREFS_NAME, 0);
        if (settings.contains("token") && settings.getLong("login_date", 0) <= StartActivity.RELOGIN_DATE) {

            Snackbar.make(findViewById(R.id.drawer_layout), getResources().getString(R.string.relogin), Snackbar.LENGTH_INDEFINITE)
                    .show();
        }
        Log.w("login_date", String.valueOf(settings.getLong("login_date", 0)));


        // TODO: Login using the zermelo OAuth page
//        Intent intent = getIntent();
//        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // TODO: This needs to be handled better
    private void attemptLogin() {
        // Check the length of the code to make sure it is valid
        if(!Framework.IsValid(codeText.getText().toString())) {
            codeTextLayout.setError(getString(R.string.code_invalid_length));
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
            int errorCode = (int)Framework.GetError();
            switch (errorCode) {
                case (int) Framework.ERROR_NONE:
                    if (token == null) {
                        // NOTE: This should never ever happen, but just in case
                        showProgress(false);
                        break;
                    }

                    String name = "";
                    String id = "";

                    Framework.RequestUserData();
                    // TODO: Add error handling
                    switch ((int) Framework.GetError()) {
                        case (int) Framework.ERROR_NONE:
                            name = Framework.GetName();
                            id = Framework.GetId();
                            break;
                    }

                    SharedPreferences settings = getSharedPreferences(StartActivity.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("token", token);
                    editor.putLong("login_date", System.currentTimeMillis() / 1000);
                    editor.putString("name", name);
                    editor.putString("id", id);
                    editor.apply();
                    Intent goToSchedule = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(goToSchedule);
                    finish();
                    break;

                case (int) Framework.ERROR_CONNECTION:
                    textView.setText("Connection error");
                    break;

                case (int) Framework.ERROR_UNKNOWN:
                    textView.setText("Unknown error");
                    break;

                case (int) Framework.ERROR_CODE:
                    codeTextLayout.setError(getString(R.string.code_invalid));
                    codeText.requestFocus();
                    codeText.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(codeText, InputMethodManager.SHOW_IMPLICIT);
                    break;

                default:
                    textView.setText("Er gaat iets mis");
                    break;

            }

            // If the login was unsuccessful display the login screen again
            if (errorCode != Framework.ERROR_NONE) {
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
