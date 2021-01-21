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

	//Main GUI���� ��Ʈ��ȣ,�̸� �޾ƿ���
	public ServerFrame(String Port, String Name) {		
		//String���� �޾ƿԱ� ������ Int�� ����ȯ�ؼ� �Ҵ�
		this.port = Integer.parseInt(Port);
		//�޾ƿ� ����� �̸� �Ҵ��ϱ�
		this.name = Name;

		//������ â �����ϱ�
		setTitle("����");
		setBounds(450, 50, 500, 350);

		//TextArea ����
		textArea = new JTextArea();		
		//TextArea���� ����ڰ� �Է� ������ �� �� ������ ����
		textArea.setEditable(false); //���� ����
		//��ȭ ������ �������� ��ũ�� �����ϵ��� scrollPane ����, scrollPane�� textarea �ֱ�
		JScrollPane scrollPane = new JScrollPane(textArea);
		//scrollPane ���̾ƿ� ����
		add(scrollPane,BorderLayout.CENTER);

				
		//ä�� ���� �Է��ϰ� ������ ��ư�� ���� �г� ����
		JPanel msgPanel = new JPanel();
		msgPanel.setLayout(new BorderLayout());
		//msgPanel ���̾ƿ� ����
		add(msgPanel,BorderLayout.SOUTH);
		
		//ä�� �޽����� �Է��� TextField ����
		Msg = new JTextField();
		//���� �Է� �� �ʿ��� ���� ��ư ����
		Send = new JButton("����");
		//�ؽ�Ʈ �ʵ� ���̾ƿ� ����
		msgPanel.add(Msg, BorderLayout.CENTER);
		//���� ��ư ���̾ƿ� ����
		msgPanel.add(Send, BorderLayout.EAST);


		//�޴��ٸ� ���� ����
		menuBar = new JMenuBar();
		//�޴��� ���̾ƿ� ����
		getContentPane().add(menuBar, BorderLayout.NORTH);
		//�ű⿡ �ý��� �κ� �ְ�
		System = new JMenu("�ý���");
		menuBar.add(System);
		//�ý��� �ȿ��ٰ� ���Ḧ �������� �ϱ�
		exit = new JMenuItem("����");
		System.add(exit);

		

		//������ ��ư Ŭ��
		Send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//�ش� �޽��� ������ ������
				//����ڰ� �Է��� ���� String�� �Ҵ��ϱ�
				String message = Msg.getText();

				//�Է� �� textFeild ����ֱ�
				Msg.setText("");
				if(!message.contentEquals(""))
					sendMessage(message);
				/*
				else
					//���â
					JOptionPane.showMessageDialog(null, "������ �Է����ּ���.");
				 */

			}

		});

		//����Ű ������ �� �����ϱ�
		//Ű���忡 ���� �������� ������ ����ǵ���
		Msg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {				
				super.keyPressed(e);
				//�Է��� ������ �ش� Ű ���� �˾Ƴ���
				int keyCode = e.getKeyCode();
				//�Է��� Ű���� �ڵ忡 ���� �ٸ��� �ϱ�
				switch(keyCode) {
				case KeyEvent.VK_ENTER:
					//����ڰ� �Է��� ���� String�� �Ҵ��ϱ�
					String message = Msg.getText();

					//�Է� �� textFeild ����ֱ�
					Msg.setText("");
					if(!message.contentEquals(""))
						sendMessage(message);
					/*
					else
						//���â
						JOptionPane.showMessageDialog(null, "������ �Է����ּ���.");
					 */

					break;
				}
			}
		});		
		setVisible(true);

		//���� �Է¿� �ڵ����� ��Ŀ�� �ǵ��� ���� - ������ ���ϰ� �޽��� ������ ���� �� �ֵ���
		Msg.requestFocus();

		//������ ������� ���� ���� �����ϴ� �۾�
		ServerThread serverThread = new ServerThread();
		//serverThread�� ���� ������� ����
		serverThread.setDaemon(true);
		//serverThread ����
		serverThread.start();

	    //�ּҷ� ���� ��ư�� ������ �� ����� �̺�Ʈ ������ ����
        exit.addActionListener (new ActionListener() {         
          public void actionPerformed(ActionEvent e) {   
              //���� ����
  				try {
  					//������ ����� �� ���� ���� ����
  					if(dos != null) dos.close();
  					if(dis != null) dis.close();
  					if(socket != null) socket.close();
  				} catch (IOException e1) {
  					e1.printStackTrace();
  				}
  				//�ش� ������ â �ݱ�
  				dispose();
            }
        });
	}//������ �޼ҵ�	

	//������ ����Ǵ� ������
	class ServerThread extends Thread {
		@Override
		public void run() {			

			try {
				//���� ���� ����
				serverSocket = new ServerSocket(port);
				//�غ�Ǿ����� GUI�� �ش� ���� ����
				textArea.append("���������� �غ�ƽ��ϴ�...\n");
				//Ŭ���̾�Ʈ ���� �Ǵ� �� ǥ���ϱ� ���� ���� ���
				textArea.append("Ŭ���̾�Ʈ�� ������ ��ٸ��ϴ�.\n");				
				//Ŭ���̾�Ʈ�� �����Ҷ����� ���
				socket = serverSocket.accept();
				
				//������ ������ ���� ��Ʈ�� ����(����� ���)
				//�������� ������ �޴� �޽����� GUI ���ؼ� ��Ȱ�ϰ�  ���� ��Ʈ���� ���
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());

				//���� �̸�
				dos.writeUTF(name);
				//�����κ��� �޾ƿ� ���� �̸� String str�� �Ҵ�
	            String str = dis.readUTF();
	            //GUI�� ������ ������ �� �� �� �ֵ��� ����
				textArea.append(str + "���� �����ϼ̽��ϴ�.\n");	
				
				while(true) {//���� �޽��� �ޱ�

					//����(Ŭ���̾�Ʈ->����)���� �޾ƿ� �޽����� String �����ڿ� �Ҵ�
					String msg = dis.readUTF();
					//�������κ��� ���� �޽��� ���� ǥ��
					textArea.append(str + " : " + msg + "\n");
					//������ �޽��� ���� �� ��ũ�ѹٰ� ���� �ؿ� �� �� �ֵ��� ����
					textArea.setCaretPosition(textArea.getText().length());
				}				
			} catch (IOException e) {
				//Ŭ���̾�Ʈ���� ������ �������� ���
				textArea.append("������ �������ϴ�.\n");
			}
		}
	}

	
	//�޽��� �����ϴ� ��� �޼ҵ�
	void sendMessage(String message) {	
		//���� ���� ���� GUI���� ��������
		textArea.append("�� : "+ message + "\n");

		//��ũ�ѹ� �߻��� �Ʒ��� ������ - ���� �ֱ� ���� �ְ���� ���� �� �� �ֵ���
		textArea.setCaretPosition(textArea.getText().length());

		//���濡�� �����ϴ� ������ ����
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					//dos�� ���� ����ڰ� �Է��� �޼��� ������ �����ϱ�
					dos.writeUTF(message);
					//dos�� ����� ���� �����
					dos.flush();	

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		};
		//�ش� ������ �����ϱ�
		t.start();
	}	
}