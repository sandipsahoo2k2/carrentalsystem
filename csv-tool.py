# By Sandeep Sahoo
#!/usr/bin/env python

from urllib.request import urlopen
import json
import csv
import urllib.parse
import smtplib
import email
import configparser
import io
import cx_Oracle
import sys

FILE_NAME = 'esper_view'
ESPER_BASE_URL = 'https://request'
ESPER_VIEW = ''
SELECT_STRING = ''
FILTER_STRING = ''

DB_QUERY=''
DB_USER=''
DB_PWD=''
DB_NAME=''

def readConfig(filename):
    global ESPER_BASE_URL
    global ESPER_VIEW
    global SELECT_STRING
    global FILTER_STRING
    global DB_QUERY
    global DB_USER
    global DB_PWD
    global DB_NAME
    global FILE_NAME

    config = configparser.RawConfigParser(allow_no_value=True)
    config.read_file(open(filename))

    ESPER_BASE_URL = config.get('esperViews', 'baseUrl')
    ESPER_VIEW=config.get('esperViews', 'viewName')
    FILE_NAME = ESPER_VIEW
    SELECT_STRING = config.get('esperViews', 'selectString')
    FILTER_STRING = config.get('esperViews', 'filterString')
    DB_QUERY = config.get('dataBaseView', 'dbQuery')
    DB_USER = config.get('dataBaseView', 'username')
    DB_PWD = config.get('dataBaseView', 'password')
    DB_NAME = config.get('dataBaseView', 'dbname')

    print("\nConfig file : " + filename + " parsed.\n")
    print("ESPER_BASE_URL : " + ESPER_BASE_URL)
    print("ESPER_VIEW : " + ESPER_VIEW)
    print("SELECT_STRING : " + SELECT_STRING)
    print("FILTER_STRING : " + FILTER_STRING)
    print("DB_NAME : " + DB_NAME)
    return

def compare_views_data(esperFile, dbFile, ignore_mismatched_columns):

    print('\nStart Comparing ' + esperFile + ' and ' + dbFile)

    eFile = open(esperFile)
    dFile = open(dbFile)
    diff_count = 0
    big_table = None
    small_table = None
    
    e_reader = csv.DictReader(eFile)
    e_reader.fieldnames = [field.strip().lower() for field in e_reader.fieldnames]
    d_reader = csv.DictReader(dFile)
    d_reader.fieldnames = [field.strip().lower() for field in d_reader.fieldnames]
    e_cols = e_reader.fieldnames
    d_cols = d_reader.fieldnames

    if len(e_cols) > len(d_cols):

        print('\n' + esperFile + ' has more columns than ' + dbFile)
        big_table = e_cols
        small_table = d_cols

    else:

        big_table = d_cols
        small_table = e_cols

    common_columns = find_common_header(small_table, big_table)
    print('\nMatched columns :\n' + ', '.join(common_columns))

    extra = get_mismatched_elements(small_table, big_table)
    print('\nIgnoring mismatched columns :\n' + ', '.join(extra))

    extra.insert(0, '**---**');
    header = common_columns + extra
    diff_file = open(FILE_NAME + '_diff.csv', 'w', newline='')
    diffwriter = csv.writer(diff_file)
    diffwriter.writerow(header)
    print('\nComparing rows  ... ')

    e_row = next(e_reader)
    d_row = next(d_reader)
    keep_reading_table_1 = True
    keep_reading_table_2 = True
    keep_comparing = True

    while keep_reading_table_1 or keep_reading_table_2 :
        diff_found = False
        for column in common_columns:

            if keep_comparing and e_row[column] != d_row[column]:
                diff_count += 1
                diff_found = True
                break
        if diff_found == True:
            diffwriter.writerow(readRow(header, e_row))
            diffwriter.writerow(readRow(header, d_row))
            diffwriter.writerow(blankRow(header))

        if keep_comparing == False:
            if keep_reading_table_1 == True:
                diffwriter.writerow(readRow(header, e_row))
                diffwriter.writerow(blankFill(header, '-'))
                diffwriter.writerow(blankRow(header))

            if keep_reading_table_2 == True:
                diffwriter.writerow(blankFill(header, '+'))
                diffwriter.writerow(readRow(header, d_row))
                diffwriter.writerow(blankRow(header))

        if keep_reading_table_1 == True:
            try:
                e_row = next(e_reader)
            except StopIteration:
                keep_comparing = False
                keep_reading_table_1 = False

        if keep_reading_table_2 == True:
            try:
                d_row = next(d_reader)
            except StopIteration:
                keep_comparing = False
                keep_reading_table_2 = False

    print('\nDifference found in ' + str(diff_count) + ' rows ')

    eFile.close()
    dFile.close()
    diff_file.close()
    return

def blankRow(columns):
    newRow = []
    for column in columns:
        value = ''
        newRow.append(value)

    return newRow

def blankFill(columns, filler):
    newRow = []
    for column in columns:
        value = filler
        newRow.append(value)

    return newRow

def readRow(columns, reader):
    newRow = []
    for column in columns:
        try:
            value = reader[column]
        except KeyError:
            value = ''
        newRow.append(value)

    return newRow

def find_common_header(arr1, arr2):
    rm = []
    for d1 in arr1 :
        if d1 in arr2 :
            rm.append(d1)

    return rm

def get_mismatched_elements(arr1, arr2):

    temp1 = arr1.copy()
    temp2 = arr2.copy()
    rm = []
    for d1 in temp1 :
        if d1 in temp2 :
            rm.append(d1)

    for d1 in rm:
        temp1.remove(d1)
        temp2.remove(d1)

    return temp1 + temp2
        
def readDb(username, password, databaseName):
    
    try:
        print("\nConnecting DB " + databaseName)
        connection = cx_Oracle.connect (username,password,databaseName)
        cursor = connection.cursor()
        print("\nExecuting query..")
        cursor.execute (DB_QUERY)
        col_names = [row[0] for row in cursor.description]
        result = cursor.fetchall()

        dbFile = FILE_NAME + '_db.csv' ;
        print("\nWritng to file " + dbFile)
        with open(dbFile, 'w', newline='') as fp:
            rswriter = csv.writer(fp, delimiter=',')
            rswriter.writerow(col_names)
            rswriter.writerows(result)
            fp.close()
        cursor.close()
        connection.close()
    except cx_Oracle.DatabaseError:
        print ('Failed to connect to %s\n', databaseName)
    return

def readEsperView(url, sqlQuery, view, filter):
    
    encodedString = urllib.parse.quote_plus((sqlQuery + " " + view + " " + filter).encode('utf8'))
    encodedString = encodedString.replace("%2A", "*", 1)
    encodedUrl = url + encodedString;
    print("\nEncodedUrl : " + encodedUrl + "\n")
    with urlopen(encodedUrl) as response:
        for line in response:
            parsed_json = json.loads(line)
        try:
            return parsed_json['entries']
        except KeyError:
            print("No data fetched !!\n")
            return None

def writeToFile(entries):
    
    if entries is None:
        print("\nNo data to write !!\n")
        return
    viewFile = FILE_NAME + '.csv' ;
    print("\nWritng to file " + viewFile)
    f = open(viewFile, 'w', newline='')
    csvwriter = csv.writer(f)
    headerwritten = 0
    for entry in entries:
        if headerwritten == 0 :
            csvwriter.writerow(entry.keys())
            headerwritten =+ 1
        csvwriter.writerow(entry.values())
    f.close()
    return

def sendMail(server, port, fromMailId, fromPwd, toMailId):

    msg = MIMEMultipart()
    msg['From'] = fromMailId
    msg['To'] = toMailId
    msg['Subject'] = "Here is the diff files attached"

    body = "File for comparing " + FILE_NAME

    msg.attach(MIMEText(body, 'plain'))

    attachment = open(FILE_NAME, "rb")

    part = MIMEBase('application', 'octet-stream')
    part.set_payload((attachment).read())
    encoders.encode_base64(part)
    part.add_header('Content-Disposition', "attachment; filename= %s" % FILE_NAME)

    msg.attach(part)

    server = smtplib.SMTP(server, port)
    server.starttls()
    server.login(fromMailId, fromPwd)
    text = msg.as_string()
    server.sendmail(fromMailId, toMailId, text)
    server.quit()

if len(sys.argv) != 2:
    print ("Usage : python " + sys.argv[0] + " <file.conf>")
    sys.exit (1)

readConfig(sys.argv[1])
readDb(DB_USER, DB_PWD, DB_NAME)
writeToFile(readEsperView(ESPER_BASE_URL, SELECT_STRING, ESPER_VIEW, FILTER_STRING))
compare_views_data(ESPER_VIEW + '.csv', ESPER_VIEW + '_db.csv' , 0)
