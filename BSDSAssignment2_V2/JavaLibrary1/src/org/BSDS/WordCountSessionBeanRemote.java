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
public interface WordCountSessionBeanRemote {

    void updateWordCount(String word);

    String getTopNWords(int N);
    
}
