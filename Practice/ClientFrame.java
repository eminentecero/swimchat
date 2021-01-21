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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.Socket;

import java.net.UnknownHostException;



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



public class ClientFrame extends JFrame{
	JTextArea textArea;
	JTextField Msg;
	JButton Send;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String ip;
	int port;
	String name;
	private JMenuBar menuBar;
	private JMenu System;
	private JMenuItem exit;
	
	//Main GUI에서 포트번호,이름, IP주소 받아오기
	public ClientFrame(String IP, String Port, String Name) {
		//받아온 IP주소 할당하기
		this.ip = IP;
		//받아온 포트번호 할당하기
		//String으로 받아왔기 때문에 Int로 형변환해서 할당
		this.port = Integer.parseInt(Port);
		//받아온 사용자 이름 할당하기
		this.name = Name;

		//윈도우 창 설정하기
		setTitle("클라이언트");
		setBounds(450, 400, 500, 350);
		
		//TextArea 생성
		textArea = new JTextArea();		
		//TextArea에는 사용자가 입력 접근을 할 수 없도록 설정
		textArea.setEditable(false);
		//대화 내용이 많아지면 스크롤 가능하도록 scrollPane 설정, scrollPane에 textarea 넣기
		JScrollPane scrollPane = new JScrollPane(textArea);
		//scrollPane 레이아웃 설정
		add(scrollPane,BorderLayout.CENTER);

		//채팅 내용 입력하고 전송할 버튼이 있을 패널 생성
		JPanel msgPanel = new JPanel();
		msgPanel.setLayout(new BorderLayout());
		//msgPanel 레이아웃 설정
		add(msgPanel,BorderLayout.SOUTH);
		//getContentPane().add(msgPanel,BorderLayout.SOUTH);
		
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
		//레이아웃 설정
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
				else
					//경고창
					JOptionPane.showMessageDialog(null, "내용을 입력해주세요.");
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
					else
						//경고창
						JOptionPane.showMessageDialog(null, "내용을 입력해주세요.");
					break;
				}
			}
		});
		setVisible(true);

		//내용 입력에 자동으로 포커스 되도록 설정 - 언제나 편하게 메시지 내용을 받을 수 있도록
		Msg.requestFocus();

		//서버와 연결하는 네트워크 작업 : 스레드 객체 생성 및 실행
		ClientThread clientThread = new ClientThread();
		//clientThread를 보조 스레드로 설정
		clientThread.setDaemon(true);
		//clientThread 시작
		clientThread.start();

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
					textArea.append("서버가 닫히지 않았습니다.\n");
				}
				//해당 윈도우 창 닫기
				dispose();
          }
        });
	}
	

	//서버와 연결하는 스레드
	class ClientThread extends Thread {
		@Override
		public void run() {
			try {
				//소켓에 IP주소와 포트 번호 입력해서 연결
				socket = new Socket(ip, port);
				//서버와 접속되면 되었다고 표시
				textArea.append("서버에 접속됐습니다.\n");

				//데이터 전송을 위한 스트림 생성(입출력 모두)
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();

				//서버에서 보내고 받는 메시지를 GUI 통해서 원활하게  보조 스트림을 사용
				dis = new DataInputStream(is);
				dos = new DataOutputStream(os);	

				//서버로부터 자기 이름 전송
				dos.writeUTF(name);
	           
				//서버로부터 받아온 상대방 이름 String str에 할당
	            String str = dis.readUTF();
	           
	            //GUI에 상대방이 접속한 거 볼 수 있도록 설정
				textArea.append(str + "님이 접속하셨습니다.\n");	

				while(true) {//상대방 메시지 받기

					//서버에서 받아온 메시지를 String 연산자에 할당
					String message = dis.readUTF();

					//상대방으로부터 받은 메시지 내용 표시
					textArea.append(str + " : " + message + "\n");

					//상대방의 메시지 받은 후 스크롤바가 제일 밑에 올 수 있도록 설정
					textArea.setCaretPosition(textArea.getText().length());
				}

			} catch (UnknownHostException e) {
				//서버 주소가 존재하지 않을 경우
				textArea.append("서버 주소가 이상합니다.\n");

			} catch (IOException e) {
				//서버가 존재하지 않을 경우
				textArea.append("존재하지 않는 서버입니다.\n");

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
						textArea.append("메시지가 전송되지 않았습니다.\n");
					}
				}
			};
			//해당 스레드 시작하기
			t.start();
		}
}