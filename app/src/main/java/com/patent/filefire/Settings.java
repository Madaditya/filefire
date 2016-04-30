package com.patent.filefire;

/**
 * Created by Mike on 30-Apr-16.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class Settings extends PreferenceActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

    }



    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        EditText sEdit;
        private View positiveAction;
        public static final String public_server_pref = "public_server";
        public static final String file_server_pref = "file_server";


        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);


            Preference ps = (Preference) findPreference("public_server");
            ps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.public_server_details)
                            .titleColorRes(R.color.md_orange_800)
                            .customView(R.layout.public_server_dialogue, true)
                            .positiveText(R.string.savedetails)
                            .neutralText(R.string.register)
                            .negativeText(R.string.negative_button)
                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xshellz.com/signup"));
                                    startActivity(browserIntent);
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sEdit = (EditText) dialog.getCustomView().findViewById(R.id.psftpuser);
                                    String sftpUserStr = sEdit.getText().toString();
                                    sEdit = (EditText) dialog.getCustomView().findViewById(R.id.psftpip);
                                    String sftpIpStr = sEdit.getText().toString();
                                    sEdit = (EditText) dialog.getCustomView().findViewById(R.id.psftpport);
                                    String sftpPort = sEdit.getText().toString();
                                    int sftpPortStr = Integer.parseInt(sftpPort);
                                    sEdit = (EditText) dialog.getCustomView().findViewById(R.id.psftppass);
                                    String sftpPassStr = sEdit.getText().toString();
                                    showtoast("Username: " + sftpUserStr + " | IP: " + sftpIpStr + " | Port: " + sftpPortStr + " | Pass: " + sftpPassStr);

                                    SharedPreferences.Editor editor = getActivity().getApplicationContext().getSharedPreferences(public_server_pref, Context.MODE_PRIVATE).edit();
                                    editor.putString("ps_uname", sftpUserStr);
                                    editor.putString("ps_ip", sftpIpStr);
                                    editor.putInt("ps_port", sftpPortStr);
                                    editor.putString("ps_pass", sftpPassStr);
                                    editor.apply();
                                    showtoast("Details Saved");

                                }
                            })
                            .build();

                    positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                    //noinspection ConstantConditions
                    sEdit = (EditText) dialog.getCustomView().findViewById(R.id.psftppass);
                    sEdit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            positiveAction.setEnabled(s.toString().trim().length() > 0);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                    CheckBox checkbox = (CheckBox) dialog.getCustomView().findViewById(R.id.p_savedetails);
                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            //TODO
                            sEdit.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                            sEdit.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
                        }
                    });

                    SharedPreferences prefs = getActivity().getSharedPreferences(Settings.SettingsFragment.public_server_pref, MODE_PRIVATE);
                    if(prefs != null) {
                        final String ps_uname = prefs.getString("ps_uname", "fs-Madaditya");//"No name defined" is the default value.
                        final String ps_ip = prefs.getString("ps_ip", "maniac.freeshells.org"); //0 is the default value.
                        final int ps_port = prefs.getInt("ps_port", 22);
                        final String ps_pass = prefs.getString("ps_pass", "test123!");
                        EditText aEdit = (EditText) dialog.getCustomView().findViewById(R.id.psftpuser);
                        aEdit.setText(ps_uname);
                        EditText bEdit = (EditText) dialog.getCustomView().findViewById(R.id.psftpip);
                        bEdit.setText(ps_ip);
                        EditText cEdit = (EditText) dialog.getCustomView().findViewById(R.id.psftpport);
                        cEdit.setText(String.valueOf(ps_port));
                        EditText dEdit = (EditText) dialog.getCustomView().findViewById(R.id.psftppass);
                        dEdit.setText(ps_pass);

                    }


                    dialog.show();
                    positiveAction.setEnabled(false);


                    return false;
                }
            });

            Preference host = findPreference("host_files");
            host.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    //TODO
                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.file_server)
                            .titleColorRes(R.color.md_orange_800)
                            .customView(R.layout.fileserver_uname_dialogue, true)
                            .positiveText(R.string.savedetails)
                            .negativeText(R.string.negative_button)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    // TODO
                                    sEdit = (EditText) dialog.getCustomView().findViewById(R.id.fs_unametext);
                                    String fs_uname = sEdit.getText().toString();
                                    sEdit = (EditText) dialog.getCustomView().findViewById(R.id.fs_passtext);
                                    String fs_pass = sEdit.getText().toString();


                                    SharedPreferences.Editor editor = getActivity().getApplicationContext().getSharedPreferences(file_server_pref, Context.MODE_PRIVATE).edit();
                                    editor.putString("fs_uname", fs_uname);
                                    editor.putString("fs_pass", fs_pass);
                                    editor.apply();
                                    showtoast("Details Saved");

                                }
                            })
                            .build();

                    positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                    //noinspection ConstantConditions
                    sEdit = (EditText) dialog.getCustomView().findViewById(R.id.fs_passtext);
                    sEdit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            positiveAction.setEnabled(s.toString().trim().length() > 0);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                    CheckBox checkbox = (CheckBox) dialog.getCustomView().findViewById(R.id.fs_savedetails);
                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            //TODO
                            sEdit.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                            sEdit.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
                        }
                    });

                    SharedPreferences prefs = getActivity().getSharedPreferences(SettingsFragment.file_server_pref, MODE_PRIVATE);
                    if(prefs != null) {
                        final String fs_uname = prefs.getString("fs_uname", "moto");
                        final String fs_pass = prefs.getString("fs_pass", "test");
                        EditText aEdit = (EditText) dialog.getCustomView().findViewById(R.id.fs_unametext);
                        aEdit.setText(fs_uname);
                        EditText dEdit = (EditText) dialog.getCustomView().findViewById(R.id.fs_passtext);
                        dEdit.setText(fs_pass);

                    }


                    dialog.show();
                    positiveAction.setEnabled(false);
                    return false;
                }
            });



        }


        /*
        @Override
        public boolean onPreferenceClick(Preference preference){
            return false;
        }
        */


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("public_server")) {
                showtoast("public_server clicked");

            }
            if(key.equals("host_files")){
                showtoast("HOst Files");
            }

            if(key.equals("onboot")){
                showtoast("On boot clicked.");
            }

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        public void showtoast(final String toast)
        {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
                }
            });

        }

    }




}