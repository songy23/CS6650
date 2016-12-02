/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.BSDS;

import javax.ejb.Remote;

/**
 *
 * @author songyang
 */
@Remote
public interface CAServerRemote {

    Integer registerPublisher(String topic, String name);

    void publishContent(int publisherID, String title, String message);

    Integer registerSubscriber(String topic);

    String getLatestContent(int subscriberID);

    String getTopNWords(int n);
    
}
