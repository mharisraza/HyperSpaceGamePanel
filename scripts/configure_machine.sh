#!/bin/bash

# update first
apt-get update -y

# for setfacl
apt-get install acl -y

# vsftpd
apt-get install vsftpd -y

# configure vsftpd
echo "chroot_local_user=YES" >> /etc/vsftpd.conf
echo "allow_writeable_chroot=YES" >> /etc/vsftpd.conf
echo "user_config_dir=/etc/vsftpd/user_config" >> /etc/vsftpd.conf

#start vsftpd
service vsftpd start

#for cs 1.6 server [this is required, otherwise cs 1.6 server will not able to run]:
dpkg --add-architecture i386

apt-get update -y

apt-get install libstdc++6 libstdc++6:i386 lib32gcc1 psmisc screen -y