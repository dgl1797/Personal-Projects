#!/bin/sh
apk add --update build-base python3-dev 
pip install --upgrade pip
pip install -r requirements.txt
python main.py

# executing every other argument as commands to allow further docker-compose commands
"$@"