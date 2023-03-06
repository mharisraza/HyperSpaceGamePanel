#!/bin/bash

# Get arguments
FTP_USERNAME=$1
FTP_PASSWORD=$2
GAME_SERVER_TYPE=$3
GAME_SERVER_ID=$4

# Create /home/ftpusers directory if it doesn't exist
if [ ! -d "/home/ftpusers" ]; then
  mkdir /home/ftpusers
fi

GAME_SERVER_DIR=/home/game-servers/$GAME_SERVER_TYPE/$GAME_SERVER_ID
mkdir -p $GAME_SERVER_DIR/game_files

# Add new FTP user
useradd -g ftp -d /home/game-servers/$GAME_SERVER_TYPE/$GAME_SERVER_ID/game_files $FTP_USERNAME

# Set the user's password
echo -e "$FTP_PASSWORD\n$FTP_PASSWORD" | passwd "$FTP_USERNAME"

groupadd $FTP_USERNAME


# Set permissions
chown -R $FTP_USERNAME:$FTP_USERNAME $GAME_SERVER_DIR/game_files/
chmod -R 770 $GAME_SERVER_DIR/game_files/
setfacl -d -m u::rwx,g::rwx,o::--- $GAME_SERVER_DIR/game_files/

touch /etc/vsftpd/user_config/$FTP_USERNAME

# Write user-specific vsftpd configuration
USER_CONFIG_FILE="/etc/vsftpd/user_config/$FTP_USERNAME"
echo "local_root=/home/game-servers/$GAME_SERVER_TYPE/$GAME_SERVER_ID/game_files" > $USER_CONFIG_FILE

# Restart vsftpd service
systemctl restart vsftpd.service

# Add user to FTP user list
echo $FTP_USERNAME >> /home/ftpusers/ftpusers

# Success
echo "GAME_SERVER_CREATED_SUCCESSFULLY"

