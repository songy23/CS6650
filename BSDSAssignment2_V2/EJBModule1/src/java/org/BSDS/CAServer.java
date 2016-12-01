/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.BSDS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.Stateless;

/**
 *
 * @author songyang
 */
@Stateless
public class CAServer implements CAServerRemote {
    private static Map<String, ArrayList<String>> messagesByTopic = new ConcurrentHashMap<String, ArrayList<String>>();
    private static Map<Integer, Pair> subscriberidToTopicPositionMap = new HashMap<Integer, Pair>();  // don't need to be concurrent
    private static Map<Integer, String> publisheridToTopicMap = new HashMap<Integer, String>();
    private static Integer publisherID = 0;
    private static Integer subscriberID = 0;

    @Override
    public Integer registerPublisher(String topic, String name) {
        synchronized (publisherID) {
            publisherID++;  // Starts from 1
            publisheridToTopicMap.put(publisherID, topic);
            return publisherID;
        }
    }

    @Override
    public void publishContent(int publisherID, String title, String message) {
        String topic = publisheridToTopicMap.get(publisherID);
        ArrayList<String> messages = messagesByTopic.get(topic);
        if (messages == null) {
            messages = new ArrayList<String>();
            messagesByTopic.put(topic, messages);
        }
        synchronized (messages) {
            messages.add(message);
        }
    }

    @Override
    public Integer registerSubscriber(String topic) {
        synchronized (subscriberID) {
            subscriberID++;
            Pair pair = new Pair(topic, 0);
            subscriberidToTopicPositionMap.put(subscriberID, pair);
            return subscriberID;
        }
    }

    @Override
    public String getLatestContent(int subscriberID) {
        String lastestContent = null;
        Pair pair = subscriberidToTopicPositionMap.get(subscriberID);
        if (pair == null) {
            return null;
        }
        String topic = pair.topic;
        int position = pair.position;
        ArrayList<String> messages = messagesByTopic.get(topic);

        if (messages != null && position < messages.size()) {
            lastestContent = messages.get(position);
            pair.position++;
        }

        if (lastestContent == null) {
            return null;
        } else {
            return lastestContent;
        }
    }
}

/**
 * A simple class used to store the topic and message position of a subscriber.
 */
class Pair {
    String topic;
    int position;

    public Pair() {
    }

    public Pair(String s, int p) {
        this.topic = s;
        this.position = p;
    }
}