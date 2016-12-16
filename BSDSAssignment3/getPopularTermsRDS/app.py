__author__ = 'songyang'

import sys
import logging
import pymysql
#rds settings
rds_host  = "wordcounter.c2srvcytep3p.us-west-2.rds.amazonaws.com"
name = "admin"
password = "password"
db_name = "wordcount"


logger = logging.getLogger()
logger.setLevel(logging.INFO)

try:
    conn = pymysql.connect(rds_host, user=name, passwd=password, db=db_name, connect_timeout=5)
except Exception as e:
    print(e);
    logger.error("ERROR: Unexpected error: Could not connect to MySql instance.")
    sys.exit()

logger.info("SUCCESS: Connection to RDS mysql instance succeeded")
def lambda_handler(event, context):
    """
    This function fetches content from mysql RDS instance
    """

    item_count = 0

    with conn.cursor() as cur:
        cur.execute("create table WordCount ( Word varchar(255) NOT NULL, Count int NOT NULL, PRIMARY KEY (Word))")
        cur.execute('insert into WordCount (Word, Count) values("word1", 1)')
        cur.execute('insert into WordCount (Word, Count) values("word2", 2)')
        conn.commit()
        cur.execute("select * from WordCount")
        for row in cur:
            item_count += 1
            logger.info(row)
            print(row)

    return "Added %d items from RDS MySQL table" %(item_count)
