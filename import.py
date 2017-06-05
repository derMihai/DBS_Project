import csv
import MySQLdb

dbs= MySQLdb.connect(host='localhost', user= 'testuser', passwd= 'testpass', db='dbs')

cursor = mydb.cursor()

csv_data = csv.reader(file('american-election-tweets.csv'))

for row in csv_data:
	cursor.execute ('INSERT INTO dbs(Tweet, Contains, Hashtag, ComesAlong)' 'VALUES("%s", "%s", "%s")', row)
