#!/bin/bash

total_ram=$(free -k | awk 'NR==2{printf "%dGB", $2/1024/1024}')
total_storage=$(df -h / | awk 'NR==2{printf "%s/%s (%s)", $3,$2,$5}')
total_cpus=$(nproc)
cpu_name=$(cat /proc/cpuinfo | grep 'model name' | uniq | awk -F':' '{print $2}')
hostname=$(hostname)
location=$(wget -qO- https://ipapi.co/country_name | grep -oE '[A-Za-z]+')

echo "total_ram=$total_ram"
echo "total_storage=$total_storage"
echo "total_cpus=$total_cpus"
echo "cpu_name=$cpu_name"
echo "hostname=$hostname"
echo "location=$location"