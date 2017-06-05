import csv
import MySQLdb

mydb = MySQLdb.connect(host='localhost',
    user='berna',
    passwd='',
    db='election')
cursor = mydb.cursor()

csv_data = csv.reader(file('american-election-tweets.csv'))
for row in csv_data:

    cursor.execute('INSERT INTO testcsv(names, \
          classes, mark )' \
          'VALUES("%s", "%s", "%s")', 
          row)
#close the connection to the database.
mydb.commit()
cursor.close()
print "Done"
