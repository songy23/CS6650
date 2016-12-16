from __future__ import print_function

import boto3
import json
import uuid

print('Loading function')

def respond(err, res=None):
    return err.message if err else res
    # return {
    #     'statusCode': '400' if err else '200',
    #     'body': err.message if err else json.dumps(res),
    #     'headers': {
    #         'Content-Type': 'application/json',
    #     },
    # }


def lambda_handler(event, context):
    print("Received event: " + json.dumps(event, indent=2))
    return respond(None, event)
    if event.get('queryStringParameters'):
        
        # --- Lookups ---
        # subID = event.get('body').get('subID')
        subID = event.get('queryStringParameters').get('subID')
        # --- DYNAMO DB ---
        # dbClient = boto3.client('dynamodb')
        # dbResponse = dbClient.get_item(
        #     TableName='SubscriberSNS',
        #     Key={'subID': {'S': subID}}
        # )
        # print(json.dumps(dbResponse,indent=2))
        # url = dbResponse.get('Item').get('sqsUrl').get('S')
        url = subID
    
        # --- SQS ---
        sqsClient = boto3.client('sqs')
        response = sqsClient.receive_message(QueueUrl = url,MaxNumberOfMessages = 1)
        if response.get("Messages"):
            msgObj = response.get('Messages')[0]
            msgBody = json.loads(msgObj.get('Body')).get('Message')
            print("MSG BODY:", msgBody)
            
            # --- Delete msg ---
            receiptHandle = msgObj.get('ReceiptHandle')
            deleteResp = sqsClient.delete_message(
                QueueUrl = url,
                ReceiptHandle = receiptHandle
            )
            print(json.dumps(deleteResp,indent=2))
            return respond(None, msgBody)
        return respond(None, "No messages in sqs")
    else:
        return respond(ValueError('Unsupported method "{}"'.format(operation)))