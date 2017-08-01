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
 *	���̽�����
 */

public class LylBoard extends JPanel implements MouseListener {
	
	private int board[][] = new int[19][19];		//���̶�ά����
	
	LylRule rule = new LylRule();			//����ʵ����

	private int x;				//��������
	private int y;
	private int pos;
	private int m;				//�Է�����
	private int n;
	private int op_pos;
	private int player;			//������ɫ
	private int op_player;		//�Է���ɫ
	private int flag;			//���ӱ�־��1���䣬-1������
	private int lastX = 0;		//�������
	private int lastY = 0;
	
	public int eat_flag = 0;	//��Ա�־��1Ϊ�д�Է�����0��
	
	LylBoard()
	{
		for(int i=0; i<19; i++)			//���̳�ʼ��
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
		Graphics2D p = (Graphics2D)g;		//����ʵ��
		
		//����
		Rectangle2D rect = new Rectangle2D.Double(20,20,780,780);
		p.setColor(new Color(244,207,150));
		p.fill(rect);

		int x = 50;			//�������Ͻ����¾���
		int y = 50;
		
		//19������
		for(int i=0; i<19; i++) {
			Line2D line = new Line2D.Double(y,y+40*i,770,y+40*i);
			p.setColor(Color.black);
			p.draw(line);
		}

		//19������
		for(int i=0; i<19; i++) {
			Line2D line = new Line2D.Double(x+40*i,x,x+40*i,770);
			p.setColor(Color.black);
			p.draw(line);
		}
		
		//��׺
		p.setColor(Color.black);
		Ellipse2D star_lu = new Ellipse2D.Double(166,166,8,8);	//������ 170 170
		p.fill(star_lu);

		Ellipse2D star_ue = new Ellipse2D.Double(406,166,8,8);	//�ϱ��� 410 170
		p.fill(star_ue);

		Ellipse2D star_ru = new Ellipse2D.Double(646,166,8,8);	//������ 650 170
		p.fill(star_ru);

		Ellipse2D star_le = new Ellipse2D.Double(166,406,8,8);	//����� 170 410
		p.fill(star_le);

		Ellipse2D star_ty = new Ellipse2D.Double(406,406,8,8);	//��Ԫ   410 410
		p.fill(star_ty);

		Ellipse2D star_re = new Ellipse2D.Double(646,406,8,8);	//�ұ��� 650 410
		p.fill(star_re);

		Ellipse2D star_ld = new Ellipse2D.Double(166,646,8,8);	//������ 170 650
		p.fill(star_ld);

		Ellipse2D star_de = new Ellipse2D.Double(406,646,8,8);	//�±��� 410 650
		p.fill(star_de);

		Ellipse2D star_rd = new Ellipse2D.Double(646,646,8,8);	//������ 650 650
		p.fill(star_rd);

		//�ػ�����
		for(int i=0; i<19; i++)
		{
			for(int j=0; j<19; j++)
			{					
				if(board[i][j] == LylDefine.BLACK)
				{
					//������
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
					//������
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
			//�������������ɫ��־
			p.setColor(Color.blue);
			
			Polygon polygon = new Polygon();
			polygon.addPoint(lastY*40+50, lastX*40+50);
			polygon.addPoint(lastY*40+50+20, lastX*40+50);
			polygon.addPoint(lastY*40+50, lastX*40+50+20);
			p.fill(polygon);
		}
	}
	
	//������������
	public void play()
	{
		//��ʱ����
		int temp_pos = (x+1)*21+y+1;
		
		//���岽�Ϸ�
     	if(rule.isValidMove(temp_pos, player))
     	{
     		//�����������鼰�������
     		board[x][y] = player;
     		lastX = x;
	        lastY = y;
	            
	        //������
	        new LylPlayMusic().Play();			
     		
	        //��������ԣ�ֱ�Ӹ�����������
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
     		
     		pos = temp_pos;		//pos��ֵ�����ⲻ�Ϸ������ֱ�����
     		flag = -flag;
     		repaint();
     	}
     	
     	//����������Ϸ���Ϣ
     	else if(rule.status==1)
     		JOptionPane.showMessageDialog(this, "��٣�","�Ƿ�����",
     				JOptionPane.WARNING_MESSAGE);
     	else if(rule.status==3)
     		JOptionPane.showMessageDialog(this, "������","�Ƿ�����",
     				JOptionPane.WARNING_MESSAGE);
	}
	
	//�Է���������
	//ͬ��
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

	//����¼�����ȡ������������
	public void mousePressed(MouseEvent e) {
		y = e.getX() - 5 ;		//��ȥ��߿�
		x = e.getY() - 30 ;		//��ȥ�ϱ߿�
		
		int m,n;
	    m=(x-50)%40;
	    n=(y-50)%40;
	    
	    if((m<=15||m>=25)&&(n<=15||n>=25))		//15px���
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




