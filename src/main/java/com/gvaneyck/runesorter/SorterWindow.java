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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.gvaneyck.util.Callback;
import com.gvaneyck.util.Tuple;

public class SorterWindow extends JFrame {
    private static final long serialVersionUID = -4909493574753556479L;

    public static final int MIN_WIDTH = 500;
    public static final int MIN_HEIGHT = 640;
    public static final int SIDE_WIDTH = 250;
    
    private final JButton btnSort = new JButton("Sort Alphabetically");
    private final JButton btnMoveUp = new JButton("Move Up");
    private final JButton btnMoveDown = new JButton("Move Down");

    private final JSeparator sep1 = new JSeparator(JSeparator.HORIZONTAL);

    private final JLabel lblRunePages = new JLabel("Rune Pages");
    private final RunePageListModel lstRuneModel = new RunePageListModel();
    private final JList<RunePage> lstRunePages = new JList<RunePage>(lstRuneModel);
    private final JScrollPane lstRuneScroll = new JScrollPane(lstRunePages);

    private final JLabel lblMasteryPages = new JLabel("Mastery Pages");
    private final MasteryPageListModel lstMasteryModel = new MasteryPageListModel();
    private final JList<MasteryPage> lstMasteryPages = new JList<MasteryPage>(lstMasteryModel);
    private final JScrollPane lstMasteryScroll = new JScrollPane(lstMasteryPages);
    
    private final JLabel lblSide1 = new JLabel("");
    
    private final JSeparator sep2 = new JSeparator(JSeparator.HORIZONTAL);
    
    private final JLabel lblSearch = new JLabel("Search & Copy");
    private final JTextField txtSearch = new JTextField();
    
    private final JLabel lblRunePages2 = new JLabel("Rune Pages");
    private final RunePageListModel lstRuneModel2 = new RunePageListModel();
    private final JList<RunePage> lstRunePages2 = new JList<RunePage>(lstRuneModel2);
    private final JScrollPane lstRuneScroll2 = new JScrollPane(lstRunePages2);
    
    private final JLabel lblMasteryPages2 = new JLabel("Mastery Pages");
    private final MasteryPageListModel lstMasteryModel2 = new MasteryPageListModel();
    private final JList<MasteryPage> lstMasteryPages2 = new JList<MasteryPage>(lstMasteryModel2);
    private final JScrollPane lstMasteryScroll2 = new JScrollPane(lstMasteryPages2);
    
    private final JLabel lblSide2 = new JLabel("");
    
    private final JButton btnCopyRune = new JButton("^^^ Copy Rune Page ^^^");
    private final JButton btnCopyMastery = new JButton("^^^ Copy Mastery Page ^^^");

    private int lastSelectedRunes = -1;
    private int lastSelectedMasteries = -1;
    private boolean changingSelection = false;
    
    private int width = MIN_WIDTH;
    private int height = MIN_HEIGHT;

    // Callbacks
    public Callback runeSorterListener;
    public Callback masterySorterListener;
    public Callback runeSwapListener;
    public Callback masterySwapListener;
    public Callback runeSelectListener;
    public Callback masterySelectListener;
    
    public Callback searchListener;
    public Callback runeSelectListener2;
    public Callback masterySelectListener2;
    
    public Callback copyRuneListener;
    public Callback copyMasteryListener;

    public SorterWindow() {
        setTitle("Rune/Mastery Page Manager");
        
        // GUI settings
        lstRunePages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lblSide1.setVerticalAlignment(SwingConstants.TOP);
        lblSide2.setVerticalAlignment(SwingConstants.TOP);

        // Initially grey out buttons
        btnSort.setEnabled(false);
        btnMoveUp.setEnabled(false);
        btnMoveDown.setEnabled(false);
        btnCopyRune.setVisible(false);
        btnCopyMastery.setVisible(false);

        // Add the items
        Container pane = getContentPane();
        pane.setLayout(null);

        pane.add(btnSort);
        pane.add(btnMoveUp);
        pane.add(btnMoveDown);

        pane.add(sep1);

        pane.add(lblRunePages);
        pane.add(lstRuneScroll);
        pane.add(lblMasteryPages);
        pane.add(lstMasteryScroll);
        
        pane.add(lblSide1);
        
        pane.add(sep2);
        
        pane.add(lblSearch);
        pane.add(txtSearch);

        pane.add(lblRunePages2);
        pane.add(lstRuneScroll2);
        pane.add(lblMasteryPages2);
        pane.add(lstMasteryScroll2);
        
        pane.add(lblSide2);
        
        pane.add(btnCopyRune);
        pane.add(btnCopyMastery);
        
        // Minor Z order edit
        pane.setComponentZOrder(btnCopyRune, 0);
        pane.setComponentZOrder(btnCopyMastery, 1);
        pane.setComponentZOrder(sep2, 2);

        // Listeners
        btnSort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (lastSelectedRunes != -1)
                    if (runeSorterListener != null)
                        runeSorterListener.callback(null);

                if (lastSelectedMasteries != -1)
                    if (masterySorterListener != null)
                        masterySorterListener.callback(null);
            }
        });

        btnMoveUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (lastSelectedRunes != -1 && lastSelectedRunes != 0) {
                    if (runeSwapListener != null)
                        runeSwapListener.callback(new Tuple(lastSelectedRunes, lastSelectedRunes - 1));

                    lastSelectedRunes--;
                    lstRunePages.setSelectedIndex(lastSelectedRunes);
                }
                if (lastSelectedMasteries != -1 && lastSelectedMasteries != 0) {
                    if (masterySwapListener != null)
                        masterySwapListener.callback(new Tuple(lastSelectedMasteries, lastSelectedMasteries - 1));

                    lastSelectedMasteries--;
                    lstMasteryPages.setSelectedIndex(lastSelectedMasteries);
                }
            }
        });

        btnMoveDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (lastSelectedRunes != -1 && lastSelectedRunes != lstRunePages.getModel().getSize() - 1) {
                    if (runeSwapListener != null)
                        runeSwapListener.callback(new Tuple(lastSelectedRunes, lastSelectedRunes + 1));

                    lastSelectedRunes++;
                    lstRunePages.setSelectedIndex(lastSelectedRunes);
                }
                if (lastSelectedMasteries != -1 && lastSelectedMasteries != lstMasteryPages.getModel().getSize() - 1) {
                    if (masterySwapListener != null)
                        masterySwapListener.callback(new Tuple(lastSelectedMasteries, lastSelectedMasteries + 1));

                    lastSelectedMasteries++;
                    lstMasteryPages.setSelectedIndex(lastSelectedMasteries);
                }
            }
        });

        lstRunePages.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (changingSelection)
                    return;

                changingSelection = true;
                lastSelectedRunes = lstRunePages.getSelectedIndex();
                lstMasteryPages.clearSelection();
                lastSelectedMasteries = -1;
                setButtonState();
                changingSelection = false;
                
                if (runeSelectListener != null)
                    runeSelectListener.callback(lastSelectedRunes);
            }
        });

        lstMasteryPages.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (changingSelection)
                    return;

                changingSelection = true;
                lastSelectedMasteries = lstMasteryPages.getSelectedIndex();
                lastSelectedRunes = -1;
                lstRunePages.clearSelection();
                setButtonState();
                changingSelection = false;

                if (masterySelectListener != null)
                    masterySelectListener.callback(lastSelectedMasteries);
            }
        });
        
        txtSearch.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n' && searchListener != null)
                    searchListener.callback(txtSearch.getText());
            }
            
            public void keyReleased(KeyEvent e) { }
            public void keyPressed(KeyEvent e) { }
        });
        
        lstRunePages2.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (changingSelection)
                    return;

                changingSelection = true;
                lstMasteryPages2.clearSelection();
                setButtonState();
                changingSelection = false;

                if (runeSelectListener2 != null)
                    runeSelectListener2.callback(lstRunePages2.getSelectedIndex());
            }
        });
        
        lstMasteryPages2.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (changingSelection)
                    return;

                changingSelection = true;
                lstRunePages2.clearSelection();
                setButtonState();
                changingSelection = false;

                if (masterySelectListener2 != null)
                    masterySelectListener2.callback(lstMasteryPages2.getSelectedIndex());
                setButtonState();
            }
        });
        
        btnCopyRune.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int lastSelectedRunes2 = lstRunePages2.getSelectedIndex();
                if (lastSelectedRunes != -1 && lastSelectedRunes2 != -1 && copyRuneListener != null) {
                	copyRuneListener.callback(new Tuple(lastSelectedRunes, lastSelectedRunes2));
                }
            }
        });

        btnCopyMastery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int lastSelectedMasteries2 = lstMasteryPages2.getSelectedIndex();
                if (lastSelectedMasteries != -1 && lastSelectedMasteries2 != -1 && copyMasteryListener != null) {
                	copyMasteryListener.callback(new Tuple(lastSelectedMasteries, lastSelectedMasteries2));
                }
            }
        });

        pane.addHierarchyBoundsListener(new HierarchyBoundsListener() {
            public void ancestorMoved(HierarchyEvent e) {}

            public void ancestorResized(HierarchyEvent e) {
                Dimension d = getSize();
                width = d.width;
                height = d.height;
                doMyLayout();
            }
        });

        // Window settings
        setSize(width, height);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    public void setButtonState() {
    	int lastSelectedRunes2 = lstRunePages2.getSelectedIndex();
    	int lastSelectedMasteries2 = lstMasteryPages2.getSelectedIndex();
    	
		btnCopyRune.setVisible(false);
		btnCopyMastery.setVisible(false);
    	if (lastSelectedRunes != -1 && lastSelectedRunes2 != -1)
    		btnCopyRune.setVisible(true);
    	else if (lastSelectedMasteries != -1 && lastSelectedMasteries2 != -1)
    		btnCopyMastery.setVisible(true);
    }
    
    public void enableButtons() {
        btnSort.setEnabled(true);
        btnMoveUp.setEnabled(true);
        btnMoveDown.setEnabled(true);
    }
    
    public void disableButtons() {
        btnSort.setEnabled(false);
        btnMoveUp.setEnabled(false);
        btnMoveDown.setEnabled(false);
    }
    
    public void updateRunePages(List<RunePage> pages) {
        lstRuneModel.clear();
        for (RunePage page : pages)
            lstRuneModel.add(page);
        lstRuneModel.update();
    }
    
    public void updateMasteryPages(List<MasteryPage> pages) {
        lstMasteryModel.clear();
        for (MasteryPage page : pages)
            lstMasteryModel.add(page);
        lstMasteryModel.update();
    }
    
    public void updateRunePages2(List<RunePage> pages) {
        lstRuneModel2.clear();
        lstRunePages2.clearSelection();
        if (pages != null)
            for (RunePage page : pages)
                lstRuneModel2.add(page);
        lstRuneModel2.update();
        runeSelectListener2.callback(-1);
    }
    
    public void updateMasteryPages2(List<MasteryPage> pages) {
        lstMasteryModel2.clear();
        lstMasteryPages2.clearSelection();
        if (pages != null)
            for (MasteryPage page : pages)
            	lstMasteryModel2.add(page);
        lstMasteryModel2.update();
        masterySelectListener2.callback(-1);
    }
    
    public void setInfo1(String text) {
        lblSide1.setText(text);
    }
    
    public void setInfo2(String text) {
        lblSide2.setText(text);
    }
    
    private void doMyLayout() {
        Insets i = getInsets();
        int twidthfull = width - i.left - i.right;
        int twidth = twidthfull - SIDE_WIDTH;
        int theight = height - i.top - i.bottom;

        btnSort.setBounds(5, 5, twidth - 10, 24);
        btnMoveUp.setBounds(5, 34, twidth / 2 - 10, 24);
        btnMoveDown.setBounds(5 + twidth / 2, 34, twidth / 2 - 10, 24);

        sep1.setBounds(5, 63, twidth - 10, 5);

        int scrollHeight = (theight - 180) / 4;
        lblRunePages.setBounds(5, 65, twidth - 9, 15);
        lstRuneScroll.setBounds(5, 80, twidth - 9, scrollHeight);
        lblMasteryPages.setBounds(5, 80 + scrollHeight, twidth - 9, 15);
        lstMasteryScroll.setBounds(5, 95 + scrollHeight, twidth - 9, scrollHeight);
        
        lblSide1.setBounds(twidth + 5, 5, SIDE_WIDTH - 10, 95 + 2 * scrollHeight);
        
        sep2.setBounds(5, 100 + 2 * scrollHeight, twidthfull - 10, 5);
        
        lblSearch.setBounds(5, 105 + 2 * scrollHeight, twidth - 9, 15);
        txtSearch.setBounds(5, 120 + 2 * scrollHeight, twidth - 9, 24);
        
        lblRunePages2.setBounds(5, 145 + 2 * scrollHeight, twidth - 9, 15);
        lstRuneScroll2.setBounds(5, 160 + 2 * scrollHeight, twidth - 9, scrollHeight);
        lblMasteryPages2.setBounds(5, 160 + 3 * scrollHeight, twidth - 9, 15);
        lstMasteryScroll2.setBounds(5, 175 + 3 * scrollHeight, twidth - 9, scrollHeight);
        
        lblSide2.setBounds(twidth + 5, 120 + 2 * scrollHeight, SIDE_WIDTH - 10, scrollHeight + 30);
        
        btnCopyRune.setBounds(twidth + 25, 87 + 2 * scrollHeight, SIDE_WIDTH - 50, 24);
        btnCopyMastery.setBounds(twidth + 25, 87 + 2 * scrollHeight, SIDE_WIDTH - 50, 24);
    }
}
