package com.gvaneyck.runesorter;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.gvaneyck.rtmp.ServerInfo;

public class SettingsWindow extends JDialog {
    private static final long serialVersionUID = -292661627143118163L;

    private final JLabel lblRegion = new JLabel("Region");
    private final JComboBox<ServerInfo> cboRegion = new JComboBox<ServerInfo>();
    
    private final JLabel lblUsername = new JLabel("Username");
    private final JTextField txtUsername = new JTextField();
    
    private final JLabel lblPassword = new JLabel("Password");
    private final JTextField txtPassword = new JPasswordField();
    
    private final JLabel lblClientVersion = new JLabel("Client Version");
    private final JTextField txtClientVersion = new JTextField();
    
    private final JLabel lblCVHelp = new JLabel("Client Version can be found at the top left of the LoL client (e.g. 3.9.whatever)");
    
    private final JButton btnOk = new JButton("OK");
    private final JButton btnCancel = new JButton("Cancel");
    
    private String configFile;
    
    private int width = 455;
    private int height = 215;

    public static Map<String, ServerInfo> regionMap;
    
    static {
        regionMap = new HashMap<String, ServerInfo>();
        regionMap.put(ServerInfo.NA.name.toUpperCase(), ServerInfo.NA);
        regionMap.put(ServerInfo.EUW.name.toUpperCase(), ServerInfo.EUW);
        regionMap.put(ServerInfo.EUNE.name.toUpperCase(), ServerInfo.EUNE);
        regionMap.put(ServerInfo.KR.name.toUpperCase(), ServerInfo.KR);
        regionMap.put(ServerInfo.BR.name.toUpperCase(), ServerInfo.BR);
        regionMap.put(ServerInfo.TR.name.toUpperCase(), ServerInfo.TR);
        regionMap.put(ServerInfo.RU.name.toUpperCase(), ServerInfo.RU);
        regionMap.put(ServerInfo.LAN.name.toUpperCase(), ServerInfo.LAN);
        regionMap.put(ServerInfo.LAS.name.toUpperCase(), ServerInfo.LAS);
        regionMap.put(ServerInfo.OCE.name.toUpperCase(), ServerInfo.OCE);
        regionMap.put(ServerInfo.PBE.name.toUpperCase(), ServerInfo.PBE);
        regionMap.put(ServerInfo.TW.name.toUpperCase(), ServerInfo.TW);
        regionMap.put(ServerInfo.SG.name.toUpperCase(), ServerInfo.SG);
        regionMap.put(ServerInfo.PH.name.toUpperCase(), ServerInfo.PH);
        regionMap.put(ServerInfo.TH.name.toUpperCase(), ServerInfo.TH);
        regionMap.put(ServerInfo.VN.name.toUpperCase(), ServerInfo.VN);
    }
    
    public SettingsWindow(String configFile) {        
        this.configFile = configFile;
        initWindow();
        loadConfig();
    }
    
    private void loadConfig() {
        File conf = new File("config.txt");
        Map<String, String> config = new HashMap<String, String>();

        if (conf.exists()) {
            try {
                String line;
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(conf), "UTF-8"));
                while ((line = in.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length != 2)
                        continue;

                    // Handle notepad saving extra bytes for UTF8
                    if (parts[0].charAt(0) == 65279)
                        parts[0] = parts[0].substring(1);

                    config.put(parts[0].trim(), parts[1].trim());
                }
                in.close();
            }
            catch (IOException e) {
                System.out.println("Encountered an error when parsing config.txt");
            }
        }
        
        if (config.containsKey("version"))
            txtClientVersion.setText(config.get("version"));
        else
            txtClientVersion.requestFocus();

        if (config.containsKey("password"))
            txtPassword.setText(config.get("password"));
        else
            txtPassword.requestFocus();
    
        if (config.containsKey("username"))
            txtUsername.setText(config.get("username"));
        else
            txtUsername.requestFocus();

        if (config.containsKey("region")) {
            String region = config.get("region");
            if (regionMap.containsKey(region.toUpperCase())) {
                cboRegion.setSelectedItem(regionMap.get(region.toUpperCase()));
            }
        }
        else {
            cboRegion.requestFocus();
        }
    }
    
    private void writeConfig() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"));
            out.write("username=" + txtUsername.getText() + "\r\n");
            //out.write("password=" + txtPassword.getText() + "\r\n"); // Don't save password by default
            out.write("version=" + txtClientVersion.getText() + "\r\n");
            out.write("region=" + cboRegion.getSelectedItem() + "\r\n");
            out.close();
        }
        catch (IOException e) {
            System.out.println("Encountered an error when writing to " + configFile + ":");
            e.printStackTrace();
        }
    }
    
    private void initWindow() {
        setTitle("Settings");
        
        cboRegion.addItem(ServerInfo.NA);
        cboRegion.addItem(ServerInfo.EUW);
        cboRegion.addItem(ServerInfo.EUNE);
        cboRegion.addItem(ServerInfo.KR);
        cboRegion.addItem(ServerInfo.BR);
        cboRegion.addItem(ServerInfo.TR);
        cboRegion.addItem(ServerInfo.RU);
        cboRegion.addItem(ServerInfo.LAN);
        cboRegion.addItem(ServerInfo.LAS);
        cboRegion.addItem(ServerInfo.OCE);
        cboRegion.addItem(ServerInfo.PBE);
        cboRegion.addItem(ServerInfo.TW);
        cboRegion.addItem(ServerInfo.SG);
        cboRegion.addItem(ServerInfo.PH);
        cboRegion.addItem(ServerInfo.TH);
        cboRegion.addItem(ServerInfo.VN);

        Container pane = getContentPane();
        pane.setLayout(null);

        pane.add(lblRegion);
        pane.add(cboRegion);
        pane.add(lblUsername);
        pane.add(txtUsername);
        pane.add(lblPassword);
        pane.add(txtPassword);
        pane.add(lblClientVersion);
        pane.add(txtClientVersion);
        pane.add(lblCVHelp);
        pane.add(btnOk);
        pane.add(btnCancel);
        
        lblRegion.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
        lblClientVersion.setHorizontalAlignment(SwingConstants.RIGHT);

        pane.addHierarchyBoundsListener(new HierarchyBoundsListener() {
            public void ancestorMoved(HierarchyEvent e) {}

            public void ancestorResized(HierarchyEvent e) {
                Dimension d = getSize();
                width = d.width;
                height = d.height;
                doMyLayout();
            }
        });
        
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writeConfig();
                setVisible(false);
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        KeyListener enterListener = new KeyListener() {
            public void keyTyped(KeyEvent e) { 
                if (e.getKeyChar() == '\n') {
                    writeConfig();
                    setVisible(false);
                }
            }
            
            public void keyReleased(KeyEvent e) { }
            public void keyPressed(KeyEvent e) { }
        };
        
        cboRegion.addKeyListener(enterListener);
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
        txtClientVersion.addKeyListener(enterListener);
        
        setSize(width, height);
        setMinimumSize(new Dimension(width, height));
        setVisible(true);
        
        doMyLayout();
    }
    
    private void doMyLayout() {
        Insets i = getInsets();
        int twidth = width - i.left - i.right;
        
        int lblwidth = 80;
        
        lblRegion.setBounds(5, 5, lblwidth, 24);
        cboRegion.setBounds(lblwidth + 10, 5, twidth - lblwidth - 15, 24);

        lblUsername.setBounds(5, 34, lblwidth, 24);
        txtUsername.setBounds(lblwidth + 10, 34, twidth - lblwidth - 15, 24);

        lblPassword.setBounds(5, 63, lblwidth, 24);
        txtPassword.setBounds(lblwidth + 10, 63, twidth - lblwidth - 15, 24);

        lblClientVersion.setBounds(5, 92, lblwidth, 24);
        txtClientVersion.setBounds(lblwidth + 10, 92, twidth - lblwidth - 15, 24);

        lblCVHelp.setBounds(5, 121, twidth - 10, 24);
        
        int btnwidth = 80;
        btnOk.setBounds(twidth - btnwidth * 2 - 10, 160, btnwidth, 24);
        btnCancel.setBounds(twidth - btnwidth - 5, 160, btnwidth, 24);
    }
    
    public Settings getSettings() {
        return new Settings(
                txtUsername.getText(), 
                txtPassword.getText(), 
                txtClientVersion.getText(), 
                (ServerInfo)cboRegion.getSelectedItem());
    }
}
