package net.sourceforge.fullsync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Preferences
{
    public void save();
    /*
     public void loadPreferences(Element rootElement) 
     {
     NodeList generalNodeList = rootElement.getElementsByTagName("General");
     if (generalNodeList.getLength() > 0) 
     {
     Element generalElement = (Element) generalNodeList.item(0);
     NodeList closeButtonPreferencesNodeList = generalPreferencesElement.getElementsByTagName("CloseButtonBehaviour");
     if (closeButtonPreferencesNodeList.getLength() > 0) {
     Element closeButtonPreferencesElement = (Element) closeButtonPreferencesNodeList.item(0);
     askOnClosing = Boolean.valueOf(closeButtonPreferencesElement.getAttribute("ask")).booleanValue();
     closingButtonMinimizes = Boolean.valueOf(closeButtonPreferencesElement.getAttribute("minimize")).booleanValue();
     }
     }
     }
     
     public void savePreferences() {
     try {
     DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
     Document doc = docBuilder.newDocument();
     
     Element rootElement = doc.createElement( "FullSyncPreferences" );
     doc.appendChild(rootElement);
     
     Element generalPreferencesElement = doc.createElement("General");
     rootElement.appendChild(generalPreferencesElement);
     
     Element closeButtonPreferencesElement = doc.createElement("CloseButtonBehaviour");
     closeButtonPreferencesElement.setAttribute("ask", String.valueOf(askOnClosing));
     closeButtonPreferencesElement.setAttribute("minimize", String.valueOf(closingButtonMinimizes));
     generalPreferencesElement.appendChild(closeButtonPreferencesElement);
     
     OutputStream out = new FileOutputStream( configFile );
     
     OutputFormat format = new OutputFormat( doc, "UTF-8", true );
     XMLSerializer serializer = new XMLSerializer ( out, format);
     serializer.asDOMSerializer();
     serializer.serialize(doc);
     
     out.close();
     } catch( Exception e ) {
     // TODO messagebox ?
     ExceptionHandler.reportException( e );
     }

     }
     */
    
    public boolean confirmExit();
    public void setConfirmExit( boolean bool );
    
    public boolean closeMinimizesToSystemTray();
    public void setCloseMinimizesToSystemTray( boolean bool );
    
    public boolean minimizeMinimizesToSystemTray();
    public void setMinimizeMinimizesToSystemTray( boolean bool );
    
    public boolean systemTrayEnabled();
    public void setSystemTrayEnabled( boolean bool );
    
    public String getProfileListStyle();
    public void setProfileListStyle( String profileListStyle );
    
    public boolean listeningForRemoteConnections();
    public void setListeningForRemoteConnections(boolean bool);
    
    public int getRemoteConnectionsPort();
    public void setRemoteConnectionsPort(int port);
    
    public String getRemoteConnectionsPassword();
    public void setRemoteConnectionsPassword(String password);
    
    public boolean showSplashScreen();
    public void setShowSplashScreen(boolean bool);

    public boolean autostartScheduler();
    public void setAutostartScheduler(boolean bool);
    
    public String getLanguageCode();
    public void setLanguageCode(String code);
}