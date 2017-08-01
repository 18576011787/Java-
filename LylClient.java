package com.lyl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *	�ͻ����������࣬�������ӷ��񣬴������ݣ�ά������ 
 */

public class LylClient {
	public static void main(String[] args) {
		
		Socket socket = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		
		LylBoard board = null;
		JFrame win = null;
		
		try {
			
			//��������
			socket = new Socket("10.24.12.142", 5678);
			System.out.println("�ɹ����Ӷ��֣�");
			
			//��ȡ��Ӧ�����������
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			
			//����ʵ��
			board = new LylBoard();
			
			//������ɫ���Ⱥ���
			board.setColor(LylDefine.WHITE);
			board.setFlag(-1);
			
			win = new JFrame();					//����ʵ��
			win.setTitle("Χ��");				//���ڱ���
			win.setBounds(300,10,830,850);		//����λ�ü���С
			win.add(board);						//�������
			win.addMouseListener(board);		//���������
			win.setVisible(true);
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			//��ʾ��Ϣ
			JOptionPane.showMessageDialog(win, "�Է�����\n\n��������ֱ���˳�", "��ʾ��",
     				JOptionPane.INFORMATION_MESSAGE);
			
			/**
			 * 	temp_pos��ѭ����ȡ���ı���������֮�Ƚϣ�����ͬ��˵���������Ӳ�����
			 * 			  ��Է����ʹ����꣬������
			 * 	
			 * 	eat_flag����������������ʱ��temp_posδ�ܼ�ʱ���¶������һ����
			 * 			  ��temp_pos��ͬʱ�޷����͵����
			 */

			int temp_pos = 0;
			
			//ѭ�������뷢��
			while(true)
			{
				//���նԷ����͵�����
				int op_pos = in.readInt();
				System.out.println("�׷��ɹ����գ�"+op_pos);
				
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
				
				while(true)
				{
					int pos = board.getPos();
					if(temp_pos != pos)
					{
						//��Է���������
						out.writeInt(pos);
						System.out.println("�׷��ɹ����ͣ�"+pos);
						temp_pos = pos;
						break;
					}
				}
			}
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(win, "�Է����ϸ���", "�����",
     				JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
