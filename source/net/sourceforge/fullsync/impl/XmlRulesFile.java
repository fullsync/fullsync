package net.sourceforge.fullsync.impl;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class XmlRulesFile
{
    private Hashtable ruleSets;
    
    public XmlRulesFile()
    {
    }
    
    public void addRuleSet( XmlRuleSet ruleSet )
    {
        this.ruleSets.put( ruleSet.getName(), ruleSet );
    }
    
    public Enumeration getRuleSets()
    {
        return ruleSets.elements();
    }
    public XmlRuleSet getRuleSet( String name )
    {
        return (XmlRuleSet)ruleSets.get( name );
    }

    /*
    public static XmlRulesFile getXmlRulesFile( InputStream in ) 
    	throws IntrospectionException, IOException, SAXException
    {
        BeanReader reader = new BeanReader();
        //reader.getXMLIntrospector().setAttributesForPrimitives( true );
        reader.registerBeanClass( "SyncRules", XmlRulesFile.class );
        reader.registerBeanClass( "RuleSet", XmlRuleSet.class );
        
        XmlRulesFile file = (XmlRulesFile)reader.parse( in );
        return file;
    }
    */
}
