package Practice;



import java.awt.BorderLayout;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.awt.event.KeyAdapter;

import java.awt.event.KeyEvent;

import java.awt.event.WindowAdapter;

import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;

import java.io.DataOutputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;

import java.net.Socket;



import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.JScrollPane;

import javax.swing.JTextArea;

import javax.swing.JTextField;



public class ServerFrame extends JFrame {

	JTextArea textArea;
	JTextField Msg;
	JButton Send;
	ServerSocket serverSocket;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	int port;
	String name;
	private JMenuBar menuBar;
	private JMenu System;
	private JMenuItem exit;

	//Main GUI에서 포트번호,이름 받아오기
	public ServerFrame(String Port, String Name) {		
		//String으로 받아왔기 때문에 Int로 형변환해서 할당
		this.port = Integer.parseInt(Port);
		//받아온 사용자 이름 할당하기
		this.name = Name;

		//윈도우 창 설정하기
		setTitle("서버");
		setBounds(450, 50, 500, 350);

		//TextArea 생성
		textArea = new JTextArea();		
		//TextArea에는 사용자가 입력 접근을 할 수 없도록 설정
		textArea.setEditable(false); //쓰기 금지
		//대화 내용이 많아지면 스크롤 가능하도록 scrollPane 설정, scrollPane에 textarea 넣기
		JScrollPane scrollPane = new JScrollPane(textArea);
		//scrollPane 레이아웃 설정
		add(scrollPane,BorderLayout.CENTER);

				
		//채팅 내용 입력하고 전송할 버튼이 있을 패널 생성
		JPanel msgPanel = new JPanel();
		msgPanel.setLayout(new BorderLayout());
		//msgPanel 레이아웃 설정
		add(msgPanel,BorderLayout.SOUTH);
		
		//채팅 메시지를 입력할 TextField 생성
		Msg = new JTextField();
		//내용 입력 후 필요한 전송 버튼 생성
		Send = new JButton("전송");
		//텍스트 필드 레이아웃 설정
		msgPanel.add(Msg, BorderLayout.CENTER);
		//전송 버튼 레이아웃 설정
		msgPanel.add(Send, BorderLayout.EAST);


		//메뉴바를 새로 만들어서
		menuBar = new JMenuBar();
		//메뉴바 레이아웃 설정
		getContentPane().add(menuBar, BorderLayout.NORTH);
		//거기에 시스템 부분 넣고
		System = new JMenu("시스템");
		menuBar.add(System);
		//시스템 안에다가 종료를 누르도록 하기
		exit = new JMenuItem("종료");
		System.add(exit);

		

		//보내기 버튼 클릭
		Send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//해당 메시지 내용이 보내짐
				//사용자가 입력한 내용 String에 할당하기
				String message = Msg.getText();

				//입력 후 textFeild 비워주기
				Msg.setText("");
				if(!message.contentEquals(""))
					sendMessage(message);
				/*
				else
					//경고창
					JOptionPane.showMessageDialog(null, "내용을 입력해주세요.");
				 */

			}

		});

		//엔터키 눌렀을 때 반응하기
		//키보드에 뭐가 눌러지면 무조건 실행되도록
		Msg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {				
				super.keyPressed(e);
				//입력이 있으면 해당 키 정보 알아내기
				int keyCode = e.getKeyCode();
				//입력한 키보드 코드에 따라 다르게 하기
				switch(keyCode) {
				case KeyEvent.VK_ENTER:
					//사용자가 입력한 내용 String에 할당하기
					String message = Msg.getText();

					//입력 후 textFeild 비워주기
					Msg.setText("");
					if(!message.contentEquals(""))
						sendMessage(message);
					/*
					else
						//경고창
						JOptionPane.showMessageDialog(null, "내용을 입력해주세요.");
					 */

					break;
				}
			}
		});		
		setVisible(true);

		//내용 입력에 자동으로 포커스 되도록 설정 - 언제나 편하게 메시지 내용을 받을 수 있도록
		Msg.requestFocus();

		//서버를 열어놓고 서버 소켓 생성하는 작업
		ServerThread serverThread = new ServerThread();
		//serverThread를 보조 스레드로 설정
		serverThread.setDaemon(true);
		//serverThread 시작
		serverThread.start();

	    //주소록 종료 버튼을 눌렀을 때 생기는 이벤트 리스너 생성
        exit.addActionListener (new ActionListener() {         
          public void actionPerformed(ActionEvent e) {   
              //연결 끊기
  				try {
  					//데이터 입출력 및 소켓 연결 끊기
  					if(dos != null) dos.close();
  					if(dis != null) dis.close();
  					if(socket != null) socket.close();
  				} catch (IOException e1) {
  					e1.printStackTrace();
  				}
  				//해당 윈도우 창 닫기
  				dispose();
            }
        });
	}//생성자 메소드	

	//서버와 연결되는 스레드
	class ServerThread extends Thread {
		@Override
		public void run() {			

			try {
				//서버 소켓 생성
				serverSocket = new ServerSocket(port);
				//준비되었으면 GUI에 해당 문구 띄우기
				textArea.append("서버소켓이 준비됐습니다...\n");
				//클라이언트 접속 되는 거 표시하기 위해 문구 띄움
				textArea.append("클라이언트의 접속을 기다립니다.\n");				
				//클라이언트가 접속할때까지 대기
				socket = serverSocket.accept();
				
				//데이터 전송을 위한 스트림 생성(입출력 모두)
				//서버에서 보내고 받는 메시지를 GUI 통해서 원활하게  보조 스트림을 사용
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());

				//상대방 이름
				dos.writeUTF(name);
				//서버로부터 받아온 상대방 이름 String str에 할당
	            String str = dis.readUTF();
	            //GUI에 상대방이 접속한 거 볼 수 있도록 설정
				textArea.append(str + "님이 접속하셨습니다.\n");	
				
				while(true) {//상대방 메시지 받기

					//서버(클라이언트->서버)에서 받아온 메시지를 String 연산자에 할당
					String msg = dis.readUTF();
					//상대방으로부터 받은 메시지 내용 표시
					textArea.append(str + " : " + msg + "\n");
					//상대방의 메시지 받은 후 스크롤바가 제일 밑에 올 수 있도록 설정
					textArea.setCaretPosition(textArea.getText().length());
				}				
			} catch (IOException e) {
				//클라이언트와의 연결이 끊어졌을 경우
				textArea.append("상대방이 나갔습니다.\n");
			}
		}
	}

	
	//메시지 전송하는 기능 메소드
	void sendMessage(String message) {	
		//내가 보낸 내용 GUI에도 나오도록
		textArea.append("나 : "+ message + "\n");

		//스크롤바 발생시 아래에 오도록 - 제일 최근 내가 주고받은 내용 볼 수 있도록
		textArea.setCaretPosition(textArea.getText().length());

		//상대방에게 전송하는 스레드 설정
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					//dos를 통해 사용자가 입력한 메세지 서버로 전송하기
					dos.writeUTF(message);
					//dos에 저장된 내용 지우기
					dos.flush();	

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		};
		//해당 스레드 시작하기
		t.start();
	}	
}