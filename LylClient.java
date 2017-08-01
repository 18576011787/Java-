package com.lyl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *	客户端主程序类，负责连接服务，传输数据，维护棋盘 
 */

public class LylClient {
	public static void main(String[] args) {
		
		Socket socket = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		
		LylBoard board = null;
		JFrame win = null;
		
		try {
			
			//建立连接
			socket = new Socket("10.24.12.142", 5678);
			System.out.println("成功连接对手！");
			
			//获取相应的输入输出流
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			
			//棋盘实例
			board = new LylBoard();
			
			//设置颜色及先后手
			board.setColor(LylDefine.WHITE);
			board.setFlag(-1);
			
			win = new JFrame();					//窗口实例
			win.setTitle("围棋");				//窗口标题
			win.setBounds(300,10,830,850);		//窗口位置及大小
			win.add(board);						//添加棋盘
			win.addMouseListener(board);		//添加鼠标监听
			win.setVisible(true);
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			//提示信息
			JOptionPane.showMessageDialog(win, "对方先手\n\n若认输请直接退出", "提示：",
     				JOptionPane.INFORMATION_MESSAGE);
			
			/**
			 * 	temp_pos：循环获取到的本方坐标与之比较，若不同则说明有新落子产生，
			 * 			  向对方发送此坐标，并更新
			 * 	
			 * 	eat_flag：用来避免产生打吃时，temp_pos未能及时更新而造成下一着手
			 * 			  与temp_pos相同时无法发送的情况
			 */

			int temp_pos = 0;
			
			//循环接收与发送
			while(true)
			{
				//接收对方发送的坐标
				int op_pos = in.readInt();
				System.out.println("白方成功接收："+op_pos);
				
				//更新本方棋盘中对手落子的局面
				board.setOp_pos(op_pos);
				board.op_play();
				
				//若产生打吃，更新temp_pos,pos,eat_flag
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
						//向对方发送坐标
						out.writeInt(pos);
						System.out.println("白方成功发送："+pos);
						temp_pos = pos;
						break;
					}
				}
			}
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(win, "对方已认负！", "结果：",
     				JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
