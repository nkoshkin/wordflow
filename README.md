# Wordflow
Консольное приложение для анализа текстовых файлов с подсчетом частоты слов.
## Требования
 - Java 21+
 - Maven 3.6+
## Сборка проекта
### 1. Клонирование репозитория
 - `git clone https://github.com/nkoshkin/wordflow.git`
 - `cd wordflow`
### 2. Сборка проекта
 - `mvn clean package`
 - `mvn clean package -DskipTest` (без тестов)
## Запуск программы
### 1. Перейти в директорию target
 - `cd target`
### 2. Запустить программу
 - `java -jar wordflow.jar --dir=<path> --min-length=<int> --top=<int> [OPTIONS]`
 - 'OPTIONS' :
   - `--help` - Вызов справки
   - `--stopwords` - Путь к файлу со словами, которые нужно пропустить
   - `--output` - Путь к файлу для сохранения результата
 
## Примеры выполонения
### 1. Запуск без передачи опциональных параметров и выводом в консоль
 - `java -jar wordflow.jar --dir=/d/source --min-length=6 --top=3`
 - ```
   Directory: D:/source
   Min length: 6
   Top count: 3
   
   Top words: [
   1. человек - 4
   2. смотрел - 4
   3. незнакомец - 3
   ]
   errors: [
   ]
   ```
### 2. Запуск с указанием файла стоп-слов и сохранением в json-файл
 - `java -jar wordflow.jar --dir=./source --min-length=6 --top=2 --stopwords=./stopwords.txt --output=result.json`
 - ```
   {
   "infoDto" : {
   "directory" : "./source",
   "minWordLength" : 6,
   "top" : 2
   },
   "words" : [ {
   "word" : "незнакомец",
   "count" : 3
   }, {
   "word" : "машины",
   "count" : 3
   }, {
   "word" : "столиком",
   "count" : 2}],
   "errors" : [ ]
   }
   ```
