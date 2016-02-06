package com.patent.filefire;

/**
 * Created by Mike on 22-Jan-16.
 */

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class FileExplorer extends ListActivity  {

    private List<String> item = null;
    private List<String> path = null;
    private String root;
    private TextView myPath;
    public String dirPath,user,ip,pass;
    public int port;
    public static String[] filearray;
    public String[] filearrayExplorer,filearrayExplorerAbsolute;
    int  filesize;
    Session session = null;
    Channel channel = null;
    ChannelSftp channelSftp = null;
    ChannelSftp.LsEntry lsEntryObj;
    Vector filelistv;
    boolean fromout=false;
    String localdir;
    int FILE_CODE;



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



       localdir = Environment.getExternalStorageDirectory().getPath();
        //showtoast(root);
        root="";

        getDir(root,fromout);


    }

    public void getDir(final String temproot,final boolean fromout)
    {


        //SFTP Connect Code Start

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    JSch jsch = new JSch();
                    session = jsch.getSession(user, ip, port);
                    session.setPassword("SoWhat?");

                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();


                    channel = session.openChannel("sftp");
                    channel.connect();
                    channelSftp = (ChannelSftp) channel;




                    if(fromout){
                    channelSftp.cd(temproot);
                    showtoast("Changed Directory to : " + temproot);
                    }
                    dirPath = channelSftp.pwd();
                    showtoast("Connection to Addy Done with Path : " + dirPath);

                    /*
                    Vector<String> filelistv = new Vector<String>();

                    filelistv = channelSftp.ls(channelSftp.pwd());
                    filearray = filelistv.toArray(new String[filelistv.size()]);
                    showtoast("Array element 1 :" + filearray[1]);
                    */
                    //local dir start
                    channelSftp.lcd(localdir);
                    //localdir = channelSftp.lpwd();
                    //showtoast("Local Dir : " + localdir);
                    //local dir end

                    filelistv = channelSftp.ls(channelSftp.pwd());
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
                   // showtoast("File Size in Thread: " +filesize);
                    //Vector<String> vector = new Vector<String>();
                    //String[] strings = vector.toArray(new String[vector.size()]);

                    // dMessage="User:" + sftpUserStr + "IP" + sftpIpStr + "Port" + sftpPort;
                    //SimpleDialogFragment.createBuilder(MainActivity.this, getSupportFragmentManager()).setTitle(dTitle).setMessage(dMessage).show();
                    channelSftp.disconnect();
                    session.disconnect();
                } catch (JSchException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    showtoast("Error" + e);
                    //dMessage= "Error" + e;
                } catch (Exception e) {
                    showtoast("Error" + e);

                }
            }

        });

        thread.start();
        try {
            thread.join();
        }catch(Exception e){
            showtoast("Thread exception : ");
        }



        //Sftp Code End
        myPath.setText("Location: " + dirPath);
        item = new ArrayList<String>();
        //path = new ArrayList<String>();
        /*
        File f = new File(dirPath);
        File[] files = f.listFiles();





        if(!dirPath.equals(root))
        {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }

        for(int i=0; i < listoffiles.length; i++)
        {
            File file = files[i];

            if(!file.isHidden() && file.canRead()){
                path.add(file.getPath());
                if(file.isDirectory()){
                    item.add(file.getName() + "/");
                }else{
                    item.add(file.getName());
                }
            }
        }
*/          //showtoast("File size out of thread : " + filesize);

        item.addAll(Arrays.asList(filearrayExplorer));
        /*
        ArrayAdapter<String> fileList =
                new ArrayAdapter<String>(this, R.layout.row, item);
        setListAdapter(fileList);
        */
        //showtoast("Checking array : " + filearrayExplorer[3]);
        ArrayAdapter<String> filelistObj = new ArrayAdapter<String>(this, R.layout.mylist_image,R.id.Itemname, filearrayExplorer);
        setListAdapter(filelistObj);
        /*
        this.setListAdapter(new ArrayAdapter<String>(

                this, R.layout.mylist_image,
                R.id.itemname, filearrayExplorer));
        */

        // For Long Click
        final ListView list = getListView();
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //File file = new File(path.get(position));
                int itemPosition = position;


                // ListView Clicked item value
                final String itemValue = (String) list.getItemAtPosition(position);

                showtoast("\" " + itemValue + "\" Long Clicked");
                final String fileToDownload = filearrayExplorerAbsolute[itemPosition];
                showtoast("File Abs Path : " + fileToDownload);

                // Download start
                /*
                try {
                    out = new FileOutputStream(localdir+"/sftp.txt");
                }
                catch (Exception e){
                    showtoast("Outputstream Error : " + e);
                }
                   */
                final String[] items = {"Copy","Paste","Rename"};
                AlertDialog.Builder build = new AlertDialog.Builder(FileExplorer.this);
                build.setTitle(itemValue);
                build.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){

                            boolean success=false;
                            showtoast("Nothing for now");
                            //channelSftp.get(fileToDownload,localdir+itemValue);
                            success = true;
                            if(success){
                                showtoast("Download Complete!");
                            }


                    }
                });
                build.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showtoast(items[which] + " : Clicked");
                    }
                }).create().show();

                //Download Stop
                return true;
            }
        });
        //Long Click End


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


}

