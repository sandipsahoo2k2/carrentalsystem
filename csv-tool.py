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

