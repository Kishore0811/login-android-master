package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputapartmentname;
    private EditText inputapart_address;
    private EditText inputlandmark;
    private EditText inputfrequency;
    private EditText inputnoflats;
    private EditText inputavg_weight;
    //private Spinner inputtime;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputapartmentname = (EditText) findViewById(R.id.apartmentname);
        inputapart_address = (EditText) findViewById(R.id.apart_address);
        inputavg_weight = (EditText) findViewById(R.id.avg_weight);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputlandmark = (EditText) findViewById(R.id.landmark);
        inputnoflats = (EditText) findViewById(R.id.noflats);
        inputfrequency = (EditText) findViewById(R.id.frequency);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        //inputtime = (Spinner) findViewById(R.id.time);

        List<String> categories = new ArrayList<>();

        categories.add("Pickup Time");
        categories.add("6 A.M - 9 A.M");
        categories.add("10 A.M - 1 P.M");
        categories.add("2 P.M - 5 P.M");
        categories.add("6 P.M - 10 P.M");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
       // inputtime.setAdapter(dataAdapter);



    // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String apartmentname = inputapartmentname.getText().toString().trim();
                String apart_address = inputapart_address.getText().toString().trim();
                String avg_weight = inputavg_weight.getText().toString().trim();
                String landmark = inputlandmark.getText().toString().trim();
                String noflats = inputnoflats.getText().toString().trim();
                //int noflats = Integer.parseInt(inputnoflats.getText().toString());
                String frequency = inputfrequency.getText().toString().trim();
                //int frequency = Integer.parseInt(inputfrequency.getText().toString());
               // String time = inputtime.getSelectedItem().toString();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !apartmentname.isEmpty() &&
                        !apart_address.isEmpty() && !avg_weight.isEmpty() && !landmark.isEmpty()) {
                    registerUser(name, email, password, apartmentname, noflats, apart_address, avg_weight, landmark, frequency);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            } //onclick
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    } //oncreate

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String email, final String password,
                              final String apartmentname, final String apart_address, final String avg_weight,
                              final String landmark, final String noflats, final String frequency) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");

        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String apartmentname = user.getString("apartmentname");
                        String apart_address = user.getString("apart_address");
                        String avg_weight = user.getString("avg_weight");
                        String landmark = user.getString("landmark");
                        String noflats = user.getString("noflats");
                        String frequency = user.getString("frequency");
                        //String time = user.getString("time");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, apartmentname, apart_address, frequency, avg_weight,
                                landmark, noflats, email, uid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("apartmentname", apartmentname);
                params.put("apart_address", apart_address);
                params.put("avg_weight", avg_weight);
                params.put("landmark", landmark);
                params.put("noflats", noflats);
              //  params.put("time", time);
                params.put("frequency", frequency);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
