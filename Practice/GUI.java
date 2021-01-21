package Practice;


import java.awt.BorderLayout;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;



import javax.swing.JButton;

import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class GUI extends JFrame {
	private JTextField Name;
	private JTextField Port;
	private JTextField IP;

	public GUI() {
		//������ â ����
		setTitle("�ϴ��� ä���ϱ� ���α׷�");
		setBounds(10, 50, 400, 172);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//panel ����
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		//Server ��ư
		JButton btnServer = new JButton("Server");
		//Client ��ư
		JButton btnClient = new JButton("Client");
		

		//Sercer ��ư�� Ŭ������ ��
		btnServer.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//ServerFrame ��ü ����
				String port = Port.getText();
				Port.setText("");
				String name = Name.getText();
				Name.setText("");
				//���� �Է��ϸ�?
				if(port.equals("")||name.equals(""))
				{
					//���â
					JOptionPane.showMessageDialog(null, "��Ʈ ��ȣ�� �̸� �� �� �Է��ϼž� �մϴ�.");
				}
				else {
					//���� �ƴ� �� Ȯ���ϸ� ����
					ServerFrame frame = new ServerFrame(port, name);
				}
			}
		});

		//Client ��ư�� Ŭ������ ��
		btnClient.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//ClientFrame ��ü ����
				String ip = IP.getText();
				IP.setText("");
				String port = Port.getText();
				Port.setText("");
				String name = Name.getText();
				Name.setText("");
				//���� �Է��ϸ�?
				if(port.equals("")||name.equals("")||ip.equals(""))
				{
					//���â
					JOptionPane.showMessageDialog(null, "��Ʈ ��ȣ�� �̸�, ip �ּ� ��� �Է��ϼž� �մϴ�.");
				}
				else {
					//���� �ƴ� �� Ȯ���ϸ� ����
					ClientFrame frame = new ClientFrame(ip, port, name);
				}
			}

		});

		//���� ��ư�� Ŭ���̾�Ʈ ��ư panel�� �߰�
		panel.add(btnServer);
		panel.add(btnClient);

		//panel�� windowâ �Ʒ��� ��ġ�ϵ��� ���̾ƿ� ����
		getContentPane().add(panel, BorderLayout.SOUTH);

		//����� �̸�, IP�ּ�, ��Ʈ ��ȣ �Է��ϴ� panel ���� �� ���̾ƿ� ����
		JPanel Name_panel = new JPanel();
		//window â���� center�� ������ ����
		getContentPane().add(Name_panel, BorderLayout.CENTER);
		Name_panel.setLayout(null);
		
		//�̸� �Է�
		JLabel name_label = new JLabel("�̸�");
		name_label.setBounds(122, 60, 37, 15);
		Name_panel.add(name_label);
		Name = new JTextField();
		Name.setBounds(171, 57, 96, 21);
		Name_panel.add(Name);
		Name.setColumns(10);
		
		//��Ʈ ��ȣ �Է�
		JLabel lblNewLabel = new JLabel("Port No");
		lblNewLabel.setBounds(34, 25, 52, 15);
		Name_panel.add(lblNewLabel);
		Port = new JTextField();
		Port.setBounds(86, 22, 96, 21);
		Port.setColumns(10);
		Name_panel.add(Port);
		
		//IP �Է�
		JLabel lblNewLabel_1 = new JLabel("IP");
		lblNewLabel_1.setBounds(208, 25, 22, 15);
		Name_panel.add(lblNewLabel_1);
		IP = new JTextField();
		IP.setBounds(235, 22, 96, 21);
		IP.setColumns(10);
		Name_panel.add(IP);
		

		setVisible(true);
	}



	public static void main(String[] args) {
		new GUI();
	}

}