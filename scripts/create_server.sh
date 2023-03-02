#!/bin/bash

# Check if script is being run as root
if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root"
   exit 1
fi

# Check if FTP user exists
FTP_USERNAME="$1"
if id "$FTP_USERNAME" >/dev/null 2>&1; then
    echo "FTP user $FTP_USERNAME already exists"
else
    echo "Creating FTP user $FTP_USERNAME"
    PASSWORD="$2"
    useradd -m "$FTP_USERNAME"
    echo "$FTP_USERNAME:$PASSWORD" | chpasswd
fi

# Create game server directory
GAME_SERVER_TYPE="$3"
GAME_SERVER_ID="$4"
GAME_SERVER_DIR="/home/hyperspacegamepanel/$GAME_SERVER_TYPE/$GAME_SERVER_ID"
echo "Creating game server directory $GAME_SERVER_DIR"
mkdir -p "$GAME_SERVER_DIR"

# Set directory permissions for FTP user
echo "Setting directory permissions for FTP user $FTP_USERNAME"
chown -R "$FTP_USERNAME":"$FTP_USERNAME" "$GAME_SERVER_DIR"
chmod -R 755 "$GAME_SERVER_DIR"


# Check if game server type is valid
if [ "$gameServerType" == "cs" ]; then
    # Create game server directory
    mkdir -p "/home/hyperspacegamepanel/$gameServerType/$gameServerId"

    # Add user with restricted access to game server directory
    useradd -d "/home/hyperspacegamepanel/$gameServerType/$gameServerId" -g www-data -s /bin/false $gameServerId
    passwd $gameServerId

    # Set permissions for game server directory
    chown -R $gameServerId:www-data "/home/hyperspacegamepanel/$gameServerType/$gameServerId"
    chmod -R 755 "/home/hyperspacegamepanel/$gameServerType/$gameServerId"

    # Download game server files using wget
    cd "/home/hyperspacegamepanel/$gameServerType/$gameServerId"
    wget https://example.com/game-server-files.zip
    unzip game-server-files.zip
    rm game-server-files.zip

else
    echo "Invalid game server type."
fi
