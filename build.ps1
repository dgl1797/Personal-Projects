if (Test-Path -path ".\MySQL\data"){ Remove-Item .\MySQL\data -Recurse -Force }
if (Test-Path -path ".\Mongo\data"){ Remove-Item .\Mongo -Recurse -Force }
if (Test-Path -path ".\Redis"){ Remove-Item .\Redis -Recurse -Force }
mkdir .\MySQL\data
mkdir .\Mongo\data
mkdir .\Redis
docker-compose up -d --build