import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;


public class Interface {

	private JFrame frame;
	private JTextArea textField;
	
	//POSTagger posTagger = new POSTagger();
	TaggerAndParser2 taggerAndParser = new TaggerAndParser2();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
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
		frame.setBounds(100, 100, 481, 355);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextArea();
		textField.setLineWrap(true);
		textField.setBounds(30, 75, 409, 81);
		frame.getContentPane().add(textField);
		textField.setColumns(10);		
		
		JLabel lblDescription = new JLabel("Enter Description");
		lblDescription.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDescription.setBounds(30, 11, 119, 23);
		frame.getContentPane().add(lblDescription);
		
		JButton btnGenerateVrml = new JButton("Generate VRML");
		btnGenerateVrml.setFont(new Font("Rockwell", Font.PLAIN, 14));
		btnGenerateVrml.setIcon(new ImageIcon("C:\\Users\\Dell\\Desktop\\Mimetype-make-icon.png"));
		btnGenerateVrml.setBounds(30, 180, 191, 50);
		frame.getContentPane().add(btnGenerateVrml);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setFont(new Font("Rockwell", Font.PLAIN, 14));
		btnClear.setIcon(new ImageIcon("C:\\Users\\Dell\\Desktop\\Actions-edit-clear-icon.png"));
		btnClear.setBounds(272, 180, 167, 50);
		frame.getContentPane().add(btnClear);
		
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblOutput.setBounds(40, 241, 66, 23);
		frame.getContentPane().add(lblOutput);
		
		final JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(40, 265, 283, 41);
		frame.getContentPane().add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Upload");
		btnNewButton.setFont(new Font("Rockwell", Font.PLAIN, 14));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FileUpload.uploadFile();
			}
		});
		btnNewButton.setBounds(350, 265, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblNewLabel_1 = new JLabel("example : There is a brown table.  There is red chair right to the table.");
		lblNewLabel_1.setBounds(30, 34, 409, 30);
		frame.getContentPane().add(lblNewLabel_1);
		
		btnGenerateVrml.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String input =textField.getText();
				System.out.println("User Input: " +input);
				try {
					String output=taggerAndParser.tagContent(input);
					lblNewLabel.setText(output);
					
				/*	//Opening the file in browser
					String url="generated.wrl";
					File vrmlFile = new File(url);
					Desktop.getDesktop().browse(vrmlFile.toURI()); */
					
					//opening file with bs contact
					try
					{
					    Process p = Runtime.getRuntime().exec("C:/Users/Dell/AppData/Local/Bitmanagement Software/BS Contact/x64/BSContact.exe D:/Eclipse-Neon-Workspace/VRMLGenerator2/generated.wrl");
					    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
					    stdInput.close();
					}
					catch(Exception k)
					{
					    k.printStackTrace();
					}
				} catch (IOException e) {
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
