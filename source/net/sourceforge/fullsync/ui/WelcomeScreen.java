package net.sourceforge.fullsync.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import org.eclipse.swt.widgets.Shell;

public class WelcomeScreen {
	
	private JFrame frame;
	private JButton okButton;
	private JCheckBox displayAgain;
	private JLabel title;
	private JLabel img;
	private JLabel releases;
	private JLabel releasesTitle;
	
	private String JLabelTitle = "Welcome to FullSync";
	private String releasesText = "<html>";//doc.body().select("li").select("ul")
	
	private java.awt.Container pane;
	
	public WelcomeScreen(final Shell shell) throws IOException{
		//shell.setEnabled(false);
		initComponents(shell);
		initJFrame();
		initJFrameLayout();
		okButton.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	okButton(shell);
            }
		});
	}
	
	private void okButton(Shell shell){
    	frame.dispose();
        //shell.setEnabled(true);
	}
	
	private void initJFrame(){
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocation(140, 130);
	}
	
	private void initComponents(final Shell shell) throws IOException{
		frame = new JFrame("Welcome to FullSync");
		okButton = new JButton("Close");
		title = new JLabel(JLabelTitle + " - " + getVersion());
		img = new JLabel(new ImageIcon("images/About.png"));
		releases = new JLabel(releasesText + getReleases());
		releasesTitle = new JLabel("<html><br>FullSync / News: Recent posts </html>");
		displayAgain = new JCheckBox("do not display again");
		
		pane = frame.getContentPane();
	}
	
	private void initJFrameLayout(){
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.add(img);
		img.setAlignmentX(Component.CENTER_ALIGNMENT);
		pane.add(title);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setForeground(Color.BLUE);
		title.setFont (title.getFont ().deriveFont (15.0f));
		pane.add(releasesTitle);
		releasesTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		releasesTitle.setForeground(Color.RED);
		pane.add(releases);
		releases.setAlignmentX(Component.CENTER_ALIGNMENT);
		pane.add(okButton);
		okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		pane.add(displayAgain);
		displayAgain.setAlignmentX(Component.CENTER_ALIGNMENT);
		displayAgain.setForeground(Color.GRAY);
		frame.pack();
	}
	
	private String getReleases() throws IOException{
		String releases  = "";
		String url = "http://fullsync.sourceforge.net/";
		try{
			Document doc = Jsoup.connect(url).get();
			releases = parseReleases(doc.body().select("li").select("ul"));
		}catch(IOException e){
			releases = "connection error(!)";
		}
		return releases;
	}
	
	private String parseReleases(Elements el){
		String allReleases = el.toString();
		String tokens[] = allReleases.split(" ");
		int i = 1;
		while(!tokens[i].contains("</ul>")){
			if(tokens[i].contains("<li>")){
				//tokens[i].replace("<li>", " ");
				releasesText += tokens[i] + " ";
			}
			else if(tokens[i].contains("</li>")){
				//tokens[i].replace("</li>", " ");
				releasesText += tokens[i] + "<br>";
			}else{
				releasesText += tokens[i] + " ";
			}
			i++;
		}
		releasesText += "</html>";
		return releasesText;
	}
	
	private String getVersion() throws IOException{
		String version;
		String url = "http://fullsync.sourceforge.net/";
		try{
			Document doc = Jsoup.connect(url).get();
			Elements el = doc.body().select("li");
			version = parseVersion(el);
		}catch(IOException e){
			BufferedReader br = new BufferedReader(new FileReader("CHANGELOG"));
			try{
				version = br.readLine();
			}finally{
				br.close();
			}
		}
		return version;
	}
	
	private String parseVersion(Elements el){
		String temp = el.toString();
		String version = "";
		int start=0;
		for(int i = 0; i<temp.length(); i++){
			if(temp.charAt(i)=='>'){
				start = i;
				break;
			}
		}
		int i = 1;
		while(temp.charAt(start+i)!=':'){
			version += temp.charAt(start+i);
			i++;
		}
		return version;
	}

}
