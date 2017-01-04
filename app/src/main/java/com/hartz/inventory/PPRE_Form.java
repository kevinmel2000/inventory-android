package com.hartz.inventory;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.hartz.inventory.model.Mrmart;
import com.hartz.inventory.model.Satuan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_ppre__form);

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
    }

}
