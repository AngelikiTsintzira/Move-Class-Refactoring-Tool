package gr.uom.java.metric.probability.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PropagationFactorDialog extends JDialog implements ActionListener {
	private JTextField textField;
	private JButton okButton;
	private ProbabilitySetDialog owner;
	private double initialPropagationFactor;
	
	public PropagationFactorDialog(ProbabilitySetDialog owner, double initialPropagationFactor) {
		super(owner, "Set propagation factor", true);
		this.owner = owner;
		this.initialPropagationFactor = initialPropagationFactor;
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		JPanel labelPane = new JPanel();
		labelPane.add(new JLabel("Value must be between 0.0 and 1.0"));
		labelPane.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		
		panel.add(labelPane);
		panel.add(Box.createRigidArea(new Dimension(0,5)));
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		textField = new JTextField(10);
		textField.setText(String.valueOf(initialPropagationFactor));
		textField.setSelectionStart(0);
		textField.setSelectionEnd(textField.getText().length());
		buttonPane.add(textField);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		buttonPane.add(okButton);
		
		panel.add(buttonPane);
		
		this.setContentPane(panel);
		this.setSize(230,100);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == okButton) {
			double propagationFactor;
			try {
				propagationFactor = Double.parseDouble(textField.getText());
				if(propagationFactor < 0.0 || propagationFactor > 1.0) {
					JOptionPane.showMessageDialog(this,
				    "The entered value must be between 0.0 and 1.0",
				    "Input error",
				    JOptionPane.ERROR_MESSAGE);
					textField.setText(String.valueOf(initialPropagationFactor));
					textField.requestFocusInWindow();
					textField.setSelectionStart(0);
					textField.setSelectionEnd(textField.getText().length());
				}
				else {
					owner.setPropagationFactor(propagationFactor);
					this.setVisible(false);
				}
			}
			catch(NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this,
				    "The entered value must be double.",
				    "Input error",
				    JOptionPane.ERROR_MESSAGE);
				textField.setText(String.valueOf(initialPropagationFactor));
				textField.requestFocusInWindow();
				textField.setSelectionStart(0);
				textField.setSelectionEnd(textField.getText().length());
			}
		}
	}
}