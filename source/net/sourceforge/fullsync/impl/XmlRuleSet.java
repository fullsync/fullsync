package net.sourceforge.fullsync.impl;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;



/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class XmlRuleSet extends AbstractRuleSet
{
    public XmlRuleSet()
    {
        
    }

    public AbstractRuleSet createChild( InputStream in, String filename ) 
    {
        XmlRulesFile file = null;
        try {
            file = XmlRulesFile.getXmlRulesFile( in );
        } catch( IntrospectionException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch( SAXException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        XmlRuleSet ruleSet = file.getRuleSet( this.getName() );
        return ruleSet;
    }
}
