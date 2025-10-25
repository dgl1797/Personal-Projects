Remove-Item .\MySQL\data -Recurse -Force
Remove-Item .\Mongo -Recurse -Force
Remove-Item .\Redis -Recurse -Force
mkdir .\MySQL\data
mkdir .\Mongo
mkdir .\Redis
docker-compose up -d --build