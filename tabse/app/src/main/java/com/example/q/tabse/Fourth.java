package com.example.q.tabse;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ButtonBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Fourth extends Fragment {

    public View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static boolean isParsable(String input) {
        boolean parsable = true;
        try {
            Integer.parseInt(input);
        } catch (Exception e) {
            parsable = false;
        }
        return parsable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fourth, null);
        Button button1 = view.findViewById(R.id.addButton);
        Button button2 = view.findViewById(R.id.subBotton);
        Button button3 = view.findViewById(R.id.multiplyButton);
        Button button4 = view.findViewById(R.id.divideButton);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText number1 = (EditText) view.findViewById(R.id.number1);
                EditText number2 = (EditText) view.findViewById(R.id.number2);
                TextView result = (TextView) view.findViewById(R.id.result);
                if (!isParsable(number1.getText().toString()) || !isParsable(number2.getText().toString())) {
                    Toast.makeText(view.getContext(), "정수를 입력해주세요.", Toast.LENGTH_LONG).show();
                    result.setText("Error");
                    return;
                }
                int n1 = Integer.parseInt(number1.getText().toString());
                int n2 = Integer.parseInt(number2.getText().toString());
                result.setText(Integer.toString(n1 + n2));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText number1 = (EditText) view.findViewById(R.id.number1);
                EditText number2 = (EditText) view.findViewById(R.id.number2);
                TextView result = (TextView) view.findViewById(R.id.result);
                if (!isParsable(number1.getText().toString()) || !isParsable(number2.getText().toString())) {
                    Toast.makeText(view.getContext(), "정수를 입력해주세요.", Toast.LENGTH_LONG).show();
                    result.setText("Error");
                    return;
                }
                int n1 = Integer.parseInt(number1.getText().toString());
                int n2 = Integer.parseInt(number2.getText().toString());
                result.setText(Integer.toString(n1 - n2));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {        EditText number1 = (EditText) view.findViewById(R.id.number1);
                EditText number2 = (EditText) view.findViewById(R.id.number2);
                TextView result = (TextView) view.findViewById(R.id.result);
                if (!isParsable(number1.getText().toString()) || !isParsable(number2.getText().toString())) {
                    Toast.makeText(view.getContext(), "정수를 입력해주세요.", Toast.LENGTH_LONG).show();
                    result.setText("Error");
                    return;
                }
                int n1 = Integer.parseInt(number1.getText().toString());
                int n2 = Integer.parseInt(number2.getText().toString());
                result.setText(Integer.toString(n1 * n2));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText number1 = (EditText) view.findViewById(R.id.number1);
                EditText number2 = (EditText) view.findViewById(R.id.number2);
                TextView result = (TextView) view.findViewById(R.id.result);
                if (!isParsable(number1.getText().toString()) || !isParsable(number2.getText().toString())) {
                    Toast.makeText(view.getContext(), "정수를 입력해주세요.", Toast.LENGTH_LONG).show();
                    result.setText("Error");
                    return;
                }
                if (Integer.parseInt(number2.getText().toString()) == 0) {
                    Toast.makeText(view.getContext(), "0으로 나눌 수 없습니다.", Toast.LENGTH_LONG).show();
                    result.setText("Error");
                    return;
                }
                int n1 = Integer.parseInt(number1.getText().toString());
                int n2 = Integer.parseInt(number2.getText().toString());
                result.setText(Integer.toString(n1 / n2) + "   나머지 = " + Integer.toString(n1 % n2));

            }
        });


        return view;
    }

}
