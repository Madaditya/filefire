package com.patent.filefire;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.processbutton.iml.ActionProcessButton;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.nononsenseapps.filepicker.FilePickerActivity;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthPassword;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    EditText mEdit,sEdit;
    String dMessage="Sample Message",dTitle="Dialogue Title",dPositiveText="Okay",dNegativeText="Cancel";
    public static final int PORT = 9;
    private static final int REQUEST_LIST_MULTIPLE = 10;
    public static final String myprefs = "DeviceList" ;
    SharedPreferences sharedpreferences;
    JSch jsch = new JSch();
    Session session = null;
    int FILE_CODE=1;
    public static String global_root,lsSting;
    public static String[] allFiles;
    public String[] filearrayMain;
    private View positiveAction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);


        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();


                if (menuItem.getItemId() == R.id.nav_item_sent) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new SentFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_inbox) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();


    }








    public void sftpconnect(final View v) {
        final ActionProcessButton filexButton = (ActionProcessButton) findViewById(R.id.filexbutton);
        filexButton.setMode(ActionProcessButton.Mode.ENDLESS);
        /*
        sEdit = (EditText) findViewById(R.id.sftpuser);
        String sftpUserStr = sEdit.getText().toString();
        sEdit = (EditText) findViewById(R.id.sftpip);
        String sftpIpStr = sEdit.getText().toString();
        sEdit = (EditText) findViewById(R.id.sftpport);
        String sftpPort = sEdit.getText().toString();
        int sftpPortStr = Integer.parseInt(sftpPort);
        sEdit = (EditText) findViewById(R.id.sftppass);
        String sftpPassStr = sEdit.getText().toString();
        */
       // AsyncTaskRunner runner = new AsyncTaskRunner();
        //runner.execute(sftpUserStr, sftpIpStr, sftpPort, sftpPassStr);
        showtoast("Returned to method");
       // filexButton.setMode(ActionProcessButton.Mode.PROGRESS);
        //filexButton.setProgress(1);



       //Intent intent = new Intent(getApplicationContext(), ru.bartwell.exfilepicker.ExFilePickerActivity.class);
        //startActivityForResult(intent, EX_FILE_PICKER_RESULT);

        /*
        Intent sftpintent = new Intent(MainActivity.this, SftpOperations.class);
        sftpintent.putExtra("sftpUserStr",sftpUserStr);
        sftpintent.putExtra("sftpTpStr",sftpIpStr);
        sftpintent.putExtra("sftpPortStr",sftpPortStr);
        sftpintent.putExtra("sftpPassStr",sftpPassStr);
        */




        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    sEdit = (EditText) findViewById(R.id.sftpuser);
                    String sftpUserStr = sEdit.getText().toString();
                    sEdit = (EditText) findViewById(R.id.sftpip);
                    String sftpIpStr = sEdit.getText().toString();
                    sEdit = (EditText) findViewById(R.id.sftpport);
                    String sftpPort = sEdit.getText().toString();
                    int sftpPortStr = Integer.parseInt(sftpPort);
                    sEdit = (EditText) findViewById(R.id.sftppass);
                    String sftpPassStr = sEdit.getText().toString();


                    Session session = null;
                    Channel channel = null;
                    ChannelSftp channelSftp = null;
                    boolean success = false;
                    JSch jsch = new JSch();
                    session = jsch.getSession(sftpUserStr, sftpIpStr, sftpPortStr);
                    session.setPassword(sftpPassStr);

                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();


                    channel = session.openChannel("sftp");
                    channel.connect();
                    //filexButton.setProgress(1);
                    channelSftp = (ChannelSftp) channel;
                    showtoast("Connection to \"" + sftpUserStr + "\" Done");
                   // dMessage="User:" + sftpUserStr + "IP" + sftpIpStr + "Port" + sftpPort;
                    //SimpleDialogFragment.createBuilder(MainActivity.this, getSupportFragmentManager()).setTitle(dTitle).setMessage(dMessage).show();

                    String fileToDownload = "/home/addy/Desktop/sftptest.txt";
                    channelSftp.cd("/home/addy/Desktop/");
                    showtoast("Present Remote Directory : " + channelSftp.pwd());
                    channelSftp.lcd("/storage/emulated/0/");
                    showtoast("Present Local Directory :" + channelSftp.lpwd());

                    channelSftp.get(fileToDownload);
                    // Explorer Start
                    //Vector<String> filelistv = new Vector<String>();
                    Vector filelistv = channelSftp.ls(channelSftp.pwd());
                    filearrayMain = new String[filelistv.size()];

                    for (int i=0; i < filelistv.size(); i++) {
                        filearrayMain[i] = filelistv.get(i).toString();
                    }

                    //filearrayMain = filelistv.toArray(new String[filelistv.size()]);

                    for(int i=0; i<filelistv.size();i++)
                    {
                        ChannelSftp.LsEntry lsEntryObj = (ChannelSftp.LsEntry) filelistv.get(i);
                        //showtoast(lsEntryObj.getFilename());
                        filearrayMain[i] = lsEntryObj.getFilename();
                    }
                    showtoast("Array element 1 :" + filearrayMain[1]);


                    int REQUEST_LIST_SIMPLE = filearrayMain.length;
                    new MaterialDialog.Builder(MainActivity.this)
                            .title("Directories")
                            .items(filearrayMain)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                }
                            })
                            .show();

                    // Explorer End

                    success = true;
                    if (success)
                        showtoast("Downloaded file: " + fileToDownload + " To " + channelSftp.lpwd());

                } catch (JSchException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    showtoast("Error" + e);
                    System.out.println("Error" + e);
                    //dMessage= "Error" + e;
                } catch (Exception e) {
                    showtoast("Error" + e);
                    System.out.println("Error" + e);

                }

            }
        });


        thread.start();



    }
    public void DialogueTest(View v){
        //Simple Dialogue BOx
        // SimpleDialogFragment.createBuilder(this, getSupportFragmentManager()).setMessage(R.string.dialoguemessage).show();

        //Dialogue with POsitive and negative
        //SimpleDialogFragment.createBuilder(this, getSupportFragmentManager()).setTitle(dTitle).setMessage(dMessage).setPositiveButtonText(dPositiveText).setNegativeButtonText(dNegativeText).show();
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Test")
                .content("Whatever You want")
                .positiveText("agree")
                .show();

    }

//LIst Dialogues
        public void onListClick(View v) {
           /*
            ListDialogFragment
                    .createBuilder(this, getSupportFragmentManager())
                    .setTitle("Your favorite character:")
                    .setItems(new String[]{"Jayne", "Malcolm", "Kaylee",
                            "Wash", "Zoe", "River", "HI", "Hello", "more", "more 1", "more2", "more2", "more2", "more2",})
                    .setRequestCode(REQUEST_LIST_MULTIPLE)
                    .setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE)
                    .setCheckedItems(new int[]{1, 3})
                    .show();
                    */

        }

    public String PortScan(View v)
    {   WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        showDialogue("Network details", "Your IP : " + ip, "Okay", "");

        String ipA=Utils.getIPAddress(true);
        showtoast(ipA);
        return ipA;
        /*
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //Your code goes here
                    int timeout=1000;
                    for (int i=1;i<255;i++){
                        String host=subnet + "." + i;
                        if (InetAddress.getByName(host).isReachable(timeout)){
                            System.out.println(host + " is reachable");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        */
    }
    public void ShowConnectDialogue(View v){
        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.ConnectTitle)
                .customView(R.layout.connect_dialogue, wrapInScrollView)
                .positiveText(R.string.connect)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                        sEdit = (EditText) dialog.getCustomView().findViewById(R.id.dsftpuser);
                        String sftpUserStr = sEdit.getText().toString();
                        sEdit = (EditText) dialog.getCustomView().findViewById(R.id.dsftpip);
                        String sftpIpStr = sEdit.getText().toString();
                        sEdit = (EditText) dialog.getCustomView().findViewById(R.id.dsftpport);
                        String sftpPort = sEdit.getText().toString();
                        int sftpPortStr = Integer.parseInt(sftpPort);
                        sEdit = (EditText) dialog.getCustomView().findViewById(R.id.dsftppass);
                        String sftpPassStr = sEdit.getText().toString();
                        showtoast("Username: " + sftpUserStr + " | IP: " + sftpIpStr + " | Port: " + sftpPortStr + " | Pass: " + sftpPassStr);
                        Intent intent = new Intent(MainActivity.this, FileExplorer.class);
                        intent.putExtra("SftpUser", sftpUserStr);
                        intent.putExtra("SftpIp", sftpIpStr);
                        intent.putExtra("SftpPort", sftpPortStr);
                        intent.putExtra("SftpPass", sftpPassStr);
                        startActivity(intent);

                    }
                })
                .build();
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        //noinspection ConstantConditions
        sEdit = (EditText) dialog.getCustomView().findViewById(R.id.dsftppass);
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

        CheckBox checkbox = (CheckBox) dialog.getCustomView().findViewById(R.id.savedetails);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO
                sEdit.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                sEdit.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
            }
        });

        dialog.show();
        positiveAction.setEnabled(false);


        }

    public void gotoppr(View v) {
       /* Uri uri = Uri.parse("http://www.google.com"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    */
        /*if (args.length != 2) {
            System.out.println("Usage: java WakeOnLan <broadcast-ip> <mac-address>");
            System.out.println("Example: java WakeOnLan 192.168.0.255 00:0D:61:08:22:4A");
            System.out.println("Example: java WakeOnLan 192.168.0.255 00-0D-61-08-22-4A");
            System.exit(1);
        }*/



        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
        try {
            mEdit = (EditText) findViewById(R.id.ipaddress);
            String ipStr = mEdit.getText().toString();
            String ipAdd = "IP :" + ipStr;
            showtoast(ipAdd);
            mEdit = (EditText) findViewById(R.id.macaddress);
            String macStr = mEdit.getText().toString();
            //System.out.println(macStr);
            String macAdd = "MAC :" + macStr;
            showtoast(macAdd);
            mEdit = (EditText) findViewById(R.id.devicename);

            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

            showtoast("Wake-on-LAN packet sent.");
            System.out.println("Wake-on-LAN packet sent.");

            sharedpreferences = getSharedPreferences(myprefs, Context.MODE_APPEND);
            SharedPreferences.Editor editor = getPreferences(MODE_APPEND).edit();
            editor.putString("devicekey", mEdit.getText().toString());
            editor.putString("ipkey", ipStr);
            editor.putString("mackey", macStr);
            editor.apply();

        } catch (Exception e) {
            showtoast("Failed to send Wake-on-LAN packet:" + e);

            System.out.println("Failed to send Wake-on-LAN packet:" + e);
            //System.exit(1);

        }

    }
});

        thread.start();

    }
    public static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");


        }
        return bytes;

    }

    public void showtoast(final String toast)
    {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showDialogue(String dTitle, String dMessage, String dPositiveText, String dNegativeText){

        //SimpleDialogFragment.createBuilder(this, getSupportFragmentManager()).setTitle(dTitle).setMessage(dMessage).setPositiveButtonText(dPositiveText).setNegativeButtonText(dNegativeText).show();
        new MaterialDialog.Builder(this)
                .title(dTitle)
                .content(dMessage)
                .positiveText(dPositiveText)
                .negativeText(dNegativeText)
                .show();
    }

    public void FilePicker(View v){
        // This always works
        Intent i = new Intent(MainActivity.this, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE);
    }

    public void FileExplorer(View v){
        sEdit = (EditText) findViewById(R.id.ipToExplorer);
        String ipToExplorer = sEdit.getText().toString();
        Intent intent2 = new Intent(this, FileExplorer.class);
        intent2.putExtra("ChangableIp", ipToExplorer);
        startActivity(intent2);
    }


    public void setupSftpServer(View v){
        final int serverPort=58060;


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    showtoast("Starting Server...");
                    final SshServer sshd = SshServer.setUpDefaultServer();
                    sshd.setPort(serverPort);
                    //sshd.setHost("0.0.0.0");
                    showtoast("Step 1");
                    sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));

                    /*
                    List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
                    userAuthFactories.add(new UserAuthNone.Factory());
                    sshd.setUserAuthFactories(userAuthFactories);
                    */

                    List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
                    userAuthFactories.add(new UserAuthPassword.Factory());
                    sshd.setUserAuthFactories(userAuthFactories);
                    //sshd.setFileSystemFactory(new );


                    sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
                        public boolean authenticate(String username, String password, ServerSession session) {
                            return "moto".equals(username) && "test".equals(password);

                        }
                    });

                    sshd.setCommandFactory(new ScpCommandFactory());
                    final String dir = Environment.getExternalStorageDirectory().getAbsolutePath();



                    List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
                    namedFactoryList.add(new SftpSubsystem.Factory());
                    sshd.setSubsystemFactories(namedFactoryList);

                    showtoast("Path : " + dir);
                    sshd.setFileSystemFactory(new VirtualFileSystemFactory(dir));



                            showtoast("Step 2");
                        sshd.start();
                    String serHost = sshd.getHost();
                        showtoast("Server Started on port :" + serverPort);



                        String SerIp = Utils.getIPAddress(true);
                    PugNotification.with(MainActivity.this)
                            .load()
                            .title("Server Started")
                            .message(SerIp + ":" + serverPort)
                            .smallIcon(R.mipmap.ic_launcher)
                            .largeIcon(R.mipmap.ic_launcher)
                            .flags(Notification.DEFAULT_ALL)
                            .simple()
                            .build();
                    Button b=(Button)findViewById(R.id.stopServer);
                    b.setVisibility(View.VISIBLE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                sshd.stop();
                                showtoast("Server Stopped.");
                            } catch (InterruptedException e) {
                                showtoast("Interrrupt Exception : " + e);

                            }

                        }
                    });

                }
                catch(Exception e){
                    showtoast("Error" + e);
                    System.out.println("Error" + e);
                }
            }
        });


        thread.start();
        try{
            thread.join();
        }catch(Exception e){
            showtoast("Error " + e);
        }






}


    //start Async
    /*
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... params) {
            String sftpUserStr=params[0];
            String sftpIpStr=params[1];
            int sftpPortStr = Integer.parseInt(params[2]);
            String sftpPassStr=params[3];
            String connectiondone="0";


            try {
                // Do your long operations here and return the result
                Session session = null;
                Channel channel = null;
                ChannelSftp channelSftp = null;
                JSch jsch = new JSch();
                session = jsch.getSession(sftpUserStr, sftpIpStr, sftpPortStr);
                session.setPassword(sftpPassStr);

                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();


                channel = session.openChannel("sftp");
                channel.connect();
                //filexButton.setProgress(1);
                channelSftp = (ChannelSftp) channel;

                    connectiondone = "1";

                showtoast("Connection to \"" + sftpUserStr + "\" Done");

                // dMessage="User:" + sftpUserStr + "IP" + sftpIpStr + "Port" + sftpPort;
                //SimpleDialogFragment.createBuilder(MainActivity.this, getSupportFragmentManager()).setTitle(dTitle).setMessage(dMessage).show();

                String fileToDownload = "/home/addy/Desktop/sftptest.txt";
                //channelSftp.cd("/home/addy/Desktop/");
                showtoast("Present Remote Directory : " + channelSftp.pwd());
                channelSftp.lcd("/storage/emulated/0/");
                showtoast("Present Local Directory :" + channelSftp.lpwd());

                channelSftp.get(fileToDownload);
                // Explorer Start
                //Vector<String> filelistv = new Vector<String>();
                String workDir = channelSftp.pwd();
                Vector filelistv = channelSftp.ls(workDir);
                filearrayMain = new String[filelistv.size()];

                for (int i=0; i < filelistv.size(); i++) {
                    filearrayMain[i] = filelistv.get(i).toString();
                }

                //filearrayMain = filelistv.toArray(new String[filelistv.size()]);

                for(int i=0; i<filelistv.size();i++)
                {
                    ChannelSftp.LsEntry lsEntryObj = (ChannelSftp.LsEntry) filelistv.get(i);
                    //showtoast(lsEntryObj.getFilename());
                    filearrayMain[i] = lsEntryObj.getFilename();
                }
                showtoast("Array element 1 :" + filearrayMain[1]);


                int REQUEST_LIST_SIMPLE = filearrayMain.length;

                ListDialogFragment
                        .createBuilder(MainActivity.this, getSupportFragmentManager())
                        .setTitle("Directories:")
                        .setItems(filearrayMain)
                        .setRequestCode(REQUEST_LIST_SIMPLE)
                        .show();




            } catch (SftpException e) {
                showtoast("Error : " + e);

            }catch (JSchException e){
                showtoast("Error : " + e);
            }
            return connectiondone;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation

            return;

        }


         //* (non-Javadoc)
         //*
         //* @see android.os.AsyncTask#onPreExecute()

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }


        // * (non-Javadoc)
         //*
         //* @see android.os.AsyncTask#onProgressUpdate(Progress[])

        @Override
        protected void onProgressUpdate(String... text) {

            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }
    */

}


