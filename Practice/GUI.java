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
		//윈도우 창 설정
		setTitle("일대일 채팅하기 프로그램");
		setBounds(10, 50, 400, 172);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//panel 생성
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		//Server 버튼
		JButton btnServer = new JButton("Server");
		//Client 버튼
		JButton btnClient = new JButton("Client");
		

		//Sercer 버튼을 클릭했을 때
		btnServer.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//ServerFrame 객체 생성
				String port = Port.getText();
				Port.setText("");
				String name = Name.getText();
				Name.setText("");
				//공백 입력하면?
				if(port.equals("")||name.equals(""))
				{
					//경고창
					JOptionPane.showMessageDialog(null, "포트 번호와 이름 둘 다 입력하셔야 합니다.");
				}
				else {
					//공백 아닌 거 확인하면 실행
					ServerFrame frame = new ServerFrame(port, name);
				}
			}
		});

		//Client 버튼을 클릭했을 때
		btnClient.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				//ClientFrame 객체 생성
				String ip = IP.getText();
				IP.setText("");
				String port = Port.getText();
				Port.setText("");
				String name = Name.getText();
				Name.setText("");
				//공백 입력하면?
				if(port.equals("")||name.equals("")||ip.equals(""))
				{
					//경고창
					JOptionPane.showMessageDialog(null, "포트 번호와 이름, ip 주소 모두 입력하셔야 합니다.");
				}
				else {
					//공백 아닌 거 확인하면 실행
					ClientFrame frame = new ClientFrame(ip, port, name);
				}
			}

		});

		//서버 버튼과 클라이언트 버튼 panel에 추가
		panel.add(btnServer);
		panel.add(btnClient);

		//panel을 window창 아래에 위치하도록 레이아웃 설정
		getContentPane().add(panel, BorderLayout.SOUTH);

		//사용자 이름, IP주소, 포트 번호 입력하는 panel 생성 및 레이아웃 설정
		JPanel Name_panel = new JPanel();
		//window 창에서 center에 오도록 설정
		getContentPane().add(Name_panel, BorderLayout.CENTER);
		Name_panel.setLayout(null);
		
		//이름 입력
		JLabel name_label = new JLabel("이름");
		name_label.setBounds(122, 60, 37, 15);
		Name_panel.add(name_label);
		Name = new JTextField();
		Name.setBounds(171, 57, 96, 21);
		Name_panel.add(Name);
		Name.setColumns(10);
		
		//포트 번호 입력
		JLabel lblNewLabel = new JLabel("Port No");
		lblNewLabel.setBounds(34, 25, 52, 15);
		Name_panel.add(lblNewLabel);
		Port = new JTextField();
		Port.setBounds(86, 22, 96, 21);
		Port.setColumns(10);
		Name_panel.add(Port);
		
		//IP 입력
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