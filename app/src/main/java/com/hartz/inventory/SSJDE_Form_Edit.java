package com.hartz.inventory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import com.hartz.inventory.model.Customer;
import com.hartz.inventory.model.Mfgart;
import com.hartz.inventory.model.SSJDE;
import com.hartz.inventory.model.Satuan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Webmaster on 1/9/2017.
 */

public class SSJDE_Form_Edit extends AppCompatActivity{

    ArrayList<Mfgart> mfgartArray;
    ArrayList<Satuan> satuanArray;
    ArrayList<Customer> customerArray;
    ArrayAdapter<Mfgart> adapterMfgart;
    ArrayAdapter<Satuan> adapterSatuan;
    ArrayAdapter<Customer> adapterCustomer;
    ArrayList<Mfgart> mfgartList;
    ArrayList<AutoCompleteTextView> autoCompleteTextViewList;
    ArrayList<Spinner> spinnerList;
    ArrayList<EditText> editTextList;
    ArrayList<EditText> editTextNoteList;
    ArrayList<RelativeLayout> relativeLayoutList;
    LinearLayout linearLayout;
    Customer selectedCustomer = null;
    AutoCompleteTextView customerTextView;
    private EditSSJDETask addSSJDETask = null;
    private View mProgressView;
    private View mLoginFormView;
    private SSJDE ssjde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_ssjde);

        //receive ppre object to be edited
        Intent i = getIntent();
        ssjde = (SSJDE) i.getSerializableExtra("ssjdeObject");

        mProgressView = (ProgressBar)findViewById(R.id.ssjde_progress);
        mLoginFormView = (View)findViewById(R.id.ssjde_form_view);

        //get the list of mfgart and satuan from preferences
        mfgartArray = Mfgart.getListFromPrefs(getApplicationContext());
        satuanArray = Satuan.getListFromPrefs(getApplicationContext());
        customerArray = Customer.getListFromPrefs(getApplicationContext());
        //Initialize our list for the views
        autoCompleteTextViewList = new ArrayList<AutoCompleteTextView>();
        spinnerList = new ArrayList<Spinner>();
        relativeLayoutList = new ArrayList<RelativeLayout>();
        editTextList = new ArrayList<EditText>();
        mfgartList = new ArrayList<Mfgart>();
        editTextNoteList = new ArrayList<EditText>();

        //the main linear layout inside scrollview
        linearLayout = (LinearLayout)findViewById(R.id.content_ssjde__form);

        //initialize our adapoters
        adapterMfgart = new ArrayAdapter<Mfgart>
                (this, R.layout.autocomplete_dropdown, mfgartArray);
        adapterSatuan = new ArrayAdapter<Satuan>
                (this, android.R.layout.simple_spinner_item, satuanArray);
        adapterCustomer = new ArrayAdapter<Customer>
                (this, R.layout.autocomplete_dropdown, customerArray);

        //change the looks of our adapter satuan
        adapterSatuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        customerTextView =
                (AutoCompleteTextView)findViewById(R.id.ssjde_form_autocompletecustomer);
        customerTextView.setThreshold(1);
        customerTextView.setAdapter(adapterCustomer);

        customerTextView.setText(ssjde.getCustomer().getName());
        selectedCustomer = ssjde.getCustomer();

        //to get selected index of autotextview, add it to the mfgart list
        customerTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = autoCompleteTextViewList.size()-1;
                selectedCustomer = (Customer) parent.getItemAtPosition(position);
            }
        });

        //add existing item
        addExistingItem(ssjde.getItemList());
    }

    protected void addExistingItem(ArrayList<Mfgart> itemList){
        for(int i = 0; i < itemList.size(); i++) {

            Mfgart mfgart = itemList.get(i);
            View itemLayout = LayoutInflater.from(this).inflate(R.layout.form_ssjde_item, null);

            AutoCompleteTextView autoCompleteTextView =
                    (AutoCompleteTextView) itemLayout.findViewById(R.id.ssjde_form_autocomplete1);
            autoCompleteTextView.setThreshold(1);
            autoCompleteTextView.setAdapter(adapterMfgart);
            int pos = adapterMfgart.getPosition(mfgart);
            autoCompleteTextView.setText(mfgart.toString());

            autoCompleteTextViewList.add(autoCompleteTextView);

            //to get selected index of autotextview, add it to the mfgart list
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int index = autoCompleteTextViewList.size() - 1;
                    mfgartList.set(index, (Mfgart) parent.getItemAtPosition(position));
                }
            });

            //add null list for future reference
            mfgartList.add(null);

            Spinner spinner = (Spinner) itemLayout.findViewById(R.id.ssjde_form_spinner1);
            spinner.setAdapter(adapterSatuan);
            pos = adapterSatuan.getPosition(new Satuan(mfgart.getSatuan()));
            spinner.setSelection(pos);

            spinnerList.add(spinner);

            relativeLayoutList.add((RelativeLayout) itemLayout.findViewById(R.id.ssjde_form_relativelayout));

            ImageButton button = (ImageButton) itemLayout.findViewById(R.id.ssjde_form_imagebutton);
            button.setTag(spinnerList.size() - 1);

            EditText editText = (EditText) itemLayout.findViewById(R.id.ssjde_form_edittext1);
            editText.setText(mfgart.getQuantity()+"");
            editTextList.add(editText);

            EditText editText1 = (EditText) itemLayout.findViewById(R.id.ssjde_form_note);
            editText1.setText(mfgart.getNote());
            editTextNoteList.add(editText1);

            linearLayout.addView(itemLayout);
        }
    }


    protected void addItem(){
        View itemLayout = LayoutInflater.from(this).inflate(R.layout.form_ssjde_item, null);

        AutoCompleteTextView autoCompleteTextView =
                (AutoCompleteTextView)itemLayout.findViewById(R.id.ssjde_form_autocomplete1);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapterMfgart);

        autoCompleteTextViewList.add(autoCompleteTextView);

        //to get selected index of autotextview, add it to the mfgart list
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = autoCompleteTextViewList.size()-1;
                mfgartList.set(index, (Mfgart) parent.getItemAtPosition(position));
            }
        });

        //add null list for future reference
        mfgartList.add(null);

        Spinner spinner = (Spinner) itemLayout.findViewById(R.id.ssjde_form_spinner1);
        spinner.setAdapter(adapterSatuan);
        spinnerList.add(spinner);

        relativeLayoutList.add((RelativeLayout)itemLayout.findViewById(R.id.ssjde_form_relativelayout));

        ImageButton button = (ImageButton) itemLayout.findViewById(R.id.ssjde_form_imagebutton);
        button.setTag(spinnerList.size()-1);

        EditText editText = (EditText) itemLayout.findViewById(R.id.ssjde_form_edittext1);
        editTextList.add(editText);

        EditText editText1 = (EditText) itemLayout.findViewById(R.id.ssjde_form_note);
        editTextNoteList.add(editText1);


        linearLayout.addView(itemLayout);
    }

    public void addRecord(View v){
        Log.v("message", "add record pressed");
        addItem();
    }

    public void deleteRecord(View v){
        Log.v("message", "delete record");
        int index = (int)v.getTag();
        relativeLayoutList.get(index).setVisibility(RelativeLayout.GONE);
    }

    public void submitRecord(View v){

        //validation of each entries
        boolean cancel = false;

        if(selectedCustomer == null){
            customerTextView.setError(getString(R.string.error_ppre_invalid_item));
            customerTextView.requestFocus();
        }

        for(int i = 0; i < relativeLayoutList.size(); i++){
            if(relativeLayoutList.get(i).getVisibility() == AutoCompleteTextView.VISIBLE) {
                if (mfgartList.get(i) == null) {
                    try {
                        ssjde.getItemList().get(i);
                    }catch (IndexOutOfBoundsException e){
                        autoCompleteTextViewList.get(i).setError(getString(R.string.error_ppre_invalid_item));
                        autoCompleteTextViewList.get(i).requestFocus();
                        cancel = true;
                    }
                }

                if (TextUtils.isEmpty(editTextList.get(i).getText())) {
                    editTextList.get(i).setError(getString(R.string.error_invalid_quantity));
                    editTextList.get(i).requestFocus();
                    cancel = true;
                }
            }
        }

        if(cancel == false) {
            JSONObject object = new JSONObject();
            try {
                object.put("token", SharedPrefsHelper.readPrefs(SharedPrefsHelper.TOKEN_PREFS,
                        getApplicationContext()));
                object.put("custid", selectedCustomer.getId());
                JSONArray array = new JSONArray();
                //for every item entry
                for (int i = 0; i < mfgartList.size(); i++) {
                    //if the component is visible (not deleted)
                    if (relativeLayoutList.get(i).getVisibility() ==
                            View.VISIBLE) {
                        //jika sudah ada item yang dipilih
                        if (!autoCompleteTextViewList.get(i).getText().equals("")) {
                            JSONObject entry = new JSONObject();
                            Mfgart mrmart = mfgartList.get(i);
                            //jika belum ada objek terpilih
                            if (mrmart == null) {
                                mrmart = ssjde.getItemList().get(i);
                            }

                            entry.put(Mfgart.MFGART_ARTICLEID, mrmart.getArticleID());
                            entry.put(Mfgart.MFGART_GROUPID, mrmart.getGroupID());
                            entry.put(Mfgart.MFGART_QUANTITY, editTextList.get(i).getText());
                            Satuan s = (Satuan) spinnerList.get(i).getSelectedItem();
                            entry.put(Mfgart.MFGART_SATUAN, s.getSatuanID());
                            entry.put(Mfgart.MFGART_NOTE, editTextNoteList.get(i).getText());
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
            addSSJDETask = new EditSSJDETask(object.toString());
            addSSJDETask.execute((Void) null);
        }
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
    public class EditSSJDETask extends AsyncTask<Void, Void, Boolean> {

        private final String jsonRequest;
        private boolean connectionProblem;

        EditSSJDETask(String jsonRequest) {
            this.jsonRequest = jsonRequest;
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            // Simulate network access.
            HttpHandler handler = new HttpHandler(getApplicationContext());
            LinkedHashMap<String, Object> parameter = new LinkedHashMap<>();
            parameter.put("_method", "patch");
            parameter.put("json", jsonRequest);


            String loginResult = null;
            try {
                loginResult = handler.makePostCall(parameter, handler.LINK_SSJDE_EDIT+"/"+ssjde.getId());
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
                Toast.makeText(getApplicationContext(), "Permintaan Pembelian Ditambahkan",
                        Toast.LENGTH_SHORT).show();
                finish();

            } else {
            Toast.makeText(getApplicationContext(), "Gagal menambahkan pembelian",
                    Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            addSSJDETask = null;
            showProgress(false);
        }
    }

}
