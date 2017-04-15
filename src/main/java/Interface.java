import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;


public class Interface {

	private JFrame frame;
	private JTextField textField;
	
	//POSTagger posTagger = new POSTagger();
	TaggerAndParser taggerAndParser = new TaggerAndParser();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface window = new Interface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(30, 61, 356, 64);
		frame.getContentPane().add(textField);
		textField.setColumns(10);		
		
		JLabel lblDescription = new JLabel("Description");
		lblDescription.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDescription.setBounds(30, 27, 82, 23);
		frame.getContentPane().add(lblDescription);
		
		JButton btnGenerateVrml = new JButton("Generate VRML");
		btnGenerateVrml.setBounds(76, 136, 142, 23);
		frame.getContentPane().add(btnGenerateVrml);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(243, 136, 89, 23);
		frame.getContentPane().add(btnClear);
		
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblOutput.setBounds(30, 185, 46, 14);
		frame.getContentPane().add(lblOutput);
		
		final JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(30, 209, 356, 41);
		frame.getContentPane().add(lblNewLabel);
		
		btnGenerateVrml.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String input =textField.getText();
				System.out.println("User Input: " +input);
				try {
					String output=taggerAndParser.tagContent(input);
					lblNewLabel.setText(output);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				textField.setText("");
			}
		});
		
	}
}
