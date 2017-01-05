package com.hartz.inventory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.hartz.inventory.model.Mrmart;
import com.hartz.inventory.model.Satuan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PPRE_Form extends AppCompatActivity {
    ArrayList<Mrmart> mrmartArray;
    ArrayList<Satuan> satuanArray;
    ArrayAdapter<Mrmart> adapterMrmart;
    ArrayAdapter<Satuan> adapterSatuan;
    ArrayList<Mrmart> mrMartList;
    ArrayList<AutoCompleteTextView> autoCompleteTextViewList;
    ArrayList<Spinner> spinnerList;
    ArrayList<EditText> editTextList;
    ArrayList<RelativeLayout> relativeLayoutList;
    LinearLayout linearLayout;

    private AddPPRETask addPPRETask = null;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_ppre__form);


        mProgressView = (ProgressBar)findViewById(R.id.ppre_progress);
        mLoginFormView = (View)findViewById(R.id.ppre_form_view);

        //get the list of mrmart and satuan from preferences
        mrmartArray = Mrmart.getListFromPrefs(getApplicationContext());
        satuanArray = Satuan.getListFromPrefs(getApplicationContext());

        //Initialize our list for the views
        autoCompleteTextViewList = new ArrayList<AutoCompleteTextView>();
        spinnerList = new ArrayList<Spinner>();
        relativeLayoutList = new ArrayList<RelativeLayout>();
        editTextList = new ArrayList<EditText>();
        mrMartList = new ArrayList<Mrmart>();


        //the main linear layout inside scrollview
        linearLayout = (LinearLayout)findViewById(R.id.content_ppre__form);

        //initialize our adapoters
        adapterMrmart = new ArrayAdapter<Mrmart>
                (this, R.layout.autocomplete_dropdown, mrmartArray);
        adapterSatuan = new ArrayAdapter<Satuan>
                (this, android.R.layout.simple_spinner_item, satuanArray);

        //change the looks of our adapter satuan
        adapterSatuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //add inital item
        addItem();
    }

    protected void addItem(){
        View itemLayout = LayoutInflater.from(this).inflate(R.layout.content_ppre_form_item,null);

        AutoCompleteTextView autoCompleteTextView =
                (AutoCompleteTextView)itemLayout.findViewById(R.id.ppre_form_autocomplete1);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(adapterMrmart);

        autoCompleteTextViewList.add(autoCompleteTextView);

        //to get selected index of autotextview, add it to the mrmart list
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = autoCompleteTextViewList.size()-1;
                mrMartList.set(index, (Mrmart) parent.getItemAtPosition(position));
            }
        });

        //add null list for future reference
        mrMartList.add(null);

        Spinner spinner = (Spinner) itemLayout.findViewById(R.id.ppre_form_spinner1);
        spinner.setAdapter(adapterSatuan);
        spinnerList.add(spinner);

        relativeLayoutList.add((RelativeLayout)itemLayout.findViewById(R.id.ppre_form_relativelayout));

        ImageButton button = (ImageButton) itemLayout.findViewById(R.id.ppre_form_imagebutton);
        button.setTag(spinnerList.size()-1);

        EditText editText = (EditText) itemLayout.findViewById(R.id.ppre_form_edittext1);
        editTextList.add(editText);

        linearLayout.addView(itemLayout);
    }

    protected void addRecord(View v){
        Log.v("message", "add record pressed");
        addItem();
    }

    protected void deleteRecord(View v){

        Log.v("message", "delete record");
        int index = (int)v.getTag();
        relativeLayoutList.get(index).setVisibility(RelativeLayout.GONE);

    }

    protected void submitRecord(View v){

        JSONObject object = new JSONObject();
        try {
            object.put("token", SharedPrefsHelper.readPrefs(SharedPrefsHelper.TOKEN_PREFS, getApplicationContext()));

        JSONArray array = new JSONArray();
        //for every item entry
        for(int i = 0; i < mrMartList.size(); i++){
            //if the component is visible (not deleted)
            if(autoCompleteTextViewList.get(i).getVisibility() != AutoCompleteTextView.GONE){
                //jika sudah ada item yang dipilih
                if(mrMartList.get(i) != null) {
                    JSONObject entry = new JSONObject();
                    entry.put(Mrmart.MRMART_ARTICLEID, mrMartList.get(i).getArticleID());
                    entry.put(Mrmart.MRMART_GROUPID, mrMartList.get(i).getGroupID());
                    entry.put(Mrmart.MRMART_QUANTITY, editTextList.get(i).getText());
                    Satuan s = (Satuan) spinnerList.get(i).getSelectedItem();
                    entry.put(Mrmart.MRMART_SATUAN, s.getSatuanID());
                    array.put(entry);
                }
            }
        }
            object.put("entries", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("message", object.toString());
        showProgress(true);
        addPPRETask = new AddPPRETask(object.toString());
        addPPRETask.execute((Void)null);
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class AddPPRETask extends AsyncTask<Void, Void, Boolean> {

        private final String jsonRequest;
        private boolean connectionProblem;

        AddPPRETask(String jsonRequest) {
            this.jsonRequest = jsonRequest;
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            // Simulate network access.
            HttpHandler handler = new HttpHandler(getApplicationContext());
            LinkedHashMap<String, Object> parameter = new LinkedHashMap<>();
            parameter.put("json", jsonRequest);


            String loginResult = null;
            try {
                loginResult = handler.makePostCall(parameter, handler.LINK_PPRE_CREATE);
            } catch (IOException e) {
                connectionProblem = true;
                return false;
            }
            Log.v("POST RESULT", loginResult);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
//
            showProgress(false);
//
            if (success) {
                Toast.makeText(getApplicationContext(), "Permintaan Pembelian Ditambahkan",Toast.LENGTH_SHORT).show();
                finish();
            }

//            } else {
//                if(connectionProblem){
//                    mPasswordView.setError(getString(R.string.error_network_problem));
//                }else{
//                    mPasswordView.setError(getString(R.string.error_incorrect_password));
//                }
//
//                mPasswordView.requestFocus();
//            }
        }

        @Override
        protected void onCancelled() {
            addPPRETask = null;
            showProgress(false);
        }
    }


}
