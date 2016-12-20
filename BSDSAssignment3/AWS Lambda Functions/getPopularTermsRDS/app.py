import sys
import logging
import pymysql
#rds settings
rds_host  = "wordcounter.c2srvcytep3p.us-west-2.rds.amazonaws.com"
name = "admin"
password = "password"
db_name = "wordcount"
port = 3306


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
    
    if (event['httpMethod'] != 'POST'):
        return "Unsupported HTTP method"
    
    # print(type(event), event)
    number = event['body']['number'];
    words = ""

    with conn.cursor() as cur:
        # cur.execute("create table WordCount ( Word varchar(255) NOT NULL, Count int NOT NULL, PRIMARY KEY (Word))")
        # cur.execute('insert into WordCount (Word, Count) values("word1", 1)')
        # cur.execute('insert into WordCount (Word, Count) values("word2", 2)')
        # conn.commit()
        cur.execute("select Word from WordCount order by Count DESC limit " + str(number))
        for row in cur:
            logger.info(row)
            words = words + row[0] + ' '

    return words


# {
#   "body": {"number" : 1},
#   "resource": "/{proxy+}",
#   "requestContext": {
#     "resourceId": "123456",
#     "apiId": "1234567890",
#     "resourcePath": "/{proxy+}",
#     "httpMethod": "POST",
#     "requestId": "c6af9ac6-7b61-11e6-9a41-93e8deadbeef",
#     "accountId": "123456789012",
#     "identity": {
#       "apiKey": null,
#       "userArn": null,
#       "cognitoAuthenticationType": null,
#       "caller": null,
#       "userAgent": "Custom User Agent String",
#       "user": null,
#       "cognitoIdentityPoolId": null,
#       "cognitoIdentityId": null,
#       "cognitoAuthenticationProvider": null,
#       "sourceIp": "127.0.0.1",
#       "accountId": null
#     },
#     "stage": "prod"
#   },
#   "queryStringParameters": {
#     "foo": "bar"
#   },
#   "headers": {
#     "Via": "1.1 08f323deadbeefa7af34d5feb414ce27.cloudfront.net (CloudFront)",
#     "Accept-Language": "en-US,en;q=0.8",
#     "CloudFront-Is-Desktop-Viewer": "true",
#     "CloudFront-Is-SmartTV-Viewer": "false",
#     "CloudFront-Is-Mobile-Viewer": "false",
#     "X-Forwarded-For": "127.0.0.1, 127.0.0.2",
#     "CloudFront-Viewer-Country": "US",
#     "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
#     "Upgrade-Insecure-Requests": "1",
#     "X-Forwarded-Port": "443",
#     "Host": "1234567890.execute-api.us-east-1.amazonaws.com",
#     "X-Forwarded-Proto": "https",
#     "X-Amz-Cf-Id": "cDehVQoZnx43VYQb9j2-nvCh-9z396Uhbp027Y2JvkCPNLmGJHqlaA==",
#     "CloudFront-Is-Tablet-Viewer": "false",
#     "Cache-Control": "max-age=0",
#     "User-Agent": "Custom User Agent String",
#     "CloudFront-Forwarded-Proto": "https",
#     "Accept-Encoding": "gzip, deflate, sdch"
#   },
#   "pathParameters": {
#     "proxy": "path/to/resource"
#   },
#   "httpMethod": "POST",
#   "stageVariables": {
#     "baz": "qux"
#   },
#   "path": "/path/to/resource"
# }