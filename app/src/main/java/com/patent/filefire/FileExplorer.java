package com.patent.filefire;

/**
 * Created by Mike on 22-Jan-16.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class FileExplorer extends ListActivity{

    private List<String> item = null;
    private List<String> path = null;
    private String root;
    private TextView myPath;
    public String dirPath,user,ip,pass;
    public int port;
    public static String[] filearray;
    public String[] filearrayExplorer,filearrayExplorerAbsolute;
    int  filesize;
    Session session = null,session_main=null;
    Channel channel = null;
    ChannelSftp channelSftp = null;
    ChannelSftp.LsEntry lsEntryObj;
    Vector filelistv;
    boolean fromout=false,isremote;
    String localdir;
    int success;
    ImageView fileb,folderb;
    EditText ed;
    String ld;
    String src;
    public long dfilesize;


    int FILE_CODE;



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent i=getIntent();
        user = i.getExtras().getString("SftpUser");
        ip = i.getExtras().getString("SftpIp");
        port = i.getExtras().getInt("SftpPort");
        pass = i.getExtras().getString("SftpPass");
        setContentView(R.layout.file_explorer_image);
        myPath = (TextView)findViewById(R.id.path);
        isremote = i.getBooleanExtra("isRemote", false);
        getPermissions();

        fileb = (ImageView) findViewById(R.id.menu_item1);
        folderb = (ImageView) findViewById(R.id.menu_item2);







       localdir = Environment.getExternalStorageDirectory().getPath();
        ld = Environment.getExternalStorageDirectory().getPath();
        //showtoast(root);
        root="";

            getConnect runner = new getConnect();
            runner.execute();



        //vfsDownload(ip,user,pass,localFilePath,remoteFilePath);

    }


    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title("Confirm Action")
                .titleColorRes(R.color.md_orange_800)
                .content("Are you sure you want to disconnect ?")
                .positiveText(R.string.positive_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        channelSftp.disconnect();
                        session.disconnect();
                        finish();
                        dialog.dismiss();
                    }
                })
                .negativeText(R.string.negative_button)
                .show();

    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        //could replace it with a switch
        if (requestCode == 1){
            Uri uri = data.getData();
            src = uri.getPath();
            showtoast("File at : " + src);

        }
        else{
            showtoast("File not found.");
        }
    }
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            src = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

        }

    }








    public void getDir(final String temproot,final boolean fromout)
    {



                try {


                    if(fromout){
                    channelSftp.cd(temproot);
                    showtoast("Changed Directory to : " + temproot);
                    }
                    dirPath = channelSftp.pwd();
                    showtoast("Connection to Addy Done with Path : " + dirPath);


                    channelSftp.lcd(localdir);
                    localdir = channelSftp.lpwd();
                    //showtoast("Local Dir : " + localdir);
                    //local dir end

                    filelistv = channelSftp.ls(".");
                    filearrayExplorer = new String[filelistv.size()];
                    filearrayExplorerAbsolute = new String[filelistv.size()];

                    /*
                    for (int i=0; i < filelistv.size(); i++) {
                        filearrayMain[i] = filelistv.get(i).toString();
                    }
                    */
                    //filearrayMain = filelistv.toArray(new String[filelistv.size()]);

                    for(int i=0; i<filelistv.size();i++)
                    {
                        lsEntryObj = (ChannelSftp.LsEntry) filelistv.get(i);
                        //showtoast(lsEntryObj.getFilename());
                        filearrayExplorer[i] = lsEntryObj.getFilename();
                       // showtoast(filearrayExplorer[i]);
                        filearrayExplorerAbsolute[i] = dirPath + "/" + filearrayExplorer[i];


                        //showtoast("LOng NAme " + lsEntryObj.getLongname());
                    }
                    filesize= filearrayExplorer.length;

                }
                catch (Exception e) {
                    showtoast("Error" + e);

                }



        //Sftp Code End
        myPath.setText("Location: " + dirPath);
        item = new ArrayList<String>();

        item.addAll(Arrays.asList(filearrayExplorer));
        /*
        ArrayAdapter<String> fileList =
                new ArrayAdapter<String>(this, R.layout.row, item);
        setListAdapter(fileList);
        */
        //showtoast("Checking array : " + filearrayExplorer[3]);
        ArrayAdapter<String> filelistObj = new ArrayAdapter<String>(this, R.layout.mylist_image,R.id.Itemname, filearrayExplorer);
        setListAdapter(filelistObj);
        //Upload Code

        fileb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO not working
                
                String toup = getFileToUpload();

                uploadFile runner = new uploadFile();

                try{
                    runner.execute(toup, channelSftp.pwd());
                }
                catch(SftpException e){
                    showtoast("Upload exception : " + e);
                }





            }
        });

        folderb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SftpcreateDir();
                //
                // getDir(".",true);

            }
        });

        //Upload Code End




        // For Long Click
        final ListView list = getListView();
        list.setDivider(new ColorDrawable(Color.TRANSPARENT));
        list.setDividerHeight(0);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //File file = new File(path.get(position));
                final int itemPosition = position;


                // ListView Clicked item value
                final String itemValue = (String) list.getItemAtPosition(position);

                showtoast("\"" + itemValue + "\" Long Clicked");
                final String fileToDownload = filearrayExplorerAbsolute[itemPosition];
                showtoast("File Abs Path : " + fileToDownload);


                // Test list dialogue


                final String[] items = {"Rename","Delete","Properties"};
                /*
                new MaterialDialog.Builder(FileExplorer.this)
                        .title("Test")
                        .items(items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            }
                        })
                        .positiveText("Download")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                               //TODO
                            }
                        })
                        .show();
                    */

                AlertDialog.Builder build = new AlertDialog.Builder(FileExplorer.this);
                build.setTitle(itemValue);
                build.setIcon(R.drawable.ic_attachment_black_36dp);
                build.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        success = 0;
                        showtoast("Creating Output Stream with itemValue : " + itemValue);
                        //verifyStoragePermissions(FileExplorer.this);
                        //getPermissions();

                        //success=downloadFile(fileToDownload,localdir,itemValue);
                        //TODO AsyncTask try

                        lsEntryObj = (ChannelSftp.LsEntry) filelistv.get(itemPosition);
                        SftpATTRS attrs = lsEntryObj.getAttrs();
                        dfilesize = attrs.getSize();
                        //String Ipo = String.valueOf(dfilesize);
                        downloadFile runner = new downloadFile();
                        runner.execute(fileToDownload, itemValue);


                    }
                });
                build.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(items[which]){
                            case "Rename":

                                renameFile(itemValue);
                                //getDir(".",true);
                                break;
                            case "Delete":
                                deleteSftpFile(itemValue,itemPosition);
                                //getDir(".",true);


                                break;
                            case "Properties":

                                getSftpFileProperties(itemPosition, itemValue);
                                break;
                        }
                    }
                }).create().show();

                //Download Stop
                return true;
            }
        });
        //Long Click End


    }

    public String getFileToUpload(){
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1)
                .withFilter(Pattern.compile(".*")) // Filtering files and directories by file name using regexp
                .withFilterDirectories(true) // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();
        showtoast(src);
        return src;
    }

    public void SftpcreateDir(){
        new MaterialDialog.Builder(this)
                .title(R.string.newdir)
                .titleColorRes(R.color.md_orange_800)
                .customView(R.layout.mkdir_dialogue, false)
                .positiveText(R.string.positive_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ed = (EditText) dialog.getCustomView().findViewById(R.id.mkdir);
                        final String newname = ed.getText().toString();
                        try{
                            channelSftp.mkdir(newname);
                        }
                        catch(SftpException e){
                            showtoast("Cannot create new Directory : " + e);
                        }
                        showtoast("Folder created : " + newname);
                    }
                })
                .negativeText(R.string.negative_button)
                .show();

    }

    public void renameFile(final String oldname){


        new MaterialDialog.Builder(this)
                .title(R.string.rename)
                .titleColorRes(R.color.md_orange_800)
                .customView(R.layout.rename_dialogue, false)
                .positiveText(R.string.positive_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ed = (EditText) dialog.getCustomView().findViewById(R.id.renameMe);
                        final String newname = ed.getText().toString();
                        try{
                            channelSftp.rename(oldname,newname);
                        }
                        catch(SftpException e){
                            showtoast("Rename error : " + e);
                        }
                        showtoast("Renamed to : " + newname);
                    }
                })
                .negativeText(R.string.negative_button)
                .show();
    }

    public void deleteSftpFile(final String filename,final int itemPosition){
        String message = getResources().getString(R.string.confirmDelete);
        message = filename + "\n\n" + message;
        new MaterialDialog.Builder(this)
                .title(R.string.confirmDeleteTitle)
                .titleColorRes(R.color.md_orange_800)
                .content(message)
                .positiveText(R.string.delete_yes)
                .iconRes(R.drawable.ic_cloud_download_black_36dp)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try{
                            lsEntryObj = (ChannelSftp.LsEntry) filelistv.get(itemPosition);
                            if(lsEntryObj.getAttrs().isDir()){
                               channelSftp.rmdir(filename);
                            }else{
                                channelSftp.rm(filename);
                            }
                        }
                        catch(SftpException e){
                            showtoast("Delete error : " + e);
                        }
                        showtoast(filename + " : Deleted Successfully");

                    }
                })
                .negativeText(R.string.negative_button)
                .show();
    }

    //Show File Properties
    public void getSftpFileProperties(int itemPosition, String itemValue){
        //TODO
        lsEntryObj = (ChannelSftp.LsEntry) filelistv.get(itemPosition);
        SftpATTRS attrs = lsEntryObj.getAttrs();
        long isize = attrs.getSize();
        String size = humanReadableByteCount(isize, true);
        String atime = attrs.getAtimeString();
        String mtime = attrs.getMtimeString();
        String perm = attrs.getPermissionsString();
        boolean isfolder = attrs.isDir();
        String type;
        String filepath = filearrayExplorerAbsolute[itemPosition];

        if(isfolder){
            type = "Directory";
        }
        else{
            type = "File";
        }

        TextView tvtype,tvpath,tvsize,tvperm,tvmtime, tvatime;




        MaterialDialog prop = new MaterialDialog.Builder(this)
                .title("Properties : " + itemValue)
                .titleColorRes(R.color.md_orange_800)
                .customView(R.layout.properties_dialogue, false)
                .positiveText(R.string.positive_button)
                .build();

        tvtype = (TextView) prop.findViewById(R.id.ftype);
        tvpath = (TextView) prop.findViewById(R.id.fpath);
        tvsize = (TextView) prop.findViewById(R.id.fsize);
        tvperm = (TextView) prop.findViewById(R.id.fpermissions);
        tvmtime = (TextView) prop.findViewById(R.id.fmtime);
        tvatime = (TextView) prop.findViewById(R.id.fatime);

        tvtype.setText(type);
        tvpath.setText(filepath);
        tvsize.setText(size);
        tvperm.setText(perm);
        tvatime.setText(atime);
        tvmtime.setText(mtime);

        prop.show();

        //showtoast("Permissions : " + attrs);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public void getPermissions(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(FileExplorer.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(FileExplorer.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }





    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        int itemPosition = position;

        // ListView Clicked item value
        String itemValue = (String) l.getItemAtPosition(position);

       // showtoast("Intem no :" + itemPosition + " | " + itemValue + " Clicked");
        lsEntryObj = (ChannelSftp.LsEntry) filelistv.get(itemPosition);
        if(lsEntryObj.getAttrs().isDir()){
            getDir(filearrayExplorerAbsolute[itemPosition],true);
        }else{
            showtoast(itemValue + " : Clicked");
        }


        /*File file = new File(path.get(position));

        if (file.isDirectory())
        {
            if(file.canRead()){
                getDir(path.get(position));
            }else{
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK", null).show();
            }
        }else {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("[" + file.getName() + "]")
                    .setPositiveButton("OK", null).show();

        }*/
    }

    public void showtoast(final String toast) {
        FileExplorer.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(FileExplorer.this, toast, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showdialogue(final String title,final String message) {
        FileExplorer.this.runOnUiThread(new Runnable() {
            public void run() {
                new MaterialDialog.Builder(FileExplorer.this)
                        .title(title)
                        .content(message)
                        .show();
            }
        });

    }


    public class downloadFile extends AsyncTask<String, String, String>
    {
        /*
        private long max                = 0;
        private long count              = 0;
        private long percent            = 0;
        //private CallbackContext callbacks = null;
        */
        MaterialDialog dialog = new MaterialDialog.Builder(FileExplorer.this)
        .title(R.string.download_dialog)
        .titleColorRes(R.color.md_orange_800)
        .content(R.string.downloading)
        .positiveText(R.string.hide_dialogue)
        .autoDismiss(false)
        .progress(true, 0)
        .progressIndeterminateStyle(true)
        .build();





        MaterialDialog cdialog = new MaterialDialog.Builder(FileExplorer.this)
                .title(R.string.download_dialog)
                .titleColorRes(R.color.md_orange_800)
                .content(R.string.download_complete)
                .positiveText(R.string.positive_button)
                .build();

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            super.onPreExecute();

                dialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            String fileToDownload=args[0];
            String itemValue=args[1];
            int buffsize = 8*1024;




                try {


                    showtoast("File to Download : " + fileToDownload);
                    File newFile = new File(localdir + "/" + itemValue);
                    showtoast("File created : " + newFile.getAbsolutePath());
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(newFile),buffsize);
                    showtoast("Downloading to : " + localdir +"/" + itemValue);

                    InputStream in = channelSftp.get(fileToDownload);
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        os.write(buffer, 0, len);

                    }



                    showtoast("Download Complete");
                    os.close();


                } catch (SftpException e) {
                    showtoast("Download Error :" + e);

                } catch (IOException e) {
                    showtoast("IO Error :" + e);
                }
            return null;
        }
        @Override
        protected void onPostExecute(String args) {
            // TODO Auto-generated method stub

            dialog.dismiss();
            cdialog.show();


        }
        //SFTPProgessMonitor Code
        /*
        public void init(int op, java.lang.String src, java.lang.String dest, long max) {
            this.max = max;
            System.out.println("Starting...");
            //showtoast("Starting...");
        }

        public boolean count(long bytes){
            this.count += bytes;
            long percentNow = this.count*100/max;
            if(percentNow>this.percent){
                this.percent = percentNow;

                System.out.println("progress : " + this.percent); // Progress 0,0
                System.out.println(max); //Total ilesize
                System.out.println(this.count); // Progress in bytes from the total
                //int local = (int) this.count;
                //dialog.setProgress(local);
            }

            return(true);
        }

        public void end(){

            System.out.println("finished");// The process is over
            System.out.println(this.percent); // Progress
            System.out.println(max); // Total filesize
            System.out.println(this.count); // Process in bytes from the total

            //showtoast("Download Complete.");
        }
        */
        //SFtpProgressMonitor End
    }


    public class uploadFile extends AsyncTask<String, String, String>
    {
        MaterialDialog dialog = new MaterialDialog.Builder(FileExplorer.this)
                .title(R.string.upload_dialogue)
                .titleColorRes(R.color.md_orange_800)
                .content(R.string.uploading)
                .positiveText(R.string.hide_dialogue)
                .autoDismiss(false)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .build();

        MaterialDialog cdialog = new MaterialDialog.Builder(FileExplorer.this)
                .title(R.string.download_dialog)
                .titleColorRes(R.color.md_orange_800)
                .content(R.string.upload_complete)
                .positiveText(R.string.positive_button)
                .build();

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            super.onPreExecute();

            dialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            String fileToUpload=args[0];
            String dest=args[1];



            try {


                channelSftp.put(fileToUpload, dest);



                showtoast("Upload Complete");



            } catch (SftpException e) {
                showtoast("Upload Error :" + e);

            }
            return null;
        }
        @Override
        protected void onPostExecute(String args) {
            // TODO Auto-generated method stub

            dialog.dismiss();
            cdialog.show();


        }


    }


    public class getConnect extends AsyncTask<String, String, String>
    {
        MaterialDialog dialog = new MaterialDialog.Builder(FileExplorer.this)
                .title(R.string.connecting)
                .titleColorRes(R.color.md_orange_800)
                .content(R.string.downloading)
                .autoDismiss(false)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .build();
        SharedPreferences prefs = getSharedPreferences(Settings.SettingsFragment.public_server_pref, MODE_PRIVATE);

        final String ps_uname = prefs.getString("ps_uname", "fs-Madaditya");//"No name defined" is the default value.
        final String ps_ip = prefs.getString("ps_ip", "maniac.freeshells.org"); //0 is the default value.
        final int ps_port = prefs.getInt("ps_port", 22);
        final String ps_pass = prefs.getString("ps_pass", "test123!");

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            super.onPreExecute();

            dialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub


            try {
                JSch jsch = new JSch();
                // Port foward
                if(isremote) {
                    session = jsch.getSession(ps_uname, ps_ip, ps_port);
                    session.setPassword(ps_pass);
                    showtoast("Connected to Server.");
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();
                    session.setPortForwardingL(20000, "127.0.0.1", 20000);
                    showtoast("POrt fowarded to :  20000, \"127.0.0.1\", 20000");
                }
                    //showdialogue("Wait","Please wait");

                //actual sftp
                session = jsch.getSession(user, ip, port);
                session.setPassword(pass);
                showtoast("Connecting to : " + user);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();


                channel = session.openChannel("sftp");
                channel.connect();
                channelSftp = (ChannelSftp) channel;


            } catch (JSchException e) {
                showtoast("Jsch Error :" + e);
            } catch (Exception e){
                showtoast("Error : " + e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String args) {
            // TODO Auto-generated method stub
            dialog.setContent(getString(R.string.download_complete));
            dialog.dismiss();
            getDir(root, fromout);


        }
    }


}

