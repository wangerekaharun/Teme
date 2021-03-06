package com.kate.teme;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SelectDriverFragment extends Fragment {
    ListView driversList;
    ArrayList<OnlineData> driversArray;
    ArrayList<Driver> driversArrayTwo;
    ArrayList<String> dDereva,dCar;
    SharedPreferences mTemeprefferences;
    Firebase myFirebaseRef;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View fWhatsapp= inflater.inflate(R.layout.fragment_select_driver, container, false);
        mTemeprefferences=getActivity().getSharedPreferences(Constants.TEME_PREFERENCES, Context.MODE_PRIVATE);
        driversList=(ListView)fWhatsapp.findViewById(R.id.listDriverstwo);
        driversArray=new ArrayList<OnlineData>();
        driversArrayTwo=new ArrayList<Driver>();
        dDereva=new ArrayList<String>();
        dCar=new ArrayList<String>();
        Firebase.setAndroidContext(getActivity().getApplicationContext());

        if( mTemeprefferences.getString(Constants.TEME_ADMIN_RATE,null)!=null
                & mTemeprefferences.getString(Constants.TEME_CURRENT_DISTANCE,null) !=null){
            String rate=mTemeprefferences.getString(Constants.TEME_ADMIN_RATE,null);
        String distance=mTemeprefferences.getString(Constants.TEME_CURRENT_DISTANCE,null);
        int iRate=Integer.valueOf(rate);
        int idstance=Integer.valueOf(distance);
        int ipay=iRate *idstance;
        String  toPay=String.valueOf(ipay);

        SharedPreferences.Editor editor2 = mTemeprefferences.edit();
        editor2.putString(Constants.TEME_CURRENT_FARE,toPay);
        editor2.commit();
            TextView er=(TextView)fWhatsapp.findViewById(R.id.showPayFare);
            er.setText("Your Fare is: "+toPay);
        }



        myFirebaseRef = new Firebase("https://teme.firebaseio.com/");


        Query queryRef = myFirebaseRef.child("DriversList").orderByChild("available").equalTo("Yes");
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                Map<String, Object> newPost = (Map<String, Object>) snapshot.getValue();
                if (newPost != null) {

                    driversArray.add(new OnlineData(new Driver(newPost.get("drivername").toString(), newPost.get("carname").toString())
                            , snapshot.getKey()));
                    driversArrayTwo.add(new Driver(newPost.get("drivername").toString(), newPost.get("carname").toString()));
                    if(driversArrayTwo !=null){
                        DriverAdapter adapters = new DriverAdapter(getActivity().getApplicationContext(),
                                R.layout.list_item,driversArrayTwo);
                        driversList.setAdapter(adapters);

                    }




                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        driversList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Map<String, String> post1 = new HashMap<String, String>();
                post1.put("drivername",driversArray.get(position).getDriver().DriverName);
                post1.put("carname",driversArray.get(position).getDriver().CarDriven);
                String flocation=mTemeprefferences.getString(Constants.TEME_CURRENT_LOCATION,null);
                String fdestination=mTemeprefferences.getString(Constants.TEME_CURRENT_DESTINATION,null);
                post1.put("fare_destination",fdestination);
                post1.put("fare_location",flocation);
                post1.put("available","Yes");
                Map<String, Object> drivers = new HashMap<String, Object>();
                drivers.put(driversArray.get(position).getItemKey(),post1);
                myFirebaseRef.child("DriversList").updateChildren(drivers);


                Toast.makeText(getActivity().getApplicationContext(), "Your Driver is on his way"
                        , Toast.LENGTH_SHORT).show();


                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new WaitDriverFragment())
                        .commit();


            }
        });






        return fWhatsapp;
    }


}
