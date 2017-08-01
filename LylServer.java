package com.lyl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *	������������࣬���𴴽��������������ݣ�ά������
 */
public class LylServer {
	public static void main(String[] args) {
		
		ServerSocket server = null;
		Socket client = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		
		LylBoard board = null;
		JFrame win = null;
		
		try {
			
			//��������
			server = new ServerSocket(5678);
			System.out.println("�ȴ���������...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			//�ȴ�����
			client = server.accept();
			System.out.println("�����ѳɹ����ӣ�");
			
			//��ȡ��Ӧ���������
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
			
			//����ʵ��
			board = new LylBoard();
			
			//������ɫ���Ⱥ���
			board.setColor(LylDefine.BLACK);			
			board.setFlag(1);
			
			win = new JFrame();					//����ʵ��
			win.setTitle("Χ��");				//���ڱ���
			win.setBounds(300,10,830,850);		//����λ�ü���С
			win.add(board);						//�������
			win.addMouseListener(board);		//���������
			win.setVisible(true);
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			
			//��ʾ��Ϣ
			JOptionPane.showMessageDialog(win, "��������\n\n��������ֱ���˳�", "��ʾ��",
     				JOptionPane.INFORMATION_MESSAGE);
			
			/**
			 * 	temp_pos��ѭ����ȡ���ı���������֮�Ƚϣ�����ͬ��˵���������Ӳ�����
			 * 			  ��Է����ʹ����꣬������
			 * 
			 * 	eat_flag����������������ʱ��temp_posδ�ܼ�ʱ���¶������һ����
			 * 			  ��temp_pos��ͬʱ�޷����͵����
			 */
			
			//��ʼpos��temp_pos��Ϊ0
			int temp_pos = 0;	
			
			//ѭ�����������
			while(true)
			{	
				while(true)
				{
					int pos = board.getPos();
					if(temp_pos != pos)
					{
						//��Է���������
						out.writeInt(pos);
						System.out.println("�ڷ��ɹ����ͣ�"+pos);
						temp_pos = pos;
						break;
					}
				}
				
				//���նԷ����͵�����
				int op_pos = in.readInt();
				System.out.println("�ڷ��ɹ����գ�"+op_pos);
				
				//���±��������ж������ӵľ���
				board.setOp_pos(op_pos);
				board.op_play();
				
				//��������ԣ�����temp_pos,pos,eat_flag
				if(board.eat_flag==1)
				{
					temp_pos = 0;
					board.setPos(0);
					board.eat_flag = 0;
				}
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(win, "�Է����ϸ���", "�����",
     				JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
}
