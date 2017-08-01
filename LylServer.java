package com.lyl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *	服务端主程序类，负责创建监听，传输数据，维护棋盘
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
			
			//创建监听
			server = new ServerSocket(5678);
			System.out.println("等待对手连接...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			//等待连接
			client = server.accept();
			System.out.println("对手已成功连接！");
			
			//获取相应输入输出流
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
			
			//棋盘实例
			board = new LylBoard();
			
			//设置颜色及先后手
			board.setColor(LylDefine.BLACK);			
			board.setFlag(1);
			
			win = new JFrame();					//窗口实例
			win.setTitle("围棋");				//窗口标题
			win.setBounds(300,10,830,850);		//窗口位置及大小
			win.add(board);						//添加棋盘
			win.addMouseListener(board);		//添加鼠标监听
			win.setVisible(true);
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			
			//提示信息
			JOptionPane.showMessageDialog(win, "本方先手\n\n若认输请直接退出", "提示：",
     				JOptionPane.INFORMATION_MESSAGE);
			
			/**
			 * 	temp_pos：循环获取到的本方坐标与之比较，若不同则说明有新落子产生，
			 * 			  向对方发送此坐标，并更新
			 * 
			 * 	eat_flag：用来避免产生打吃时，temp_pos未能及时更新而造成下一着手
			 * 			  与temp_pos相同时无法发送的情况
			 */
			
			//初始pos与temp_pos都为0
			int temp_pos = 0;	
			
			//循环发送与接收
			while(true)
			{	
				while(true)
				{
					int pos = board.getPos();
					if(temp_pos != pos)
					{
						//向对方发送坐标
						out.writeInt(pos);
						System.out.println("黑方成功发送："+pos);
						temp_pos = pos;
						break;
					}
				}
				
				//接收对方发送的坐标
				int op_pos = in.readInt();
				System.out.println("黑方成功接收："+op_pos);
				
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
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(win, "对方已认负！", "结果：",
     				JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
}
