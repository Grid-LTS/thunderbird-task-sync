## Thunderbird Google Task Sync

This is a little utility script, written in Groovy, 
synchronizes the Google tasks saved in Thunderbird's database by means of the addon *Provider for Google calendar*
with the tasks in the cloud (aka Google Tasks app)
Specifically it will:
* delete tasks that are marked deleted in the cloud, but were not deleted locally
* mark tasks as completed that were not completed locally
* delete tasks that duplicates (having exact same title is the criterium)
* delete tasks that are only exists in Thunderbird, but not in the cloud 
(disconnected/orphaned tasks)

### How to run
* provide Thunderbird parameters (e.g. profile) by copying `application-default.yml.example` 
to `application-default.yml` and add properties
* provide Google credentials (API key, secret key, ect.) by copying `google.properties.example`
to `google.properties` and add properties
* create jar
```
./gradlew jar
```
* run
```
java -jar build/libs/tbtasksync*.jar 
```

### Troubleshooting
```
 [SQLITE_CORRUPT]  The database disk image is malformed (database disk image is malformed)
```
Your SQLite database might be corrupted.
Reasons for corruption can be checked with
```
PRAGMA identity_check
```
__Solution__  
Export database dump and reimport database
```bash
# create backup
mv cache.sqlite cache.sqlite.bkp
sqlite3 cache.sqlite.bkp ".dump" | sqlite3 cache.sqlite
```


