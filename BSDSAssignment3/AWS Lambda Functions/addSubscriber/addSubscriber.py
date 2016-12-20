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
    # print("Received event: " + json.dumps(event, indent=2))
    if event.get('httpMethod') == "POST":
        
        # --- UUID ---
        newID = str(uuid.uuid4())
        
        # --- SNS ---
        snsClient = boto3.client('sns')
        topicResponse = snsClient.create_topic(Name = event.get('body').get('topic'))
        topicArn = topicResponse.get('TopicArn')
        
        # print(json.dumps(response,indent=2))
        
        
        # --- SQS ---
        sqsClient = boto3.client('sqs')
        queue = sqsClient.create_queue(QueueName = newID)
        queueUrl = queue.get('QueueUrl')
        attrs = sqsClient.get_queue_attributes(QueueUrl= queueUrl,AttributeNames= ['QueueArn'])
        queueArn = attrs.get('Attributes').get('QueueArn')
        
        
        # update permissions
        sqsClient.set_queue_attributes(
            QueueUrl= queueUrl,
            Attributes={
                'Policy': generatePolicy(queueArn, topicArn)
            }
        )
        
        
        # newSQS subscribes to SNS
        re = snsClient.subscribe(TopicArn = topicArn, Protocol = 'sqs', Endpoint = queueArn)
        
        
        # --- DYNAMO DB ---
        # dbClient = boto3.client('dynamodb')
        # dbResponse = dbClient.put_item(
        #     TableName='SubscriberSNS',
        #     Item={
        #         'subID': {
        #             'S': newID
        #         },
        #         'topic': {
        #             'S':event.get('body').get('topic')
        #         },
        #         'name': {
        #             'S': event.get('body').get('subName')
        #         },
        #         'sqsArn': {
        #             'S': queueArn
        #         },
        #         'sqsUrl': {
        #             'S': queueUrl
        #         }
        #     }
        # )
        # print(json.dumps(dbResponse,indent=2))
        
        return respond(None, queueUrl)
    elif not event.get('topics'):
        return respond(ValueError('topics not present'))
    else:
        return respond(ValueError('Unsupported method "{}"'.format(operation)))

def generatePolicy(queueArn, topicArn):
    return json.dumps({
        "Version": "2012-10-17",
        "Id": "SQSDefaultPolicy",
        "Statement": [{
            "Sid": "1",
            "Effect": "Allow",
            "Principal": {
                    "AWS": "*"
                },
            "Action": "SQS:SendMessage",
            "Resource": queueArn,
            "Condition": {
                "ArnEquals": {
                    "aws:SourceArn": topicArn
                }
            }
        }]
    })

#  {
#   "body": {
#      "subName": "Alec",
#      "topic": "TAPIC"
#   },
#   "httpMethod": "POST"
#  }