import csv

filepath = "american-election-tweets.csv"

csvfile = open(filepath, "r", encoding="cp1252")		#datei zum lesen öffnen
csv_filewrite = open("test.csv", "w", encoding="cp1252")	#neue datei zum schreiben öffnen

csv_reader = csv.DictReader(csvfile, delimiter=";", quotechar='"')		#datei einlesen

csv_writer = csv.DictWriter(csv_filewrite, fieldnames=csv_reader.fieldnames,  delimiter=';', quotechar='"')	#in test.csv reinschreiben
csv_writer.writeheader()		#erstelle den header der neuen Datei mit den fieldnames die im csv_writer angegeben sind 

print('\033[1m'+ 'Die aussortierten Zeilen sind: \n')  #diese zeile ist fett gedruckt
i=0
for row in csv_reader:
    if not (row['truncated'] == 'True' or u"\u2026" in row['text']):		#nur die Zeilen kopieren, die nicht truncated sind und keine ... Sequenz haben
       # print(row['text'])
        csv_writer.writerow(row)
    else:
        i+=1
        print('\033[0m' + str(i) + ' ' + row['text'])				#wieder normal drucken


