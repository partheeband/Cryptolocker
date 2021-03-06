package com.example.cryptolocker.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cryptolocker.Aes256;
import com.example.cryptolocker.Home;
import com.example.cryptolocker.HomeAdapter;
import com.example.cryptolocker.R;
import com.example.cryptolocker.ViewDataActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements HomeAdapter.onNoteListener{

//    private HomeViewModel homeViewModel;
    RecyclerView recyclerView;
    HomeAdapter adapter;
    List<Home> homeList;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference dbHome;

    private ProgressDialog progressDialog;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        textView.setVisibility(View.GONE);
        //textView.setText("This is home fragment");

//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        progressDialog= new ProgressDialog(getContext());
        progressDialog.setMessage(" Decrypting Data \n Please Wait...");
        progressDialog.show();

        recyclerView=(RecyclerView)root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        homeList=new ArrayList<>();

        adapter =new HomeAdapter(getActivity(),homeList,this);
        recyclerView.setAdapter(adapter);

        firebaseAuth= FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        dbHome = FirebaseDatabase.getInstance().getReference("Home").child(user.getUid());
        dbHome.addListenerForSingleValueEvent(valueEventListener);


        return root;
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            homeList.clear();
            if (!dataSnapshot.exists()) {
                progressDialog.dismiss();
            }
            else {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Home home = snapshot.getValue(Home.class);

                    //decrypting here
                    home.decryptHome(user.getUid());
                    homeList.add(home);
                    //Toast.makeText(getActivity(), String.valueOf(homeList), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public void onNoteClick(int position, String title, String subTitle1, String subTitle2) {
        Home home=homeList.get(position);

        //Toast.makeText(getActivity(), "CardView Position: "+String.valueOf(position)+" Title:"+home.getTitle(), Toast.LENGTH_SHORT).show();
        //Log.d("cardviewPosition", String.valueOf(position));

        Intent i=new Intent(getActivity(), ViewDataActivity.class);
        i.putExtra("title",title);
        i.putExtra("subtitle1",subTitle1);
        i.putExtra("subtitle2",subTitle2);
        i.putExtra("category",home.getCategory());

        startActivity(i);

    }
}