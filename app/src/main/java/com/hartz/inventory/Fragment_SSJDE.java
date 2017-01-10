package com.hartz.inventory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hartz.inventory.model.SSJDE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Webmaster on 1/9/2017.
 */

public class Fragment_SSJDE extends Fragment {
    private View fragmentSSJDEView, mProgressView;
    //generate list
    ArrayList<SSJDE> list;
    String loginResult = null;
    Fragment_SSJDE.LoadSSJDETask ssjdeLoadTask;
    Fragment_SSJDE.DeleteSSJDETask ssjdeDeleteTask;
    ListView lView;
    Fragment_SSJDE.SSJDEAdapter adapter;


    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_ssjde, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //handle listview and assign adapter
        lView = (ListView)view.findViewById(R.id.fragment_ssjde_listview);
        fragmentSSJDEView = (View)view.findViewById(R.id.fragment_ssjde_layout);
        mProgressView = (View)view.findViewById(R.id.fragment_ssjde_process);
    }

    @Override
    public void onResume() {
        showProgress(true);
        ssjdeLoadTask = new Fragment_SSJDE.LoadSSJDETask(
                SharedPrefsHelper.readPrefs(SharedPrefsHelper.NAME_PREFS,
                getActivity().getApplicationContext()));
        ssjdeLoadTask.execute((Void)null);

        super.onResume();
    }

    private void loadSSJDEList() {
        list = SSJDE.getFromJSON(loginResult);
        //instantiate custom adapter
        adapter = new Fragment_SSJDE.SSJDEAdapter(list, getActivity().getApplicationContext());
        lView.setAdapter(adapter);
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

            fragmentSSJDEView.setVisibility(show ? View.GONE : View.VISIBLE);
            fragmentSSJDEView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fragmentSSJDEView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            fragmentSSJDEView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoadSSJDETask extends AsyncTask<Void, Void, Boolean> {

        private String username;
        private boolean connectionProblem;

        LoadSSJDETask(String username) {
            this.username = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulate network access.
            HttpHandler handler = new HttpHandler(getActivity().getApplicationContext());

            try {
                loginResult = handler.makeGetCall(handler.LINK_SSJDE_BY_USER+username);
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
                loadSSJDEList();
            } else {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        }

        @Override
        protected void onCancelled() {
            ssjdeLoadTask = null;
            showProgress(false);
        }
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class DeleteSSJDETask extends AsyncTask<Void, Void, Boolean> {

        private String id;
        private int position;
        private boolean connectionProblem;

        DeleteSSJDETask(String id, int position) {
            this.id = id;this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulate network access.
            //access the network
            HttpHandler handler = new HttpHandler(getActivity());
            LinkedHashMap<String, Object> parameter = new LinkedHashMap<>();
            parameter.put("token", SharedPrefsHelper.readPrefs(SharedPrefsHelper.TOKEN_PREFS, getActivity().getApplicationContext()));
            parameter.put("_method", "delete");
            Log.v("token = ", SharedPrefsHelper.readPrefs(SharedPrefsHelper.TOKEN_PREFS, getActivity().getApplicationContext()));
            String loginResult = null;
            try {
                loginResult = handler.makePostCall(parameter, handler.LINK_DELETE_SSJDE+"/"+id);
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
                Toast.makeText(getActivity(), "Data dihapus", Toast.LENGTH_SHORT).show();
                list.remove(position);
                adapter.notifyDataSetChanged();
            }
            //TODO jika gagal
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
            ssjdeLoadTask = null;
            showProgress(false);
        }
    }

    class SSJDEAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<SSJDE> list = new ArrayList<SSJDE>();
        private Context context;



        public SSJDEAdapter(ArrayList<SSJDE> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        public String getItemIdString(int pos){
            return list.get(pos).getId();
        }

        @Override
        public long getItemId(int pos) {
            return 0;
            //just return 0 if your list items do not have an Id variable.
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_ssjde_item, null);
            }

            final SSJDE ssjde = list.get(position);
            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_ssjde_item_id);
            listItemText.setText(ssjde.getId());

            TextView listItemDesc = (TextView)view.findViewById(R.id.list_ssjde_item_items);
            listItemDesc.setText(ssjde.itemListToString());

            TextView listItemCust = (TextView)view.findViewById(R.id.list_ssjde_item_customer);
            listItemCust.setText("Cust: "+ssjde.getCustomer().getName());
            //Handle buttons and add onClickListeners
            ImageButton deleteBtn = (ImageButton)view.findViewById(R.id.list_ssjde_item_delete);
            ImageButton editBtn = (ImageButton)view.findViewById(R.id.list_ssjde_item_edit);


            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //TODO delete and edit button

                    /** convert builder to dialog */
                    AlertDialog alert = new AlertDialog.Builder(getActivity()).create();

                    /** disable cancel outside touch */
                    alert.setCanceledOnTouchOutside(true);
                    /** disable cancel on press back button */
                    alert.setCancelable(true);
                    alert.setMessage("Yakin mau menghapus?");
                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "Hapus", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            showProgress(true);
                            ssjdeDeleteTask = new Fragment_SSJDE.DeleteSSJDETask(list.get(position).getId(), position);
                            ssjdeDeleteTask.execute((Void)null);
                            dialog.dismiss();

                        }
                    });
                    alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Batal", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });
            editBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    Intent i = new Intent(getActivity(), SSJDE_Form_Edit.class);
                    i.putExtra("ssjdeObject", ssjde);
                    startActivity(i);
                }
            });

            return view;
        }
    }
}
