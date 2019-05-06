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

