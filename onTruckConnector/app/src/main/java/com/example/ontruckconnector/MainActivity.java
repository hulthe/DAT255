package com.example.ontruckconnector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new TCPChecker().execute();
    }
}
