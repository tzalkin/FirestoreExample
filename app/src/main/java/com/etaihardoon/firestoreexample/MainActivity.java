package com.etaihardoon.firestoreexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, EventListener<QuerySnapshot> {
    private FirebaseFirestore firestore;

    private FloatingActionButton btnAdd;

    private ListView toyListView;
    private ToysAdapter adapter;

    private ArrayList<Toy> toyArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        toyArrayList = new ArrayList<Toy>();

        btnAdd = findViewById(R.id.btnAdd);
        toyListView = findViewById(R.id.listViewToys);
        adapter = new ToysAdapter(this, R.layout.toy_row, toyArrayList);

        toyListView.setAdapter(adapter);

        firestore
                .collection("toys")
                .addSnapshotListener(this);

        firestore.collection("toys")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> docList = task.getResult().getDocuments();
                            toyArrayList.clear();

                            for (DocumentSnapshot doc : docList) {
                                Toy toy = new Toy(
                                        doc.getString("name"),
                                        doc.getDouble("price")
                                );

                                toyArrayList.add(toy);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnAdd) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_toy, null, false);

            Button buttonAdd = dialogView.findViewById(R.id.buttonAdd);
            EditText etToyName = dialogView.findViewById(R.id.etToyName);
            EditText etPrice = dialogView.findViewById(R.id.etPrice);

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = etToyName.getText().toString();
                    Double price = Double.parseDouble(etPrice.getText().toString());

                    if (name.isEmpty() || price == null) {
                        Toast.makeText(MainActivity.this, "Please type both name and price", Toast.LENGTH_SHORT).show();
                    } else {
                        Toy toy = new Toy(name, price);

                        /*
                        Map<String, Object> map = new HashMap();
                        map.put("name", name);
                        map.put("price", price);

                        ...

                        .set(map)...
                         */

                        firestore
                                .collection("toys")
                                .document(System.currentTimeMillis() + "")
                                .set(toy)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Toy was added!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
            });

            builder.setView(dialogView);
            builder.create().show();


        }
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        List<DocumentSnapshot> docList = value.getDocuments();
        toyArrayList.clear();

        for (DocumentSnapshot doc : docList) {
            Toy toy = new Toy(
                    doc.getString("name"),
                    doc.getDouble("price")
            );

            toyArrayList.add(toy);
        }

        adapter.notifyDataSetChanged();
    }
}

