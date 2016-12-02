/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.BSDS;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;

/**
 *
 * @author songyang
 */
@Singleton
public class WordCountSessionBean implements WordCountSessionBeanRemote {
    private WordFrequencyDAO wordFrequencyDAO = WordFrequencyDAO.getInstance();

    @Override
    public void updateWordCount(String word) {
        try {
            wordFrequencyDAO.updateWordFrequency(word);
        } catch (SQLException ex) {
            Logger.getLogger(WordCountSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getTopNWords(int N) {
        String terms = null;
        try {
            terms = wordFrequencyDAO.getTopNPopularWords(N);
        } catch (SQLException ex) {
            Logger.getLogger(WordCountSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return terms;
    }
}
