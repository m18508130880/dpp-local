Current Version: 1.4.5
======================
This software is allowed to use under freeware license or you need to buy commercial license for better support or other purpose.
Please contact us at info@jeasyui.com
#!/bin/bash
# This is mysql mysqlfullbak scripts
# 2015-09-07
# Best
user=root
passwd=111111
databak_dir=/mysql_data_bak   #备份的目录
eMailFile=$databak_dir/email.txt
 
DATE=`date +%Y%m%d`
logFile=$databak_dir/logs/mysql$DATE.log
database=LNG-LOCAL
echo "     " > $eMailFile
echo "---------------------------------" >> $eMailFile
echo $(date +"%y-%m-%d %H:%M:%S") >> $eMailFile

dumpFile=$database$DATE.sql
GZDumpFile=$database$DATE.tar.gz
options="-u$user -p$passwd --opt --extended-insert=false --triggers=false -R --hex-blob --flush-logs --delete-master-logs -B $database"
mysqldump $options > $dumpFile  #导出数据文件
if [[ $? == 0 ]]; then
  tar cvzf $GZDumpFile $dumpFile >> $eMailFile 2>&1
  echo "BackupFileName:$GZDumpFile" >> $eMailFile
  echo "DataBase Backup Success" >> $eMailFile
 # scp $GZDumpFile 192.168.0.202:/mysql_data_bak   #传送备份文件到另一台计算机，需要做好ssh信任
  rm -f $dumpFile           #删除备份的文件
 # rm –rf $databak_dir/daily/*  #删除每天备份的文件
else
  echo "DataBase Backup Fail!" >> $emailFile
  mail -s " DataBase Backup Fail " $eMail < $eMailFile  #如果备份不成功发送邮件通知
fi
echo "--------------------------------------------------------" >> $logFile
cat $eMailFile >> $logFile