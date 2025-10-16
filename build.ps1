Remove-Item .\MySQL -Recurse -Force
Remove-Item .\Mongo -Recurse -Force
Remove-Item .\Redis -Recurse -Force
mkdir .\MySQL
mkdir .\Mongo
mkdir .\Redis
docker-compose up -d --build