package umm.digiquilt.view.challenge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import umm.digiquilt.model.Challenge;

/**
 * A panel to display various challenges to try.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-16 23:17:14 $
 * @version $Revision: 1.3 $
 *
 */
//@SuppressWarnings("serial")
// Remove suppression to see impact
public class ChallengePanel extends JPanel{
    
    /**
     * The background color of the challenge panel.
     */
    public static final Color CHALLENGE_COLOR = new Color(146, 149, 249);
    
    /**
     * Size to set to the text area.
     */
    private static final Dimension TEXTDISPLAYSIZE = new Dimension(1000, 40);

    /**
     * The challenge box.
     */
    private JComboBox<Challenge> challengeList;
    
    /**Create a ChallengePanel to display the given challenges.
     * @param list a list of all challenges to show.
     */
    public ChallengePanel(ComboBoxModel<Challenge> list){
        challengeList = new JComboBox<>(list);
        challengeList.setName("Challenge box");
        challengeList.setEditable(false);
        challengeList.setFont(challengeList.getFont().deriveFont(Font.BOLD, 17));
        challengeList.setMinimumSize(TEXTDISPLAYSIZE);
        challengeList.setPreferredSize(TEXTDISPLAYSIZE);
        
//        JButton nextButton = new JButton("Next");
//        nextButton.setName("Next");
//        nextButton.addActionListener(new ActionListener(){
//
//            public void actionPerformed(ActionEvent e) {
//                int index = comboBox.getSelectedIndex();
//                index++;
//                if (index < comboBox.getItemCount()){
//                    comboBox.setSelectedIndex(index);
//                }
//            }
//        });
//        JButton prevButton = new JButton("Previous");
//        prevButton.setName("Previous");
//        prevButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e){
//                int index = comboBox.getSelectedIndex();
//                index--;
//                if (index < comboBox.getItemCount()){
//                    comboBox.setSelectedIndex(index);
//                }
//            }
//        });
        this.setBackground(CHALLENGE_COLOR);
//        this.add(prevButton);
        this.add(challengeList);
//        this.add(nextButton);
        validate();
    }

    /**
     * @param listener
     */
    public void addActionListener(ActionListener listener) {
        challengeList.addActionListener(listener);
    }

    
}
