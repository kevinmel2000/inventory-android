package com.hartz.inventory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.hartz.inventory.model.PPRE;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Handler;

/**
 * Created by Webmaster on 1/5/2017.
 */

public class Fragment_PPRE extends Fragment {
private View fragmentPPREView, mProgressView;
    //generate list
    ArrayList<PPRE> list;
    String loginResult = null;
    LoadPPRETask ppreTask;
    ListView lView;


    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_ppre, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //handle listview and assign adapter
        lView = (ListView)view.findViewById(R.id.fragment_ppre_listview);
        fragmentPPREView = (View)view.findViewById(R.id.fragment_ppre_layout);
        mProgressView = (View)view.findViewById(R.id.fragment_ppre_process);
        showProgress(true);
        ppreTask = new LoadPPRETask(SharedPrefsHelper.readPrefs(SharedPrefsHelper.NAME_PREFS,
                getActivity().getApplicationContext()));
        ppreTask.execute((Void)null);
    }


    private void loadPPREList() {
        list = PPRE.getFromJSON(loginResult);
        //instantiate custom adapter
        PPREAdapter adapter = new PPREAdapter(list, getActivity().getApplicationContext());
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

            fragmentPPREView.setVisibility(show ? View.GONE : View.VISIBLE);
            fragmentPPREView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fragmentPPREView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            fragmentPPREView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoadPPRETask extends AsyncTask<Void, Void, Boolean> {

        private String username;
        private boolean connectionProblem;

        LoadPPRETask(String username) {
            this.username = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulate network access.
            HttpHandler handler = new HttpHandler(getActivity().getApplicationContext());

            try {
                loginResult = handler.makeGetCall(handler.LINK_PPRE_BY_USER+username);
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
               loadPPREList();
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
            ppreTask = null;
            showProgress(false);
        }
    }

    class PPREAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<PPRE> list = new ArrayList<PPRE>();
        private Context context;



        public PPREAdapter(ArrayList<PPRE> list, Context context) {
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
                view = inflater.inflate(R.layout.list_ppre_item, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_ppre_item_id);
            listItemText.setText(list.get(position).getId());

            TextView listItemDesc = (TextView)view.findViewById(R.id.list_ppre_item_items);
            listItemDesc.setText(list.get(position).itemListToString());

            //Handle buttons and add onClickListeners
            ImageButton deleteBtn = (ImageButton)view.findViewById(R.id.list_ppre_item_delete);
            ImageButton editBtn = (ImageButton)view.findViewById(R.id.list_ppre_item_edit);

            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //TODO delete and edit button
                    list.remove(position); //or some other task
                    notifyDataSetChanged();
                }
            });
            editBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    notifyDataSetChanged();
                }
            });

            return view;
        }
    }
}



