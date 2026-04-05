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
 - `mvn clean package -DskipTests` (без тестов)
## Запуск программы
### 1. Перейти в директорию target
 - `cd target`
### 2. Запустить программу
 - `java -jar wordflow.jar --dir=<path> --min-length=<int> --top=<int> [OPTIONS]`
 - 'OPTIONS' :
   - `--help` - Вызов справки
   - `--stopwords` - Путь к файлу со словами, которые нужно пропустить
   - `--output` - Путь к файлу для сохранения результата
   - `--mode` - Режим запуска [single, multi]
   - `--threads` - Количество потоков в режиме *multi*, `default=2` 
 
## Примеры выполонения
### 1. Запуск без передачи опциональных параметров и выводом в консоль
 - `java -jar wordflow.jar --dir=/d/source --min-length=6 --top=3 --mode=single`
 - ```
   Mode: SINGLE (1 workers)
   Processed 4 files in 24 ms
   Top 10 words (min length = 5):
   
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
   "minWordLength" : 5,
   "top" : 3,
   "mode" : "multi",
   "threads" : 4,
   "processedFiles" : 4,
   "executionTimeMs" : 21
   },
   "words" : [ {
   "word" : "человек",
   "count" : 15
   }, {
   "word" : "ошибка",
   "count" : 13
   }, {
   "word" : "знание",
   "count" : 13
   } ],
   "errors" : [ ]
   }
   ```
## Технологии
- Для многопоточности используется ExecutorService с фиксированным тред-пулом.Потокобезопасные коллекции Concurent
- Каждый файл обрабатывается в отдельном потоке.
## Масштабирование
- Использовать очереди сообщений в качестве источника данных для обработки
- запись в файл вынести в отдельный поток, чтобы не блокировать основной

