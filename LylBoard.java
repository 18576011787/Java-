package com.lyl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *	棋盘界面类
 */

public class LylBoard extends JPanel implements MouseListener {
	
	private int board[][] = new int[19][19];		//棋盘二维数组
	
	LylRule rule = new LylRule();			//规则实例化

	private int x;				//本方坐标
	private int y;
	private int pos;
	private int m;				//对方坐标
	private int n;
	private int op_pos;
	private int player;			//本方颜色
	private int op_player;		//对方颜色
	private int flag;			//落子标志，1可落，-1不可落
	private int lastX = 0;		//最后落手
	private int lastY = 0;
	
	public int eat_flag = 0;	//打吃标志，1为有打吃发生，0无
	
	LylBoard()
	{
		for(int i=0; i<19; i++)			//棋盘初始化
		{
			for(int j=0; j<19; j++)
			{
				board[i][j] = 0;
			}
		}
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getOp_pos() {
		return op_pos;
	}

	public void setOp_pos(int op_pos) {
		this.op_pos = op_pos;
	}

	public void setColor(int color)
	{
		player = color;
		op_player = color++%2+1;
	}
	
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	
	public void paint(Graphics g) {
		Graphics2D p = (Graphics2D)g;		//画笔实例
		
		//棋盘
		Rectangle2D rect = new Rectangle2D.Double(20,20,780,780);
		p.setColor(new Color(244,207,150));
		p.fill(rect);

		int x = 50;			//棋盘左上角上下距离
		int y = 50;
		
		//19条横线
		for(int i=0; i<19; i++) {
			Line2D line = new Line2D.Double(y,y+40*i,770,y+40*i);
			p.setColor(Color.black);
			p.draw(line);
		}

		//19条竖线
		for(int i=0; i<19; i++) {
			Line2D line = new Line2D.Double(x+40*i,x,x+40*i,770);
			p.setColor(Color.black);
			p.draw(line);
		}
		
		//点缀
		p.setColor(Color.black);
		Ellipse2D star_lu = new Ellipse2D.Double(166,166,8,8);	//左上星 170 170
		p.fill(star_lu);

		Ellipse2D star_ue = new Ellipse2D.Double(406,166,8,8);	//上边星 410 170
		p.fill(star_ue);

		Ellipse2D star_ru = new Ellipse2D.Double(646,166,8,8);	//右上星 650 170
		p.fill(star_ru);

		Ellipse2D star_le = new Ellipse2D.Double(166,406,8,8);	//左边星 170 410
		p.fill(star_le);

		Ellipse2D star_ty = new Ellipse2D.Double(406,406,8,8);	//天元   410 410
		p.fill(star_ty);

		Ellipse2D star_re = new Ellipse2D.Double(646,406,8,8);	//右边星 650 410
		p.fill(star_re);

		Ellipse2D star_ld = new Ellipse2D.Double(166,646,8,8);	//左下星 170 650
		p.fill(star_ld);

		Ellipse2D star_de = new Ellipse2D.Double(406,646,8,8);	//下边星 410 650
		p.fill(star_de);

		Ellipse2D star_rd = new Ellipse2D.Double(646,646,8,8);	//右下星 650 650
		p.fill(star_rd);

		//重绘棋盘
		for(int i=0; i<19; i++)
		{
			for(int j=0; j<19; j++)
			{					
				if(board[i][j] == LylDefine.BLACK)
				{
					//画黑棋
					RadialGradientPaint paint = new RadialGradientPaint(j*40+50, i*40+50, 20, new float[]{0f, 1f}  
		               , new Color[]{Color.WHITE, Color.BLACK});  
					p.setPaint(paint);  
		            p.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
		            p.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);  
		  
					p.setColor(Color.black);
					p.fill(new Ellipse2D.Double(j*40+50-20, i*40+50-20, 40, 40));
					
				}
				else if(board[i][j] == LylDefine.WHITE)
				{
					//画白棋
					RadialGradientPaint paint = new RadialGradientPaint(j*40+50, i*40+50, 20, new float[]{0f, 1f}  
		               , new Color[]{Color.WHITE, Color.BLACK});  
					p.setPaint(paint);  
		            p.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
		            p.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
					
					p.setColor(Color.white);
					p.fill(new Ellipse2D.Double(j*40+50-20, i*40+50-20, 40, 40));
					
				}
			}
		}
		
		if(board[lastX][lastY] != 0)
		{
			//最后落手三角蓝色标志
			p.setColor(Color.blue);
			
			Polygon polygon = new Polygon();
			polygon.addPoint(lastY*40+50, lastX*40+50);
			polygon.addPoint(lastY*40+50+20, lastX*40+50);
			polygon.addPoint(lastY*40+50, lastX*40+50+20);
			p.fill(polygon);
		}
	}
	
	//本方更新棋盘
	public void play()
	{
		//临时坐标
		int temp_pos = (x+1)*21+y+1;
		
		//若棋步合法
     	if(rule.isValidMove(temp_pos, player))
     	{
     		//更新棋盘数组及最后落手
     		board[x][y] = player;
     		lastX = x;
	        lastY = y;
	            
	        //落子声
	        new LylPlayMusic().Play();			
     		
	        //若产生打吃，直接更新棋盘数组
     		if(rule.isEatMove())
     		{
     			LylEatString es = rule.getEatString();
     			int k = es.count;
     			while(k!=0)
     			{
     				k--;
     				int x = es.eated[k]/21-1;
     				int y = es.eated[k]%21-1;
     				board[x][y] = LylDefine.NOSTONE;
     			}
     		}	
     		
     		pos = temp_pos;		//pos赋值，避免不合法的着手被发送
     		flag = -flag;
     		repaint();
     	}
     	
     	//否则输出不合法信息
     	else if(rule.status==1)
     		JOptionPane.showMessageDialog(this, "打劫！","非法着手",
     				JOptionPane.WARNING_MESSAGE);
     	else if(rule.status==3)
     		JOptionPane.showMessageDialog(this, "无气！","非法着手",
     				JOptionPane.WARNING_MESSAGE);
	}
	
	//对方更新棋盘
	//同上
	public void op_play()
	{
		m = op_pos/21-1;
		n = op_pos%21-1;
		
     	if(rule.isValidMove(op_pos, op_player))
     	{
     		board[m][n] = op_player;
     		lastX = m;
	        lastY = n;
	          
	        new LylPlayMusic().Play();
     		
     		if(rule.isEatMove())
     		{
     			eat_flag = 1;
     			
     			LylEatString es = rule.getEatString();
     			int k = es.count;
     			while(k!=0)
     			{
     				k--;
     				int x = es.eated[k]/21-1;
     				int y = es.eated[k]%21-1;
     				board[x][y] = LylDefine.NOSTONE;
     			}
     		}	
     		
     		flag = -flag;
     		repaint();
     	}
	}

	//鼠标事件，获取本方落子坐标
	public void mousePressed(MouseEvent e) {
		y = e.getX() - 5 ;		//减去左边框
		x = e.getY() - 30 ;		//减去上边框
		
		int m,n;
	    m=(x-50)%40;
	    n=(y-50)%40;
	    
	    if((m<=15||m>=25)&&(n<=15||n>=25))		//15px误差
	    {
	        if(m<=15)
	        {
	            x -= m;
	            x = (x-50)/40;
	        }
	        if(m>=25)
	        {
	            x += 40-m;
	            x = (x-50)/40;
	        }
	        if(n<=15)
	        {
	            y -= n;
	            y = (y-50)/40;
	        }
	        if(n>=25)
	        {
	            y += 40-n;
	            y = (y-50)/40;
	        }
	        
	        if(flag==1)
	        	play();
	    }
	}
	
	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mouseClicked(MouseEvent e) {

	}

}




