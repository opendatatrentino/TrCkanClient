/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.trentorise.opendata.trckanclient;

import java.util.List;
import org.ckan.CKANException;
import org.ckan.Client;
import org.ckan.Connection;
import org.ckan.resource.impl.Dataset;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Leoni
 */
public class Tester {
    static String DATI_TRENTINO = "http://dati.trentino.it";
    static Logger logger = LoggerFactory.getLogger(Tester.class);
    static String DATA_GOV_UK = "http://data.gov.uk";
    
    public Tester() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        logger.info("Set \tlog4j.rootLogger=DEBUG, console\t in src/test/resources/log4j.properties to see logging messages");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void testDatasetList() throws CKANException {
        
        Connection c = new Connection(DATI_TRENTINO);
        Client cl = new Client(c, null);
        List<String> dsl = cl.getDatasetList().result;
        assertTrue(dsl.size() > 0);                  
    }
    
  /*  @Test
    public void testDataGovUkDatasetList() throws CKANException {
        
        Connection c = new Connection(DATA_GOV_UK);
        Client cl = new Client(c, null);
        List<String> dsl = cl.getDatasetList().result;
        assertTrue(dsl.size() > 0);          
        
    } */    
    
    @Test
    public void testGetDataset() throws CKANException {
        Connection c = new Connection(DATI_TRENTINO);
        Client cl = new Client(c, null);
        Dataset dataset = cl.getDataset("anagrafica-sensori-ufficio-dighe");
        logger.debug("dataset = " + dataset);
    }    
}
