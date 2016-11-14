#!/bin/sh
echo "Installing required dependencies..."
for i in sshpass sl openssh-server zenity jftp; do 
  dpkg -l $i > /dev/null 2>&1
  if [ $? -ne 0 ];then
     echo "---------------------------------------------"
     echo "Installing $i...."
     echo "---------------------------------------------"
     sudo apt-get install $i
     clear 
  fi 
done
echo "Done..."


a=`zenity --forms --title="Details for Hosting Server" --ok-label="Host Files" --cancel-label="Open Client" --text="Enter Details" \
   --add-entry="Username" \
   --add-entry="Ip/Hostname" \
   --add-entry="Public Port to Use" \
   --add-password="Password"`

if [[ $? -eq 0 ]]; then
        #  timeout
        echo "In Server"
        usname=`echo $a | cut -d'|' -f1`
        hsname=`echo $a | cut -d'|' -f2`
        sport=`echo $a | cut -d'|' -f3`
        spass=`echo $a | cut -d'|' -f4`

        dollar="@"
        localip=":127.0.0.1:22"
        sshpass -p $spass ssh -R $sport$localip $usname$dollar$hsname
        #sshpass -p $spass ssh -R $sport$localip $usname$dollar$hsname
        if [ $? -eq 0 ]; then
            echo OK
        else
            echo FAIL
        fi

elif [[ $? -eq 1 ]]; then
        #  cancel button pressed
        #clear
        echo "Setting up route..."
        c=`zenity --forms --title="Enter Details" --text="Enter Details" \
   --add-entry="Username" \
   --add-entry="Ip/Hostname" \
   --add-entry="Public Port to Use" \
   --add-password="Password"`
        usname=`echo $c | cut -d'|' -f1`
        hsname=`echo $c | cut -d'|' -f2`
        sport=`echo $c | cut -d'|' -f3`
        spass=`echo $c | cut -d'|' -f4`
        dollar="@"
        localip=":127.0.0.1:"
        
        sshpass -p $spass ssh -L $sport$localip$sport $usname$dollar$hsname 
        #sshpass -p $spass ssh -R $sport$localip $usname$dollar$hsname
        if [ $? -eq 0 ]; then
            echo "Route Established."
            
        else
            echo "Can't establish a route!"
        fi
         echo "Opening Client..."
        #gnome-terminal -x jftp > /dev/null 2>&1 
        #clear

fi




