package com.example.saumya.sharegram.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saumya.sharegram.R;

public class ConfirmPasswordPopup extends DialogFragment {
    private static final String TAG = "ConfirmPasswordPopup";

    public interface OnConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }
    OnConfirmPasswordListener mOnConfirmPasswordListener;

    TextView mPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_confirm_password, container, false);
        mPassword = (TextView) view.findViewById(R.id.confirm_password);

        Log.d(TAG, "onCreateView: started.");


        TextView confirmPopUp = (TextView) view.findViewById(R.id.popUpConfirm);
        confirmPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: captured password and confirming.");

                String password = mPassword.getText().toString();
                if(!password.equals("")){
                    mOnConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                }else{
                    Toast.makeText(getActivity(), "you must enter a password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        TextView cancelPopUp = (TextView) view.findViewById(R.id.popUpCancel);
        cancelPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog");
                getDialog().dismiss();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }
}
